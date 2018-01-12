package com.sampleboard.view.fragment.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.sampleboard.R;
import com.sampleboard.databinding.FragmentProfilePicBinding;
import com.sampleboard.permission.PermissionsAndroid;
import com.sampleboard.utils.Constants;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseFragment;
import com.sampleboard.view.activity.HolderActivity;
import com.sampleboard.view.activity.ProfileActivity;
import com.sampleboard.viewmodel.ProfilePicViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.databinding.adapters.ImageViewBindingAdapter.setImageUri;

/**
 * @author anuj on 4/8/2017.
 */

public class ProfilePicFragment extends BaseFragment implements ImagePickerCallback {
    private FragmentProfilePicBinding binding;
    private ProfilePicViewModel viewModel;
    private String profilePicUrl;

    private CameraImagePicker cameraPicker;
    private ImagePicker imagePicker;
    private boolean isImageSelected = false, cameraSelected = false, gallerySelected = false;
    private String pickerPath = "", profileImagePath = "";

    //request code
//    private final int IMAGE_CAPTURE_REQUEST_CODE = 0;
//    private final int IMAGE_CHOOSER_REQUEST_CODE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_profile_pic, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                //show Edit options
                checkCameraPermission();
                break;
            case R.id.action_share:
                //show share dialog
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_pic, container, false);
        viewModel = ViewModelProviders.of(this).get(ProfilePicViewModel.class);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        subscriveObservers();
    }

    private void subscriveObservers() {
        if (viewModel != null) {
            viewModel.getMessageObserver().observe(this, s -> {
                if (!TextUtils.isEmpty(s))
                    Utils.getInstance().showSnakBar(binding.getRoot(), s);
            });

            viewModel.getIsProfileUpdated().observe(this, aBoolean -> {
                if (aBoolean) {
                    //File upload successfully
                    if (!TextUtils.isEmpty(profileImagePath)) {
                        Picasso.with(getActivity()).load(new File(profileImagePath)).resize(200,200).centerCrop().into(binding.profilePic, new Callback() {
                            @Override
                            public void onSuccess() {
                                binding.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        });
                        //send broadcast
                        Intent profilePicBrodcastIntent = new Intent(Constants.BROADCAST_ACTIONS.PROFILE_UPDATE_BROADCAST);
                        profilePicBrodcastIntent.putExtra(Constants.PATH_IMAGE, profileImagePath);
                        getActivity().sendBroadcast(profilePicBrodcastIntent);
                    }
                } else {
                    //File did not uploaded
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void initViews() {
        if (getActivity() instanceof ProfileActivity) {
            ((ProfileActivity) getActivity()).setSupportActionBar(binding.includeToolbar.toolbar);
            ((ProfileActivity) getActivity()).getSupportActionBar().setTitle(R.string.profile_pic);
        } else if (getActivity() instanceof HolderActivity) {
            ((HolderActivity) getActivity()).setSupportActionBar(binding.includeToolbar.toolbar);
            ((HolderActivity) getActivity()).getSupportActionBar().setTitle("Image");
        }

        binding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_navigation_back);
        binding.includeToolbar.toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof ProfileActivity) {
                ((ProfileActivity) getActivity()).oneStepBack();
            } else if (getActivity() instanceof HolderActivity) {
                ((HolderActivity) getActivity()).oneStepBack();
            }

        });

        if (getArguments() != null && getArguments().getString(Constants.PATH_IMAGE) != null) {
            profilePicUrl = getArguments().getString(Constants.PATH_IMAGE);
        }
        if (!TextUtils.isEmpty(profilePicUrl)) {
            Picasso.with(getActivity()).load(profilePicUrl).into(binding.profilePic, new Callback() {
                @Override
                public void onSuccess() {
                    binding.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    binding.profilePic.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.def_profile_img));
                    binding.progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            binding.profilePic.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.def_profile_img));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionsAndroid.CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission();
                } else {
                    //permission denied
                    Utils.getInstance().showToast("Camera/Storage permission required");
                }
                break;
        }
    }

    /*private void showOptins() {
        PackageManager packageManager = getActivity().getPackageManager();
        List<Intent> cameraIntents = new ArrayList<>();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, IMAGE_CAPTURE_REQUEST_CODE);
        for (ResolveInfo res : listCam) {
            String packageName = res.activityInfo.packageName;
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri(getActivity()));
            cameraIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image*//*");

        Intent chooserIntent = Intent.createChooser(galleryIntent, "Choose Option");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        getActivity().startActivityForResult(chooserIntent, IMAGE_CHOOSER_REQUEST_CODE);
    }

    // function to set the path of picture captured from camera.
    private Uri setImageUri(Context context) {
        checkFolder(context);
        File file = new File(Environment.getExternalStorageDirectory()
                + "/" + context.getResources().getString(R.string.app_name) + "/originalImage.png");
        return Uri.fromFile(file);
    }

    private void checkFolder(Context context) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/" + context.getResources().getString(R.string.app_name));
            if (!folder.exists()) {
                folder.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   */
    //  @TargetApi(Build.VERSION_CODES.M)
    private void checkCameraPermission() {
        boolean isExternalStorage = PermissionsAndroid.getInstance().checkCameraPermission(getActivity());
        if (!isExternalStorage) {
            PermissionsAndroid.getInstance().requestForCameraPermission(ProfilePicFragment.this);
        } else if (cameraSelected) {
            takePicture();
        } else if (gallerySelected) {
            pickImageSingle();
        } else {
            pickImageSingle();
        }
    }

    private void takePicture() {
        cameraPicker = new CameraImagePicker(this);
//        cameraPicker.setCacheLocation(CacheLocation.INTERNAL_APP_DIR);
        cameraPicker.setImagePickerCallback(this);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        pickerPath = cameraPicker.pickImage();
    }

    private void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        imagePicker.pickImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isVisible()) {
            if (resultCode == RESULT_OK) {
                if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                    if (imagePicker == null) {
                        imagePicker = new ImagePicker(getActivity());
                    }
                    imagePicker.submit(data);
                } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                    if (cameraPicker == null) {
                        cameraPicker = new CameraImagePicker(getActivity());
                        cameraPicker.setImagePickerCallback(this);
                        cameraPicker.reinitialize(pickerPath);
                    }
                    cameraPicker.submit(data);
                }
            } /*else if (resultCode == 999) {
                if (data.getData() != null) {
                    profileImagePath = data.getData().toString();
                    isImageSelected = true;

                    //check image size should be less than 5MB
//                    File img = new File();
                    Picasso.with(getActivity()).load(new File(data.getData().toString())).into(profileImg);
                }*/
        } else {
            Utils.getInstance().showToast("Request cancelled");
        }

    }


    @Override
    public void onImagesChosen(List<ChosenImage> list) {
        ChosenImage image = list.get(0);
        if (Utils.isNetworkAvailable(getActivity())) {
            binding.progressBar.setVisibility(View.VISIBLE);
            profileImagePath = image.getOriginalPath();
            //Upload Image
            viewModel.uploadProfilePic(profileImagePath, 11);
        }
    }

    @Override
    public void onError(String s) {

    }
}

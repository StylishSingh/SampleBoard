package com.sampleboard.view.fragment.profile;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampleboard.R;
import com.sampleboard.bean.api_response.UserResponse;
import com.sampleboard.databinding.FragmentEditProfileBinding;
import com.sampleboard.utils.Constants;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseFragment;
import com.sampleboard.view.activity.HolderActivity;
import com.sampleboard.view.activity.ProfileActivity;
import com.sampleboard.viewmodel.EditProfileViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;


/**
 * @author Anuj Sharma on 4/6/2017.
 */

public class EditProfileFragment extends BaseFragment implements View.OnClickListener {
    private FragmentEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private int gender = 1;
    private String profilePicUrl = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        viewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        //set broadcast receiver
        profileBroadIntentFilter.addAction(Constants.BROADCAST_ACTIONS.PROFILE_UPDATE_BROADCAST);
        getActivity().registerReceiver(profilePicBroadcastReceiver, profileBroadIntentFilter);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        if (profilePicBroadcastReceiver != null)
            getActivity().unregisterReceiver(profilePicBroadcastReceiver);
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVIews();
        if (Utils.isNetworkAvailable(getActivity())) viewModel.getProfileInfo(11);
        else Utils.getInstance().showSnakBar(binding.getRoot(), getString(R.string.error_internet));
    }

    private void initVIews() {
        if (binding != null) {
            if (getActivity() instanceof ProfileActivity) {
                ((ProfileActivity) getActivity()).setSupportActionBar(binding.includeToolbar.toolbar);
                ((ProfileActivity) getActivity()).getSupportActionBar().setTitle(R.string.edit_profile);
            } else if (getActivity() instanceof HolderActivity) {
                ((HolderActivity) getActivity()).setSupportActionBar(binding.includeToolbar.toolbar);
                ((HolderActivity) getActivity()).getSupportActionBar().setTitle(R.string.edit_profile);
            }

            binding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_navigation_back);
            binding.includeToolbar.toolbar.setNavigationOnClickListener(v -> {
                if (getActivity() instanceof ProfileActivity)
                    ((ProfileActivity) getActivity()).oneStepBack();
                else if (getActivity() instanceof HolderActivity)
                    ((HolderActivity) getActivity()).oneStepBack();
            });
            binding.profileImage.setOnClickListener(this);
            binding.btnEditProfilePic.setOnClickListener(this);
            binding.btnSaveProfile.setOnClickListener(this);

            binding.groupGender.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.radio_male:
                        gender = 1;
                        break;
                    case R.id.radio_female:
                        gender = 2;
                        break;
                }
            });
            subscribeObservers();
        }
    }

    private void subscribeObservers() {
        if (viewModel != null) {
            viewModel.getMessageObserver().observe(this, s -> {
                if (!TextUtils.isEmpty(s)) Utils.getInstance().showSnakBar(binding.getRoot(), s);
            });
            viewModel.getUserResponseObserver().observe(this, (UserResponse userResponse) -> {
                if (userResponse != null) {
                    //Update UI accordingly
                    if (!TextUtils.isEmpty(userResponse.getProfile_avatar())) {
                        profilePicUrl = userResponse.getProfile_avatar();
                        Picasso.with(getActivity()).load(Constants.API_BASE_URL + userResponse.getProfile_avatar()).into(binding.profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                binding.profileProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                binding.profileProgressBar.setVisibility(View.GONE);
                                binding.profileImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.def_profile_img));
                            }
                        });
                    } else {
                        binding.profileImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.def_profile_img));
                    }

                    switch (userResponse.getGender()) {
                        case "1":
                            gender = 1;
                            binding.radioMale.setChecked(true);
                            binding.radioFemale.setChecked(false);
                            break;
                        case "2":
                            gender = 2;
                            binding.radioFemale.setChecked(true);
                            binding.radioMale.setChecked(false);
                            break;
                    }

                    binding.setViewModel(userResponse);
                    binding.executePendingBindings();
                    if (!TextUtils.isEmpty(userResponse.getFirst_name()))
                        binding.etFirstname.setSelection(userResponse.getFirst_name().length());
                }
            });

            viewModel.getUpdateProfileObserver().observe(this, aBoolean -> {
//                if (aBoolean) {
//                    //Profile Updated
//                } else {
//                    //Error Occured
//                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                moveToProfilePicFragment();
                break;
            case R.id.btn_edit_profile_pic:
                moveToProfilePicFragment();
                break;
            case R.id.btn_saveProfile:
                if (Utils.isNetworkAvailable(getActivity())) {
                    UserResponse userObj = new UserResponse();
                    userObj.setFirst_name(binding.etFirstname.getText().toString());
                    userObj.setLast_name(binding.etLastname.getText().toString());
                    userObj.setGender(String.valueOf(gender));
                    viewModel.validateProfile(userObj, 11);
                } else {
                    Utils.getInstance().showSnakBar(binding.getRoot(), getString(R.string.error_internet));
                }
                break;
        }
    }

    private void moveToProfilePicFragment() {
        /*btnEditProfile.hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnEditProfile.show();
                Intent profilePicIntent = new Intent(getActivity(),ProfileActivity.class);
                profilePicIntent.putExtra("destination","profile_pic");
                if (Utils.getInstance().isEqualLollipop()) {
                    Pair<View, String> p1 = Pair.create((View) mCircleImageView, "profile_pic");
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(), p1);
                    getActivity().startActivity(profilePicIntent, options.toBundle());
                } else {
                    getActivity().startActivity(profilePicIntent);
                }
            }
        },500);*/

        Intent profilePicIntent = new Intent(getActivity(), ProfileActivity.class);
        profilePicIntent.putExtra(Constants.DESTINATION, Constants.PROFILE_PIC_SCREEN);
        profilePicIntent.putExtra(Constants.PATH_IMAGE, profilePicUrl);
        if (Utils.getInstance().isEqualLollipop()) {
            Pair<View, String> p1 = Pair.create(binding.profileImage, binding.profileImage.getTransitionName());
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(), p1);
            getActivity().startActivity(profilePicIntent, options.toBundle());
        } else {
            getActivity().startActivity(profilePicIntent);
        }
    }

    /**
     * Broadcast Receiver to handle the Profile Pic upload status
     */
    private IntentFilter profileBroadIntentFilter = new IntentFilter();
    private BroadcastReceiver profilePicBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && Objects.equals(intent.getAction(), Constants.BROADCAST_ACTIONS.PROFILE_UPDATE_BROADCAST)) {
                if (!TextUtils.isEmpty(intent.getStringExtra(Constants.PATH_IMAGE))) {
                    Picasso.with(getActivity()).load(new File(intent.getStringExtra(Constants.PATH_IMAGE)))
                            .resize(200, 200).centerCrop().into(binding.profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            binding.profileProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            binding.profileProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    };
}

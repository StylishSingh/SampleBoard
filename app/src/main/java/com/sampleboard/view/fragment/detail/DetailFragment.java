package com.sampleboard.view.fragment.detail;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sampleboard.GlobalActivity;
import com.sampleboard.R;
import com.sampleboard.adapter.LikedAdapter;
import com.sampleboard.bean.PostDetailBean;
import com.sampleboard.enums.CurrentScreen;
import com.sampleboard.permission.PermissionsAndroid;
import com.sampleboard.utils.Constants;
import com.sampleboard.utils.CustomAnimationDrawableNew;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseFragment;
import com.sampleboard.view.activity.DetailActivityV2;
import com.sampleboard.viewmodel.DetailFragmentViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author Anuj Sharma on 4/10/2017.
 */

public class DetailFragment extends BaseFragment implements View.OnClickListener {
    private View rootVIew;
    private DetailFragmentViewModel viewModel;
    private ImageView mDetailImage, mLikeImgInitial, mLikeImgFinal;
    private FloatingActionButton fabDownload;
    private ProgressBar mProgresbar;
    private TextView mOwnerName, mLikeCount, mCommentCount, mTags, mDesc, mReadMore;
    private PostDetailBean bean;


    private String descString = "testing testing testing testing testing testing testing testing testing testing testing testing testing" +
            "testing testing testing";

//    private AnimationDrawable frameAnimation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootVIew = inflater.inflate(R.layout.fragment_post_detail, container, false);
        viewModel = ViewModelProviders.of(this).get(DetailFragmentViewModel.class);
        subscribeDownloadReceiver();
        return rootVIew;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        loadIntiialData();
    }

    @Override
    public void onDestroyView() {
        unSubscribeDownloadReceiver();
        super.onDestroyView();
    }

    private void initViews() {
        Toolbar mToolbar = rootVIew.findViewById(R.id.toolbar);
        if (getActivity() instanceof DetailActivityV2) {
            ((DetailActivityV2) getActivity()).setSupportActionBar(mToolbar);
            ((DetailActivityV2) getActivity()).getSupportActionBar().setTitle("Detail");
            ((DetailActivityV2) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(v -> ((DetailActivityV2) getActivity()).oneStepBack());
        }

        mDetailImage = rootVIew.findViewById(R.id.post_detail_img);
        fabDownload = rootVIew.findViewById(R.id.fab_download);
        mProgresbar = rootVIew.findViewById(R.id.progress_bar);
//        CircleImageView mOwnerImg = rootVIew.findViewById(R.id.owner_img);
        mOwnerName = rootVIew.findViewById(R.id.owner_name);
        mLikeImgInitial = rootVIew.findViewById(R.id.ic_heart_initial);
        mLikeImgFinal = rootVIew.findViewById(R.id.ic_heart_final);
        mLikeCount = rootVIew.findViewById(R.id.like_count);
        mCommentCount = rootVIew.findViewById(R.id.comment_count);
        mTags = rootVIew.findViewById(R.id.tags);
        mDesc = rootVIew.findViewById(R.id.description);
        mReadMore = rootVIew.findViewById(R.id.readmore);
        LinearLayout ownerProfileView = rootVIew.findViewById(R.id.view_owner_info);

        RecyclerView relatedRecycler = rootVIew.findViewById(R.id.related_recycler);
        StaggeredGridLayoutManager sm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        relatedRecycler.setLayoutManager(sm);

        LikedAdapter mAdapter = new LikedAdapter(getActivity(), null, DetailFragment.this);
        relatedRecycler.setAdapter(mAdapter);

        /*try {
            Gson gson = new Gson();
            MediaModel mediaModel = gson.fromJson(getStringFromLocalJson("media_list.json", getActivity()), MediaModel.class);

            mAdapter.updateList(mediaModel.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        mAdapter.updateData(viewModel.loadDummyRelatedData());

        //set click listeners
        mLikeImgInitial.setOnClickListener(this);
        mLikeImgFinal.setOnClickListener(this);
        rootVIew.findViewById(R.id.comment_container).setOnClickListener(this);
        mReadMore.setOnClickListener(this);
        ownerProfileView.setOnClickListener(this);
        fabDownload.setOnClickListener(this);

    }


    private void loadIntiialData() {
        if (getArguments() != null && getArguments().getParcelable(Constants.OBJ_DETAIL) != null) {
            bean = getArguments().getParcelable(Constants.OBJ_DETAIL);
            final int position = getArguments().getInt(Constants.POSITION);
            Bitmap imageCoverBitmap = GlobalActivity.photoCache.get(position);
            //safety check to prevent nullPointer in the palette if the detailActivity was in the background for too long
            /*if (imageCoverBitmap == null || imageCoverBitmap.isRecycled()) {
                getActivity().finish();
                return;
            }*/
            if (imageCoverBitmap != null && !imageCoverBitmap.isRecycled()) {
                mDetailImage.setImageBitmap(imageCoverBitmap);
                mProgresbar.setVisibility(View.GONE);
                generatePallet(imageCoverBitmap);
            } else {
                assert bean != null;
                String imageUrl;
                if (bean.photoUrl.startsWith("http")) {
                    imageUrl = bean.photoUrl;
                } else {
                    imageUrl = "file://" + bean.photoUrl;
                }

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mProgresbar.setVisibility(View.GONE);
                        assert mDetailImage != null;
                        generatePallet(bitmap);
                        if (bitmap != null)
                            mDetailImage.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        mProgresbar.setVisibility(View.GONE);
                        mDetailImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_default_image));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                // set the tag to the view
                mDetailImage.setTag(target);
                Picasso.with(getActivity()).load(imageUrl)
                        .into(target);
            }

            if (getActivity() instanceof DetailActivityV2) {
                ((DetailActivityV2) getActivity()).getSupportActionBar().setTitle(String.valueOf(bean.photoName));
            }
            //set owner info
            mOwnerName.setText(bean.ownerName);
            mLikeCount.setText(String.valueOf(bean.likeCount));
            mCommentCount.setText(String.valueOf(bean.commentCount));
            // check post is liked or not
            if (bean.isLiked) {
                //show final image
                mLikeImgFinal.setVisibility(View.VISIBLE);
                mLikeCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                mLikeImgInitial.setVisibility(View.GONE);
            } else {
                //show initial image
                mLikeImgInitial.setVisibility(View.VISIBLE);
                mLikeCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_textcolor));
                mLikeImgFinal.setVisibility(View.GONE);
            }

            //set tags
            String tagString = "#office #nature #wild #beauty";
            mTags.setText(tagString);

            //set descString
            if (descString.length() > 100) {
                mDesc.setText(descString.substring(0, 100));
                mReadMore.setVisibility(View.VISIBLE);
            } else {
                mDesc.setText(descString);
                mReadMore.setVisibility(View.GONE);
            }
        }
    }


    @SuppressLint("NewApi")
    private void generatePallet(Bitmap bitmap) {
        if (bitmap != null) {
            Palette.from(bitmap)
                    .generate(palette -> {
                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                        if (textSwatch == null) {
                            return;
                        }
                        int mDefaultBackgroundColor = 0;
                        Utils.animateViewColor(rootVIew.findViewById(R.id.appBar),
                                mDefaultBackgroundColor, textSwatch.getRgb());
                        Utils.animateViewColor(rootVIew.findViewById(R.id.toolbar),
                                mDefaultBackgroundColor, textSwatch.getRgb());
                        Utils.animateBackgroundTintColor(fabDownload,
                                mDefaultBackgroundColor, textSwatch.getRgb());
                    });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_heart_initial:
                doLike();
                break;
            case R.id.ic_heart_final:
                doUnLike();
                break;
            case R.id.comment_container:
                //Move to comment screen
                if (getActivity() instanceof DetailActivityV2) {
                    CommentFragment commentFragment = new CommentFragment();
                    FragmentTransaction fts = getActivity().getSupportFragmentManager().beginTransaction();
//                    commentFragment.setArguments(bundle);
                    fts.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
                    fts.add(R.id.container_detail, commentFragment, commentFragment.getClass().getSimpleName());
                    fts.addToBackStack(commentFragment.getClass().getSimpleName());
                    fts.commit();
                }
                break;
            case R.id.readmore:
                showMoreDescription();
                break;
            case R.id.view_owner_info:
                if (getActivity() instanceof DetailActivityV2) {
                    ((DetailActivityV2) getActivity()).changeScreen(R.id.container_detail, CurrentScreen.USER_PROFILE_SCREEN, true, true, null);
                }
                break;
            case R.id.fab_download:
                if (bean != null) {
                    downloadFile(bean.photoUrl, bean.photoName);
                }
                break;
        }
    }

    /**
     * DO Like functionality and update UI
     */
    private void doLike() {
        mLikeImgInitial.setClickable(false);
        CustomAnimationDrawableNew cad = new CustomAnimationDrawableNew((AnimationDrawable) ContextCompat.getDrawable(getActivity(),
                R.drawable.animation_list_layout)) {
            @Override
            public void onAnimationFinish() {
                mLikeImgInitial.setVisibility(View.GONE);
                mLikeImgFinal.setVisibility(View.VISIBLE);
                mLikeImgFinal.setClickable(true);
            }

            @Override
            public void onAnimtionStart() {
                final LinearInterpolator interpolator = new LinearInterpolator();
                int updatedCount = viewModel.updateLikesCounter(bean.likeCount,
                        true);
                bean.likeCount = updatedCount;
                mLikeCount.animate()
                        .alpha(0)
                        .setDuration(100)
                        .setStartDelay(200)
                        .setInterpolator(interpolator)
                        .withEndAction(() -> {
                            mLikeCount.animate()
                                    .alpha(1)
                                    .setDuration(100)
                                    .setInterpolator(interpolator);
                            mLikeCount.setText(String.valueOf(updatedCount));
                            mLikeCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                        });
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mLikeImgInitial.setBackground(cad);
        } else {
            mLikeImgInitial.setBackgroundDrawable(cad);
        }
        cad.start();
    }

    /**
     * DO UnLike functionality and update UI
     */
    private void doUnLike() {
        mLikeImgInitial.setVisibility(View.VISIBLE);
        mLikeImgInitial.setClickable(true);
        mLikeImgFinal.setVisibility(View.GONE);
        mLikeImgFinal.setClickable(false);
        mLikeImgInitial.setBackgroundResource(R.drawable.animation_list_layout);
        int updatedCount = viewModel.updateLikesCounter(bean.likeCount,
                false);
        bean.likeCount = updatedCount;
        mLikeCount.setText(String.valueOf(updatedCount));
        mLikeCount.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_textcolor));
    }


    private void showMoreDescription() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition((ViewGroup) rootVIew);
        }
        try {
            if (mReadMore.getText().equals(getString(R.string.show_more))) {
                mDesc.setText(descString.substring(0, descString.length()));
                mReadMore.setText(getString(R.string.show_less));
            } else if (mReadMore.getText().equals(getString(R.string.show_less))) {
                mDesc.setText(descString.substring(0, 100));
                mReadMore.setText(getString(R.string.show_more));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Download File Concept implemented here
     */
    private DownloadManager dm;
    private long enqueue;

    public void downloadFile(String url, String photoName) {
        if (!PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(getActivity())) {
            PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(this);
            return;
        }

        dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        request.setTitle(photoName);
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        File file = Utils.getInstance().saveToDirectory(getActivity(), photoName, "jpg");
        if (file == null)
            return;

        request.setDestinationUri(Uri.parse("file://" + "/" + file.getAbsolutePath()));
//        if (file != null && file.exists()) file.delete();
        enqueue = dm.enqueue(request);

    }

    public void subscribeDownloadReceiver() {
        getActivity().registerReceiver(downloadCompleteReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void unSubscribeDownloadReceiver() {
        try {
            if (downloadCompleteReceiver != null)
                getActivity().unregisterReceiver(downloadCompleteReceiver);
        } catch (IllegalStateException illegal) {
            illegal.printStackTrace();
        }

    }

    BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isVisible()) return;

            String action = intent.getAction();

            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//                long downloadId = intent.getLongExtra(
//                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(enqueue);
                if (dm == null) return;

                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {
                        Utils.getInstance().showToast(getString(R.string.message_download_complete));
                    }
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsAndroid.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (bean != null) {
                    downloadFile(bean.photoUrl, bean.photoName);
                }
            } else {
                // permission denied, boo! Disable the
                Utils.getInstance().showToast("Permission denied");
            }
        }

    }
}

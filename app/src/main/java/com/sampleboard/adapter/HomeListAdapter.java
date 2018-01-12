package com.sampleboard.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sampleboard.R;
import com.sampleboard.bean.MediaItem;
import com.sampleboard.bean.MediaModel;
import com.sampleboard.interfaces.MediaListInterface;
import com.sampleboard.utils.CustomAnimationDrawableNew;
import com.sampleboard.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Mobilyte India Pvt Ltd on 3/1/2017.
 */

public class HomeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private MediaModel.DataBean mResponse;
    private AnimationDrawable frameAnimation;
    private int lastPosition = -1;
    public final static int COLOR_ANIMATION_DURATION = 1000;
    private int mDefaultBackgroundColor;
    private MediaListInterface listener;

    public HomeListAdapter(Context ctx, MediaModel.DataBean response, MediaListInterface listener) {
        this.mContext = ctx;
        this.mResponse = response;
        this.listener = listener;
    }

    public void updateList(MediaModel.DataBean response) {
        this.mResponse = response;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        RecyclerView.ViewHolder vh;
        rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list, parent, false);
        vh = new LoadMoreViewHolder(rowView);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final LoadMoreViewHolder vh = (LoadMoreViewHolder) holder;
        try {
            MediaItem obj = mResponse.getMediaList().get(position);
            vh.mTitle.setText(obj.getTitle());
            vh.mLikesCount.setText(obj.getLike_count());
            if (obj.isLiked()) {
                vh.mLikeImgInitial.setVisibility(View.GONE);
                vh.mLikeImgFinal.setVisibility(View.VISIBLE);
                vh.mLikesCount.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                vh.mLikeImgFinal.setClickable(true);
            } else {
                vh.mLikeImgInitial.setVisibility(View.VISIBLE);
                vh.mLikeImgInitial.setClickable(true);
                vh.mLikeImgFinal.setVisibility(View.GONE);
                vh.mLikesCount.setTextColor(ContextCompat.getColor(mContext, R.color.app_textcolor_heading));
                vh.mLikeImgFinal.setClickable(false);
                vh.mLikeImgInitial.setBackgroundResource(R.drawable.animation_list_layout);
            }
            if (obj.isShared()) {
                vh.reshareImg.setColorFilter(ContextCompat.getColor(mContext, R.color.green));
            } else {
                vh.reshareImg.setColorFilter(ContextCompat.getColor(mContext, R.color.app_textcolor_heading));
            }
            //cancel any loading images on this view
            Picasso.with(mContext).cancelRequest(vh.mImage);
            vh.mImage.setImageBitmap(null);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    assert vh.mImage != null;
                            /* Save the bitmap or do something with it here */
                    Palette.from(bitmap)
                            .generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                    if (textSwatch == null) {
                                        return;
                                    }
                                    vh.mParentLayout.setBackgroundColor(textSwatch.getRgb());
//                                            vh.mInfoContainer.setBackgroundColor(textSwatch.getRgb());
                                    vh.mTitle.setTextColor(textSwatch.getTitleTextColor());
                                    Utils.animateViewColor(vh.mInfoContainer, mDefaultBackgroundColor, textSwatch.getRgb());
                                }
                            });
                    if (bitmap != null)
                        vh.mImage.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    vh.mImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_default_image));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            // set the tag to the view
            vh.mImage.setTag(target);
            Picasso.with(mContext).load(obj.getMedia())
                    .resize(500, 500).centerCrop()
                    .into(target);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.bottom_anim);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return (mResponse == null) ? 0 : mResponse.getMediaList().size();
    }

    /*
    View Holder For Trip History
     */
    public class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        //        CardView mCardView;
        private LinearLayout mParentLayout;
        private RelativeLayout mInfoContainer;
        private ImageView reshareImg, mImage, mLikeImgInitial, mLikeImgFinal;
        private TextView mTitle, mLikesCount;

        private LoadMoreViewHolder(View itemView) {
            super(itemView);
            mParentLayout = itemView.findViewById(R.id.parent);
            mInfoContainer = itemView.findViewById(R.id.info_container);
            reshareImg = itemView.findViewById(R.id.img_re_share);
            mImage = itemView.findViewById(R.id.image);
            mTitle = itemView.findViewById(R.id.title);
            mLikeImgInitial = itemView.findViewById(R.id.ic_heart_initial);
            mLikeImgFinal = itemView.findViewById(R.id.ic_heart_final);
            mLikesCount = itemView.findViewById(R.id.likes_count);

            mImage.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClick(mResponse.getMediaList().get(getAdapterPosition()), (ImageView) v, getAdapterPosition());

            });
            reshareImg.setOnClickListener(view -> {

            });

            mLikeImgInitial.setOnClickListener(v -> {
                mLikeImgInitial.setClickable(false);
                CustomAnimationDrawableNew cad = new CustomAnimationDrawableNew((AnimationDrawable) mContext.getResources().getDrawable(
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
                        int updatedCount = updateLikesCounter(Integer.parseInt(mResponse.getMediaList().get(getAdapterPosition()).getLike_count()),
                                true);
                        mResponse.getMediaList().get(getAdapterPosition()).setLike_count(String.valueOf(updatedCount));
                        mLikesCount.animate()
                                .alpha(0)
                                .setDuration(100)
                                .setStartDelay(200)
                                .setInterpolator(interpolator)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLikesCount.animate()
                                                .alpha(1)
                                                .setDuration(100)
                                                .setInterpolator(interpolator);
                                        mLikesCount.setText(String.valueOf(updatedCount));
                                        mLikesCount.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                                    }
                                });
                    }

                };
                mLikeImgInitial.setBackgroundDrawable(cad);
                cad.start();
            });

            mLikeImgFinal.setOnClickListener(v -> {
                mLikeImgInitial.setVisibility(View.VISIBLE);
                mLikeImgInitial.setClickable(true);
                mLikeImgFinal.setVisibility(View.GONE);
                mLikesCount.setTextColor(ContextCompat.getColor(mContext, R.color.app_textcolor));
                mLikeImgFinal.setClickable(false);
                mLikeImgInitial.setBackgroundResource(R.drawable.animation_list_layout);
                int updatedCount = updateLikesCounter(Integer.parseInt(mResponse.getMediaList().get(getAdapterPosition()).getLike_count()),
                        false);
                mResponse.getMediaList().get(getAdapterPosition()).setLike_count(String.valueOf(updatedCount));
                mLikesCount.setText(String.valueOf(updatedCount));
            });
        }
    }

    private int updateLikesCounter(int count, boolean isIncreased) {
        if (isIncreased)
            return count + 1;
        else
            return count - 1;
    }

    /*private void manageShortView(View v, PhotosBean bean) {
        v.setHapticFeedbackEnabled(true);
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        PeekAndPop peekAndPop = new PeekAndPop.Builder(fragment.getActivity())
                .peekLayout(R.layout.fragment_shortview)
                .longClickViews(v)
                .blurBackground(true)
                .parentViewGroupToDisallowTouchEvents((ViewGroup) fragment.getView())
                .build();
        View peekView = peekAndPop.getPeekView();
        CardView cardView = (CardView) peekView.findViewById(R.id.parent_shortView);
        ImageView imageView = (ImageView) peekView.findViewById(R.id.img_shortview);
        ImageView likeImg = (ImageView) peekView.findViewById(R.id.btn_like);
        ImageView commentImg = (ImageView) peekView.findViewById(R.id.btn_message);
        ImageView shareImg = (ImageView) peekView.findViewById(R.id.btn_share);
        final ProgressBar progressBar = (ProgressBar) peekView.findViewById(R.id.progress_bar);

        peekAndPop.addLongHoldView(R.id.parent_shortView, false);
        peekAndPop.addHoldAndReleaseView(R.id.btn_like);
        peekAndPop.addHoldAndReleaseView(R.id.btn_message);
        peekAndPop.addHoldAndReleaseView(R.id.btn_share);
        peekAndPop.setOnHoldAndReleaseListener(new PeekAndPop.OnHoldAndReleaseListener() {
            @Override
            public void onHold(View view, int POSITION) {
                switch (view.getId()) {
                    case R.id.btn_like:
                        Toast.makeText(mContext, "Like", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btn_message:
                        Toast.makeText(mContext, "Message", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btn_share:
                        Toast.makeText(mContext, "Share", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onLeave(View view, int POSITION) {

            }

            @Override
            public void onRelease(View view, int POSITION) {

            }
        });

//                    peekAndPop.addHoldAndReleaseView(R.id.img_shortview);

        Picasso.with(mContext).load(bean.photoUrl).resize(500, 500).
                centerCrop().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }*/
}

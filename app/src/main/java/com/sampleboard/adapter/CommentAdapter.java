package com.sampleboard.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampleboard.R;
import com.sampleboard.bean.CommentBean;
import com.sampleboard.bean.CommentItemBean;
import com.sampleboard.databinding.ViewCommentBinding;
import com.sampleboard.databinding.ViewCommentMediaBinding;
import com.sampleboard.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * @author AnujSharma on 12/27/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    enum CommentType {
        TEXT,
        IMAGE,
    }

    public interface CommentInterface {

        void onProfileClick();

        void onLikeUnLikeClick();

    }

    public void updateList(List<CommentItemBean> comment_list) {
        this.response = comment_list;
        notifyDataSetChanged();
    }

    private Context mContext;
    private List<CommentItemBean> response;
    private CommentInterface listener;
    private int mDefaultBackgroundColor;

    public CommentAdapter(Context context, List<CommentItemBean> response, CommentInterface listerner) {
        this.mContext = context;
        this.response = response;
        this.listener = listerner;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        CommentItemBean obj = response.get(viewType);
        if (obj.getType().equalsIgnoreCase(CommentType.TEXT.toString())) {
            ViewCommentBinding commentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.view_comment, parent, false);
            holder = new CommentHolder(commentBinding);
        } else {
            //its an image
            ViewCommentMediaBinding commentMediaBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.view_comment_media, parent, false);
            holder = new CommentImageHolder(commentMediaBinding);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentHolder) {
            ((CommentHolder) holder).bindData(response.get(position));
        } else if (holder instanceof CommentImageHolder) {
            ((CommentImageHolder) holder).bindData(response.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return (response == null) ? 0 : response.size();
    }

    private class CommentHolder extends RecyclerView.ViewHolder {
        ViewCommentBinding commentBinding;

        private CommentHolder(ViewCommentBinding commentBinding) {
            super(commentBinding.getRoot());
            this.commentBinding = commentBinding;
        }

        void bindData(CommentItemBean obj) {
            commentBinding.tvMessage.setText(obj.getText());

            if (!TextUtils.isEmpty(obj.getUser_profile_pic())) {
                Picasso.with(mContext).load(obj.getUser_profile_pic()).resize(100, 100)
                        .centerCrop().into(commentBinding.userImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        commentBinding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        commentBinding.userImage.setImageResource(R.drawable.def_profile_img);
                        commentBinding.progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                commentBinding.userImage.setImageResource(R.drawable.def_profile_img);
                commentBinding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private class CommentImageHolder extends RecyclerView.ViewHolder {
        ViewCommentMediaBinding commentMediaBinding;

        private CommentImageHolder(ViewCommentMediaBinding commentMediaBinding) {
            super(commentMediaBinding.getRoot());
            this.commentMediaBinding = commentMediaBinding;
        }

        void bindData(CommentItemBean obj) {

            if (!TextUtils.isEmpty(obj.getUser_profile_pic())) {
                Picasso.with(mContext).load(obj.getUser_profile_pic()).resize(100, 100)
                        .centerCrop().into(commentMediaBinding.userImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        commentMediaBinding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        commentMediaBinding.userImage.setImageResource(R.drawable.def_profile_img);
                        commentMediaBinding.progressBar.setVisibility(View.GONE);
                    }
                });
            } else

            {
                commentMediaBinding.userImage.setImageResource(R.drawable.def_profile_img);
                commentMediaBinding.progressBar.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(obj.getMedia()))

            {
                //cancel any loading images on this view
                Picasso.with(mContext).cancelRequest(commentMediaBinding.imgAttachment);
                commentMediaBinding.imgAttachment.setImageBitmap(null);
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            /* Save the bitmap or do something with it here */
                        Palette.from(bitmap)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                        if (textSwatch == null) {
                                            return;
                                        }
                                        commentMediaBinding.rightView.setCardBackgroundColor(textSwatch.getRgb());
//                                            vh.mInfoContainer.setBackgroundColor(textSwatch.getRgb());
                                        commentMediaBinding.userName.setTextColor(textSwatch.getTitleTextColor());
                                        commentMediaBinding.tvDate.setTextColor(textSwatch.getTitleTextColor());
                                    }
                                });
                        if (bitmap != null)
                            commentMediaBinding.imgAttachment.setImageBitmap(bitmap);
                        commentMediaBinding.attachmentProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        commentMediaBinding.imgAttachment.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_default_image));
                        commentMediaBinding.attachmentProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                // set the tag to the view
                commentMediaBinding.imgAttachment.setTag(target);
                Picasso.with(mContext).load(obj.getMedia())
                        .resize(300, 300).centerCrop()
                        .into(target);
            }
        }
    }
}

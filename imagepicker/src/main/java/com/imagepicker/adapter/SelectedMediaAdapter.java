package com.imagepicker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.imagepicker.R;
import com.imagepicker.model.MediaItemBean;
import com.imagepicker.ui.selectedMedia.SelectedMediaPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * auther Anuj Sharma on 9/21/2017.
 */

public class SelectedMediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnTouchListener {
    private Context context;
    private List<MediaItemBean> mediaList;
    private SelectedMediaPresenter presenter;
    public int selectedItem = -1;

    int windowwidth;
    int windowheight;


    public SelectedMediaAdapter(Activity activity, List<MediaItemBean> mediaList, SelectedMediaPresenter listener) {
        this.context = context;
        this.mediaList = mediaList;
        this.presenter = listener;
        windowwidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        windowheight = activity.getWindowManager().getDefaultDisplay().getHeight();
    }


    public List<MediaItemBean> getList() {
        if (mediaList == null) mediaList = new ArrayList<>();
        return mediaList;
    }

    public void updateList(List<MediaItemBean> mediaList) {
        this.mediaList = mediaList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_selected_media, null);
        return new SelectedMediaHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SelectedMediaHolder) {
            SelectedMediaHolder vh = (SelectedMediaHolder) holder;
            MediaItemBean obj = mediaList.get(position);

//            vh.imageView.setImageURI(Uri.fromFile(new File(obj.getMediaPath())));
            Uri uri;
            if (!TextUtils.isEmpty(obj.getCroppedPath())) {
                uri = Uri.fromFile(new File(obj.getCroppedPath()));
            } else {
                uri = Uri.fromFile(new File(obj.getMediaPath()));
            }
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(150, 150))
                    .setProgressiveRenderingEnabled(true)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(vh.imageView.getController())
                    .setAutoPlayAnimations(true)
                    .build();
            vh.imageView.setController(controller);

            if (selectedItem == position) {
                vh.viewSelected.setVisibility(View.VISIBLE);
            } else {
                vh.viewSelected.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mediaList == null) ? 0 : mediaList.size();
    }


    private class SelectedMediaHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        private View viewSelected;

        private SelectedMediaHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.media_image);
            viewSelected = itemView.findViewById(R.id.selected_view);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (presenter != null) {
                        selectedItem = getLayoutPosition();
                        presenter.onMediaClick(mediaList.get(getLayoutPosition()), getLayoutPosition());
                    }
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    if (presenter != null)
                        presenter.onMediaLongClick(mediaList.get(getLayoutPosition()), getLayoutPosition(), imageView);
                    return false;
                }
            });
            imageView.setOnTouchListener(SelectedMediaAdapter.this);
        }
    }

    private float initialX = 0, initialY = 0;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float xCoOrdinate = 0, yCoOrdinate = 0;

        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                initialX = view.getX();
//                initialY = view.getY();
//                xCoOrdinate = view.getX() - event.getRawX();
//                yCoOrdinate = view.getY() - event.getRawY();
//                break;
//            case MotionEvent.ACTION_UP:
//                view.animate().x(event.getRawX() + initialX).y(event.getRawY() + initialY).setDuration(200).start();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                System.out.println("dragging");
//                view.animate().x(xCoOrdinate).y(yCoOrdinate).setDuration(200).start();
//                break;
            default:
                return false;

        }
    }

}
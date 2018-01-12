package com.sampleboard.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sampleboard.R;
import com.sampleboard.bean.LikedBean;
import com.sampleboard.bean.PostDetailBean;
import com.sampleboard.utils.Constants;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.activity.DetailActivityV2;
import com.sampleboard.view.fragment.profile.DownloadedFragment;
import com.sampleboard.view.activity.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Anuj Sharma on 4/5/2017.
 */

public class DownloadedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<LikedBean> list;
    Context context;
    DownloadedFragment fragment;
    public DownloadedAdapter(Context ctx,List<LikedBean> listing,DownloadedFragment fragment){
        this.context = ctx;
        this.list = listing;
        this.fragment = fragment;
    }
    public void updateData(List<LikedBean> downloadedList) {
        this.list = downloadedList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_downloaded,parent,false);
        DownloadedHolder vh = new DownloadedHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DownloadedHolder){
            DownloadedHolder vh = (DownloadedHolder)holder;
            Picasso.with(context).load("file://" + list.get(position).imageUrl).resize(200,200).centerCrop().into(vh.mDownloadedImg);
        }
    }

    @Override
    public int getItemCount() {
        return (list==null)?0:list.size();
    }



    private class DownloadedHolder extends RecyclerView.ViewHolder{
        ImageView mDownloadedImg;

        public DownloadedHolder(View itemView) {
            super(itemView);
            mDownloadedImg = (ImageView) itemView.findViewById(R.id.img_download);

            mDownloadedImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Move to Image
                    PostDetailBean detailBean = new PostDetailBean();
                    detailBean.photoName = list.get(getAdapterPosition()).imageName;
                    detailBean.photoUrl = list.get(getAdapterPosition()).imageUrl;
                    detailBean.likeCount = 514;
                    detailBean.commentCount = 356;
                    detailBean.isLiked = true;
                    detailBean.ownerName = "Anuj Sharma";

                    Intent profilePicIntent = new Intent(context,DetailActivityV2.class);
                    profilePicIntent.putExtra(Constants.DESTINATION,Constants.DETAIL_SCREEN);
                    profilePicIntent.putExtra(Constants.OBJ_DETAIL,detailBean);
                    if (Utils.getInstance().isEqualLollipop() && fragment!=null) {
                        Pair<View, String> p1 = Pair.create((View) mDownloadedImg, mDownloadedImg.getTransitionName());
                        ActivityOptions options =
                                ActivityOptions.makeSceneTransitionAnimation(fragment.getActivity(), p1);
                        context.startActivity(profilePicIntent, options.toBundle());
                    } else {
                        context.startActivity(profilePicIntent);
                    }
                }
            });
        }
    }
}

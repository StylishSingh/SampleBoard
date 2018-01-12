package com.sampleboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sampleboard.R;
import com.sampleboard.bean.TrendingCatBean;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Anuj Sharma on 4/19/2017.
 */

public class TrendingCatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    List<TrendingCatBean> list;

    public TrendingCatAdapter(Context ctx, List<TrendingCatBean> list){
        this.context = ctx;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_trending_category,parent,false);
        TrendingHolder holder = new TrendingHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TrendingHolder){
            TrendingHolder vh = (TrendingHolder)holder;
            Picasso.with(context).load(list.get(position).categoryImage).resize(100,100).centerCrop().into(vh.trendingImg);
        }
    }

    @Override
    public int getItemCount() {
        return (list==null)?0:list.size();
    }

    private class TrendingHolder extends RecyclerView.ViewHolder{
        CircleImageView trendingImg;
        private RelativeLayout parentLayout;
        public TrendingHolder(View itemView) {
            super(itemView);
            trendingImg = (CircleImageView)itemView.findViewById(R.id.trend_img);
        }
    }
}

package com.sampleboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.sampleboard.R;
import com.sampleboard.bean.MusicBean;
import com.sampleboard.view.musicModule.MusicListFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by Anuj Sharma on 3/20/2017.
 */

public class MusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_PROG = 0;
    private final int VIEW_ITEM = 1;
    private List<MusicBean> mResponse;

    MusicListFragment fragment;
    Context context;

    public MusicListAdapter(MusicListFragment fragment, List<MusicBean> o) {
        this.mResponse = o;
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    public void updateAdapter(List<MusicBean> response) {
        if (response != null) {
            this.mResponse = response;
            notifyDataSetChanged();
        } else {
            this.mResponse = new ArrayList<>();
            notifyDataSetChanged();
        }

    }

    public void searchMusic(List<MusicBean> response) {
        this.mResponse = response;
        notifyDataSetChanged();
    }

    public void removeProgress() {
        if (mResponse != null) {
            mResponse.removeAll(Collections.singleton(null));
            int position = mResponse.size() - 1;
            notifyItemRangeChanged(0, position);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_songs_list, parent, false);
            vh = new MusicListViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_progress_item, parent, false);
            vh = new ProgressViewHolder(view);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MusicListViewHolder) {
            MusicListViewHolder vh = (MusicListViewHolder) holder;
//            Picasso.with(context).load(mResponse.get(POSITION).getCover_image()).resize(100,100).into(vh.musicIcon);
            vh.musicTitle.setText(String.valueOf(mResponse.get(position).getSong()));
            vh.musicArtist.setText(String.valueOf(mResponse.get(position).getArtists()));

            OkHttpClient client = new OkHttpClient();
            Picasso picasso = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(client))
                    .build();
//                    Picasso.Builder builder = new Picasso.Builder(context);
            if (!TextUtils.isEmpty(mResponse.get(position).getCover_image()))
                picasso.load(mResponse.get(position).getCover_image()).resize(100, 100).into(vh.musicIcon);
//            builder.listener(new Picasso.Listener() {
//                @Override
//                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//
//                }
//            });
//            builder.downloader(new OkHttpDownloader(context));
//            builder.build().load(mResponse.get(POSITION).getCover_image()).into(vh.musicIcon);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mResponse != null && mResponse.get(position) != null) {
            return VIEW_ITEM;
        }
        return VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return (mResponse == null) ? 0 : mResponse.size();
    }

    /**
     * Search Data according to query
     */
    public void searchData(String query) {

    }

    private class MusicListViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout parentLayout;
        private ImageView musicIcon, downloadBtn;
        private TextView musicTitle;
        private TextView musicArtist;
        private ProgressBar progressBar;

        public MusicListViewHolder(View itemView) {
            super(itemView);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parent_song);
            musicIcon = (ImageView) itemView.findViewById(R.id.music_icon);
            musicTitle = (TextView) itemView.findViewById(R.id.music_title);
            musicArtist = (TextView) itemView.findViewById(R.id.music_artist);
            downloadBtn = (ImageView) itemView.findViewById(R.id.btn_download);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.onSongClick(mResponse, getAdapterPosition());
                }
            });

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment != null) {
                        fragment.onDownloadClick(mResponse.get(getAdapterPosition()), progressBar);
                    }
                }
            });
        }

    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}

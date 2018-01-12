package com.sampleboard.view.fragment.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sampleboard.R;
import com.sampleboard.adapter.DownloadedAdapter;
import com.sampleboard.bean.LikedBean;
import com.sampleboard.view.BaseFragment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anuj Sharma on 4/5/2017.
 */

public class DownloadedFragment extends BaseFragment {

    private View rootView;
    private RecyclerView mRecyclerView;
    private DownloadedAdapter mAdapter;
    private List<LikedBean> sdCardImagesList;
    private RelativeLayout mEmptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState!=null){
            //retrieve data first here
        }
        initViews();
    }

    private void initViews() {
        mEmptyView = (RelativeLayout)rootView.findViewById(R.id.included_emptyview);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.completed_recycler);
        StaggeredGridLayoutManager sm = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sm);

        getFromSdcard(); // get images from particular folder
    }

    public void getFromSdcard()
    {
        File folder = new File(android.os.Environment.getExternalStorageDirectory(),getString(R.string.app_name));

        File[] allFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
            }

        });

        if(allFiles==null){
            //show empty view
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.INVISIBLE);
            System.out.println("Total Files-> " + allFiles.length);
            LikedBean obj;
            if(sdCardImagesList==null)sdCardImagesList = new ArrayList<>();
            for (int i = 0; i < allFiles.length; i++) {
                obj = new LikedBean();
                if (allFiles[i].isFile()) {
                    System.out.println("File " + allFiles[i].getName());
                    System.out.println("File " + allFiles[i].getAbsolutePath());
                    obj.imageName = allFiles[i].getName();
                    obj.imageUrl = allFiles[i].getAbsolutePath();
                    sdCardImagesList.add(obj);
                }
            }
            mAdapter = new DownloadedAdapter(getActivity(), sdCardImagesList, DownloadedFragment.this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}

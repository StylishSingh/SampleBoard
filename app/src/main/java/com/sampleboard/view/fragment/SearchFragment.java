package com.sampleboard.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampleboard.R;
import com.sampleboard.view.BaseFragment;
import com.sampleboard.view.activity.DashBoardActivity;
import com.sampleboard.view.activity.HolderActivity;

/**
 * Created by Anuj Sharma on 4/3/2017.
 */

public class SearchFragment extends BaseFragment {
    View rootView;

    @Override
    public void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        if(getActivity() instanceof HolderActivity){
            ((HolderActivity)getActivity()).setSupportActionBar(toolbar);
            ((HolderActivity)getActivity()).getSupportActionBar().setTitle("");
            ((HolderActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(view1->{
                ((HolderActivity)getActivity()).onBackPressed();
            });
        }
    }
}

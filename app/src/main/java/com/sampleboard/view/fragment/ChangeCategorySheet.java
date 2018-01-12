package com.sampleboard.view.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sampleboard.R;
import com.sampleboard.adapter.TrendingCatAdapter;
import com.sampleboard.bean.TrendingCatBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anuj Sharma on 4/18/2017.
 */

public class ChangeCategorySheet extends BottomSheetDialogFragment {

    private View rootView;
    private BottomSheetDialog mBottomSheetDialog;
    private RecyclerView categoryRecycler, trendingRecycler;
    private List<TrendingCatBean> trendingList;
    private TrendingCatAdapter trendAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_category_sheet,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        categoryRecycler = (RecyclerView)rootView.findViewById(R.id.category_recycler);
        trendingRecycler = (RecyclerView)rootView.findViewById(R.id.trending_recycler);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecycler.setLayoutManager(lm);

        LinearLayoutManager lm1 = new LinearLayoutManager(getActivity());
        lm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        trendingRecycler.setLayoutManager(lm1);

        setDummyTrending();
    }

    private void setDummyTrending() {
        //put dummy trending category
        if(trendingList==null)trendingList = new ArrayList<>();
        TrendingCatBean obj = new TrendingCatBean();
        obj.categoryImage = "http://freenology.com/images/71442946288pineapple.jpg";

        trendingList.add(obj);

        obj = new TrendingCatBean();
        obj.categoryImage = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Table_grapes_on_white.jpg/220px-Table_grapes_on_white.jpg";

        trendingList.add(obj);

        obj = new TrendingCatBean();
        obj.categoryImage = "https://cdn.authoritynutrition.com/wp-content/uploads/2015/02/three-blueberries.jpg";

        trendingList.add(obj);

        obj = new TrendingCatBean();
        obj.categoryImage = "https://cdn.authoritynutrition.com/wp-content/uploads/2015/02/three-blueberries.jpg";

        trendingList.add(obj);

        obj = new TrendingCatBean();
        obj.categoryImage = "http://www.hoax-slayer.com/images/hiv-infected-blood-oranges-3.jpg";

        trendingList.add(obj);

        obj = new TrendingCatBean();
        obj.categoryImage = "http://theguamguide.com/wp-content/uploads/african-mango-review.jpg";

        trendingList.add(obj);

        trendAdapter = new TrendingCatAdapter(getActivity(),trendingList);
        trendingRecycler.setAdapter(trendAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mBottomSheetDialog = (BottomSheetDialog) dialog;
                View bottomSheetInternal = mBottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).
                        setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }
}

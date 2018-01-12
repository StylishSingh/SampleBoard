package com.sampleboard.view.fragment.dashboard;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.sampleboard.GlobalActivity;
import com.sampleboard.R;
import com.sampleboard.adapter.HomeListAdapter;
import com.sampleboard.bean.MediaItem;
import com.sampleboard.bean.MediaModel;
import com.sampleboard.bean.PhotosBean;
import com.sampleboard.bean.PostDetailBean;
import com.sampleboard.databinding.FragmentHomeBinding;
import com.sampleboard.interfaces.MediaListInterface;
import com.sampleboard.utils.Constants;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseFragment;
import com.sampleboard.view.activity.DashBoardActivity;
import com.sampleboard.view.activity.DetailActivityV2;
import com.sampleboard.view.activity.HolderActivity;
import com.sampleboard.viewmodel.HomeFragmentViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import io.reactivex.Flowable;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Anuj Sharma on 2/28/2017.
 */

public class HomeFragment extends BaseFragment implements MediaListInterface {
    private FragmentHomeBinding binding;
    private HomeFragmentViewModel viewModel;
    private HomeListAdapter mAdapter;
    private List<PhotosBean> list;

    @Override
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_search_icon).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_icon:
                Intent intent = new Intent(getActivity(), HolderActivity.class);
                intent.putExtra(Constants.DESTINATION, Constants.SEARCH_SCREEN);
                getActivity().startActivity(intent);
                break;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        viewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel.class);

//        List<String> list = new ArrayList<>();
//        list.add("xang");
//        list.add("rital");
//        list.add("pital");
//        list.add("gital");
//        list.add("anuj");
//        list.forEach(new Consumer<String>() {
//            @Override
//            public void accept(String s) {
//                if (s.equals("anuj"))
//                    System.out.println("anuj exist in this code");
//            }
//        });


//        Observable.fromIterable(list)
//                .sorted(new Comparator<String>() {
//                    @Override
//                    public int compare(String o1, String o2) {
//                        return o1.compareTo(o2);
//                    }
//                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new DisposableObserver<String>() {
//                    @Override
//                    public void onNext(String s) {
//                        System.out.println("List Item-> "+ s);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DashBoardActivity) getActivity()).setSupportActionBar(binding.includeToolbar.toolbar);
        ((DashBoardActivity) getActivity()).getSupportActionBar().setTitle("");
        binding.includeToolbar.toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_launcher));

        StaggeredGridLayoutManager sm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerItems.setLayoutManager(sm);
        mAdapter = new HomeListAdapter(getActivity(), null, this);
        binding.recyclerItems.setAdapter(mAdapter);
        //Static Data coming from Json stored in assets folder
        try {
            Gson gson = new Gson();
            MediaModel mediaModel = gson.fromJson(getStringFromLocalJson("media_list.json", getActivity()), MediaModel.class);
            mAdapter.updateList(mediaModel.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(MediaItem obj, ImageView imageView, int position) {
        PostDetailBean detailBean = new PostDetailBean();
        detailBean.photoName = obj.getTitle();
        detailBean.photoUrl = obj.getMedia();
        detailBean.likeCount = 514;
        detailBean.commentCount = 356;
        detailBean.isLiked = true;
        detailBean.ownerName = "Anuj Sharma";
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.OBJ_DETAIL, detailBean);

        //Move to Detail Activity
        Intent intent = new Intent(getActivity(), DetailActivityV2.class);
        intent.putExtra(Constants.DESTINATION, Constants.DETAIL_SCREEN);
        intent.putExtra(Constants.OBJ_DETAIL, detailBean);
        intent.putExtra(Constants.POSITION, position);
        if (imageView != null && imageView.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                GlobalActivity.photoCache.put(position, bitmap);
            }
        }
        if (Utils.getInstance().isEqualLollipop()) {
            Pair<View, String> p1 = Pair.create(imageView, getString(R.string.transition_image));
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(), p1);
            getActivity().startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

}

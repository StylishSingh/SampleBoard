package com.sampleboard.view.musicModule;

import android.app.SearchManager;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sampleboard.R;
import com.sampleboard.bean.MusicBean;
import com.sampleboard.databinding.FragmentMusicListBinding;
import com.sampleboard.permission.PermissionsAndroid;
import com.sampleboard.presenters.MusicPlayerPresenter;
import com.sampleboard.presenters.MusicListPresenter;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseFragment;

import java.util.List;

/**
 * Created by Anuj Sharma on 3/20/2017.
 */

public class MusicListFragment extends BaseFragment {
    private FragmentMusicListBinding binder;
    private MusicListPresenter presenter;
    private static MusicListFragment musicListFragment;

    public static MusicListFragment getInstance(){
        return musicListFragment;
    }

    public MusicListPresenter getMusicListPresenter(){
        return presenter;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        if(isVisible() && presenter!=null)presenter.unSubscribeMusicDownload();
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        if(isVisible() && presenter!=null)presenter.subscribeMusicDownload();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(getActivity().SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //search query listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //text has changed apply filter
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //perform the final search
                if (presenter != null) {
                    presenter.searchSongs(newText);
                }
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_music_list, container, false);
        presenter = new MusicListPresenter(binder, MusicListFragment.this);
        ((MusicPlayerActivity) getActivity()).setSupportActionBar(binder.toolbar);
        musicListFragment = MusicListFragment.this;
        return binder.getRoot();
    }

    /**
     * Music info which need to download
     *
     * @param musicBean
     */
    private MusicBean musicBean;
    protected ProgressBar progressBar;
    public void onDownloadClick(MusicBean musicBean, final ProgressBar progressBar) {
        this.musicBean = musicBean;
        this.progressBar = progressBar;
        if(PermissionsAndroid.getInstance().checkWriteExternalStoragePermission(getActivity())){
            if(presenter!=null){
                presenter.downloadFile(musicBean,progressBar);
            }
        }else{
            //ask for permission
            PermissionsAndroid.getInstance().requestForWriteExternalStoragePermission(getActivity());
        }

    }

    /**
     * Music data need to play and manage bottomsheet here
     * @param mResponse
     * @param adapterPosition
     */
    public void onSongClick(List<MusicBean> mResponse, int adapterPosition) {
        //play music
        MusicPlayerPresenter dashbpardPresenter = MusicPlayerActivity.getInstance().getDashboardPresenter();
        if (dashbpardPresenter != null) {
            MusicBean obj = mResponse.get(adapterPosition);
            dashbpardPresenter.playMusic(obj, adapterPosition);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PermissionsAndroid.WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(presenter!=null){
                        presenter.downloadFile(musicBean,progressBar);
                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Utils.getInstance().showToast("External storage permission required to do this operation.");
                }
                break;
        }
    }


}

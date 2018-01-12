package com.sampleboard.view.fragment.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sampleboard.R;
import com.sampleboard.adapter.CommentAdapter;
import com.sampleboard.bean.CommentBean;
import com.sampleboard.bean.MediaModel;
import com.sampleboard.databinding.FragmentCommentBinding;
import com.sampleboard.utils.Utils;
import com.sampleboard.view.BaseFragment;
import com.sampleboard.view.activity.DetailActivityV2;
import com.sampleboard.viewmodel.CommentViewModel;

/**
 * Created by AnujSharma on 12/27/2017.
 */

public class CommentFragment extends BaseFragment implements CommentAdapter.CommentInterface {
    private CommentViewModel viewModel;
    private FragmentCommentBinding binding;
    private CommentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);
        viewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        if (Utils.isNetworkAvailable(getActivity())) {
            viewModel.getComments(1);
        } else {
            Utils.getInstance().showSnakBar(binding.getRoot(), getString(R.string.error_internet));
        }
    }

    private void initViews() {
        if (getActivity() instanceof DetailActivityV2) {
            ((DetailActivityV2) getActivity()).setSupportActionBar(binding.includeToolbar.toolbar);
            ((DetailActivityV2) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((DetailActivityV2) getActivity()).getSupportActionBar().setTitle("Comments");
            binding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_close);
            binding.includeToolbar.toolbar.setNavigationOnClickListener(v -> ((DetailActivityV2) getActivity()).oneStepBack());
        }
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerComment.setLayoutManager(lm);
        adapter = new CommentAdapter(getActivity(), null, this);
        binding.recyclerComment.setAdapter(adapter);

        try {
            Gson gson = new Gson();
            CommentBean commentBean = gson.fromJson(getStringFromLocalJson("comment_list.json", getActivity()), CommentBean.class);
            if (commentBean.getData().getComment_list().size() > 0) {
                adapter.updateList(commentBean.getData().getComment_list());
                binding.emptyLayout.setVisibility(View.GONE);
            } else {
                binding.emptyLayout.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProfileClick() {

    }

    @Override
    public void onLikeUnLikeClick() {

    }
}

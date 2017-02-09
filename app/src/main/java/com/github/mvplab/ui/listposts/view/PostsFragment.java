package com.github.mvplab.ui.listposts.view;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mvplab.R;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.listposts.adapter.PostsAdapter;
import com.github.mvplab.ui.listposts.presenter.PostsPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PostsFragment extends Fragment implements PostsView, PostInteractors {
    private static final String TAG = "PostsFragment";

    @BindView(R.id.rvPosts)
    RecyclerView rvPosts;

    private Unbinder unbinder;

    private PostsAdapter postsAdapter;
    private PostsPresenter presenter;

    private ProgressDialog progressDialog;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment getInstance() {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        unbinder = ButterKnife.bind(this, view);
        Log.i("***", "onCreateView: ");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("***", "onViewCreated: ");
        init();
        getPresenter().loadPosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeActionBar();
    }

    private void changeActionBar() {
        try {
            getActivity().setTitle(R.string.app_name);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }

    private void init() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);
        postsAdapter = new PostsAdapter(getContext(), this, new ArrayList<PostModel>());
        rvPosts.setAdapter(postsAdapter);
    }


    @Override
    public void onPostClick(int postId) {
        getPresenter().onPostClick(postId);
    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPosts(@NonNull List<PostModel> models) {
        postsAdapter.update(models);
    }

    @Override
    public void bindPresenter(PostsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public PostsPresenter getPresenter() {
        return presenter;
    }

}

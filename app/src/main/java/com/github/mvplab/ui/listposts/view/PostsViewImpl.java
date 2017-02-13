package com.github.mvplab.ui.listposts.view;


import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.OnPostSelectedListener;
import com.github.mvplab.ui.navigation.Viewed;
import com.github.mvplab.ui.listposts.adapter.PostsAdapter;
import com.github.mvplab.ui.listposts.presenter.PostsPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PostsViewImpl implements PostsView, PostInteractors, Viewed {
    private static final String TAG = "PostsViewImpl";

    @BindView(R.id.rvPosts)
    RecyclerView rvPosts;

    private Unbinder unbinder;

    private PostsAdapter postsAdapter;
    private PostsPresenter presenter;

    private ProgressDialog progressDialog;
    @NonNull
    private final AppCompatActivity parent;
    @NonNull
    private View view;

    public PostsViewImpl(@Nullable OnPostSelectedListener onPostSelectedListener, @NonNull AppCompatActivity parent) {
        this.parent = parent;
        LabApp.getApplication().inject(this);
        getPresenter().setOnPostSelectedListener(onPostSelectedListener);
    }

    private void init() {
        postsAdapter = new PostsAdapter(parent, this, new ArrayList<PostModel>());
        rvPosts.setAdapter(postsAdapter);
    }

    private void changeActionBar() {
        parent.setTitle(R.string.app_name);
        if (parent.getSupportActionBar() != null)
            parent.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    @NonNull
    public View getView() {
        return view;
    }

    @Override
    public void onBindView() {
        this.view = LayoutInflater.from(parent).inflate(R.layout.view_posts, null);
        unbinder = ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(parent);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(parent);
        rvPosts.setLayoutManager(layoutManager);
        getPresenter().bindView(this);
        init();
        changeActionBar();
        getPresenter().loadPosts();
    }

    @Override
    public void onUnbindView() {
        Log.i(TAG, "onUnbindView: ");
        getPresenter().unbindView();
    }

    @Override
    public void onDestroy() {
        try {
            if (unbinder != null)
                unbinder.unbind();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        view = null;
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
        Toast.makeText(parent, errorMessage, Toast.LENGTH_SHORT).show();
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

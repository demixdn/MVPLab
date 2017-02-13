package com.github.mvplab.ui.listposts.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.OnPostSelectedListener;
import com.github.mvplab.ui.listposts.adapter.PostsAdapter;
import com.github.mvplab.ui.listposts.presenter.PostsPresenter;
import com.github.mvplab.ui.post.view.PostDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements OnPostSelectedListener, PostsView, PostInteractors {

    private static final String ACTIVITY_TAG = "MainActivity";

    @BindView(R.id.rvPosts)
    RecyclerView rvPosts;

    private Unbinder unbinder;

    private PostsAdapter postsAdapter;
    private PostsPresenter presenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(ACTIVITY_TAG, "onCreate: ");
        initUI();
        init();
        initPresenter();
    }

    private void initUI() {
        unbinder = ButterKnife.bind(this);
        setTitle(R.string.app_name);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvPosts.setLayoutManager(layoutManager);
    }

    private void init() {
        postsAdapter = new PostsAdapter(this, this, new ArrayList<PostModel>());
        rvPosts.setAdapter(postsAdapter);
    }

    private void initPresenter() {
        LabApp.getApplication().inject(this);
        getPresenter().setOnPostSelectedListener(this);
        getPresenter().loadPosts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_TAG, "onResume: ");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(ACTIVITY_TAG, "onPostResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroy();
        Log.i(ACTIVITY_TAG, "onDestroy: ");
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
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPostSelected(int postId) {
        showPostDetail(postId);
    }

    private void showPostDetail(int postId) {
        PostDetailActivity.navigate(this, postId);
    }
}

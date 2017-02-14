package com.github.mvplab.ui.listposts.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.OnPostSelectedListener;
import com.github.mvplab.ui.base.BaseController;
import com.github.mvplab.ui.listposts.adapter.PostsAdapter;
import com.github.mvplab.ui.listposts.presenter.PostsPresenter;
import com.github.mvplab.ui.post.view.PostViewController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 14.02.2017
 * Time: 13:59
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostsViewController extends BaseController implements PostsView, PostInteractors, OnPostSelectedListener {

    @BindView(R.id.rvPosts)
    RecyclerView rvPosts;

    private PostsAdapter postsAdapter;
    private PostsPresenter presenter;

    private ProgressDialog progressDialog;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.view_posts, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        initUI(view);
        init(view.getContext());
    }

    private void initUI(@NonNull View view) {
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rvPosts.setLayoutManager(layoutManager);
    }

    @Override
    protected String getTitle() {
        return getApplicationContext() != null ? getApplicationContext().getString(R.string.app_name) : "";
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        LabApp.getApplication().inject(this);
        getPresenter().setOnPostSelectedListener(this);
        getPresenter().loadPosts();
    }

    @Override
    protected void onDetach(@NonNull View view) {
        getPresenter().unbindView();
        super.onDetach(view);
    }

    private void init(Context context) {
        postsAdapter = new PostsAdapter(context, this, new ArrayList<PostModel>());
        rvPosts.setAdapter(postsAdapter);
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
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostClick(int postId) {
        getPresenter().onPostClick(postId);
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
    public void showPosts(@NonNull List<PostModel> models) {
        postsAdapter.update(models);
    }

    @Override
    public void onPostSelected(int postId) {
        getRouter().pushController(RouterTransaction.with(new PostViewController(postId))
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }
}

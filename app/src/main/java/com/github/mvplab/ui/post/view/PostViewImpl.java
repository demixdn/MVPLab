package com.github.mvplab.ui.post.view;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.navigation.Viewed;
import com.github.mvplab.ui.post.adapter.CommentItemDecoration;
import com.github.mvplab.ui.post.adapter.PostCommentsAdapter;
import com.github.mvplab.ui.post.presenter.PostPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PostViewImpl implements PostView, Viewed {
    private static final String POST_TAG = "PostViewImpl";

    @BindView(R.id.tvPostDetailTitle)
    TextView tvPostTitle;
    @BindView(R.id.tvPostDetailAuthor)
    TextView tvPostAuthor;
    @BindView(R.id.tvPostDetailText)
    TextView tvPostText;
    @BindView(R.id.tvPostDetailCommentsTitle)
    TextView tvCommentsTitle;
    @BindView(R.id.rvPostDetailComments)
    RecyclerView rvComments;
    @BindView(R.id.progress)
    ProgressBar progress;

    private Unbinder unbinder;

    private PostPresenter presenter;

    @NonNull
    private final AppCompatActivity parentActivity;

    @NonNull
    private View view;

    private int postId;

    private PostViewImpl(@NonNull AppCompatActivity parent, int postId) {
        this.parentActivity = parent;
        this.postId = postId;
        LabApp.getApplication().inject(this);
    }

    public static PostViewImpl getInstance(@NonNull AppCompatActivity parent, int postId) {
        return new PostViewImpl(parent, postId);
    }

    @Override
    @NonNull
    public View getView() {
        return view;
    }

    @Override
    public void onBindView() {
        this.view = LayoutInflater.from(parentActivity).inflate(R.layout.view_post, null);
        unbinder = ButterKnife.bind(this, view);
        rvComments.addItemDecoration(new CommentItemDecoration(parentActivity));
        rvComments.setLayoutManager(new LinearLayoutManager(parentActivity));
        getPresenter().bindView(this);
        initUI();
        initModel();
    }

    private void initUI() {
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initModel() {
        getPresenter().setPostId(postId);
        getPresenter().loadPost();
    }

    @Override
    public void onUnbindView() {
        Log.i(POST_TAG, "onUnbindView: ");
        getPresenter().unbindView();
    }

    @Override
    public void onDestroy() {
        if (unbinder != null)
            unbinder.unbind();
        view = null;
    }

    private void showAll(@NonNull PostModel model) {
        parentActivity.setTitle(model.getTitle());
        tvPostTitle.setText(model.getTitle());
        tvPostText.setText(model.getBody());

        tvCommentsTitle.setText(parentActivity.getString(R.string.comments_title, model.getComments() != null ? model.getComments().size() : 0));

        if (model.getAuthorFullName() != null) {
            tvPostAuthor.setText(parentActivity.getString(R.string.by_author, model.getAuthorFullName()));
        }
        if (model.getComments() != null && !model.getComments().isEmpty()) {
            rvComments.setAdapter(new PostCommentsAdapter(parentActivity, model.getComments()));
        } else {
            tvCommentsTitle.setText(parentActivity.getString(R.string.comments_title, 0));
        }
    }

    @Override
    public void showPostModel(@NonNull PostModel postModel) {
        showAll(postModel);
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(parentActivity, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bindPresenter(PostPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public PostPresenter getPresenter() {
        return presenter;
    }
}

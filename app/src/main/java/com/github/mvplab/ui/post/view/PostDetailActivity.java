package com.github.mvplab.ui.post.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.post.adapter.CommentItemDecoration;
import com.github.mvplab.ui.post.adapter.PostCommentsAdapter;
import com.github.mvplab.ui.post.presenter.PostPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PostDetailActivity extends AppCompatActivity implements PostView {

    private static final String PARAM_POST_ID = "extra_post_id";
    private static final String TAG = "PostDetailActivity";

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

    public static void navigate(Activity activity, int postId) {
        Intent postDetailIntent = new Intent(activity, PostDetailActivity.class);
        postDetailIntent.putExtra(PARAM_POST_ID, postId);
        activity.startActivity(postDetailIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post);
        initUI();
        LabApp.getApplication().inject(this);
        initModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(TAG, "onPostResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        unbinder = ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvComments.addItemDecoration(new CommentItemDecoration(this));
        rvComments.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initModel() {
        if (getIntent() != null) {
            getPresenter().setPostId(getIntent().getIntExtra(PARAM_POST_ID, 0));
            getPresenter().loadPost();
        } else {
            showError("Post id not set");
        }
    }

    private void showAll(@NonNull PostModel model) {
        setTitle(model.getTitle());
        tvPostTitle.setText(model.getTitle());
        tvPostText.setText(model.getBody());

        tvCommentsTitle.setText(getString(R.string.comments_title, model.getComments() != null ? model.getComments().size() : 0));

        if (model.getAuthorFullName() != null) {
            tvPostAuthor.setText(getString(R.string.by_author, model.getAuthorFullName()));
        }
        if (model.getComments() != null && !model.getComments().isEmpty()) {
            rvComments.setAdapter(new PostCommentsAdapter(this, model.getComments()));
        } else {
            tvCommentsTitle.setText(getString(R.string.comments_title, 0));
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
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
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

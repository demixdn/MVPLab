package com.github.mvplab.ui.post.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mvplab.BundleBuilder;
import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.base.BaseController;
import com.github.mvplab.ui.post.adapter.CommentItemDecoration;
import com.github.mvplab.ui.post.adapter.PostCommentsAdapter;
import com.github.mvplab.ui.post.presenter.PostPresenter;

import butterknife.BindView;

public class PostViewController extends BaseController implements PostView {

    private static final String POST_TAG = "PostViewImpl";
    public static final String EXTRA_POST_ID = "extra_post_id";

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

    private PostPresenter presenter;

    private int postId;

    public PostViewController(int postId) {
        this(new BundleBuilder(new Bundle()).putInt(EXTRA_POST_ID, postId).build());
    }

    public PostViewController(Bundle args) {
        super(args);
        this.postId = args.getInt(EXTRA_POST_ID);
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
        if (getActivity() != null) {
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void bindPresenter(PostPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public PostPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void showPostModel(@NonNull PostModel postModel) {
        showAll(postModel);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.view_post, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        LabApp.getApplication().inject(this);
        initUI();
        initModel();
    }

    @Override
    protected void onDetach(@NonNull View view) {
        getPresenter().unbindView();
        super.onDetach(view);
    }

    private void initUI() {
//        if (getActivity().getSupportActionBar() != null) {
//            getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
        rvComments.addItemDecoration(new CommentItemDecoration(getActivity()));
        rvComments.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void initModel() {
        getPresenter().setPostId(postId);
        getPresenter().loadPost();
    }

    private void showAll(@NonNull PostModel model) {
        if (getActivity() != null) {
            getActionBar().setTitle(model.getTitle());
            tvPostTitle.setText(model.getTitle());
            tvPostText.setText(model.getBody());

            tvCommentsTitle.setText(getActivity().getString(R.string.comments_title, model.getComments() != null ? model.getComments().size() : 0));

            if (model.getAuthorFullName() != null) {
                tvPostAuthor.setText(getActivity().getString(R.string.by_author, model.getAuthorFullName()));
            }
            if (model.getComments() != null && !model.getComments().isEmpty()) {
                rvComments.setAdapter(new PostCommentsAdapter(getActivity(), model.getComments()));
            } else {
                tvCommentsTitle.setText(getActivity().getString(R.string.comments_title, 0));
            }
        }
    }
}

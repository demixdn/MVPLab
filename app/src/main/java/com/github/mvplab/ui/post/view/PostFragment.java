package com.github.mvplab.ui.post.view;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment implements PostView {
    private static final String PARAM_POST_ID = "extra_post_id";
    private static final String POST_TAG = "PostFragment";

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

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment getInstance(int postId) {
        PostFragment postFragment = new PostFragment();
        Bundle args = new Bundle(1);
        args.putInt(PARAM_POST_ID, postId);
        postFragment.setArguments(args);
        return postFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LabApp.getApplication().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        initModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(POST_TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(POST_TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(POST_TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(POST_TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
        Log.i(POST_TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(POST_TAG, "onDestroy: ");
    }

    private void initModel() {
        if(getArguments()!=null) {
            getPresenter().setPostId(getArguments().getInt(PARAM_POST_ID));
            getPresenter().loadPost();
        }else {
            showError("Post id not set");
        }
    }

    private void initUI() {
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        rvComments.addItemDecoration(new CommentItemDecoration(getContext()));
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showAll(@NonNull PostModel model) {
        getActivity().setTitle(model.getTitle());
        tvPostTitle.setText(model.getTitle());
        tvPostText.setText(model.getBody());

        tvCommentsTitle.setText(getString(R.string.comments_title, model.getComments() != null ? model.getComments().size() : 0));

        if (model.getAuthorFullName() != null) {
            tvPostAuthor.setText(getString(R.string.by_author, model.getAuthorFullName()));
        }
        if (model.getComments() != null && !model.getComments().isEmpty()) {
            rvComments.setAdapter(new PostCommentsAdapter(getContext(), model.getComments()));
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
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
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

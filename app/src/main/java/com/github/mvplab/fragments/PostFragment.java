package com.github.mvplab.fragments;


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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.adapters.PostCommentsAdapter;
import com.github.mvplab.data.callback.PostCallback;
import com.github.mvplab.decoration.CommentItemDecoration;
import com.github.mvplab.models.PostModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {
    private static final String PARAM_POST_ID = "extra_post_id";

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

    Unbinder unbinder;

    private int postId;


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
        if (getArguments() != null) {
            postId = getArguments().getInt(PARAM_POST_ID);
        }
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

    private void initUI() {
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    private void initModel() {
        loadPost(postId);
    }

    private void loadPost(int postId) {
        LabApp.getPostsRepository().getPost(postId, new PostCallback<PostModel>() {
            @Override
            public void onEmit(PostModel data) {
                if (data != null)
                    showAll(data);
            }

            @Override
            public void onCompleted() {
                Log.i("***", "onCompleted: PostCallback<PostModel>");
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(getContext(), "getPost error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAll(@NonNull PostModel model) {
        getActivity().setTitle(model.getTitle());
        tvPostTitle.setText(model.getTitle());
        tvPostText.setText(model.getBody());

        rvComments.addItemDecoration(new CommentItemDecoration(getContext()));
        tvCommentsTitle.setText(getString(R.string.comments_title, model.getComments() != null ? model.getComments().size() : 0));
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        if (model.getAuthorFullName() != null) {
            tvPostAuthor.setText(getString(R.string.by_author, model.getAuthorFullName()));
        }
        if (model.getComments() != null && !model.getComments().isEmpty()) {
            rvComments.setAdapter(new PostCommentsAdapter(getContext(), model.getComments()));
        } else {
            tvCommentsTitle.setText(getString(R.string.comments_title, 0));
        }
    }
}

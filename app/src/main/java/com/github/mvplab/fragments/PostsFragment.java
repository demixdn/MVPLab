package com.github.mvplab.fragments;


import android.os.Bundle;
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

import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.adapters.PostInteractors;
import com.github.mvplab.adapters.PostsAdapter;
import com.github.mvplab.data.callback.PostCallback;
import com.github.mvplab.models.PostModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PostsFragment extends Fragment implements PostInteractors {
    private static final String TAG = "PostsFragment";

    @BindView(R.id.rvPosts)
    RecyclerView rvPosts;

    private Unbinder unbinder;

    private OnPostSelectedListener onPostSelectedListener;
    private PostsAdapter postsAdapter;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment getInstance() {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnPostSelectedListener(OnPostSelectedListener onPostSelectedListener) {
        this.onPostSelectedListener = onPostSelectedListener;
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
        initUI();
        initModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeActionBar();
    }

    private void changeActionBar() {
        try {
            getActivity().setTitle(R.string.app_name);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

    private void initUI() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);
    }

    private void initModel() {
        postsAdapter = new PostsAdapter(getContext(), this, new ArrayList<PostModel>());
        rvPosts.setAdapter(postsAdapter);
        LabApp.getPostsRepository().getPosts(new PostCallback<List<PostModel>>() {
            @Override
            public void onEmit(List<PostModel> data) {
                postsAdapter.update(data);
            }

            @Override
            public void onCompleted() {
                Log.i("***", "onCompleted: PostCallback<List<PostModel>>");
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(getContext(), "getPosts error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onPostClick(int postId) {
        Log.i(TAG, "onPostClick: ");
        if (onPostSelectedListener != null)
            onPostSelectedListener.onPostSelected(postId);
    }

    @Override
    public void onCommentNeeded(final int postId) {
        Log.i(TAG, "onCommentNeeded: " + postId);

    }

    @Override
    public void onAuthorNeeded(int postId) {
        Log.i(TAG, "onAuthorNeeded: " + postId);
    }

    public interface OnPostSelectedListener {
        void onPostSelected(int postId);
    }

}

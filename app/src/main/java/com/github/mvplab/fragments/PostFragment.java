package com.github.mvplab.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mvplab.R;
import com.github.mvplab.models.Comment;
import com.github.mvplab.models.Post;
import com.github.mvplab.models.User;

import java.util.List;

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
    private Post post;
    private User author;
    private List<Comment> comments;



    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment getInstance(int postId){
        PostFragment postFragment = new PostFragment();
        Bundle args = new Bundle(1);
        args.putInt(PARAM_POST_ID, postId);
        postFragment.setArguments(args);
        return postFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
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
        initModel();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }

    private void initModel(){
    }
}

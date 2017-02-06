package com.github.mvplab.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mvplab.R;
import com.github.mvplab.adapters.PostInteractors;
import com.github.mvplab.adapters.PostsAdapter;
import com.github.mvplab.db.DatabaseConst;
import com.github.mvplab.db.DatabaseHelper;
import com.github.mvplab.models.Comment;
import com.github.mvplab.models.Post;
import com.github.mvplab.models.PostModel;
import com.github.mvplab.models.User;
import com.github.mvplab.net.RestApi;
import com.github.mvplab.net.RestModule;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsFragment extends Fragment implements PostInteractors {


    @BindView(R.id.rvPosts)
    RecyclerView rvPosts;

    private Unbinder unbinder;

    private OnPostSelectedListener onPostSelectedListener;

    private List<PostModel> postList;
    private List<User> users;
    private List<Post> posts;
    private HashMap<Integer, List<Comment>> commentsMap;
    private PostsAdapter postsAdapter;
    private RestApi restApi;
    private SQLiteDatabase database;


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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        initModel();
    }

    private void initUI() {
        postsAdapter = new PostsAdapter(getContext(), this, postList);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }

    private void initModel() {
        restApi = new RestModule().provideRestApi();
        loadUsers();
        loadPosts();
    }

    private void openDB() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();
    }

    private void loadPosts() {
        Cursor cursor = database.query(DatabaseConst.TABLE.POSTS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.ID));
                String title = cursor.getString(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.TITLE));
                String text = cursor.getString(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.BODY));
                int userId = cursor.getInt(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.USER_ID));
                User user = findUserById(userId);
                PostModel postModel = new PostModel(id, title, text);
                if (user != null)
                    postModel.setAuthorName(user.getUsername());
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    else

    {
        restApi.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                onPostsLoaded(response.body());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getContext(), "Callback posts failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

    private void loadUsers() {

        restApi.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                onUsersLoaded(response.body());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Callback users failure", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void onUsersLoaded(List<User> users) {
        this.users = users;
    }

    private void onPostsLoaded(List<Post> posts) {

    }

    private void onCommentsLoaded(int postId, List<Comment> comments) {

    }

    @Override
    public void onPostClick(int postId) {

    }

    @Override
    public void onCommentNeeded(int postId) {

    }

    @Override
    public void onAuthorNeeded(int postId) {
        if (users != null && !users.isEmpty()) {
            Post post = findPostById(postId);
            if (post != null) {
                User user = findUserById(post.getUserId());
                PostModel model = findPostModelBy(postId);
                if (model != null && user != null) {
                    model.setAuthorName(user.getUsername());
                }
            }
        } else {
            loadUsers();
        }
    }

    @Nullable
    private Post findPostById(int postId) {
        if (posts != null)
            for (Post post : posts)
                if (post.getId() == postId)
                    return post;
        return null;
    }

    @Nullable
    private User findUserById(int userId) {
        if (users != null)
            for (User user : users)
                if (user.getId() == userId)
                    return user;
        return null;
    }

    @Nullable
    private PostModel findPostModelBy(int postId) {
        if (postList != null)
            for (PostModel model : postList)
                if (model.getId() == postId)
                    return model;
        return null;
    }

public interface OnPostSelectedListener {
    void onPostSelected(int postId);
}

}

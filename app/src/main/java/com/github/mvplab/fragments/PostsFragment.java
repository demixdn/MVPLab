package com.github.mvplab.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsFragment extends Fragment implements PostInteractors {
    private static final String TAG = "PostsFragment";

    @BindView(R.id.rvPosts)
    RecyclerView rvPosts;

    private Unbinder unbinder;

    private OnPostSelectedListener onPostSelectedListener;

    private List<PostModel> postList;
    private List<User> users;
    private List<Post> posts;
    private SparseArrayCompat<List<Comment>> commentsMap;
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.app_name);
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
        restApi = new RestModule(getContext()).provideRestApi();
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), this, postList);
        rvPosts.setAdapter(postsAdapter);

        loadUsers();
        loadPosts();
    }

    private void openDB() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();
    }

    private void closeDB() {
        database.close();
    }

    private void loadPosts() {
        openDB();
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
                findCommentsAndPutSizeIn(postModel);
                postList.add(postModel);
                cursor.moveToNext();
            }
            cursor.close();
            postsAdapter.notifyDataSetChanged();
        } else {
            restApi.getPosts().enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    Log.i(TAG, "onResponse: posts");
                    onPostsLoaded(response.body());
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    Toast.makeText(getContext(), "Callback posts failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
        closeDB();
    }

    private void findCommentsAndPutSizeIn(PostModel postModel) {
        String where = DatabaseConst.TABLE.COMMENTS + "." + DatabaseConst.COMMENT_FIELDS.POST_ID + " = ?";
        String[] args = new String[]{String.valueOf(postModel.getId())};
        Cursor commentsCursor = database.query(DatabaseConst.TABLE.COMMENTS, null, where, args, null, null, null);
        if (commentsCursor != null && commentsCursor.moveToFirst()) {
            List<Comment> comments = new ArrayList<>();
            while (!commentsCursor.isAfterLast()) {
                int commentPostId = commentsCursor.getInt(commentsCursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.POST_ID));
                int commentId = commentsCursor.getInt(commentsCursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.ID));
                String commentName = commentsCursor.getString(commentsCursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.NAME));
                String commentBody = commentsCursor.getString(commentsCursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.BODY));
                String commentEmail = commentsCursor.getString(commentsCursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.EMAIL));
                Comment comment = new Comment(commentPostId, commentId, commentName, commentEmail, commentBody);
                comments.add(comment);
                commentsCursor.moveToNext();
            }
            commentsCursor.close();
            putCommentsInMap(postModel.getId(), comments);
            postModel.setCommentsCount(comments.size());
        }
    }

    private void loadUsers() {
        openDB();
        Cursor cursor = database.query(DatabaseConst.TABLE.USERS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            users = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.NAME));
                String username = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.USERNAME));
                String email = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.EMAIL));
                User user = new User(id, name, username, email);
                users.add(user);
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            restApi.getUsers().enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    Log.i(TAG, "onResponse: users");
                    onUsersLoaded(response.body());
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Toast.makeText(getContext(), "Callback users failure", Toast.LENGTH_SHORT).show();

                }
            });
        }
        closeDB();
    }

    private void onUsersLoaded(List<User> users) {
        this.users = users;
        openDB();
        SQLiteStatement stmt = database.compileStatement(
                String.format("INSERT INTO %s ('%s', '%s', '%s', '%s') VALUES(?1, ?2, ?3, ?4)",
                        DatabaseConst.TABLE.USERS,
                        DatabaseConst.USER_FIELDS.ID, DatabaseConst.USER_FIELDS.NAME, DatabaseConst.USER_FIELDS.USERNAME, DatabaseConst.USER_FIELDS.EMAIL));
        database.beginTransaction();

        for (User user : users) {
            stmt.clearBindings();
            stmt.bindLong(1, user.getId());
            stmt.bindString(2, user.getName());
            stmt.bindString(3, user.getUsername());
            stmt.bindString(4, user.getEmail());
            stmt.executeInsert();
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        closeDB();
    }

    private void onPostsLoaded(List<Post> posts) {
        openDB();
        SQLiteStatement stmt = database.compileStatement(
                String.format("INSERT INTO %s ('%s', '%s', '%s', '%s') VALUES(?1, ?2, ?3, ?4)",
                        DatabaseConst.TABLE.POSTS,
                        DatabaseConst.POST_FIELDS.ID, DatabaseConst.POST_FIELDS.USER_ID, DatabaseConst.POST_FIELDS.TITLE, DatabaseConst.POST_FIELDS.BODY));
        database.beginTransaction();
        for (Post post : posts) {
            stmt.clearBindings();
            stmt.bindLong(1, post.getId());
            stmt.bindLong(2, post.getUserId());
            stmt.bindString(3, post.getTitle());
            stmt.bindString(4, post.getBody());
            stmt.executeInsert();
            int userId = post.getUserId();
            User user = findUserById(userId);
            PostModel postModel = new PostModel(post.getId(), post.getTitle(), post.getBody());
            if (user != null)
                postModel.setAuthorName(user.getUsername());
            postList.add(postModel);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        closeDB();
        postsAdapter.notifyDataSetChanged();
    }

    private void onCommentsLoaded(int postId, List<Comment> comments) {
        putCommentsInMap(postId, comments);

        openDB();
        SQLiteStatement stmt = database.compileStatement(
                String.format("INSERT INTO %s ('%s', '%s', '%s', '%s', '%s') VALUES(?1, ?2, ?3, ?4, ?5)",
                        DatabaseConst.TABLE.COMMENTS,
                        DatabaseConst.COMMENT_FIELDS.POST_ID, DatabaseConst.COMMENT_FIELDS.ID,
                        DatabaseConst.COMMENT_FIELDS.NAME, DatabaseConst.COMMENT_FIELDS.BODY,
                        DatabaseConst.COMMENT_FIELDS.EMAIL));
        database.beginTransaction();

        for (Comment comment : comments) {
            stmt.clearBindings();
            stmt.bindLong(1, comment.getPostId());
            stmt.bindLong(2, comment.getId());
            stmt.bindString(3, comment.getName());
            stmt.bindString(4, comment.getBody());
            stmt.bindString(5, comment.getEmail());
            stmt.executeInsert();
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        closeDB();

        invalidateCommentsSize(postId, comments.size());
    }

    private void putCommentsInMap(int postId, List<Comment> comments) {
        if (commentsMap == null)
            commentsMap = new SparseArrayCompat<>(1);
        commentsMap.put(postId, comments);
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
        if (commentsMap == null)
            commentsMap = new SparseArrayCompat<>(1);
        if (commentsMap.get(postId) != null) {
            invalidateCommentsSize(postId, commentsMap.get(postId).size());
        } else {
            openDB();
            String where = DatabaseConst.TABLE.COMMENTS + "." + DatabaseConst.COMMENT_FIELDS.POST_ID + " = ?";
            String[] args = new String[]{String.valueOf(postId)};
            Cursor cursor = database.query(DatabaseConst.TABLE.COMMENTS, null, where, args, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                List<Comment> comments = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    int commentPostId = cursor.getInt(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.POST_ID));
                    int id = cursor.getInt(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.ID));
                    String name = cursor.getString(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.NAME));
                    String text = cursor.getString(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.BODY));
                    String email = cursor.getString(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.EMAIL));
                    Comment comment = new Comment(commentPostId, id, name, email, text);
                    comments.add(comment);
                    cursor.moveToNext();
                }
                cursor.close();
                putCommentsInMap(postId, comments);
                invalidateCommentsSize(postId, comments.size());
            } else {
                restApi.getCommentByPost(postId).enqueue(new Callback<List<Comment>>() {
                    @Override
                    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                        onCommentsLoaded(postId, response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Comment>> call, Throwable t) {
                        Toast.makeText(getContext(), "onFailure comments", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            closeDB();
        }
    }

    private void invalidateCommentsSize(int postId, int commentsSize) {
        int position = findPostPositionBy(postId);
        if (position >= 0) {
            postList.get(position).setCommentsCount(commentsSize);
            postsAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onAuthorNeeded(int postId) {
        Log.i(TAG, "onAuthorNeeded: " + postId);
        if (users != null && !users.isEmpty()) {
            Post post = findPostById(postId);
            if (post != null) {
                User user = findUserById(post.getUserId());
                PostModel model = findPostModelBy(postId);
                if (model != null && user != null) {
                    model.setAuthorName(user.getUsername());
                    int position = findPostPositionBy(postId);
                    if (position >= 0)
                        postsAdapter.notifyItemChanged(position);
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

    private int findPostPositionBy(int postId) {
        for (int i = 0; i < postList.size(); i++)
            if (postList.get(i).getId() == postId)
                return i;
        return -1;
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

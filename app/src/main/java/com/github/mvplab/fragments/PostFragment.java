package com.github.mvplab.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mvplab.R;
import com.github.mvplab.adapters.PostCommentsAdapter;
import com.github.mvplab.db.DatabaseConst;
import com.github.mvplab.db.DatabaseHelper;
import com.github.mvplab.decoration.CommentItemDecoration;
import com.github.mvplab.models.Comment;
import com.github.mvplab.models.Post;
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
    @Nullable
    private Post post;
    @Nullable
    private User author;
    @Nullable
    private List<Comment> comments;


    private RestApi restApi;
    private SQLiteDatabase database;


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
        initModel();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null)
            unbinder.unbind();
        super.onDestroyView();
    }

    private void initModel() {
        restApi = new RestModule(getContext()).provideRestApi();
        loadPost(postId);
        if (post != null) {
            loadUser(post.getUserId());
            loadComments(postId);
            showAll();
        }
    }

    private void openDB() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        database = databaseHelper.getWritableDatabase();
    }

    private void closeDB() {
        database.close();
    }

    private void loadPost(int postId) {
        openDB();
        String where = DatabaseConst.TABLE.POSTS + "." + DatabaseConst.POST_FIELDS.ID + " = ?";
        String[] args = new String[]{String.valueOf(postId)};
        Cursor cursor = database.query(DatabaseConst.TABLE.POSTS, null, where, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.ID));
            String title = cursor.getString(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.TITLE));
            String text = cursor.getString(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.BODY));
            int userId = cursor.getInt(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.USER_ID));
            post = new Post(userId, id, title, text);
            cursor.close();
        }
        closeDB();
    }

    private void loadUser(int userId) {
        openDB();
        String where = DatabaseConst.TABLE.USERS + "." + DatabaseConst.USER_FIELDS.ID + " = ?";
        String[] args = new String[]{String.valueOf(userId)};
        Cursor cursor = database.query(DatabaseConst.TABLE.USERS, null, where, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.NAME));
            String username = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.USERNAME));
            String email = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.EMAIL));
            author = new User(id, name, username, email);
            cursor.close();
        } else {
            restApi.getUserById(userId).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    author = response.body();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getContext(), "onFailure: user", Toast.LENGTH_SHORT).show();
                }
            });
        }
        closeDB();
    }

    private void loadComments(int postId) {
        openDB();
        String where = DatabaseConst.TABLE.COMMENTS + "." + DatabaseConst.COMMENT_FIELDS.POST_ID + " = ?";
        String[] args = new String[]{String.valueOf(postId)};
        Cursor cursor = database.query(DatabaseConst.TABLE.COMMENTS, null, where, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            comments = new ArrayList<>();
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
        } else {
            restApi.getCommentByPost(postId).enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    PostFragment.this.comments = response.body();
                    saveComments();
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {
                    Toast.makeText(getContext(), "onFailure comments", Toast.LENGTH_SHORT).show();
                }
            });
        }
        closeDB();
    }

    private void saveComments() {
        if (comments != null && !comments.isEmpty()) {
            openDB();
            SQLiteStatement stmt = database.compileStatement(
                    String.format("INSERT INTO %s ('%s', '%s', '%s', '%s', '%s') VALUES(?1, ?2, ?3, ?4, ?5)",
                            DatabaseConst.TABLE.COMMENTS,
                            DatabaseConst.COMMENT_FIELDS.POST_ID, DatabaseConst.COMMENT_FIELDS.ID,
                            DatabaseConst.COMMENT_FIELDS.NAME, DatabaseConst.COMMENT_FIELDS.BODY,
                            DatabaseConst.COMMENT_FIELDS.EMAIL));
            database.beginTransaction();

            for (Comment comment : PostFragment.this.comments) {
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
        }
    }

    private void showAll() {
        if (post != null) {
            getActivity().setTitle(post.getTitle());
            tvPostTitle.setText(post.getTitle());
            tvPostText.setText(post.getBody());
        }
        if (author != null) {
            tvPostAuthor.setText(getString(R.string.by_author, author.getName()));
        }
        if (comments != null && !comments.isEmpty()) {
            rvComments.addItemDecoration(new CommentItemDecoration(getContext()));
            tvCommentsTitle.setText(getString(R.string.comments_title, comments.size()));
            rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
            rvComments.setAdapter(new PostCommentsAdapter(getContext(), comments));
        } else {
            tvCommentsTitle.setText(getString(R.string.comments_title, 0));
        }
    }
}

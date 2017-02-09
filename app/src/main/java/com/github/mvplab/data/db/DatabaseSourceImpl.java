package com.github.mvplab.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.github.mvplab.data.models.Comment;
import com.github.mvplab.data.models.Post;
import com.github.mvplab.data.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 08.02.2017
 * Time: 14:05
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class DatabaseSourceImpl implements DatabaseSource {

    private static final long CLOSE_DB_DELAY_MILLIS = 60 * 1000L;

    private SQLiteDatabase database;

    private boolean isClosed;

    @NonNull
    private final Handler handler;
    @NonNull
    private final Runnable closeDbRunnable;
    @NonNull
    private final DatabaseHelper databaseHelper;

    public DatabaseSourceImpl(@NonNull Context context) {
        databaseHelper = new DatabaseHelper(context);
        isClosed = true;

        handler = new Handler();
        closeDbRunnable = new Runnable() {
            @Override
            public void run() {
                closeDB();
            }
        };

        openDB();
    }

    private void openDB() {
        open(0);
    }

    public synchronized void open(int attempt) {
        close();
        try {
            database = databaseHelper.getWritableDatabase();
            isClosed = false;
            setTime();
        } catch (Exception e) {
            e.printStackTrace();
            if (attempt < 3) {
                int attemptNext = attempt + 1;
                open(attemptNext);
            }
        }
    }

    private synchronized void close() {
        databaseHelper.close();
    }

    private void closeDB() {
        database.close();
        database = null;
        databaseHelper.close();
        isClosed = true;
    }

    private synchronized void setTime() {
        handler.removeCallbacks(closeDbRunnable);
        handler.postDelayed(closeDbRunnable, CLOSE_DB_DELAY_MILLIS);
    }

    @WorkerThread
    @Override
    public void savePosts(@NonNull List<Post> posts) {
        if (!isClosed) {
            insertPosts(posts);
        } else {
            openDB();
            insertPosts(posts);
        }
        setTime();
    }

    private synchronized void insertPosts(@NonNull List<Post> posts) {
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
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @WorkerThread
    @Override
    public void saveUsers(@NonNull List<User> users) {
        if (!isClosed) {
            insertUsers(users);
        } else {
            openDB();
            insertUsers(users);
        }
        setTime();
    }

    private synchronized void insertUsers(@NonNull List<User> users) {
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
    }

    @WorkerThread
    @Override
    public void saveComments(@NonNull List<Comment> comments) {
        if (!isClosed) {
            insertComments(comments);
        } else {
            openDB();
            insertComments(comments);
        }
        setTime();
    }

    private synchronized void insertComments(@NonNull List<Comment> comments) {
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
    }

    @WorkerThread
    @Nullable
    @Override
    public List<Post> getAllPosts() {
        List<Post> posts;
        if (!isClosed) {
            posts = postsQuery();
        } else {
            openDB();
            posts = postsQuery();
        }
        setTime();
        return posts;
    }

    @Nullable
    @Override
    public Post getPost(int postId) {
        Post post;
        if(!isClosed){
            post = postQuery(postId);
        }else {
            openDB();
            post = postQuery(postId);
        }
        setTime();
        return post;
    }

    @Nullable
    private synchronized Post postQuery(int postId) {
        Post post = null;
        String where = DatabaseConst.TABLE.POSTS + "." + DatabaseConst.POST_FIELDS.ID + " = ?";
        String[] args = new String[]{String.valueOf(postId)};
        Cursor cursor = database.query(DatabaseConst.TABLE.POSTS, null, where, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            post = parseCursorToPost(cursor);
            cursor.close();
        }
        return post;
    }

    @Nullable
    private synchronized List<Post> postsQuery() {
        List<Post> posts = null;
        Cursor cursor = database.query(DatabaseConst.TABLE.POSTS, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            posts = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                Post post = parseCursorToPost(cursor);
                posts.add(post);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return posts;
    }

    @WorkerThread
    @Nullable
    @Override
    public User getUserBy(int userId) {
        User user;
        if (!isClosed) {
            user = userQuery(userId);
        } else {
            openDB();
            user = userQuery(userId);
        }
        setTime();
        return user;
    }

    @Nullable
    private synchronized User userQuery(int userId) {
        User user = null;
        String where = DatabaseConst.TABLE.USERS + "." + DatabaseConst.USER_FIELDS.ID + " = ?";
        String[] args = new String[]{String.valueOf(userId)};
        Cursor cursor = database.query(DatabaseConst.TABLE.USERS, null, where, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            user = parseCursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    @WorkerThread
    @Nullable
    @Override
    public List<Comment> getCommentsBy(int postId) {
        List<Comment> comments;
        if (!isClosed) {
            comments = commentsQuery(postId);
        } else {
            openDB();
            comments = commentsQuery(postId);
        }
        setTime();
        return comments;
    }

    @Nullable
    private synchronized List<Comment> commentsQuery(int postId) {
        List<Comment> comments = null;
        String where = DatabaseConst.TABLE.COMMENTS + "." + DatabaseConst.COMMENT_FIELDS.POST_ID + " = ?";
        String[] args = new String[]{String.valueOf(postId)};
        Cursor cursor = database.query(DatabaseConst.TABLE.COMMENTS, null, where, args, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            comments = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                Comment comment = parseCursorToComment(cursor);
                comments.add(comment);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return comments;
    }

    @WorkerThread
    @Override
    public void dropAll() {
        if (!isClosed) {
            dropTables();
        } else {
            openDB();
            dropTables();
        }
        setTime();
    }

    private void dropTables() {
        database.execSQL(DatabaseConst.QUERY.DROP_TABLE_COMMENTS);
        database.execSQL(DatabaseConst.QUERY.DROP_TABLE_POSTS);
        database.execSQL(DatabaseConst.QUERY.DROP_TABLE_USERS);
    }

    @NonNull
    private Post parseCursorToPost(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.ID));
        String title = cursor.getString(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.TITLE));
        String text = cursor.getString(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.BODY));
        int userId = cursor.getInt(cursor.getColumnIndex(DatabaseConst.POST_FIELDS.USER_ID));
        return new Post(userId, id, title, text);
    }

    @NonNull
    private User parseCursorToUser(@NonNull Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.NAME));
        String username = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.USERNAME));
        String email = cursor.getString(cursor.getColumnIndex(DatabaseConst.USER_FIELDS.EMAIL));
        return new User(id, name, username, email);
    }

    @NonNull
    private Comment parseCursorToComment(@NonNull Cursor cursor) {
        int commentPostId = cursor.getInt(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.POST_ID));
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.NAME));
        String text = cursor.getString(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.BODY));
        String email = cursor.getString(cursor.getColumnIndex(DatabaseConst.COMMENT_FIELDS.EMAIL));
        return new Comment(commentPostId, id, name, email, text);
    }
}

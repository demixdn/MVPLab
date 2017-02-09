package com.github.mvplab.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.github.mvplab.data.models.Comment;
import com.github.mvplab.data.models.Post;
import com.github.mvplab.data.models.User;

import java.util.List;

/**
 * Date: 08.02.2017
 * Time: 13:58
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface DatabaseSource {
    @WorkerThread
    void savePosts(@NonNull List<Post> posts);

    @WorkerThread
    void saveUsers(@NonNull List<User> users);

    @WorkerThread
    void saveComments(@NonNull List<Comment> comments);

    @WorkerThread
    @Nullable
    List<Post> getAllPosts();

    @WorkerThread
    @Nullable
    Post getPost(int postId);

    @WorkerThread
    @Nullable
    User getUserBy(int userId);

    @WorkerThread
    @Nullable
    List<Comment> getCommentsBy(int postId);

    @WorkerThread
    void dropAll();
}

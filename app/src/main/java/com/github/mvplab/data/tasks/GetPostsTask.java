package com.github.mvplab.data.tasks;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.mvplab.data.cache.MemoryCache;
import com.github.mvplab.data.callback.CommentsNeedCallback;
import com.github.mvplab.data.callback.PostCallback;
import com.github.mvplab.data.db.DatabaseSource;
import com.github.mvplab.data.net.RestApi;
import com.github.mvplab.models.Comment;
import com.github.mvplab.models.Post;
import com.github.mvplab.models.PostModel;
import com.github.mvplab.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Date: 08.02.2017
 * Time: 18:59
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class GetPostsTask implements Runnable {
    @NonNull
    private final DatabaseSource diskDataSource;
    @NonNull
    private final RestApi restApi;
    @NonNull
    private final Handler mainHandler;
    @NonNull
    private final PostCallback<List<PostModel>> callback;
    @NonNull
    private final MemoryCache memoryCache;
    @NonNull
    private final CommentsNeedCallback commentsNeedCallback;

    public GetPostsTask(@NonNull DatabaseSource diskDataSource,
                        @NonNull RestApi restApi,
                        @NonNull Handler mainHandler,
                        @NonNull PostCallback<List<PostModel>> callback,
                        @NonNull MemoryCache memoryCache,
                        @NonNull CommentsNeedCallback commentsNeedCallback) {
        this.diskDataSource = diskDataSource;
        this.restApi = restApi;
        this.mainHandler = mainHandler;
        this.callback = callback;
        this.memoryCache = memoryCache;
        this.commentsNeedCallback = commentsNeedCallback;
        Log.i("***", "GetPostsTask: ");
    }

    @Override
    public void run() {
        Log.i("***", "run: GetPostsTask");
        List<PostModel> postModels = new ArrayList<>();
        List<Post> posts = getPosts();
        for (Post post : posts) {
            int postId = post.getId();
            List<Comment> comments = getComments(postId);
            int userId = post.getUserId();
            User user = getUser(userId);
            postModels.add(new PostModel(post.getId(), post.getTitle(), post.getBody(), user, comments));
        }
        memoryCache.setPostModels(postModels);
        mainHandler.post(new CallbackToUI(postModels));
    }

    @NonNull
    private List<Post> getPosts() {
        List<Post> allPosts = memoryCache.getPosts();

        if (allPosts == null) {
            allPosts = diskDataSource.getAllPosts();
            memoryCache.setPosts(allPosts);
        }

        if (allPosts == null) {
            allPosts = getRemotePosts();
            savePosts(allPosts);
        }

        return allPosts == null ? Collections.<Post>emptyList() : allPosts;
    }

    private List<Comment> getComments(int postId) {
        List<Comment> comments = memoryCache.getPostComments(postId);
        if (comments == null) {
            comments = diskDataSource.getCommentsBy(postId);
            memoryCache.putComments(postId, comments);
        }

        if (comments == null) {
            commentsNeedCallback.needCommentsFor(postId);
        }
        return comments;
    }

    @Nullable
    private User getUser(int userId) {
        User user = memoryCache.getUser(userId);
        if (user == null)
            user = diskDataSource.getUserBy(userId);
        if (user == null) {
            user = getRemoteUser(userId);
        }
        return user;
    }

    @Nullable
    private User getRemoteUser(int userId) {
        User user = null;
        try {
            loadRemoteUsersAndSave();
            user = memoryCache.getUser(userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    private void loadRemoteUsersAndSave() throws IOException {
        List<User> users = restApi.getAllUsers().execute().body();
        diskDataSource.saveUsers(users);
        memoryCache.setUsers(users);
    }

    private void savePosts(List<Post> posts) {
        if (posts != null) {
            diskDataSource.savePosts(posts);
            memoryCache.setPosts(posts);
        }
    }

    @Nullable
    private List<Post> getRemotePosts() {
        List<Post> posts = null;
        try {
            posts = restApi.getAllPosts().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(new IOException("Response not converted to list of post"));
                }
            });
        }
        return posts;
    }

    private class CallbackToUI implements Runnable {

        private final List<PostModel> postModels;

        CallbackToUI(List<PostModel> postModels) {
            this.postModels = postModels;
        }

        @Override
        public void run() {
            Log.i("***", "run: postModels callback to ui");
            callback.onEmit(postModels);
            callback.onCompleted();
        }
    }
}

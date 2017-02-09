package com.github.mvplab.data.tasks;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.mvplab.data.cache.MemoryCache;
import com.github.mvplab.data.callback.PostCallback;
import com.github.mvplab.data.db.DatabaseSource;
import com.github.mvplab.data.net.RestApi;
import com.github.mvplab.data.models.Comment;
import com.github.mvplab.data.models.Post;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.data.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 09.02.2017
 * Time: 14:10
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class GetPostTask implements Runnable {
    @NonNull
    private final DatabaseSource diskDataSource;
    @NonNull
    private final RestApi restApi;
    @NonNull
    private final Handler mainHandler;
    @NonNull
    private final PostCallback<PostModel> callback;
    @NonNull
    private final MemoryCache memoryCache;

    private final int postId;

    public GetPostTask(int postId, @NonNull DatabaseSource diskDataSource,
                       @NonNull RestApi restApi,
                       @NonNull Handler mainHandler,
                       @NonNull PostCallback<PostModel> callback,
                       @NonNull MemoryCache memoryCache) {
        this.postId = postId;
        this.diskDataSource = diskDataSource;
        this.restApi = restApi;
        this.mainHandler = mainHandler;
        this.callback = callback;
        this.memoryCache = memoryCache;
        Log.i("***", "GetPostTask #" + postId);
    }


    @Override
    public void run() {
        Log.i("***", "run: GetPostTask #" + postId);
        PostModel postModel;
        postModel = memoryCache.getPostModel(postId);
        if (postModel == null) {
            postModel = getLocalPostModel(postId);
            if (postModel == null) {
                postModel = getRemotePostModel(postId);
                if (postModel != null) {
                    returnSuccess(postModel);
                    onCompleted();
                }
                else
                    returnError();
            } else {
                returnSuccess(postModel);
                onCompleted();
            }
        } else {
            checkAuthor(postModel);
            returnSuccess(postModel);
            checkComments(postModel);
            returnSuccess(postModel);
            onCompleted();
        }
    }

    private void onCompleted() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onCompleted();
            }
        });
    }

    private void checkAuthor(PostModel postModel) {
        if(!postModel.isHaveAuthor()) {
            Post post = memoryCache.getPost(postId);
            if(post!=null) {
                int userId = post.getUserId();
                User user = getUser(userId);
                postModel.setAuthor(user);
            }
        }
    }

    private void checkComments(PostModel postModel) {
        if(!postModel.isHaveComments()) {
            List<Comment> comments = getComments(postId);
            postModel.setComments(comments);
        }
    }

    private void returnError() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(new IOException("Post model not loaded"));
            }
        });

    }

    private void returnSuccess(PostModel postModel) {
        mainHandler.post(new CallbackToUI(postModel));
    }

    private PostModel getRemotePostModel(int postId) {
        PostModel postModel = null;
        Post post;
        try {
            post = restApi.getPost(postId).execute().body();
            savePost(post);
            postModel = createPostModel(postId, post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postModel;
    }

    private void savePost(@Nullable Post post) {
        if (post != null) {
            List<Post> singlePostList = new ArrayList<>(1);
            singlePostList.add(post);
            diskDataSource.savePosts(singlePostList);
            memoryCache.addPost(post);
        }
    }

    @Nullable
    private PostModel getLocalPostModel(int postId) {
        PostModel postModel;
        Post post = diskDataSource.getPost(postId);
        memoryCache.addPost(post);
        postModel = createPostModel(postId, post);
        return postModel;
    }

    @Nullable
    private PostModel createPostModel(int postId, @Nullable Post post) {
        PostModel postModel = null;
        if (post != null) {
            int userId = post.getUserId();
            User user = getUser(userId);
            List<Comment> comments = getComments(postId);
            postModel = new PostModel(post.getId(), post.getTitle(), post.getBody(), user, comments);
        }
        return postModel;
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

    @Nullable
    private List<Comment> getComments(int postId) {
        List<Comment> comments = memoryCache.getPostComments(postId);
        if (comments == null) {
            comments = getLocalComments(postId);
        }

        if (comments == null) {
            comments = getRemoteComments(postId);
        }
        return comments;
    }

    private List<Comment> getLocalComments(int postId) {
        List<Comment> comments;
        comments = diskDataSource.getCommentsBy(postId);
        memoryCache.putComments(postId, comments);
        return comments;
    }

    @Nullable
    private List<Comment> getRemoteComments(int postId) {
        List<Comment> comments = null;
        try {
            comments = restApi.getCommentByPost(postId).execute().body();
            if (comments != null) {
                diskDataSource.saveComments(comments);
                memoryCache.putComments(postId, comments);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return comments;
    }

    private class CallbackToUI implements Runnable {

        private final PostModel postModel;

        CallbackToUI(PostModel postModel) {
            this.postModel = postModel;
        }

        @Override
        public void run() {
            Log.i("***", "run: PostModel callback to ui");
            callback.onEmit(postModel);
        }
    }
}

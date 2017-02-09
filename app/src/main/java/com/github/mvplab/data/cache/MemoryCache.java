package com.github.mvplab.data.cache;

import android.support.annotation.Nullable;

import com.github.mvplab.models.Comment;
import com.github.mvplab.models.Post;
import com.github.mvplab.models.PostModel;
import com.github.mvplab.models.User;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Date: 08.02.2017
 * Time: 19:02
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class MemoryCache {
    private CopyOnWriteArrayList<PostModel> postModels;
    private CopyOnWriteArrayList<Post> posts;
    private CopyOnWriteArrayList<User> users;
    private ConcurrentHashMap<Integer, List<Comment>> commentsMap;

    private MemoryCache() {
        //empty
    }

    public static MemoryCache create() {
        return new MemoryCache();
    }

    public void setPostModels(@Nullable List<PostModel> postModels) {
        if (postModels != null)
            this.postModels = new CopyOnWriteArrayList<>(postModels);
    }

    public void setPosts(@Nullable List<Post> posts) {
        if (posts != null)
            this.posts = new CopyOnWriteArrayList<>(posts);
    }

    public void setUsers(@Nullable List<User> users) {
        if (users != null)
            this.users = new CopyOnWriteArrayList<>(users);
    }

    public void putComments(int postId, @Nullable List<Comment> comments) {
        if (this.commentsMap == null)
            this.commentsMap = new ConcurrentHashMap<>(1);
        if (comments != null)
            this.commentsMap.put(postId, comments);
    }

    @Nullable
    public List<Post> getPosts() {
        return posts;
    }

    @Nullable
    public List<PostModel> getPostModels() {
        return postModels;
    }

    @Nullable
    public Post getPost(int postId) {
        if (posts == null)
            return null;
        for (Post post : posts)
            if (post.getId() == postId)
                return post;
        return null;
    }

    @Nullable
    public PostModel getPostModel(int postId) {
        if (postModels == null)
            return null;
        for (PostModel model : postModels)
            if (model.getId() == postId)
                return model;
        return null;
    }

    @Nullable
    public List<Comment> getPostComments(int postId) {
        if (commentsMap == null)
            return null;
        return commentsMap.get(postId);
    }

    @Nullable
    public User getUser(int userId) {
        if (users == null)
            return null;
        for (User user : users)
            if (user.getId() == userId)
                return user;
        return null;
    }

    public void clear() {
        this.postModels = null;
        this.posts = null;
        this.users = null;
        this.commentsMap = null;
    }

    public void addPost(@Nullable Post post) {
        if (post != null) {
            if (this.posts == null) {
                this.posts = new CopyOnWriteArrayList<>();
            }
            posts.add(post);
        }
    }
}

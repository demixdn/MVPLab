package com.github.mvplab.data.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Date: 06.02.2017
 * Time: 18:47
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostModel {
    private int id;
    @NonNull
    private String title;
    @NonNull
    private String body;
    @Nullable
    private User author;
    @Nullable
    private List<Comment> comments;

    public PostModel(int id, @NonNull String title, @NonNull String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public PostModel(int id, @NonNull String title, @NonNull String body, @Nullable User author, @Nullable List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = author;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getBody() {
        return body;
    }

    @Nullable
    public String getAuthorFullName() {
        return author == null ? null : author.getName();
    }

    @Nullable
    public List<Comment> getComments() {
        return comments;
    }

    @Nullable
    public String getAuthorName() {
        return author == null ? null : author.getUsername();
    }

    public int getCommentsCount() {
        return comments == null ? 0 : comments.size();
    }

    public boolean isHaveAuthor() {
        return author != null;
    }

    public boolean isHaveComments() {
        return comments != null;
    }

    public void setAuthor(@Nullable User author) {
        this.author = author;
    }

    public void setComments(@Nullable List<Comment> comments) {
        this.comments = comments;
    }
}

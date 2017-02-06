package com.github.mvplab.models;

/**
 * Date: 06.02.2017
 * Time: 18:47
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostModel {
    private int id;
    private String title;
    private String body;
    private String authorName;
    private int commentsCount;

    public PostModel(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public PostModel(int id, String title, String body, String authorName, int commentsCount) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.authorName = authorName;
        this.commentsCount = commentsCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", authorName='" + authorName + '\'' +
                ", commentsCount=" + commentsCount +
                '}';
    }
}

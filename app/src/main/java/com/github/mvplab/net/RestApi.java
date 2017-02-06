package com.github.mvplab.net;

import com.github.mvplab.models.Comment;
import com.github.mvplab.models.Post;
import com.github.mvplab.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Date: 06.02.2017
 * Time: 13:42
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface RestApi {
    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("comments")
    Call<List<Comment>> getCommentByPost(@Query("postId") int postId);

    @GET("users")
    Call<List<User>> getUsers();
}

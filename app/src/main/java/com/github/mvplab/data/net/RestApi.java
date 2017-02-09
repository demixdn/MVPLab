package com.github.mvplab.data.net;

import com.github.mvplab.AppConst;
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

    @GET(AppConst.POSTS)
    Call<List<Post>> getAllPosts();

    @GET(AppConst.POSTS)
    Call<Post> getPost(@Query(AppConst.POST_ID) int postId);

    @GET(AppConst.COMMENTS)
    Call<List<Comment>> getAllComments();

    @GET(AppConst.COMMENTS)
    Call<List<Comment>> getCommentByPost(@Query(AppConst.POST_ID) int postId);

    @GET(AppConst.USERS)
    Call<List<User>> getAllUsers();

    @GET(AppConst.USERS)
    Call<User> getUserById(@Query(AppConst.USER_ID) int userId);
}

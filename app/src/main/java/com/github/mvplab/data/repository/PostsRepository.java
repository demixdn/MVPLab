package com.github.mvplab.data.repository;

import android.support.annotation.NonNull;

import com.github.mvplab.data.callback.PostCallback;
import com.github.mvplab.data.models.PostModel;

import java.util.List;

/**
 * Date: 08.02.2017
 * Time: 12:54
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface PostsRepository {
    void getPosts(@NonNull PostCallback<List<PostModel>> callback);

    void getPost(int postId, @NonNull PostCallback<PostModel> callback);
}

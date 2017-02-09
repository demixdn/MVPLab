package com.github.mvplab;

import android.app.Application;

import com.github.mvplab.data.repository.PostsRepository;
import com.github.mvplab.data.repository.PostsRepositoryImpl;

/**
 * Date: 06.02.2017
 * Time: 13:25
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class LabApp extends Application {

    private static PostsRepository postsRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        postsRepository = new PostsRepositoryImpl(this);
    }

    public static PostsRepository getPostsRepository() {
        return postsRepository;
    }
}

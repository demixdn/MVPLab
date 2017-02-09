package com.github.mvplab.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mvplab.AppConst;
import com.github.mvplab.data.cache.MemoryCache;
import com.github.mvplab.data.callback.CommentsNeedCallback;
import com.github.mvplab.data.callback.PostCallback;
import com.github.mvplab.data.db.DatabaseSource;
import com.github.mvplab.data.db.DatabaseSourceImpl;
import com.github.mvplab.data.executor.JobExecutor;
import com.github.mvplab.data.net.RestApi;
import com.github.mvplab.data.net.RestModule;
import com.github.mvplab.data.tasks.GetPostTask;
import com.github.mvplab.data.tasks.GetPostsTask;
import com.github.mvplab.data.tasks.GetRemoteCommentsTask;
import com.github.mvplab.data.models.PostModel;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Date: 08.02.2017
 * Time: 13:56
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostsRepositoryImpl implements PostsRepository {

    private final RestApi restApi;
    private final DatabaseSource diskDataSource;
    private final ExecutorService executorService;
    private final Handler mainUiHandler;
    private final MemoryCache memoryCache;
    private final CommentsNeedCallback commentsNeedCallback;

    public PostsRepositoryImpl(@NonNull Context context) {
        restApi = new RestModule(context).provideRestApi(AppConst.ENDPOINT);
        diskDataSource = new DatabaseSourceImpl(context);
        JobExecutor jobExecutor = new JobExecutor();
        executorService = jobExecutor.getThreadPoolExecutor();
        mainUiHandler = new Handler(Looper.getMainLooper());
        memoryCache = MemoryCache.create();
        commentsNeedCallback = new CommentsNeedCallback() {
            @Override
            public void needCommentsFor(int postId) {
                Log.i("***", "needCommentsFor: #" + postId);
//                loadCommentsFor(postId);
            }
        };
    }

    @Override
    public void getPosts(@NonNull PostCallback<List<PostModel>> callback) {
        Log.i("TAG", "getPosts: ");
        executorService.execute(new GetPostsTask(diskDataSource, restApi, mainUiHandler, callback, memoryCache, commentsNeedCallback));
    }

    @Override
    public void getPost(int postId, @NonNull PostCallback<PostModel> callback) {
        executorService.execute(new GetPostTask(postId, diskDataSource, restApi, mainUiHandler, callback, memoryCache));
    }

    private void loadCommentsFor(int postId) {
        executorService.execute(new GetRemoteCommentsTask(diskDataSource, restApi, memoryCache, postId));
    }
}

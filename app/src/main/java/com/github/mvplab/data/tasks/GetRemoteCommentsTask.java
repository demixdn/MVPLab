package com.github.mvplab.data.tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mvplab.data.cache.MemoryCache;
import com.github.mvplab.data.db.DatabaseSource;
import com.github.mvplab.data.net.RestApi;
import com.github.mvplab.models.Comment;

import java.io.IOException;
import java.util.List;

/**
 * Date: 09.02.2017
 * Time: 16:00
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class GetRemoteCommentsTask implements Runnable {

    @NonNull
    private final DatabaseSource diskDataSource;
    @NonNull
    private final RestApi restApi;
    @NonNull
    private final MemoryCache memoryCache;

    private final int postId;

    public GetRemoteCommentsTask(@NonNull DatabaseSource diskDataSource, @NonNull RestApi restApi, @NonNull MemoryCache memoryCache, int postId) {
        this.diskDataSource = diskDataSource;
        this.restApi = restApi;
        this.memoryCache = memoryCache;
        this.postId = postId;
        Log.i("***", "GetRemoteCommentsTask: #" + postId);
    }

    @Override
    public void run() {
        try {
            Log.i("***", "run: GetRemoteCommentsTask: #" + postId);
            List<Comment> comments = restApi.getCommentByPost(postId).execute().body();
            if (comments != null) {
                diskDataSource.saveComments(comments);
                memoryCache.putComments(postId, comments);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

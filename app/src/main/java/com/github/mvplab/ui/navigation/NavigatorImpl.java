package com.github.mvplab.ui.navigation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Date: 13.02.2017
 * Time: 16:07
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class NavigatorImpl implements Navigator {
    private static final String TAG = "NavigatorImpl";
    private Deque<Viewed> viewsQueue = new ArrayDeque<>();

    public NavigatorImpl() {
        //empty
    }

    @Nullable
    @Override
    public Viewed getCurrentView() {
        Log.i(TAG, "getCurrentView. size: [" + viewsQueue.size() + "]");
        return viewsQueue.peekLast();
    }

    @Override
    public boolean hasPrevious() {
        boolean condition = viewsQueue.size() - 1 > 0;
        Log.i(TAG, "hasPrevious = " + condition + ". size: [" + viewsQueue.size() + "]");
        return condition;
    }

    @Nullable
    @Override
    public Viewed getPrevious() {
        viewsQueue.pollLast();
        Viewed viewed = viewsQueue.peekLast();
        Log.i(TAG, "getPrevious. size: [" + viewsQueue.size() + "]");
        return viewed;
    }

    @Override
    public void setCurrentView(@NonNull Viewed view) {
        viewsQueue.add(view);
        Log.i(TAG, "setCurrentView. size: [" + viewsQueue.size() + "]");
    }

    @Override
    public void clearNavigation() {
        viewsQueue.clear();
    }
}

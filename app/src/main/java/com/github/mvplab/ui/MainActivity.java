package com.github.mvplab.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.ui.listposts.view.PostsViewImpl;
import com.github.mvplab.ui.navigation.Navigator;
import com.github.mvplab.ui.navigation.Viewed;
import com.github.mvplab.ui.post.view.PostViewImpl;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnPostSelectedListener {

    private static final String ACTIVITY_TAG = "MainActivity";

    @BindView(R.id.container)
    FrameLayout container;

    private Navigator navigator;

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.i(ACTIVITY_TAG, "onCreate: ");
        LabApp.getApplication().inject(this);
        if (savedInstanceState == null)
            showPosts();
        else
            restoreView();
    }

    private void showPosts() {
        PostsViewImpl postsView = new PostsViewImpl(this, this);
        showNewView(postsView);
    }

    private void showNewView(Viewed view) {
        if (navigator.getCurrentView() != null) {
            container.removeAllViews();
            navigator.getCurrentView().onUnbindView();
        }
        navigator.setCurrentView(view);
        view.onBindView();
        container.addView(view.getView());
    }

    private void restoreView() {
        Viewed currentView = navigator.getCurrentView();
        if (currentView != null) {
            currentView.onBindView();
            container.addView(currentView.getView());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(ACTIVITY_TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_TAG, "onResume: ");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(ACTIVITY_TAG, "onPostResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(ACTIVITY_TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        unbindCurrentView();
        Log.i(ACTIVITY_TAG, "onDestroy: ");
        super.onDestroy();
    }

    private void unbindCurrentView() {
        Log.i(ACTIVITY_TAG, "unbindCurrentView: ");
        Viewed currentView = navigator.getCurrentView();
        if (currentView != null) {
            container.removeAllViews();
            currentView.onUnbindView();
        }
    }

    @Override
    public void onPostSelected(int postId) {
        PostViewImpl postView = PostViewImpl.getInstance(this, postId);
        showNewView(postView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (navigator.hasPrevious())
            showPreviousView();
        else {
            navigator.clearNavigation();
            super.onBackPressed();
        }
    }

    private void showPreviousView() {
        if (navigator.getCurrentView() != null) {
            container.removeAllViews();
            navigator.getCurrentView().onUnbindView();
            navigator.getCurrentView().onDestroy();
        }
        Viewed previousView = navigator.getPrevious();
        if (previousView != null) {
            previousView.onBindView();
            container.addView(previousView.getView());
        }
    }
}

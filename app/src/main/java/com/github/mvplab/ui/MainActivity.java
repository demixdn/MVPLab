package com.github.mvplab.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.github.mvplab.R;
import com.github.mvplab.ui.listposts.view.PostsFragment;
import com.github.mvplab.ui.post.view.PostFragment;

public class MainActivity extends AppCompatActivity implements OnPostSelectedListener {

    private static final String ACTIVITY_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(ACTIVITY_TAG, "onCreate: ");
        if (savedInstanceState == null)
            startPostsFragment();
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
        super.onDestroy();
        Log.i(ACTIVITY_TAG, "onDestroy: ");
    }

    private int startPostsFragment() {
        PostsFragment fragment = PostsFragment.getInstance();
        return getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onPostSelected(int postId) {
        startDetailFragment(postId);
    }

    private void startDetailFragment(int postId) {
        PostFragment fragment = PostFragment.getInstance(postId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}

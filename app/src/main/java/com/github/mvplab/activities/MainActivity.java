package com.github.mvplab.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mvplab.R;
import com.github.mvplab.fragments.PostFragment;
import com.github.mvplab.fragments.PostsFragment;

public class MainActivity extends AppCompatActivity implements PostsFragment.OnPostSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startPostsFragment();
    }

    private int startPostsFragment() {
        PostsFragment fragment = PostsFragment.getInstance();
        fragment.setOnPostSelectedListener(this);
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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, PostFragment.getInstance(postId))
                .addToBackStack(null)
                .commit();
    }
}

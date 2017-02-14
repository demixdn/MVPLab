package com.github.mvplab.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.github.mvplab.R;
import com.github.mvplab.ui.listposts.view.PostsViewController;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ActionBarProvider{

    private static final String ACTIVITY_TAG = "MainActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.controller_container)
    ViewGroup container;

    private Router router;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(new PostsViewController()));
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
        Log.i(ACTIVITY_TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed();
        }
    }
}

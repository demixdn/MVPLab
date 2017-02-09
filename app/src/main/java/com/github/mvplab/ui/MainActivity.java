package com.github.mvplab.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mvplab.LabApp;
import com.github.mvplab.R;
import com.github.mvplab.ui.post.presenter.PostPresenter;
import com.github.mvplab.ui.post.presenter.PostPresenterImpl;
import com.github.mvplab.ui.post.view.PostFragment;
import com.github.mvplab.ui.listposts.presenter.PostsPresenter;
import com.github.mvplab.ui.listposts.presenter.PostsPresenterImpl;
import com.github.mvplab.ui.listposts.view.PostsFragment;

public class MainActivity extends AppCompatActivity implements OnPostSelectedListener {

    private PostPresenter postPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startPostsFragment();
    }

    private int startPostsFragment() {
        PostsPresenter presenter = new PostsPresenterImpl(this, LabApp.getPostsRepository());
        PostsFragment fragment = PostsFragment.getInstance();
        presenter.bindView(fragment);
        fragment.bindPresenter(presenter);
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
        if (postPresenter == null)
            postPresenter = new PostPresenterImpl(LabApp.getPostsRepository());
        postPresenter.setPostId(postId);
        fragment.bindPresenter(postPresenter);
        postPresenter.bindView(fragment);
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

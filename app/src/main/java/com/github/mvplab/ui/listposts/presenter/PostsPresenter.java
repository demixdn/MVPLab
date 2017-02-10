package com.github.mvplab.ui.listposts.presenter;

import android.support.annotation.Nullable;

import com.github.mvplab.ui.OnPostSelectedListener;
import com.github.mvplab.ui.base.Presenter;
import com.github.mvplab.ui.listposts.view.PostsView;

/**
 * Date: 09.02.2017
 * Time: 17:47
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface PostsPresenter extends Presenter<PostsView> {

    void setOnPostSelectedListener(@Nullable OnPostSelectedListener onPostSelectedListener);

    void loadPosts();

    void onPostClick(int postId);
}

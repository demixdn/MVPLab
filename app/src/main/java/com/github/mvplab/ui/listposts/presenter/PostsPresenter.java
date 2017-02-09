package com.github.mvplab.ui.listposts.presenter;

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
    void loadPosts();

    void onPostClick(int postId);
}

package com.github.mvplab.ui.post.presenter;

import com.github.mvplab.ui.base.Presenter;
import com.github.mvplab.ui.post.view.PostView;

/**
 * Date: 09.02.2017
 * Time: 18:24
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface PostPresenter extends Presenter<PostView> {
    void setPostId(int postId);
    void loadPost();
}

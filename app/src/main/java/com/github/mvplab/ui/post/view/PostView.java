package com.github.mvplab.ui.post.view;

import android.support.annotation.NonNull;

import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.base.ProgressView;
import com.github.mvplab.ui.base.View;
import com.github.mvplab.ui.post.presenter.PostPresenter;

/**
 * Date: 09.02.2017
 * Time: 18:23
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface PostView extends View<PostPresenter>, ProgressView{
    void showPostModel(@NonNull PostModel postModel);
}

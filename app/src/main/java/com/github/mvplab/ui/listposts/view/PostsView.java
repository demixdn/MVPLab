package com.github.mvplab.ui.listposts.view;

import android.support.annotation.NonNull;

import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.base.ProgressView;
import com.github.mvplab.ui.base.View;
import com.github.mvplab.ui.listposts.presenter.PostsPresenter;

import java.util.List;

/**
 * Date: 09.02.2017
 * Time: 17:47
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface PostsView extends View<PostsPresenter>, ProgressView {

    void showPosts(@NonNull List<PostModel> models);
}

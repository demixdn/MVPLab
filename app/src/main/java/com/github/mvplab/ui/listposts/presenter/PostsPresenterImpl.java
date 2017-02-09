package com.github.mvplab.ui.listposts.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.mvplab.data.callback.PostCallback;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.data.repository.PostsRepository;
import com.github.mvplab.ui.base.BasePresenter;
import com.github.mvplab.ui.OnPostSelectedListener;
import com.github.mvplab.ui.listposts.view.PostsView;

import java.util.List;

/**
 * Date: 09.02.2017
 * Time: 17:56
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostsPresenterImpl extends BasePresenter<PostsView> implements PostsPresenter {

    @Nullable
    private List<PostModel> postModels;

    private final OnPostSelectedListener onPostSelectedListener;
    private final PostsRepository postsRepository;

    public PostsPresenterImpl(@Nullable OnPostSelectedListener onPostSelectedListener, @NonNull PostsRepository postsRepository) {
        this.onPostSelectedListener = onPostSelectedListener;
        this.postsRepository = postsRepository;
    }

    @Override
    public void loadPosts() {
        if (postModels != null && getView() != null) {
            getView().showPosts(postModels);
        } else {
            showProgress();
            postsRepository.getPosts(new PostCallback<List<PostModel>>() {
                @Override
                public void onEmit(List<PostModel> data) {
                    postsLoaded(data);
                }

                @Override
                public void onCompleted() {
                    hideProgress();
                }

                @Override
                public void onError(Throwable throwable) {
                    showError();
                }
            });
        }
    }

    private void showProgress() {
        if (getView() != null)
            getView().showProgress();
    }

    private void postsLoaded(List<PostModel> data) {
        if (data != null) {
            postModels = data;
            if (getView() != null)
                getView().showPosts(postModels);
        }
    }

    private void showError() {
        if (getView() != null) {
            getView().hideProgress();
            getView().showError("Error retrieving posts");
        }
    }

    private void hideProgress() {
        if (getView() != null)
            getView().hideProgress();
    }


    @Override
    public void onPostClick(int postId) {
        if (onPostSelectedListener != null)
            onPostSelectedListener.onPostSelected(postId);
    }
}

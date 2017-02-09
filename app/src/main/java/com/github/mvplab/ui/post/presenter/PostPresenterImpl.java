package com.github.mvplab.ui.post.presenter;

import android.util.SparseArray;

import com.github.mvplab.data.callback.PostCallback;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.data.repository.PostsRepository;
import com.github.mvplab.ui.base.BasePresenter;
import com.github.mvplab.ui.post.view.PostView;

/**
 * Date: 09.02.2017
 * Time: 18:26
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostPresenterImpl extends BasePresenter<PostView> implements PostPresenter {

    private final PostsRepository postsRepository;

    private SparseArray<PostModel> postModelsMap;

    private int currentPostId;


    public PostPresenterImpl(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
        postModelsMap = new SparseArray<>();
    }

    @Override
    public void setPostId(int postId) {
        this.currentPostId = postId;
    }

    @Override
    public void loadPost() {
        PostModel model = postModelsMap.get(currentPostId);
        if (model == null) {
            loadModel();
        } else if (getView() != null)
            getView().showPostModel(model);
    }

    private void loadModel() {
        if (getView() != null)
            getView().showProgress();
        postsRepository.getPost(currentPostId, new PostCallback<PostModel>() {
            @Override
            public void onEmit(PostModel data) {
                modelLoaded(data);
            }

            @Override
            public void onCompleted() {
                if (getView() != null)
                    getView().hideProgress();
            }

            @Override
            public void onError(Throwable throwable) {
                showError();
            }
        });
    }

    private void showError() {
        if (getView() != null) {
            getView().hideProgress();
            getView().showError("Error retrieving post");
        }
    }

    private void modelLoaded(PostModel data) {
        if (data != null) {
            postModelsMap.put(data.getId(), data);
            if (getView() != null && data.getId() == currentPostId)
                getView().showPostModel(data);
        }
    }
}

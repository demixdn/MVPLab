package com.github.mvplab.adapters;

/**
 * Date: 06.02.2017
 * Time: 18:45
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface PostInteractors {
    void onPostClick(int postId);

    void onCommentNeeded(int postId);

    void onAuthorNeeded(int postId);
}

package com.github.mvplab.ui.base;

/**
 * Date: 09.02.2017
 * Time: 18:36
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface ProgressView {

    void showProgress();

    void hideProgress();

    void showError(String errorMessage);
}

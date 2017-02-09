package com.github.mvplab.ui.base;

/**
 * Date: 09.02.2017
 * Time: 17:47
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface View<P extends Presenter> {

    void bindPresenter(P presenter);

    P getPresenter();
}

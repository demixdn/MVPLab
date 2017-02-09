package com.github.mvplab.ui.base;

/**
 * Date: 09.02.2017
 * Time: 17:47
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface Presenter<V extends View> {

    void bindView(V view);

    void unbindView();

    V getView();
}

package com.github.mvplab.ui.navigation;

import android.view.View;

/**
 * Date: 13.02.2017
 * Time: 15:43
 *
 * @author Aleks Sander
 *         Project MVPLab
 */
public interface Viewed {
    View getView();

    void onBindView();

    void onUnbindView();

    void onDestroy();
}

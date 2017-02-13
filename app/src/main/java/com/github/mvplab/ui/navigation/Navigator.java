package com.github.mvplab.ui.navigation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Date: 13.02.2017
 * Time: 16:03
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface Navigator {
    @Nullable
    Viewed getCurrentView();

    boolean hasPrevious();

    @Nullable
    Viewed getPrevious();

    void setCurrentView(@NonNull Viewed view);

    void clearNavigation();
}

package com.github.mvplab.data.callback;

/**
 * Date: 08.02.2017
 * Time: 12:50
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public interface PostCallback<T> {
    void onEmit(T data);

    void onCompleted();

    void onError(Throwable throwable);
}

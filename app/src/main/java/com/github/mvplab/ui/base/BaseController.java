package com.github.mvplab.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.bluelinelabs.conductor.Controller;
import com.github.mvplab.ui.ActionBarProvider;

public abstract class BaseController extends ButterKnifeController {

    protected BaseController() { }

    protected BaseController(Bundle args) {
        super(args);
    }

    // Note: This is just a quick demo of how an ActionBar *can* be accessed, not necessarily how it *should*
    // be accessed. In a production app, this would use Dagger instead.
    protected ActionBar getActionBar() {
        ActionBarProvider actionBarProvider = ((ActionBarProvider)getActivity());
        return actionBarProvider != null ? actionBarProvider.getSupportActionBar() : null;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        setTitle();
        super.onAttach(view);
    }

    protected void setTitle() {
        Controller parentController = getParentController();
        while (parentController != null) {
            if (parentController instanceof BaseController && ((BaseController)parentController).getTitle() != null) {
                return;
            }
            parentController = parentController.getParentController();
        }

        String title = getTitle();
        ActionBar actionBar = getActionBar();
        if (title != null && actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected String getTitle() {
        return null;
    }
}

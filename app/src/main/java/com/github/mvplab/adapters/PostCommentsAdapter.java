package com.github.mvplab.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Date: 06.02.2017
 * Time: 15:52
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.CommentHolder> {

    public PostCommentsAdapter() {
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class CommentHolder extends RecyclerView.ViewHolder {
        public CommentHolder(View itemView) {
            super(itemView);
        }
    }
}

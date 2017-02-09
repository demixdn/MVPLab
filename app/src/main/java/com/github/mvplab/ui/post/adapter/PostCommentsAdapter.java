package com.github.mvplab.ui.post.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mvplab.R;
import com.github.mvplab.data.models.Comment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 06.02.2017
 * Time: 15:52
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.CommentHolder> {

    @NonNull
    private final Context context;
    @NonNull
    private final List<Comment> comments;

    public PostCommentsAdapter(@NonNull Context context, @NonNull List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.email.setText(comment.getEmail());
        holder.title.setText(comment.getName());
        holder.text.setText(comment.getBody());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvCommentItemTitle)
        TextView title;
        @BindView(R.id.tvCommentItemText)
        TextView text;
        @BindView(R.id.tvCommentItemEmail)
        TextView email;

        CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

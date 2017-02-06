package com.github.mvplab.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mvplab.R;
import com.github.mvplab.models.PostModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 06.02.2017
 * Time: 15:33
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostHolder> implements View.OnClickListener {

    private final Context context;
    private final PostInteractors interactors;
    private final List<PostModel> postList;

    public PostsAdapter(Context context, PostInteractors postInteractor, List<PostModel> postList) {
        this.context = context;
        this.interactors = postInteractor;
        this.postList = postList;
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostHolder(view, this);
    }

    @Override
    public void onBindViewHolder(PostHolder holder, int position) {
        PostModel model = postList.get(position);
        if (model.getCommentsCount() > 0) {
            holder.comments.setText(context.getString(R.string.comments_title, model.getCommentsCount()));
        } else {
            holder.comments.setText(null);
            interactors.onCommentNeeded(model.getId());
        }
        if (!TextUtils.isEmpty(model.getAuthorName())) {
            holder.author.setText(model.getAuthorName());
        } else {
            interactors.onAuthorNeeded(model.getId());
            holder.author.setText(null);
        }
        holder.title.setText(model.getTitle());
        holder.text.setText(model.getBody());
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }


    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        interactors.onPostClick(postList.get(position).getId());
    }

    static class PostHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvPostItemTitle)
        TextView title;
        @BindView(R.id.tvPostItemText)
        TextView text;
        @BindView(R.id.tvPostItemAuthor)
        TextView author;
        @BindView(R.id.tvPostItemCommentsTitle)
        TextView comments;

        PostHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(onClickListener);
        }
    }

}

package com.github.mvplab.ui.listposts.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mvplab.R;
import com.github.mvplab.data.models.PostModel;
import com.github.mvplab.ui.listposts.view.PostInteractors;

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

    @NonNull
    private final Context context;
    @NonNull
    private final PostInteractors interactors;
    @NonNull
    private List<PostModel> postList;

    public PostsAdapter(@NonNull Context context, @NonNull PostInteractors postInteractor, @NonNull List<PostModel> postList) {
        this.context = context;
        this.interactors = postInteractor;
        this.postList = postList;
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(PostHolder holder, int position) {
        PostModel model = postList.get(position);
        holder.setOnClickListener(position, this);
        if (model.getCommentsCount() > 0) {
            holder.comments.setText(context.getString(R.string.comments_title, model.getCommentsCount()));
        } else {
            holder.comments.setText(null);
        }
        if (!TextUtils.isEmpty(model.getAuthorName())) {
            String nick = "@" + model.getAuthorName();
            holder.author.setText(nick);
        } else {
            holder.author.setText(null);
        }
        holder.title.setText(model.getTitle());
        holder.text.setText(model.getBody());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        interactors.onPostClick(postList.get(position).getId());
    }

    public void update(List<PostModel> postModels) {
        this.postList = postModels;
        notifyDataSetChanged();
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

        PostHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setOnClickListener(int position, View.OnClickListener onClickListener) {
            itemView.setTag(position);
            itemView.setOnClickListener(onClickListener);
        }
    }

}

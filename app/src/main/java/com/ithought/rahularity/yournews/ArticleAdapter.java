package com.ithought.rahularity.yournews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ithought.rahularity.yournews.Models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private static final String TAG = "ArticleAdapter";
    private List<Article> articles;
    private int layout;
    private Context context;

    public ArticleAdapter(List<Article> articles, int layout, Context context) {
        this.articles = articles;
        this.layout = layout;
        this.context = context;
    }

    @NonNull
    @Override
    public ArticleAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent ,false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleAdapter.ArticleViewHolder holder, int position) {
        holder.title.setText(articles.get(position).getTitle());
        holder.description.setText(articles.get(position).getDescription());
        holder.date.setText(articles.get(position).getPublishedAt());

        String url = articles.get(position).getUrlToImage();
        Log.d(TAG, "onBindViewHolder: url link is "+ url);
        Picasso.get()
                .load(url)
                .fit()
                .into(holder.newsImage);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder{

        ImageView newsImage;
        TextView title;
        TextView date;
        TextView description;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            newsImage = (ImageView) itemView.findViewById(R.id.news_pic);
            title = (TextView)itemView.findViewById(R.id.title);
            description = (TextView)itemView.findViewById(R.id.description);
            date = (TextView)itemView.findViewById(R.id.date);
        }
    }
}

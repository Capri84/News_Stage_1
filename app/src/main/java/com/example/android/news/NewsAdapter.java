package com.example.android.news;

/**
 * Created by Capri on 04.06.2018.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context context;
    private List<News> newsList;

    public NewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent,
                false);
        NewsAdapter.ViewHolder viewHolder = new NewsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NewsAdapter.ViewHolder holder, int position) {
        final News currentNews = newsList.get(position);
        holder.newsSection.setText(currentNews.getSection());
        holder.newsTitle.setText(currentNews.getNewsTitle());
        holder.newsTextPreview.setText(currentNews.getNewsTextPreview());
        holder.newsAuthor.setText(currentNews.getAuthor());

        String image = currentNews.getImageUrl();
        if (TextUtils.isEmpty(image)) {
            Picasso.get().load(R.drawable.holder);
        } else {
            Picasso.get().load(currentNews.getImageUrl()).into(holder.newsImage);
        }

        //holder.newsImage.setImageBitmap(currentNews.getImageUrl());
        // Create a new Date object from the time in milliseconds of the news.
//        Date newsDate = new Date(currentNews.getDate());
//        SimpleDateFormat dateFormat = new SimpleDateFormat("mm.dd.yyyy");
        holder.newsDate.setText(currentNews.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNews.getUrl()));
                if (newsWebIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    v.getContext().startActivity(newsWebIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public void clear() {
        newsList.clear();
        notifyItemRangeRemoved(0, getItemCount());
    }

    public void addAll(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView newsSection;
        TextView newsDate;
        ImageView newsImage;
        TextView newsTitle;
        TextView newsTextPreview;
        TextView newsAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            newsSection = itemView.findViewById(R.id.tv_news_section);
            newsDate = itemView.findViewById(R.id.tv_news_date);
            newsImage = itemView.findViewById(R.id.img_image);
            newsTitle = itemView.findViewById(R.id.tv_news_title);
            newsTextPreview = itemView.findViewById(R.id.tv_news_preview);
            newsAuthor = itemView.findViewById(R.id.tv_news_author);
        }
    }
}
package com.example.android.scoopup.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.scoopup.model.News;
import com.example.android.scoopup.R;
import com.example.android.scoopup.utils.Utils;
import com.example.android.scoopup.WebActivity;

import java.util.ArrayList;

public class RecyclerNewsAdapter extends RecyclerView.Adapter<RecyclerNewsAdapter.MyViewHolder> {

    private ArrayList<News> news;
    private LayoutInflater inflater;
    private Context context;

    public RecyclerNewsAdapter(Context context, ArrayList<News> news){
        inflater = LayoutInflater.from(context);
        this.news = news;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view =  inflater.inflate(R.layout.news_item_template,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        News current_news = news.get(position);
        holder.setData(current_news,position);
        holder.cardView.setOnClickListener(v -> {
            Intent browser_intent = new Intent(context,WebActivity.class);
            browser_intent.putExtra("url",current_news.getmUrl());
            context.startActivity(browser_intent);
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;
        private TextView author;
        private TextView category;
        private TextView date;
        private CardView cardView;
        private Context context;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            this.title = itemView.findViewById(R.id.title);
            this.author = itemView.findViewById(R.id.author);
            this.image = itemView.findViewById(R.id.imageView);
            this.category = itemView.findViewById(R.id.section);
            this.date = itemView.findViewById(R.id.publishDate);
            this.cardView = itemView.findViewById(R.id.parent_layout);
        }

        public void setData(News current, int position){
            this.title.setText(current.getmTitle());
            this.date.setText(Utils.formatDate(current.getmDate()));
            this.author.setText(current.getmAuthor());
            this.category.setText(current.getmCategory());
            Glide.with(context).load(current.getThumbnailUrl()).into(image);
        }

    }
}

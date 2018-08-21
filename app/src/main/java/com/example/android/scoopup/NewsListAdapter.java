package com.example.android.scoopup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsListAdapter extends ArrayAdapter<News> {
    public NewsListAdapter(@NonNull Context context, int resource, @NonNull List<News> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if(view==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_template,parent,false);
        }
        News current_news = getItem(position);

        TextView news = view.findViewById(R.id.news);
        assert current_news != null;
        news.setText(current_news.getNews());

        TextView category = view.findViewById(R.id.category);
        category.setText(current_news.getCategory());

        TextView date = view.findViewById(R.id.date);
        DateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat output = new SimpleDateFormat("dd MMM yyyy");

        try {
            Date date_id = input.parse(current_news.getPublishDate());
            date.setText(output.format(date_id));
        } catch (ParseException e) {
            e.printStackTrace();
            date.setText(null);
        }

        TextView author = view.findViewById(R.id.author);
        author.setText(current_news.getAuthor());
        return view;
    }
}
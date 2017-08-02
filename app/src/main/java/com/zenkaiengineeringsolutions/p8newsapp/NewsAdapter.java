package com.zenkaiengineeringsolutions.p8newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// Adapter for View Recycling

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter (Context context, List<News> news){
        super (context, 0, news);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        // Use a ViewHolder and Butterknife to improve performance
        ViewHolder holder;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(listItemView);
            listItemView.setTag(holder);
        }
        else
            holder = (ViewHolder) listItemView.getTag();

        News currentNewsItem = getItem(position);

        holder.titleTextView.setText(currentNewsItem.getTitle());
        holder.sectionTextView.setText(currentNewsItem.getSection());
        holder.dateTextView.setText(currentNewsItem.getDate());

        // Add the author name if it's available, if not, don't show the segment
        if (!TextUtils.isEmpty(currentNewsItem.getAuthor())) {
            holder.authorSegmentLayoutView.setVisibility(View.VISIBLE);
            holder.authorTextView.setText(currentNewsItem.getAuthor());

        }
        else
            holder.authorSegmentLayoutView.setVisibility(View.INVISIBLE);                 // Setting this to GONE messes up with the view recycling

        return listItemView;
    }

    static class ViewHolder{
        @BindView(R.id.title) TextView titleTextView;
        @BindView(R.id.section) TextView sectionTextView;
        @BindView(R.id.date) TextView dateTextView;
        @BindView(R.id.author) TextView authorTextView;
        @BindView(R.id.authorSegment) LinearLayout authorSegmentLayoutView;

        public ViewHolder (View view){
            ButterKnife.bind(this, view);
        }

    }
}

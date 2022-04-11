package com.example.labfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ArticleAdapter extends BaseAdapter {

    private ArrayList<Article> list;

    public ArticleAdapter(ArrayList<Article> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        }

        TextView txtTitle = view.findViewById(R.id.txtTitle);

        // article Item
        Article item = this.list.get(position);
        txtTitle.setText(item.webTitle);            // news article title

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

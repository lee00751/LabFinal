package com.example.labfinal;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;

public class FavoritesArticleActivity extends ArticleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_article);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // display home button on action bar
        setTitle(R.string.title_article_info);

        // initialization
        init();
    }

    @Override
    protected void goHelp() {
        // to display AlertDialog
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.dialog_ok, null)
                .setCancelable(false)
                .setTitle(R.string.dialog_title_help)
                .setMessage(R.string.help_favorites_article)
                .show();
    }
}
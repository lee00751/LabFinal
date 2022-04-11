package com.example.labfinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Objects;

public class WebActivity extends AppCompatActivity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // Receive article information
        Intent intent = getIntent();
        String url = intent.getStringExtra(MyOpener.COL_WEB_URL);

        // display home button on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_web);

        // loading dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);

        this.web = findViewById(R.id.webView);
        this.web.setVerticalScrollBarEnabled(true);
        this.web.requestFocus();
        this.web.getSettings().setDefaultTextEncodingName("utf-8");
        this.web.getSettings().setJavaScriptEnabled(true);

        this.web.loadUrl(url);

        this.web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        this.web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // help menu on action bar
        inflater.inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_help:
                // help

                // to display AlertDialog
                new AlertDialog.Builder(this)
                        .setPositiveButton(R.string.dialog_ok, null)
                        .setCancelable(false)
                        .setTitle(R.string.dialog_title_help)
                        .setMessage(R.string.help_web)
                        .show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
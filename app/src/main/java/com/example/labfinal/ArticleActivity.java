package com.example.labfinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public abstract class ArticleActivity extends AppCompatActivity {

    protected String id, sectionName, webTitle, webUrl;

    /* initialization */
    protected void init() {
        // receive article information
        Intent intent = getIntent();
        this.id = intent.getStringExtra(MyOpener.COL_ID);
        this.sectionName = intent.getStringExtra(MyOpener.COL_SECTION_NAME);
        this.webTitle = intent.getStringExtra(MyOpener.COL_WEB_TITLE);
        this.webUrl = intent.getStringExtra(MyOpener.COL_WEB_URL);

        TextView txtSection = findViewById(R.id.txtSection);
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtUrl = findViewById(R.id.txtUrl);

        txtSection.setText(this.sectionName);
        txtTitle.setText(this.webTitle);
        txtUrl.setText(this.webUrl);
        txtUrl.setText(this.webUrl);

        // to underline
        txtUrl.setPaintFlags(txtUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txtUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // view news article url webpage
                Intent intent1 = new Intent(ArticleActivity.this, WebActivity.class);
                intent1.putExtra(MyOpener.COL_WEB_URL, webUrl);
                startActivity(intent1);
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
                goHelp();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* View Help */
    protected abstract void goHelp();
}
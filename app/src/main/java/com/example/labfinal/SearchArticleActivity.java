package com.example.labfinal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class SearchArticleActivity extends ArticleActivity {
    private static final String TAG = "lab-final";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_article);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // display home button on action bar
        setTitle(R.string.title_article_info);

        // initialization
        init();

        findViewById(R.id.btnFavoritesAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add to favorites
                addFavorites();
            }
        });
    }

    @Override
    protected void goHelp() {
        // to display AlertDialog
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.dialog_ok, null)
                .setCancelable(false)
                .setTitle(R.string.dialog_title_help)
                .setMessage(R.string.help_search_article)
                .show();
    }

    /**
     *  Add to Favorites
     */
    private void addFavorites() {
        MyOpener dbOpener = new MyOpener(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        // Delete this article first if it's in favorites
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "=?", new String[] { this.id });

        // save to DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyOpener.COL_ID, this.id);
        contentValues.put(MyOpener.COL_SECTION_NAME, this.sectionName);
        contentValues.put(MyOpener.COL_WEB_TITLE, this.webTitle);
        contentValues.put(MyOpener.COL_WEB_URL, this.webUrl);
        contentValues.put(MyOpener.COL_MS, System.currentTimeMillis());

        long newId = db.insert(MyOpener.TABLE_NAME, null, contentValues);
        Log.d(TAG, "newId:" + newId);

        Toast.makeText(this, R.string.msg_favorites_add, Toast.LENGTH_SHORT).show();
    }
}

package com.example.labfinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    private static final String TAG = "lab-final";

    private Context context;

    private ArticleAdapter adapter;
    private ArrayList<Article> list;

    private TextView txtMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        this.list = new ArrayList<>();      // list initialization

        // import favorites
        loadFavorites();

        // list configuration
        ListView listView = view.findViewById(R.id.listView);
        this.adapter = new ArticleAdapter(this.list);
        listView.setAdapter(this.adapter);

        this.txtMessage = view.findViewById(R.id.txtMessage);

        // click on list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                // news article selection
                if (context != null) {
                    Article article = list.get(position);       // article information

                    // call article information activity
                    Intent intent = new Intent(context, FavoritesArticleActivity.class);
                    intent.putExtra(MyOpener.COL_ID, article.getId());
                    intent.putExtra(MyOpener.COL_SECTION_NAME, article.getSectionName());
                    intent.putExtra(MyOpener.COL_WEB_TITLE, article.getWebTitle());
                    intent.putExtra(MyOpener.COL_WEB_URL, article.getWebUrl());
                    startActivity(intent);
                }
            }
        });

        // long click on list item
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                // delete
                if (context != null) {
                    new AlertDialog.Builder(context)
                            .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // delete favorites
                                    delFavorites(position);
                                }
                            })
                            .setNegativeButton(R.string.dialog_no, null)
                            .setCancelable(false)
                            .setTitle(R.string.dialog_title_delete)
                            .setMessage(R.string.dialog_msg_favorites_delete)
                            .show();
                }

                return true;
            }
        });

        // have additional articles in favorites
        if (this.list.size() > 0) {
            this.txtMessage.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        this.context = null;
        super.onDetach();
    }

    /** Get Favorites from DB */
    private void loadFavorites() {
        if (this.context == null) {
            return;
        }

        MyOpener dbOpener = new MyOpener(this.context);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        // column item to get
        String[] columns = { MyOpener.COL_ID, MyOpener.COL_SECTION_NAME, MyOpener.COL_WEB_TITLE, MyOpener.COL_WEB_URL };

        // get favorites from DB (sort by newest)
        Cursor cursor = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, MyOpener.COL_MS + " DESC", null);

        int idColIndex = cursor.getColumnIndex(MyOpener.COL_ID);
        int sectionNameColIndex = cursor.getColumnIndex(MyOpener.COL_SECTION_NAME);
        int webTitleColIndex = cursor.getColumnIndex(MyOpener.COL_WEB_TITLE);
        int webUrlColIndex = cursor.getColumnIndex(MyOpener.COL_WEB_URL);

        while (cursor.moveToNext()) {
            String id = cursor.getString(idColIndex);
            String sectionName = cursor.getString(sectionNameColIndex);
            String webTitle = cursor.getString(webTitleColIndex);
            String webUrl = cursor.getString(webUrlColIndex);

            // add to list
            this.list.add(new Article(id, sectionName, webTitle, webUrl));
        }

        cursor.close();
    }

    /** Delete Favorites */
    private void delFavorites(int position) {
        if (this.context == null) {
            return;
        }

        MyOpener dbOpener = new MyOpener(this.context);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        // delete from DB
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "=?", new String[] { this.list.get(position).getId() });

        // delete selected item from list
        this.list.remove(position);

        // list update
        this.adapter.notifyDataSetChanged();

        // all favorites have been deleted
        if (this.list.size() == 0) {
            this.txtMessage.setVisibility(View.VISIBLE);
        }

        Toast.makeText(this.context, R.string.msg_favorites_del, Toast.LENGTH_SHORT).show();
    }
}

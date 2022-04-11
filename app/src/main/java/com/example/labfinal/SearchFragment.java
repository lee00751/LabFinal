package com.example.labfinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private static final String TAG = "lab-final";

    private Context context;

    private InputMethodManager imm;                 // required to hide keyboard

    private ArticleAdapter adapter;
    private ArrayList<Article> list;

    private EditText editKeyword;

    private LinearLayout laySearch;
    private ProgressBar progressBar;

    private TextView txtMessage;

    private static final String API_URL = "https://content.guardianapis.com/search";    // Guardian news api url
    private static final String API_KEY = "1fb36b70-1588-4259-b703-2570ea1fac6a";       // Guardian news api key

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        this.list = new ArrayList<>();      // list initialization

        ListView listView = view.findViewById(R.id.listView);
        this.adapter = new ArticleAdapter(this.list);
        listView.setAdapter(this.adapter);

        this.laySearch = view.findViewById(R.id.laySearch);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.txtMessage = view.findViewById(R.id.txtMessage);

        this.editKeyword = view.findViewById(R.id.editKeyword);
        if (getArguments() != null) {
            // keyword Value
            this.editKeyword.setText(getArguments().getString(MainActivity.KEYWORD));
        }

        if (this.context != null) {
            // required to hide keyboard
            this.imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        // click on list item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                // article selection
                if (context != null) {
                    Article article = list.get(position);       // article information

                    // call article information activity
                    Intent intent = new Intent(context, SearchArticleActivity.class);
                    intent.putExtra(MyOpener.COL_ID, article.getId());
                    intent.putExtra(MyOpener.COL_SECTION_NAME, article.getSectionName());
                    intent.putExtra(MyOpener.COL_WEB_TITLE, article.getWebTitle());
                    intent.putExtra(MyOpener.COL_WEB_URL, article.getWebUrl());
                    startActivity(intent);
                }
            }
        });

        // click the search button
        view.findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // search
                String keyword = editKeyword.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    // if there are no search words
                    Snackbar.make(view, R.string.msg_keyword_empty, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (context != null) {
                    // save keyword in SharedPreferences
                    SharedPreferences preferences = context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME,
                            context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(MainActivity.KEYWORD, keyword);
                    editor.apply();
                }

                if (imm != null) {
                    // hide keyboard
                    imm.hideSoftInputFromWindow(editKeyword.getWindowToken(), 0);
                }

                // hide search layout and view progressBar
                laySearch.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);

                // list clear
                list.clear();
                adapter.notifyDataSetChanged();
                txtMessage.setText(R.string.msg_loading);
                txtMessage.setVisibility(View.VISIBLE);

                // Execute AsyncTask class that downloads information
                // from Guardian news api and composes a list
                GuardianNewsApi api = new GuardianNewsApi();
                api.execute(API_URL, keyword);
            }
        });

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

    @Override
    public void onStop() {
        if (this.imm != null) {
            // hide keyboard
            this.imm.hideSoftInputFromWindow(this.editKeyword.getWindowToken(), 0);
        }
        super.onStop();
    }

    /* Downloads information from Guardian news api and composes a list */
    private class GuardianNewsApi extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            // strings => 0:url, 1:keyword

            boolean exist;

            try {
                String address = strings[0];
                address += "?api-key=" + API_KEY;                           // api key
                address += "&q=" + URLEncoder.encode(strings[1], "UTF-8");  // search word

                // Guardian news api connection
                URL url = new URL(address);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream response = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String result = sb.toString();

                urlConnection.disconnect();

                // convert string to json object
                JSONObject json = new JSONObject(result);
                Log.d(TAG, "json:" + result);

                JSONObject jsonObject = json.getJSONObject("response");

                // results json array
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                if (jsonArray.length() == 0) {
                    exist = false;
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        // article list item configuration
                        list.add(new Article(object.getString("id"), object.getString("sectionName"),
                                object.getString("webTitle"), object.getString("webUrl")));

                        // progress bar
                        publishProgress(i + 1, jsonArray.length());
                        Thread.sleep(50);
                    }
                    exist = true;
                }
            } catch (Exception e) {
                Log.d(TAG, "error:" + e.toString());
                exist = false;
            }

            return exist;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // values => 0:progress , 1:maximum

            if (values[0] == 1) {
                // If start, set the maximum
                progressBar.setMax(values[1]);
            }

            // progress bar
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                // have articles
                txtMessage.setVisibility(View.GONE);

                // list update
                adapter.notifyDataSetChanged();
            } else {
                // do not have articles
                txtMessage.setText(R.string.msg_list_empty);
            }

            // hide progress bar and show search layout
            laySearch.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}

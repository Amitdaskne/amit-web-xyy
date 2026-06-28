package com.amitweb.xyy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BookmarksActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        
        SharedPreferences prefs = getSharedPreferences("AmitWebPrefs", MODE_PRIVATE);
        Set<String> bookmarksSet = prefs.getStringSet("bookmarks", new HashSet<>());
        ArrayList<String> bookmarks = new ArrayList<>(bookmarksSet);
        
        ListView listView = findViewById(R.id.listBookmarks);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookmarks);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String url = bookmarks.get(position);
            Intent result = new Intent();
            result.putExtra("url", url);
            setResult(RESULT_OK, result);
            finish();
        });
        
        findViewById(R.id.btnClearBookmarks).setOnClickListener(v -> {
            prefs.edit().remove("bookmarks").apply();
            Toast.makeText(this, "Bookmarks cleared", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

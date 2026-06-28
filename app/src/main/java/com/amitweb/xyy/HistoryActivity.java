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

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        
        SharedPreferences prefs = getSharedPreferences("AmitWebPrefs", MODE_PRIVATE);
        Set<String> historySet = prefs.getStringSet("history", new HashSet<>());
        ArrayList<String> history = new ArrayList<>(historySet);
        
        ListView listView = findViewById(R.id.listHistory);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String item = history.get(position);
            String url = item.split("\\|")[0];
            Intent result = new Intent();
            result.putExtra("url", url);
            setResult(RESULT_OK, result);
            finish();
        });
        
        findViewById(R.id.btnClearHistory).setOnClickListener(v -> {
            prefs.edit().remove("history").apply();
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

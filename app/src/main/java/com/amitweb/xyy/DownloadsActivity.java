package com.amitweb.xyy;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;

public class DownloadsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        ArrayList<String> files = new ArrayList<>();
        
        if (downloadDir.exists() && downloadDir.listFiles() != null) {
            for (File file : downloadDir.listFiles()) {
                if (file.isFile()) {
                    files.add(file.getName() + " (" + file.length()/1024 + " KB)");
                }
            }
        }
        
        ListView listView = findViewById(R.id.listDownloads);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files);
        listView.setAdapter(adapter);
        
        if (files.isEmpty()) {
            Toast.makeText(this, "No downloads found", Toast.LENGTH_SHORT).show();
        }
    }
}

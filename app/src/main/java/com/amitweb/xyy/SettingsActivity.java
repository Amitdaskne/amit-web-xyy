package com.amitweb.xyy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        findViewById(R.id.btnSetHomepage).setOnClickListener(v -> {
            EditText input = new EditText(this);
            SharedPreferences prefs = getSharedPreferences("AmitWebPrefs", MODE_PRIVATE);
            input.setText(prefs.getString("homepage", "https://www.google.com"));
            new AlertDialog.Builder(this)
                .setTitle("Set Homepage")
                .setView(input)
                .setPositiveButton("Save", (d, i) -> {
                    String url = input.getText().toString();
                    prefs.edit().putString("homepage", url).apply();
                    Toast.makeText(this, "Homepage saved", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
        
        findViewById(R.id.btnClearCache).setOnClickListener(v -> {
            getSharedPreferences("AmitWebPrefs", MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "All data cleared!", Toast.LENGTH_SHORT).show();
        });
        
        findViewById(R.id.btnAbout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("About Amit Web XYY")
                .setMessage("Version 1.0\n\nA fast, feature-rich Android browser\nDeveloped with ❤️")
                .setPositiveButton("OK", null)
                .show();
        });
    }
}

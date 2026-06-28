package com.amitweb.xyy;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class IncognitoActivity extends AppCompatActivity {
    private WebView webView;
    private EditText urlBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incognito);
        
        webView = findViewById(R.id.incognitoWebView);
        urlBar = findViewById(R.id.incognitoUrlBar);
        ImageButton goButton = findViewById(R.id.incognitoGoButton);
        ImageButton closeButton = findViewById(R.id.incognitoClose);
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(false);
        webView.getSettings().setCacheMode(WebView.getSettings().LOAD_NO_CACHE);
        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setSavePassword(false);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                urlBar.setText(url);
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient());
        
        goButton.setOnClickListener(v -> {
            String url = urlBar.getText().toString();
            if (!url.startsWith("http") && !url.startsWith("https")) {
                if (url.contains(".")) {
                    url = "https://" + url;
                } else {
                    url = "https://www.google.com/search?q=" + url;
                }
            }
            webView.loadUrl(url);
        });
        
        closeButton.setOnClickListener(v -> finish());
        
        Toast.makeText(this, "🕶️ Incognito Mode - No history saved", Toast.LENGTH_LONG).show();
        webView.loadUrl("https://www.google.com");
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}

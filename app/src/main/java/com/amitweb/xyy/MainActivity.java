package com.amitweb.xyy;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private EditText urlBar;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private ImageButton btnBack, btnForward, btnRefresh, btnTabs, btnBookmark, btnHome;
    private SharedPreferences prefs;
    private static final int REQUEST_STORAGE = 100;
    private ArrayList<String> tabUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getSharedPreferences("AmitWebPrefs", MODE_PRIVATE);
        initViews();
        setupToolbar();
        setupWebView();
        setupPermissions();
        loadHomePage();
    }

    private void initViews() {
        webView = findViewById(R.id.webView);
        urlBar = findViewById(R.id.urlBar);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        btnForward = findViewById(R.id.btnForward);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnTabs = findViewById(R.id.btnTabs);
        btnBookmark = findViewById(R.id.btnBookmark);
        btnHome = findViewById(R.id.btnHome);
        
        btnBack.setOnClickListener(v -> { if (webView.canGoBack()) webView.goBack(); });
        btnForward.setOnClickListener(v -> { if (webView.canGoForward()) webView.goForward(); });
        btnRefresh.setOnClickListener(v -> webView.reload());
        btnTabs.setOnClickListener(v -> showTabManager());
        btnBookmark.setOnClickListener(v -> addBookmark());
        btnHome.setOnClickListener(v -> loadHomePage());
        
        urlBar.setOnEditorActionListener((v, actionId, event) -> {
            loadUrl(urlBar.getText().toString());
            return true;
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                urlBar.setText(url);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                urlBar.setText(url);
                saveHistory(url);
            }
            
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false;
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
            
            @Override
            public void onReceivedTitle(WebView view, String title) {
                getSupportActionBar().setTitle(title);
            }
        });
        
        swipeRefresh.setOnRefreshListener(() -> webView.reload());
    }

    private void loadUrl(String url) {
        if (url.isEmpty()) return;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (url.contains(".")) {
                url = "https://" + url;
            } else {
                url = "https://www.google.com/search?q=" + url;
            }
        }
        webView.loadUrl(url);
    }

    private void loadHomePage() {
        String home = prefs.getString("homepage", "https://www.google.com");
        webView.loadUrl(home);
        if (!tabUrls.contains(home)) {
            tabUrls.add(home);
        }
    }

    private void addBookmark() {
        String url = webView.getUrl();
        if (url != null && !url.equals("about:blank")) {
            Set<String> bookmarks = prefs.getStringSet("bookmarks", new HashSet<>());
            bookmarks.add(url);
            prefs.edit().putStringSet("bookmarks", bookmarks).apply();
            Toast.makeText(this, "⭐ Bookmark added!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveHistory(String url) {
        if (url == null || url.equals("about:blank")) return;
        Set<String> history = prefs.getStringSet("history", new HashSet<>());
        history.add(url + "|" + System.currentTimeMillis());
        if (history.size() > 200) {
            ArrayList<String> list = new ArrayList<>(history);
            while (list.size() > 200) list.remove(0);
            history = new HashSet<>(list);
        }
        prefs.edit().putStringSet("history", history).apply();
    }

    private void setupPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            }
        }
    }

    private void showTabManager() {
        if (tabUrls.isEmpty()) {
            tabUrls.add("https://www.google.com");
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📑 Tabs (" + tabUrls.size() + ")");
        
        String[] tabs = tabUrls.toArray(new String[0]);
        builder.setItems(tabs, (dialog, which) -> {
            webView.loadUrl(tabUrls.get(which));
        });
        
        builder.setPositiveButton("➕ New Tab", (d, i) -> {
            tabUrls.add("https://www.google.com");
            webView.loadUrl("https://www.google.com");
        });
        
        builder.setNeutralButton("🗑️ Close All", (d, i) -> {
            tabUrls.clear();
            tabUrls.add("https://www.google.com");
            webView.loadUrl("https://www.google.com");
            Toast.makeText(this, "All tabs closed", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Close", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_bookmarks) {
            startActivity(new Intent(this, BookmarksActivity.class));
        } else if (id == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
        } else if (id == R.id.action_downloads) {
            startActivity(new Intent(this, DownloadsActivity.class));
        } else if (id == R.id.action_incognito) {
            startActivity(new Intent(this, IncognitoActivity.class));
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_share) {
            shareUrl();
        } else if (id == R.id.action_desktop) {
            toggleDesktopMode();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareUrl() {
        String url = webView.getUrl();
        if (url != null && !url.equals("about:blank")) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(share, "Share URL"));
        }
    }

    private void toggleDesktopMode() {
        WebSettings settings = webView.getSettings();
        String ua = settings.getUserAgentString();
        if (ua.contains("Mobile")) {
            settings.setUserAgentString(ua.replace("Mobile", "Desktop"));
            Toast.makeText(this, "💻 Desktop Mode ON", Toast.LENGTH_SHORT).show();
        } else {
            settings.setUserAgentString(ua.replace("Desktop", "Mobile"));
            Toast.makeText(this, "📱 Mobile Mode ON", Toast.LENGTH_SHORT).show();
        }
        webView.reload();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}

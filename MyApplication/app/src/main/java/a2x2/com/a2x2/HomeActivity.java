package a2x2.com.a2x2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.File;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;
    private BroadcastReceiver receiver;
    private TextView tvInterner;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById();

        if (NetworkUtil.getConnectivityStatus(HomeActivity.this) == 0) {
            tvInterner.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            tvInterner.setVisibility(View.GONE);
            webView();
        }
        webView.getSettings().setJavaScriptEnabled(true);
        registerReceiver();
        webViewSettings();
        clearCookiesAndCache(HomeActivity.this);
    }

    private void webViewSettings() {
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);
    }

    public void clearCookiesAndCache(Context context) {
        File dir = getCacheDir();
        if (dir != null && dir.isDirectory()) {
            try {
                File[] children = dir.listFiles();
                if (children.length > 0) {
                    for (int i = 0; i < children.length; i++) {
                        File[] temp = children[i].listFiles();
                        for (int x = 0; x < temp.length; x++) {
                            temp[x].delete();
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Cache", "failed cache clean");
            }
        }

        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        } else {
            cookieManager.removeAllCookie();
        }
    }

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (NetworkUtil.getConnectivityStatus(HomeActivity.this) != 0) {
                    tvInterner.setVisibility(View.GONE);
                    webView();
                } else {
                    tvInterner.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        };
        IntentFilter intFilt = new IntentFilter("status");
        registerReceiver(receiver, intFilt);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void findViewById() {
        tvInterner = (TextView) findViewById(R.id.tv_not_internet);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        webView = (WebView) findViewById(R.id.webview);
        FloatingActionButton call = (FloatingActionButton) findViewById(R.id.call);
        call.setOnClickListener(this);
        initAlertDialog();
    }

    private void initAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.call, null);
        ImageView vivacell = dialogView.findViewById(R.id.vivacell);
        ImageView ucom = dialogView.findViewById(R.id.ucom);
        ImageView beeline = dialogView.findViewById(R.id.beeline);
        vivacell.setOnClickListener(this);
        ucom.setOnClickListener(this);
        beeline.setOnClickListener(this);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
    }

    private void webView() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        webView.loadUrl("http://2x2.am/");
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call: {
                alertDialog.show();
                break;
            }
            case R.id.vivacell: {
                call("077535254");
                break;
            }
            case R.id.ucom: {
                call("044535254");
                break;
            }
            case R.id.beeline: {
                call("099535256");
                break;
            }
        }
    }

    private void call(String number) {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        alertDialog.dismiss();
        startActivity(i);
    }

}

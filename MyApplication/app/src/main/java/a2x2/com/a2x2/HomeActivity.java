package a2x2.com.a2x2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.File;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private AlertDialog alertDialog;
    private BroadcastReceiver receiver;
    private TextView tvInternet;
    private static final Integer CALL = 0x2;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById();
        askForPermission(Manifest.permission.CALL_PHONE, CALL);
        isConnected();
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
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
        } else {
            CookieManager.getInstance().removeAllCookie();
        }
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new NetworkChangeReceiver(), intentFilter);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isConnected();
            }
        };
        IntentFilter intFilt = new IntentFilter("status");
        registerReceiver(receiver, intFilt);
    }

    private void isConnected() {
        if (NetworkUtil.getConnectivityStatus(HomeActivity.this) != 0) {
            tvInternet.setVisibility(View.GONE);
            webView();
        } else {
            tvInternet.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void findViewById() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        tvInternet = (TextView) findViewById(R.id.tv_not_internet);
        webView = (WebView) findViewById(R.id.webview);
        FloatingActionButton call = (FloatingActionButton) findViewById(R.id.call);
        call.setOnClickListener(this);
        initAlertDialog();

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(HomeActivity.this, R.color.colorGreen));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView();
            }
        });
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
                if (newProgress == 100) {
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    mSwipeRefreshLayout.setRefreshing(true);
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
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        alertDialog.dismiss();
        startActivity(i);
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, permission)) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}

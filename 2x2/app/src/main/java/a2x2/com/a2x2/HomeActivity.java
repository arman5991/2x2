package a2x2.com.a2x2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.clans.fab.FloatingActionButton;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webview;
    private ProgressBar progressBar;
    private AlertDialog alertDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById();
        webview.getSettings().setJavaScriptEnabled(true);
        webView();
    }

    private void findViewById() {
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        webview = (WebView) findViewById(R.id.webview);
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
        webview.setWebChromeClient(new WebChromeClient() {
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

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        webview.loadUrl("http://2x2.am/");
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
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

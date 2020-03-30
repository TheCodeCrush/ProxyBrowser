package com.wordpress.fruitything.browser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.ProxyConfig;
import androidx.webkit.ProxyController;
import androidx.webkit.WebViewFeature;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    EditText urlText;
    ImageButton homeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlText = findViewById(R.id.urlText);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        urlText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO)
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    webView.loadUrl("https://" + v.getText().toString());
                    v.setText("");
                }
                return false;
            }
        });
        ImageButton menuButton  = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.menuCL).getVisibility()==View.VISIBLE){
                    findViewById(R.id.menuCL).setVisibility(View.GONE);
                }
                else
                    findViewById(R.id.menuCL).setVisibility(View.VISIBLE);

            }
        });
        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("https://google.com");
            }
        });
        Button setButton = findViewById(R.id.setButton);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ipET = findViewById(R.id.ipET);
                EditText portET = findViewById(R.id.portET);
                setProxy(ipET.getText().toString(),portET.getText().toString());
                findViewById(R.id.menuCL).setVisibility(View.GONE);

            }
        });
        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ipET = findViewById(R.id.ipET);
                EditText portET = findViewById(R.id.portET);
                ipET.setText("");
                portET.setText("");
                resetProxy();
                findViewById(R.id.menuCL).setVisibility(View.GONE);
            }
        });
        //setProxy();
        if(savedInstanceState!=null){
            webView.restoreState(savedInstanceState);
        }
        else{
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.setBackgroundColor(Color.WHITE);

            webView.setWebViewClient(new onView());

            webView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    progressBar.setProgress(progress);
                    if(progress < 100 && progressBar.getVisibility() == ProgressBar.GONE){
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                    }
                    if(progress == 100){
                        progressBar.setVisibility(ProgressBar.GONE);

                    }

                }
            });
        }
        final SwipeRefreshLayout webViewSRL = findViewById(R.id.webViewSRL);
        webViewSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
                webViewSRL.setRefreshing(false);
            }
        });
    }
    public  class onView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url) {
            view.loadUrl(url);
            CookieManager.getInstance().setAcceptCookie(true);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);

    }
    private void setProxy(String ip, String port)
    {
        ProxyConfig proxyConfig = new ProxyConfig.Builder()
                .addProxyRule(ip+":"+port)
                .build();
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {

            }
        };
        Runnable listener = new Runnable() {
            @Override
            public void run() {

            }
        };
        if(WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE))
        {
            ProxyController.getInstance().setProxyOverride(proxyConfig, executor, listener);
            Log.e("Done","Proxy is set");
            Toast.makeText(this,"Proxy is Set",Toast.LENGTH_LONG).show();

        }
        else
            Log.e("Cant","No proxy");

    }
    private void resetProxy()
    {
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {

            }
        };
        Runnable listener = new Runnable() {
            @Override
            public void run() {

            }
        };
        ProxyController.getInstance().clearProxyOverride(executor, listener);

        Toast.makeText(this,"Proxy Reset",Toast.LENGTH_LONG).show();
    }
}

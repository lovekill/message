package com.hh.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.webkit.*;
import android.widget.Toast;

/**
 * Created by engine on 15/2/9.
 */
public class WebActivity extends Activity {
    private WebView mWebView;
    public static final String URL = "URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mWebView = new WebView(this);
        setContentView(mWebView);
        init();
        mWebView.loadUrl(getIntent().getStringExtra(URL));
    }

    @SuppressLint("JavascriptInterface")
    private void init() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.addJavascriptInterface(this, "zdsdkinterface");
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // mWebView.getSettings().setPluginsEnabled(true);
        mWebView.requestFocus();
        mWebView.getSettings().setUseWideViewPort(true);
        CookieManager.getInstance().setAcceptCookie(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("start url",url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("overried url",url);
                if (url.startsWith("tel:")) {
                    return true;
                }
                if (url.startsWith("mailto:")) {
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("finish url",url);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("error url",description+"|"+failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                // TODO Auto-generated method stub
                handler.proceed();// 接受证书
                super.onReceivedSslError(view, handler, error);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
            }
        });

    }
    @JavascriptInterface
    public void toast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void backgame(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                WebActivity.this.finish();
            }
        }) ;
    }
}

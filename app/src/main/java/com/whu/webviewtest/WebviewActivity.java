package com.whu.webviewtest;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class WebviewActivity extends AppCompatActivity {

    //传入的资源url
    private String resourceUrl = "file:///android_asset/test.html";
//    private String resourceUrl = "http://192.168.1.116:9111/render/exhibition_detail?type=0&id=31&url=http://www.toyota.com.cn/mobile/vehicles/";
//    private String resourceUrl = "http://192.168.1.116:9111/test.html";

    //WebView
    private FrameLayout mWebViewContainer;
    private WebView mWebView;

//    加载进度
//    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initData();

        initView();
    }

    private void initData() {
        String resource_url = getIntent().getStringExtra("resource_url");
        if (resource_url != null) {
            resourceUrl = resource_url;
        }
    }

    private void initView() {
//        progressDialog = new ProgressDialog(WebviewActivity.this);
//        progressDialog.setIndeterminate(false);
//        progressDialog.setTitle("加载中");
//        progressDialog.setMessage("正在加载中，请稍后");
//        progressDialog.setCancelable(true);
//        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                progressDialog.dismiss();
//            }
//        });
//        progressDialog.show();

        initWebView();

//        mWebView = findViewById(R.id.info_content_webview);
        MyWebChromeClient.initWebViewSettings(mWebView);

//        mWebView.loadUrl("file:///android_asset/html/business.html");
        mWebView.loadUrl(resourceUrl);
    }


    //初始化WebView
    private void initWebView() {
        mWebViewContainer = findViewById(R.id.webview_container);
        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(this);
        mWebView.setLayoutParams(params);

        //设置webview属性
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);

        //开启WebView调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebViewContainer.addView(mWebView);

        //设置WebViewClient类
        //WebViewClient是处理各种通知和请求事件的
        mWebView.setWebViewClient(new MyWebViewClient());

        //设置WebChromeClient类
        //辅助WebView处理JS的对话框，网站图标和网站标题等
        mWebView.setWebChromeClient(new MyWebChromeClient(this, mWebViewContainer));
    }


    //系统回退按钮监听
    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
            mWebViewContainer.removeAllViews();
            mWebViewContainer = null;
        }
    }
}

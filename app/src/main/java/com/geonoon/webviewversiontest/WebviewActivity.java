package com.geonoon.webviewversiontest;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebviewActivity extends AppCompatActivity {

    //传入的资源url
//    private String resourceUrl = "http://geonoon.com/mobile/";
//    private String resourceUrl = "file:///android_asset/test.html";
//    private String resourceUrl = "http://www.geonoon.com:9111/render/exhibition_detail?type=0&id=31&url=http://www.toyota.com.cn/mobile/vehicles/";
//    private String resourceUrl = "http://192.168.1.116:9111/render/exhibition_detail?type=0&id=31&url=http://www.toyota.com.cn/mobile/vehicles/";
    private String resourceUrl = "http://192.168.1.116:9111/test.html";

    //WebView
    private FrameLayout mWebViewContainer;
    private WebView mWebView;
    private WebSettings mWebSettings;

    //加载进度
    private ProgressDialog progressDialog;

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
        progressDialog = new ProgressDialog(WebviewActivity.this);
//        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("加载中");
        progressDialog.setMessage("正在加载中，请稍后");
        progressDialog.setCancelable(true);
//        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                progressDialog.dismiss();
//            }
//        });
        progressDialog.show();

        initWebView();

//        mWebView = findViewById(R.id.info_content_webview);
        initWebViewSettings();

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
        mWebView.setWebViewClient(new WebViewClient() {
//            打开网页不调用系统浏览器，在定义的WebView中显示
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog.show();


                view.loadUrl(url);
                Log.e("WebView", "SDK版本在24以前: " + url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                progressDialog.show();

                view.loadUrl(request.getMethod().toString());
                Log.e("WebView", "SDK版本在24以后: " + request.getUrl().toString());
                return true;
            }

            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("WebView","开始加载...");
            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();

                Log.e("WebView","结束加载...");
            }

            //加载页面资源时
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);

                Log.e("WebView","加载资源: " + url);
            }

//            //App里面使用webview控件的时候遇到了诸如404这类的错误的时候，若也显示浏览器里面的那种错误提示页面就显得很丑陋了，那么这个时候我们的app就需要加载一个本地的错误提示页面
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//
////                //步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
////                //步骤2：将该html文件放置到代码根目录的assets文件夹下
////                //步骤3：复写WebViewClient的onRecievedError方法
////
////                switch (errorCode) {
////                    //自定义不同的错误类型
////                    case 0:
////                        view.loadUrl("file:///android_assets/html/error_handle.html");
////                        break;
////                }
//            }

            //处理https请求
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();       //表示等待证书响应
//                handler.cancel();        //表示挂起连接，为默认方式
//                handler.handleMessage(null);     //其他处理
            }
        });

        //设置WebChromeClient类
        //辅助WebView处理JS的对话框，网站图标和网站标题等
        mWebView.setWebChromeClient(new WebChromeClient() {

            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.e("WebView", title);
            }

            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                String progress = newProgress + "%";
//                progressDialog.setProgress(newProgress);
                progressDialog.setMessage("已加载" + progress + "，请稍后...");
                Log.e("WebView", progress);
            }

            //JS弹出框
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(WebviewActivity.this)
                        .setTitle("JsAlert")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }

//            //JS确认框
//            @Override
//            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
//                return super.onJsConfirm(view, url, message, result);
//            }
//
//            //JS输入框
//            @Override
//            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                return super.onJsPrompt(view, url, message, defaultValue, result);
//            }
        });
    }

    //初始化WebView设置
    private void initWebViewSettings() {
        mWebSettings = mWebView.getSettings();

        //如果访问的页面中要与JS交互，则WebView必须设置支持JS
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口

        mWebSettings.setAllowFileAccess(true); //设置可以访问文件
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        mWebSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        mWebSettings.setBlockNetworkImage(true);        //设置是否不加载网络图片
        mWebSettings.setPluginState(WebSettings.PluginState.ON);               //是否启用插件(默认false，deprecated)

        //设置自适应屏幕
        mWebSettings.setUseWideViewPort(true);          //将图片调整到适合WebView的大小
        mWebSettings.setLoadWithOverviewMode(true);     //缩放至屏幕大小
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //设置缩放操作
        mWebSettings.setSupportZoom(true);      //支持缩放，默认为true.是下面的前提
        mWebSettings.setBuiltInZoomControls(true);     //设置内置的缩放控件.若为false，则该WebView不可缩放
        mWebSettings.setDisplayZoomControls(false);     //是否显示原生的缩放控件

        //优先使用缓存:
        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        mWebSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
//        mWebSettings.setAppCacheEnabled(true);//开启 Application Caches 功能

        //当加载html页面时，WebView会在/data/data包名目录下生成database和cache两个文件夹
        //请求的URL记录保存在WebViewCache.db，而URL的内容保存在WebViewCache文件夹下
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
        }
    }
}

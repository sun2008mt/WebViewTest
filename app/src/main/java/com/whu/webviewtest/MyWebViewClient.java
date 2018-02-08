package com.whu.webviewtest;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by marc on 2/6/18.
 */

public class MyWebViewClient extends WebViewClient {

//    private ProgressDialog progressDialog;

//    MyWebViewClient(ProgressDialog progressDialog) {
//        this.progressDialog = progressDialog;
//    }

    //            打开网页不调用系统浏览器，在定义的WebView中显示
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        progressDialog.show();

        return super.shouldOverrideUrlLoading(view, url);

////                view.loadUrl(url);
//                Log.e("WebView", "SDK版本在24以前: " + url);
//                return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//        progressDialog.show();

        ViewGroup container = (ViewGroup) view.getParent();
        int childCount = container.getChildCount();

        Log.e("*************", "*************");
        for (int i = 0; i < childCount; i++) {
            Log.e("WebView", "url地址: " + ((WebView) container.getChildAt(i)).getUrl());
        }
        Log.e("WebView", "容器中的WebView数量为： " + childCount);
        Log.e("*************", "*************");

        return super.shouldOverrideUrlLoading(view, request);

////                view.loadUrl(request.getMethod().toString());
//                Log.e("WebView", "SDK版本在24以后: " + request.getUrl().toString());
//                return false;
    }

    //设置加载前的函数
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.e("WebView", "开始加载...");
    }

    //设置结束加载函数
    @Override
    public void onPageFinished(WebView view, String url) {
//        progressDialog.dismiss();

        Log.e("WebView", "结束加载...");
    }

    //加载页面资源时
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);

        Log.e("WebView", "加载资源: " + url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        Log.e("WebView", description);

        switch (errorCode) {
            //自定义不同的错误类型

            //连接错误
            case WebViewClient.ERROR_CONNECT:
                view.loadUrl("file:///android_asset/html/error_handle.html");
                break;

            //连接超时
            case WebViewClient.ERROR_TIMEOUT:
                break;

            default:
                break;
        }
    }

    //App里面使用webview控件的时候遇到了诸如404这类的错误的时候，若也显示浏览器里面的那种错误提示页面就显得很丑陋了，那么这个时候我们的app就需要加载一个本地的错误提示页面
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        //步骤1：写一个html文件（error_handle.html），用于出错时展示给用户看的提示页面
        //步骤2：将该html文件放置到代码根目录的assets文件夹下
        //步骤3：复写WebViewClient的onRecievedError方法

        Log.e("WebView", error.getDescription().toString());


        switch (error.getErrorCode()) {
            //自定义不同的错误类型

            //连接错误
            case WebViewClient.ERROR_CONNECT:
                view.loadUrl("file:///android_asset/html/error_handle.html");
                break;

            //连接超时
            case WebViewClient.ERROR_TIMEOUT:
                view.loadUrl("file:///android_asset/html/error_handle.html");
                break;

            default:
                view.loadUrl("file:///android_asset/html/error_handle.html");
                break;
        }
    }

    //处理https请求
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();       //表示等待证书响应
//                handler.cancel();        //表示挂起连接，为默认方式
//                handler.handleMessage(null);     //其他处理
    }
}

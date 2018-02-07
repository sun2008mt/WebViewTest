package com.whu.webviewtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by marc on 2/6/18.
 */

public class MyWebChromeClient extends WebChromeClient {

    private Context context;

    private ViewGroup container;

    private ProgressDialog progressDialog;

    MyWebChromeClient(Context context, ViewGroup container, ProgressDialog progressDialog) {
        this.context = context;
        this.container = container;
        this.progressDialog = progressDialog;
    }

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

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        Log.e("WebView", "新窗口被创建...\n是否为对话框：" + isDialog + "\n是否是用户触发：" + isUserGesture);

        WebView childView = new WebView(context);
        initWebViewSettings(childView);
        childView.setWebChromeClient(this);
        childView.setWebViewClient(new MyWebViewClient(progressDialog));
        childView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(childView);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(childView);
        resultMsg.sendToTarget();
        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {

        int childCount = container.getChildCount();

        Log.e("*************", "*************");
        for (int i = 0; i < childCount; i++) {
            Log.e("WebView", ((WebView) container.getChildAt(i)).getUrl());
        }
        Log.e("WebView",  "容器中的WebView数量为： " + childCount);
        Log.e("*************", "*************");

        //如果WebView数量大于1，则关闭子WebView时移除控件
        if (childCount > 1) {
            container.removeViewAt(childCount - 1);
        }

        Log.e("WebView", "窗口被关闭...");
        Log.e("WebView",  "关闭后容器中的WebView数量为： " + container.getChildCount());

        //如果其他打开的WebView被关闭，表明登录成功，刷新第一个页面
        if (container.getChildCount() == 1) {
            ((WebView) container.getChildAt(0)).reload();
        }
    }

    //JS弹出框
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        new AlertDialog.Builder(context)
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

    //初始化WebView设置
    private void initWebViewSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();

        //如果访问的页面中要与JS交互，则WebView必须设置支持JS
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setSupportMultipleWindows(true);         //支持多窗口

        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
//        webSettings.setBlockNetworkImage(true);        //设置是否不加载网络图片
        webSettings.setPluginState(WebSettings.PluginState.ON);               //是否启用插件(默认false，deprecated)

        //设置自适应屏幕
        webSettings.setUseWideViewPort(true);          //将图片调整到适合WebView的大小
        webSettings.setLoadWithOverviewMode(true);     //缩放至屏幕大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //设置缩放操作
        webSettings.setSupportZoom(true);      //支持缩放，默认为true.是下面的前提
        webSettings.setBuiltInZoomControls(true);     //设置内置的缩放控件.若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false);     //是否显示原生的缩放控件

        //优先使用缓存:
        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);   //开启 database storage API 功能
//        webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能

        //当加载html页面时，WebView会在/data/data包名目录下生成database和cache两个文件夹
        //请求的URL记录保存在WebViewCache.db，而URL的内容保存在WebViewCache文件夹下
    }
}

package com.tc.autosearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 10;

    WebView webView;
    TextView tvClose;
    View viewRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        findViewById(R.id.btn_open_web).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (webView != null && webView.getParent() != null) {
//                    Toast.makeText(MainActivity.this, "已经打开了", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    gotoAppDetail();
                } else {
                    addWeb();
                }
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String key = intent.getStringExtra("key");
                key = "https://www.baidu.com/s?wd=" + key;
                LogUtil.logE(key);
                if (webView != null) {
                    webView.loadUrl(key);
                }
            }
        }, new IntentFilter(AutoSearchService.ACTION_KEY));
    }

    private void addWeb() {
        WindowManager windowManager = getWindowManager();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = 400;
        layoutParams.gravity = Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManager.addView(createWeb(), layoutParams);
    }

    private View createWeb() {
        if (viewRoot == null) {
            viewRoot = View.inflate(this, R.layout.layout_search, null);
            webView = (WebView) viewRoot.findViewById(R.id.webview);
            tvClose = (TextView) viewRoot.findViewById(R.id.tv_close_web);
            tvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWindowManager().removeView(viewRoot);
                }
            });

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
        }
        return viewRoot;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void gotoAppDetail() {
        try {
            if (!Settings.canDrawOverlays(this)) {
                showToast("请同意悬浮窗权限");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                addWeb();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("打开失败，稍后再试");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            addWeb();
        }
    }

    private void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}

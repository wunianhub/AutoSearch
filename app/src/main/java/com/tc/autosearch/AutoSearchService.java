package com.tc.autosearch;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tiancheng on 2018/1/16.
 */

public class AutoSearchService extends AccessibilityService {
    LocalBroadcastManager manager;
    public static final String ACTION_KEY = "action_key";
    final List<String> packageList = new ArrayList<>();

    public AutoSearchService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.logE("AutoSearchService create");
        manager = LocalBroadcastManager.getInstance(this);
        packageList.add("com.tc.hackwe");
        packageList.add("com.chongdingdahui.app");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        LogUtil.logE(event.toString());

        if (!checkPackage(event)) {
            return;
        }
        List<CharSequence> infos = event.getText();
        StringBuilder builder = new StringBuilder();
        for (CharSequence info : infos) {
            builder.append(info.toString());
        }
        LogUtil.logE(builder.toString());

        Intent data = new Intent(ACTION_KEY);
        data.putExtra("key", builder.toString());
        manager.sendBroadcast(data);
    }

    @Override
    public void onInterrupt() {
        LogUtil.logE("AutoSearchService interrupt");
    }

    private boolean checkPackage(AccessibilityEvent event) {
        for (String p : packageList) {
            if (p.equals(event.getPackageName().toString())) {
                return true;
            }
        }
        return false;
    }
}

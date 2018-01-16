package com.tc.autosearch;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

/**
 * Created by tiancheng on 2018/1/16.
 */

public class AutoSearchService extends AccessibilityService {
    LocalBroadcastManager manager;
    public static final String ACTION_KEY = "action_key";

    public AutoSearchService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.logE("AutoSearchService create");
        manager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!event.getPackageName().toString().equals("com.tc.hackwe")) {
            return;
        }
        LogUtil.logE("AutoSearchService onAccessibilityEvent");
        LogUtil.logE(event.toString());
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
}

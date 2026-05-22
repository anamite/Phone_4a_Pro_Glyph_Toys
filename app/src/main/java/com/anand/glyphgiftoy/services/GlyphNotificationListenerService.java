package com.anand.glyphgiftoy.services;

import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.anand.glyphgiftoy.GifGlyphToyService;
import com.anand.glyphgiftoy.data.RuleManager;
import com.anand.glyphgiftoy.models.GlyphRule;

public class GlyphNotificationListenerService extends NotificationListenerService {
    private static final String TAG = "GlyphNotifListener";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        Log.d(TAG, "Notification received from: " + packageName);

        GlyphRule rule = RuleManager.getInstance(this).getRuleForPackage(packageName);
        if (rule != null) {
            Log.d(TAG, "Matching rule found for " + packageName + "! Triggering animation " + rule.getAnimationIndex());
            triggerGlyphAnimation(rule);
        }
    }

    private void triggerGlyphAnimation(GlyphRule rule) {
        Intent intent = new Intent(this, GifGlyphToyService.class);
        intent.setAction(GifGlyphToyService.ACTION_TRIGGER_OVERRIDE);
        intent.putExtra(GifGlyphToyService.EXTRA_ANIM_INDEX, rule.getAnimationIndex());
        intent.putExtra(GifGlyphToyService.EXTRA_CUSTOM_ANIM_ID, rule.getCustomAnimationId());
        intent.putExtra(GifGlyphToyService.EXTRA_DURATION_SEC, rule.getDurationSec());
        intent.putExtra(GifGlyphToyService.EXTRA_BRIGHTNESS, rule.getBrightness());
        intent.putExtra(GifGlyphToyService.EXTRA_SCALE, rule.getScale());
        startService(intent);
    }
}
package com.anand.glyphgiftoy.models;

import java.util.UUID;

public class GlyphRule {
    private String id;
    private String packageName;
    private String appName;
    private int animationIndex;
    private String customAnimationId;
    private int durationSec;
    private int brightness;
    private int scale;
    private boolean enabled;

    public GlyphRule(String packageName, String appName, int animationIndex, String customAnimationId, int durationSec, int brightness, int scale) {
        this.id = UUID.randomUUID().toString();
        this.packageName = packageName;
        this.appName = appName;
        this.animationIndex = animationIndex;
        this.customAnimationId = customAnimationId;
        this.durationSec = durationSec;
        this.brightness = brightness;
        this.scale = scale;
        this.enabled = true;
    }

    public String getId() { return id; }
    public String getPackageName() { return packageName; }
    public String getAppName() { return appName; }
    public int getAnimationIndex() { return animationIndex; }
    public String getCustomAnimationId() { return customAnimationId; }
    public int getDurationSec() { return durationSec; }
    public int getBrightness() { return brightness; }
    public int getScale() { return scale; }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setAnimationIndex(int animationIndex) { this.animationIndex = animationIndex; }
    public void setCustomAnimationId(String customAnimationId) { this.customAnimationId = customAnimationId; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }
    public void setBrightness(int brightness) { this.brightness = brightness; }
    public void setScale(int scale) { this.scale = scale; }
}
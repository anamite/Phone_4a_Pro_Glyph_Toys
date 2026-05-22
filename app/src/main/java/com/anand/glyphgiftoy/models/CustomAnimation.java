package com.anand.glyphgiftoy.models;

import java.util.List;
import java.util.UUID;

public class CustomAnimation {
    private String id;
    private String name;
    private int frameCount;
    private List<CustomAnimationFrame> frames;

    public CustomAnimation(String name, int frameCount, List<CustomAnimationFrame> frames) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.frameCount = frameCount;
        this.frames = frames;
    }

    public void ensureId() {
        if (id == null) id = UUID.randomUUID().toString();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getFrameCount() { return frameCount; }
    public List<CustomAnimationFrame> getFrames() { return frames; }
}

package com.anand.glyphgiftoy.models;

public class DrawCommand {
    private String action; // clear, rect, circle, line, point
    private String color;  // #FFFFFF
    private float[] params;

    public DrawCommand(String action, String color, float[] params) {
        this.action = action;
        this.color = color;
        this.params = params;
    }

    public String getAction() { return action; }
    public String getColor() { return color; }
    public float[] getParams() { return params; }
}

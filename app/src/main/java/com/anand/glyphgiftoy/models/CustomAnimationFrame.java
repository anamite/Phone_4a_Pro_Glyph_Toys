package com.anand.glyphgiftoy.models;

import java.util.List;

public class CustomAnimationFrame {
    private List<DrawCommand> commands;

    public CustomAnimationFrame(List<DrawCommand> commands) {
        this.commands = commands;
    }

    public List<DrawCommand> getCommands() {
        return commands;
    }
}

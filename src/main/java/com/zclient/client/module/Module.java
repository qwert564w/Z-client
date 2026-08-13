package com.zclient.client.module;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private String name;
    private String description;
    private int bind;
    private Category category;
    private boolean enabled;
    private List<Object> settings = new ArrayList<>();

    public enum Category {
        COMBAT, RENDER, MOVEMENT, PLAYER, WORLD, NETWORK, MISC
    }

    public Module(String name, String description, int bind, Category category) {
        this.name = name;
        this.description = description;
        this.bind = bind;
        this.category = category;
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    protected void addSettings(Object... settings) {
        for (Object setting : settings) {
            this.settings.add(setting);
        }
    }

    public List<Object> getSettings() {
        return settings;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
}

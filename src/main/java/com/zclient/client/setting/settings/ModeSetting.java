package com.zclient.client.setting.settings;

public class ModeSetting {
    private String name;
    private String value;
    private String[] modes;

    public ModeSetting(String name, String defaultValue, String... modes) {
        this.name = name;
        this.value = defaultValue;
        this.modes = modes;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        for (String mode : modes) {
            if (mode.equals(value)) {
                this.value = value;
                return;
            }
        }
    }

    public String[] getModes() {
        return modes;
    }
}

package com.zclient.client.setting.settings;

public class NumberSetting {
    private String name;
    private double value;
    private double min;
    private double max;
    private double step;

    public NumberSetting(String name, double value, double min, double max, double step) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if (value >= min && value <= max) {
            this.value = value;
        }
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }
}

package com.qdd.narutofix.cap.soul;

import com.qdd.narutofix.Configs;

public class SoulEnergyData implements ISoulEnergyData {
    private double current;
    private double max;

    public SoulEnergyData() {
        this.max = Configs.soul.soulInitialMax;
        this.current = Math.min(Configs.soul.soulInitialCurrent, this.max);
    }

    @Override
    public double getCurrent() {
        return this.current;
    }

    @Override
    public void setCurrent(double value) {
        this.current = Math.min(Math.max(0.0, value), this.max);
    }

    @Override
    public void addCurrent(double delta) {
        this.setCurrent(this.current + delta);
    }

    @Override
    public double getMax() {
        return this.max;
    }

    @Override
    public void setMax(double value) {
        this.max = Math.max(0.0, value);
        this.setCurrent(this.current);
    }

    @Override
    public void addMax(double delta) {
        this.setMax(this.max + delta);
    }

    @Override
    public void copyFrom(ISoulEnergyData other) {
        this.current = other.getCurrent();
        this.max = other.getMax();
    }
}

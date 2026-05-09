package com.qdd.narutofix.cap.body;

import com.qdd.narutofix.Configs;

public class BodyEnergyData implements IBodyEnergyData {
    private double current;
    private double max;
    private int dataVersion;
    private int bloodlineAppliedVersion;
    
    public BodyEnergyData() {
        this.max = Configs.body.bodyInitialMax;
        this.current = Math.min(Configs.body.bodyInitialCurrent, this.max);
        this.dataVersion = 0;
        this.bloodlineAppliedVersion = 0;
    }
    
    @Override
    public double getCurrent() { return this.current; }
    
    @Override
    public void setCurrent(double value) {
        this.current = Math.min(Math.max(0.0, value), this.max);
    }
    
    @Override
    public void addCurrent(double delta) {
        this.setCurrent(this.current + delta);
    }
    
    @Override
    public double getMax() { return this.max; }
    
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
    public void copyFrom(IBodyEnergyData other) {
        this.current = other.getCurrent();
        this.max = other.getMax();
        this.dataVersion = other.getDataVersion();
        this.bloodlineAppliedVersion = other.getBloodlineAppliedVersion();
    }

    @Override
    public int getDataVersion() {
        return this.dataVersion;
    }

    @Override
    public void setDataVersion(int version) {
        this.dataVersion = version;
    }

    @Override
    public int getBloodlineAppliedVersion() {
        return this.bloodlineAppliedVersion;
    }

    @Override
    public void setBloodlineAppliedVersion(int version) {
        this.bloodlineAppliedVersion = version;
    }
}

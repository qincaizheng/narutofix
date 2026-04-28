package com.qdd.narutofix.cap.soul;

public interface ISoulEnergyData {
    double getCurrent();
    void setCurrent(double value);
    void addCurrent(double delta);

    double getMax();
    void setMax(double value);
    void addMax(double delta);

    void copyFrom(ISoulEnergyData other);
}

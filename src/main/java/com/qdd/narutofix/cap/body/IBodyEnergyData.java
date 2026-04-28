package com.qdd.narutofix.cap.body;

public interface IBodyEnergyData {
    double getCurrent();
    void setCurrent(double value);
    void addCurrent(double delta);
    
    double getMax();
    void setMax(double value);
    void addMax(double delta);
    
    void copyFrom(IBodyEnergyData other);
}

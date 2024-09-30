package com.example.robosmart.data.repository;

public class Robo {
    private float x, y, theta;

    public Robo(float x, float y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = (float) theta;
    }

    public Robo() {
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getTheta() {
        return theta;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setTheta(float theta) {
        this.theta = theta;
    }
}

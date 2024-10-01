package com.example.robosmart.data.repository;

public class Robo extends Coordenadas {
    private float theta; // Ângulo de orientação do robô

    public Robo(float x, float y, float theta) {
        super(x, y);
        this.theta = theta;
    }

    public Robo() {

    }

    public float getTheta() {
        return theta;
    }

    public void setTheta(float theta) {
        this.theta = theta;
    }
}

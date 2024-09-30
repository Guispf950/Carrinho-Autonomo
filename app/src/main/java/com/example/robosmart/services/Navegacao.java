package com.example.robosmart.services;

import android.util.Log;

import com.example.robosmart.data.repository.Obstaculo;
import com.example.robosmart.data.repository.Robo;

import java.util.List;

public class Navegacao {

    public void navegarParaObjetivo(Robo robo, float goalX, float goalY, List<Obstaculo> obstaculos) {

        float deltaX = goalX - robo.getX();
        float deltaY = goalY - robo.getY();
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if(distance > 8){
            NavegaçãoParaPonto navigate = new NavegaçãoParaPonto();
            navigate.navigateToPoint(robo, goalX, goalY, obstaculos);
        } else{
            Log.d("Sucesso", "Chegou no objetivo"); //margem de erro de 8cm
        }


    }
}

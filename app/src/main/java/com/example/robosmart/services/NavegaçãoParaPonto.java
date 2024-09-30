package com.example.robosmart.services;

import android.util.Log;

import com.example.robosmart.data.repository.ComunicacaoEsp;
import com.example.robosmart.data.repository.Obstaculo;
import com.example.robosmart.data.repository.Robo;

public class NavegaçãoParaPonto {

    public void navigateToPoint(Robo robo, float goalX, float goalY, Obstaculo[]obstaculos) {
        float deltaX = goalX - robo.getX();
        float deltaY = goalY - robo.getY();
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float angleToTarget = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));

        float angleToTurn = angleToTarget - robo.getTheta();

        float stepSize = 30.0f;

        while (distance > 5) {
            Log.d("NavigateToPoint", "Girar para posição: " + angleToTurn);

            // Atualiza as forças
            float[] forces = CamposPotenciaisArtificiais.atualizarForcas(robo,goalX, goalY, obstaculos);
            float forceX = forces[0];
            float forceY = forces[1];

            Log.d("NavigateToPoint", "Força X: " + forceX);
            Log.d("NavigateToPoint", "Força Y: " + forceY);

            // Calcula o ângulo resultante das forças
            float resultantAngle = (float) Math.toDegrees(Math.atan2(forceY, forceX));
            angleToTurn = resultantAngle - robo.getTheta();

            Log.d("NavigateToPoint", "Ângulo resultante: " + resultantAngle);
            Log.d("NavigateToPoint", "Ângulo do robô (Theta): " + robo.getTheta());

            // Movimento em frente
            if (distance < stepSize) {  // Se a distância for menor que o stepSize, anda a distância restante

                //chama o metodo que envia dados ao esp32
                ComunicacaoEsp comunicacaoEsp = new ComunicacaoEsp(angleToTurn, distance);
                comunicacaoEsp.execute();
               // andarFrente(distance);

            } else {

                //chama o metodo que envia dados ao esp32
                ComunicacaoEsp comunicacaoEsp = new ComunicacaoEsp(angleToTurn, stepSize);
                comunicacaoEsp.execute();
               // andarFrente(stepSize);

            }

            // Recalcula as distâncias para o objetivo
            deltaX = goalX - robo.getX();
            deltaY = goalY - robo.getY();
            distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        }
    }

}

package com.example.robosmart.services;

import com.example.robosmart.data.repository.ComunicacaoEsp;
import com.example.robosmart.data.repository.Obstaculo;
import com.example.robosmart.data.repository.Robo;

import java.util.List;

public class NavegaçãoParaPonto {

    float distance = 0f;

    public void navigateToPoint(Robo robo, float goalX, float goalY, List<Obstaculo> obstaculos) {
        float deltaX = goalX - robo.getX();
        float deltaY = goalY - robo.getY();
        distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        float angleToTarget = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));

        float angleToTurn = angleToTarget - robo.getTheta();

        float stepSize = 30.0f;

        // Atualiza as forças
        float[] forces = CamposPotenciaisArtificiais.atualizarForcas(robo,goalX, goalY, obstaculos);
        float forceX = forces[0];
        float forceY = forces[1];

        // Calcula o ângulo resultante das forças
        float resultantAngle = (float) Math.toDegrees(Math.atan2(forceY, forceX));
        angleToTurn = resultantAngle - robo.getTheta();
        // Movimento em frente

        if (distance < stepSize) {  // Se a distância for menor que o stepSize, anda a distância restante
        // chama o metodo que envia dados ao esp32
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

package com.example.robosmart.services;

import android.util.Log;

import com.example.robosmart.data.repository.Obstaculo;
import com.example.robosmart.data.repository.Robo;

import java.util.List;

public class CamposPotenciaisArtificiais {
    private static float k_rep = 5000;
    private static float d0 = 25; //zona de influencia obstaculos
    private static float dq = 1; //ao estar 1m do objetivo o modo de aproximação passa de conico para quadratico
    private static float maxSpeed = 100;
    private static float k_att_quadratico = 5.0f;
    private static float k_att_conico = 0.1f;

    public static float[] atualizarForcas(Robo robo, float goalX, float goalY, List<Obstaculo> obstaculos ) {
        float distToGoal = (float) Math.sqrt(Math.pow(goalX - robo.getX(), 2) + Math.pow(goalY - robo.getY(), 2));


        float forceAttConicoX = 0;
        float forceAttConicoY = 0;

        float forceAttQuadraticoX = 0;
        float forceAttQuadraticoY = 0;

        if (distToGoal < dq) {
            forceAttQuadraticoX = k_att_quadratico * (goalX - robo.getX()) * distToGoal;
            forceAttQuadraticoY = k_att_quadratico * (goalY - robo.getY()) * distToGoal;

        } else {
            forceAttConicoX = k_att_conico * (goalX - robo.getX());
            forceAttConicoY = k_att_conico * (goalY - robo.getY());
            Log.d("AtualizarForcas", "forceAttConicoX: " + forceAttConicoX);
            Log.d("AtualizarForcas", "forceAttConicoY: " + forceAttConicoY);
        }

        float forceRepX = 0;
        float forceRepY = 0;

        if (obstaculos.size() > 0) {
            for (int i = 0; i < obstaculos.size(); i++) {
                float obstDistX =  obstaculos.get(i).getX() - robo.getX();
                float obstDistY =  obstaculos.get(i).getY() - robo.getY();
                float obstDist = (float) Math.sqrt(Math.pow(obstDistX, 2) + Math.pow(obstDistY, 2));
                Log.d("AtualizarForcas", "obstDist: " + obstDist);

                // Calcula a força de repulsão apenas se o obstáculo estiver dentro da zona de influência
                if (obstDist < d0) {
                    float repulsion = k_rep * (1.0f / obstDist - 1.0f / d0) / (obstDist * obstDist);
                    forceRepX += repulsion * (obstDistX / obstDist);
                    Log.d("AtualizarForcas", "forceRepX: " + forceRepX);

                    forceRepY += repulsion * (obstDistY / obstDist);
                    Log.d("AtualizarForcas", "forceRepY: " + forceRepY);
                }
            }
        }

        float forceX = forceAttConicoX + forceAttQuadraticoX - forceRepX;
        float forceY = forceAttConicoY + forceAttQuadraticoY - forceRepY;

        float forceMagnitude = (float) Math.sqrt(forceX * forceX + forceY * forceY);
        if (forceMagnitude > maxSpeed) {
            forceX = (forceX / forceMagnitude) * maxSpeed;
            forceY = (forceY / forceMagnitude) * maxSpeed;
            Log.d("AtualizarForcas", "Forca X1: " + forceX);
            Log.d("AtualizarForcas", "Forca Y1: " + forceY);
        }

        float[] result = new float[2];
        result[0] = forceX ;
        result[1] = forceY ;
        //Log.d("AtualizarForcas", "Forca X2: " + result[0]);
        //Log.d("AtualizarForcas", "Forca Y2: " + result[1]);
        return result;
    }
}

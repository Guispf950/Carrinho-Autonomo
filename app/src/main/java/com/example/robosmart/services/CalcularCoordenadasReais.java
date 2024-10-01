package com.example.robosmart.services;

import static android.content.Context.MODE_PRIVATE;

import static java.security.AccessController.getContext;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.robosmart.data.repository.Objetivo;
import com.example.robosmart.data.repository.Obstaculo;
import com.example.robosmart.data.repository.Robo;

import java.util.ArrayList;
import java.util.List;
import com.example.robosmart.databinding.FragmentCapturaImagemBinding;
public class CalcularCoordenadasReais {



    public void calcularCoordenadaReais(Robo robo, Objetivo objetivo, List<Obstaculo> obstaculo) {
        Log.i("Touch", "TOQUEI");

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("DimensoesAmbiente", MODE_PRIVATE);
        String larguraReal = sharedPreferences.getString("comprimento", "");
        String alturaReal = sharedPreferences.getString("largura", "");

        float larguraRealfloat = Float.parseFloat(larguraReal);
        float alturaRealfloat = Float.parseFloat(alturaReal);

        int imageViewLargura = binding.imageViewImagem.getWidth();
        int imageViewAltura = binding.imageViewImagem.getHeight();
        int bitmapLargura = bitmap.getWidth();
        int bitmapAltura = bitmap.getHeight();

        float scaleX = (float) bitmapLargura / imageViewLargura;
        float scaleY = (float) bitmapAltura / imageViewAltura;

        float bitmapX = objetivo.getX() * scaleX;
        float bitmapY = objetivo.getY() * scaleY;

        float goalX = (bitmapX / bitmapLargura) * larguraRealfloat;
        float goalY = (bitmapY / bitmapAltura) * alturaRealfloat;

        objetivo.setX(goalX);
        objetivo.setY(goalY);

        float roboX = (robo.getX() / bitmapLargura) * larguraRealfloat;
        float roboY = (robo.getY() / bitmapAltura) * alturaRealfloat;
        robo.setX(roboX);
        robo.setY(roboY);

        for (int i = 0; i < obstaculo.size(); i++) {
            float xReal = (obstaculo.get(i).getX() / bitmapLargura) * larguraRealfloat;
            float yReal = (obstaculo.get(i).getY() / bitmapAltura) * alturaRealfloat;
            obstaculo.get(i).setX(xReal);
            obstaculo.get(i).setY(yReal);

            //obstaculo1 = new Obstaculo(xReal, yReal);
           //obstaculo.add(obstaculo1);

        }
        Log.i("Touch", "ANTES DA CHAMADA DA COMUNICAÇÃO");

    }


}

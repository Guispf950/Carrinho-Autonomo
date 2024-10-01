package com.example.robosmart.services;

import android.media.Image;
import android.os.Handler;
import android.util.Log;

import com.example.robosmart.data.repository.GetStatusTask;
import com.example.robosmart.data.repository.Objetivo;
import com.example.robosmart.data.repository.Obstaculo;
import com.example.robosmart.data.repository.Robo;
import com.example.robosmart.ui.viewmodel.ImagemViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Navegacao {
    Objetivo objetivo = new Objetivo();
    Robo robo = new Robo();
    List<Obstaculo> obstaculos = new ArrayList<>();
    boolean sucesso = false;
    String status = "desocupado";
    ImagemViewModel imageViewModel;

    public Navegacao(ImagemViewModel imageViewModel){
        this.imageViewModel = imageViewModel;
    }

    public void navegarParaObjetivo(Objetivo objetivo) {

        new ImagemViewModel().captureImage();
        HashMap<String, Object> lista = new HashMap<>();
        //identificar marcadores
        //lista = new DetectarMarcadores().detectarMarcadores();
        Robo roboAtualizado = (Robo) lista.get("robo");
        List<Obstaculo> obstaculosAtualizados = (List<Obstaculo>) lista.get("obstaculos");

        this.objetivo.setX(objetivo.getX());
        this.objetivo.setY(objetivo.getY());
        this.robo.setX(roboAtualizado.getX());
        this.robo.setY(roboAtualizado.getY());
        this.robo.setTheta(roboAtualizado.getTheta());
        this.obstaculos.addAll(obstaculosAtualizados);

        //atualiza coordendas
        new CalcularCoordenadasReais().calcularCoordenadaReais(this.robo, this.objetivo, this.obstaculos);
        NavegaçãoParaPonto navigate = new NavegaçãoParaPonto();

        while (!sucesso) {
            roboAtualizado = (Robo) lista.get("robo");
            obstaculosAtualizados = (List<Obstaculo>) lista.get("obstaculos");
            robo.setX(roboAtualizado.getX());
            robo.setY(roboAtualizado.getY());
            robo.setTheta(roboAtualizado.getTheta());
            obstaculos.clear(); // Limpar a lista de obstáculos
            obstaculos.addAll(obstaculosAtualizados);
            //atualiza coordendas
            new CalcularCoordenadasReais().calcularCoordenadaReais(robo, this.objetivo, obstaculos);

            if(status.equals("desocupado")){
                sucesso = navigate.navigateToPoint(robo, this.objetivo, obstaculos);
                new ImagemViewModel().captureImage();
                //identificar marcadores
                //lista = new DetectarMarcadores().detectarMarcadores();

            } else {
                checkStatusRepeatedly(); // Chama o método que inicia a verificação
            }

        }
        Log.d("Sucesso", "Chegou no objetivo"); //margem de erro de 8cm
        // LOGICA PARA QUANDO CHEGAR NO OBJETIVO

    }

    private void checkStatusRepeatedly() {
        // Criar um Handler para o atraso
        Handler handler = new Handler();
        // Criar um Runnable para checar o status
        Runnable statusCheckRunnable = new Runnable() {
            @Override
            public void run() {
                // Chama o método para verificar o status
                checkStatus();
                if(!status.equals("desocupado")){
                    // Agendar a próxima verificação em 3 segundos
                    handler.postDelayed(this, 3000);
                }

            }
        };

        // Iniciar a primeira chamada
        handler.post(statusCheckRunnable); // Chama a primeira vez
    }

    private void checkStatus() {
        new GetStatusTask(imageViewModel) {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                status = result;
                // Verifica se o status é "desocupado"
                if ("desocupado".equals(result.trim())) {
                    imageViewModel.captureImage(); // Captura a imagem se desocupado


                    Log.i("STATUS", "Estado mudou para desocupado, capturando imagem.");
                } else {
                    Log.i("STATUS", "STATUS ESTÁ OCUPADO: " + result);
                }
            }
        }.execute(); // Executa a tarefa
    }


}

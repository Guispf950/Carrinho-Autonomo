package com.example.robosmart.data.repository;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ComunicacaoEsp extends AsyncTask<Void, Void, String> {

    private String orientacao, distancia, goalX, goalY;
    private String orientacaoRobo;
    private String[] obstaculosX, obstaculosY;
    List<String> obstaculosXList = new ArrayList<>();
    List<String> obstaculosYList = new ArrayList<>();


    public ComunicacaoEsp(float orientacao, float distancia) {
        this.orientacao = String.valueOf(orientacao);
        this.distancia = String.valueOf(distancia);
    }

    @Override
    protected String doInBackground(Void... voids) {

        try {
            // URL do servidor ESP32
            URL url = new URL("http://192.168.71.71/enviar-coord");

            // Abre a conexão
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Monta os dados a serem enviados
            StringBuilder postData = new StringBuilder();
            postData.append("orientacao=").append(URLEncoder.encode(orientacao, "UTF-8"))
                    .append("&distancia=").append(URLEncoder.encode(distancia, "UTF-8"));

            //EXEMPLO DE ENVIO DE DADOS (RoboX=1&RoboY=2&RoboTheta=3&goalX=4&goalY=5&obstaculo1X=6&obstaculo1Y=7&obstaculo2X=8&obstaculo2Y=9)
            // Envia os dados
            OutputStream os = connection.getOutputStream();
            os.write(postData.toString().getBytes());
            os.flush();
            os.close();

            Log.i("Comunicação ESP", "Dados enviados: " + postData.toString());

            // Recebe a resposta do servidor
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i("Acerto", "Acerto ao enviar os dados. Código: ");
                return "Dados enviados com sucesso!";
            } else {
                Log.i("Erro", "Erro ao enviar os dados. Código: ");
                return "Erro ao enviar os dados. Código: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }



        /*try {
            // URL do servidor ESP32
            URL url = new URL("http://192.168.71.71/enviar-coord");

            // Abre a conexão
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Monta os dados a serem enviados
            StringBuilder postData = new StringBuilder();
            postData.append("roboX=").append(URLEncoder.encode(xRobo, "UTF-8"))
                    .append("&roboY=").append(URLEncoder.encode(yRobo, "UTF-8"))
                    .append("&roboTheta=").append(URLEncoder.encode(orientacaoRobo, "UTF-8"))
                    .append("&goalX=").append(URLEncoder.encode(goalX, "UTF-8"))
                    .append("&goalY=").append(URLEncoder.encode(goalY, "UTF-8"));

            // Adiciona os obstáculos
            for (int i = 0; i < obstaculosX.length; i++) {
                postData.append("&obstaculo").append(i + 1).append("X=").append(URLEncoder.encode(obstaculosX[i], "UTF-8"))
                        .append("&obstaculo").append(i + 1).append("Y=").append(URLEncoder.encode(obstaculosY[i], "UTF-8"));
            }
            //EXEMPLO DE ENVIO DE DADOS (RoboX=1&RoboY=2&RoboTheta=3&goalX=4&goalY=5&obstaculo1X=6&obstaculo1Y=7&obstaculo2X=8&obstaculo2Y=9)
            // Envia os dados
            OutputStream os = connection.getOutputStream();
            os.write(postData.toString().getBytes());
            os.flush();
            os.close();

            Log.i("Comunicação ESP", "Dados enviados: " + postData.toString());

            // Recebe a resposta do servidor
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i("Acerto", "Acerto ao enviar os dados. Código: ");
                return "Dados enviados com sucesso!";
            } else {
                Log.i("Erro", "Erro ao enviar os dados. Código: ");
                return "Erro ao enviar os dados. Código: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        } */


    }

    @Override
    protected void onPostExecute(String result) {
        // Aqui você pode atualizar a interface do usuário com o resultado da requisição
        // Exemplo: Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}

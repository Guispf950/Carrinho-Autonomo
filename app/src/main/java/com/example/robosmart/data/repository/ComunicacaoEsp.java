package com.example.robosmart.data.repository;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComunicacaoEsp extends AsyncTask<Void, Void, String> {

    private String xRobo, yRobo , goalX, goalY;
    private String orientacaoRobo;
    private String[] obstaculosX, obstaculosY;
    List<String> obstaculosXList = new ArrayList<>();
    List<String> obstaculosYList = new ArrayList<>();


    public ComunicacaoEsp(float xRobo, float yRobo, double orientacaoRobo,
                          float goalX, float goalY, float[] obstaculosX, float[] obstaculosY) {
        this.xRobo = String.valueOf(xRobo);
        this.yRobo = String.valueOf(yRobo);
        this.orientacaoRobo = String.valueOf(orientacaoRobo);
        this.goalX = String.valueOf(goalX);
        this.goalY = String.valueOf(goalY);
        for(int i = 0 ; i < obstaculosX.length; i++){
            obstaculosXList.add(String.valueOf(obstaculosX[i]));
            obstaculosYList.add(String.valueOf(obstaculosY[i]));
        }
        this.obstaculosX = obstaculosXList.toArray(new String[0]);
        this.obstaculosY = obstaculosYList.toArray(new String[0]);
    }

    @Override
    protected String doInBackground(Void... voids) {

        Log.i("Comunicação ESP", "Xrobo" + this.xRobo + "Obstaculo: " + this.obstaculosY[0]);

        try {
            // URL do servidor ESP32
            URL url = new URL("http://192.168.6.71/enviar-coord");

            // Abre a conexão
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // Monta os dados a serem enviados
            StringBuilder postData = new StringBuilder();
            postData.append("roboX=").append(xRobo)
                    .append("&roboY=").append(yRobo)
                    .append("&roboTheta=").append(orientacaoRobo)
                    .append("&goalX=").append(goalX)
                    .append("&goalY=").append(goalY);

            // Adiciona os obstáculos
            for (int i = 0; i < obstaculosX.length; i++) {
                postData.append("&obstaculo").append(i + 1).append("X=").append(obstaculosX[i])
                        .append("&obstaculo").append(i + 1).append("Y=").append(obstaculosY[i]);
            }
            //EXEMPLO DE ENVIO DE DADOS (RoboX=1&RoboY=2&RoboTheta=3&goalX=4&goalY=5&obstaculo1X=6&obstaculo1Y=7&obstaculo2X=8&obstaculo2Y=9)
            // Envia os dados
            OutputStream os = connection.getOutputStream();
            os.write(postData.toString().getBytes());
            os.flush();
            os.close();

            Log.i("Comunicação ESP", "FOI");

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
    }

    @Override
    protected void onPostExecute(String result) {
        // Aqui você pode atualizar a interface do usuário com o resultado da requisição
        // Exemplo: Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
    }
}

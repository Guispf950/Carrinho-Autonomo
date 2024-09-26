package com.example.robosmart.data.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ComunicacaoEspCam extends AsyncTask<Void, Void, Bitmap> {

    private ImageView imageView;
    private OnBitmapDownloadedListener listener;

    public interface OnBitmapDownloadedListener {
        void onBitmapDownloaded(Bitmap bitmap);
    }

    public ComunicacaoEspCam(OnBitmapDownloadedListener listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap foto = null;
        try {
            URL url = new URL("http://192.168.141.214/obter-foto");// alterar para /obter-foto
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

            conexao.setReadTimeout(5000);
            conexao.setConnectTimeout(5000);
            conexao.setRequestMethod("GET");
            conexao.setDoInput(true);
            conexao.connect();

            int responseCode = conexao.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = conexao.getInputStream();
                foto = BitmapFactory.decodeStream(in);
            } else {
                Log.e("ComunicacaoESPCAM", "Erro na conexão. Código de resposta: " + responseCode);
            }
        } catch (Exception e) {
            Log.e("ComunicacaoESPCAM", "Erro ao baixar a foto: " + e.getMessage());
        }
        return foto;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (listener != null) {
            listener.onBitmapDownloaded(bitmap);
        }
    }

}

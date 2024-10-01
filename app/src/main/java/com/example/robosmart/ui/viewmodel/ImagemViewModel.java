package com.example.robosmart.ui.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.robosmart.data.repository.ComunicacaoEspCam;
import com.example.robosmart.ui.view.CapturaImagemFragment;

import java.io.Closeable;

public class ImagemViewModel extends ViewModel {
    private final MutableLiveData<Bitmap> imageBitmap = new MutableLiveData<>();

    public LiveData<Bitmap> getImageBitmap() {
        return imageBitmap;
    }

    public void captureImage() {
        ComunicacaoEspCam comunicacaoEspCam = new ComunicacaoEspCam(new ComunicacaoEspCam.OnBitmapDownloadedListener() {
            @Override
            public void onBitmapDownloaded(Bitmap bitmap) {

                imageBitmap.postValue(bitmap);
            }
        });
        comunicacaoEspCam.execute();
    }
}

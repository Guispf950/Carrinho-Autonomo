package com.example.robosmart.ui.view;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.robosmart.R;
import com.example.robosmart.data.repository.ComunicacaoEsp;
import com.example.robosmart.data.repository.GetStatusTask;
import com.example.robosmart.databinding.FragmentCapturaImagemBinding;
import com.example.robosmart.ui.viewmodel.ImagemViewModel;
import com.example.robosmart.utils.TipoFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CapturaImagemFragment extends Fragment {
    private TextInputEditText editTextIdRobo;
    private TextInputEditText editTextComprimento;
    private TextInputEditText editTextLargura;
    private Spinner spinner;
    private Bitmap bitmap;
    private  float xBitmapRobo;
    private  float yBitmapRobo;
    private  double thetaZ;
    private float [] xObstaculo;
    private float [] yObstaculo;
    private List<Float> xObstaculoList = new ArrayList<>();
    private List<Float> yObstaculoList = new ArrayList<>();
    private String status = "desocupado";
    float goalX ,goalY ,roboX, roboY;
    float bitmapX = 0;
    float bitmapY = 0;
    double roboTheta = thetaZ;
    Float xObstaculo1[];
    Float yObstaculo1[];

    private FragmentCapturaImagemBinding binding;
    private TipoFragment tipoFragment;
    private ImagemViewModel imageViewModel;

    public static CapturaImagemFragment newInstance(TipoFragment tipo){
        CapturaImagemFragment fragment = new CapturaImagemFragment();
        fragment.tipoFragment = tipo;
        return fragment;
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCapturaImagemBinding.inflate(inflater, container, false);

        imageViewModel = new ViewModelProvider(requireActivity()).get(ImagemViewModel.class);

        imageViewModel.getImageBitmap().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmapImagem) {
                if(bitmapImagem != null){
                    bitmap = bitmapImagem;
                    binding.imageViewImagem.setImageBitmap(bitmap);
                    detectarMarcadorAruco(bitmap);
                    if(status.equals("desocupado")){
                        if(bitmapX != 0 && bitmapY != 0){
                            Log.i("STATUS2: ", "Status: "+ status.toString());
                            calculateRealCoordinates(xBitmapRobo, yBitmapRobo, thetaZ, bitmapX, bitmapY, xObstaculoList, yObstaculoList);
                        }

                    }
                }else{
                    binding.imageViewImagem.setImageResource(R.drawable.nenhuma_imagem_capturada);
                }
            }
        });


        binding.imageViewImagem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX();
                    float y = event.getY();
                    Log.i("ROBO", "Robo X:" + xBitmapRobo + "Robo Y: " + yBitmapRobo);
                    Log.i("OBSTACULO", "Obstaculo X: " + xObstaculo[0] + "Obstaculo Y: " + yObstaculo[0]);
                    calculateRealCoordinates(xBitmapRobo, yBitmapRobo, thetaZ, x, y, xObstaculoList, yObstaculoList);



                    view.performClick(); // Importante para acessibilidade
                    return true;
                }
                return false;
            }
        });

        binding.buttonCapturarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewModel.captureImage();
                //detectarMarcadorAruco(bitmap);
            }
        });

        binding.imageViewConfiguracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialog(tipoFragment);
            }
        });

        binding.fabFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), ImagemFullScreenActivity.class);
                startActivity(it);
            }
        });

        return binding.getRoot();
    }

    private void calculateRealCoordinates(float xBitmapRobo, float yBitmapRobo, double thetaZ, float x, float y, List<Float> xObstaculo, List<Float> yObstaculo) {
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

        bitmapX = x * scaleX;
        bitmapY = y * scaleY;

        goalX = (bitmapX / bitmapLargura) * larguraRealfloat;
        goalY = (bitmapY / bitmapAltura) * alturaRealfloat;
        roboX = (xBitmapRobo / bitmapLargura) * larguraRealfloat;
        roboY = (yBitmapRobo / bitmapAltura) * alturaRealfloat;
        roboTheta = thetaZ;

        Float[] xObstaculo1 = new Float[xObstaculo.size()];
        Float[] yObstaculo1 = new Float[yObstaculo.size()];
        for (int i = 0; i < xObstaculo.size(); i++) {
            xObstaculo1[i] = (xObstaculo.get(i) / bitmapLargura) * larguraRealfloat;
            yObstaculo1[i] = (yObstaculo.get(i) / bitmapAltura) * alturaRealfloat;
        }
        Log.i("Touch", "ANTES DA CHAMADA DA COMUNICAÇÃO");

        Log.i("Touch", "roboX: " + roboX + " roboY: " + roboY + " roboTheta: " + roboTheta + " goalX: " + goalX + " goalY: " + goalY);

        ComunicacaoEsp comunicacaoEsp = new ComunicacaoEsp(roboX, roboY, roboTheta, goalX, goalY, xObstaculo1, yObstaculo1);
        comunicacaoEsp.execute();
        // Iniciar a verificação de status
        checkStatusRepeatedly(); // Chama o método que inicia a verificação

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
                // Agendar a próxima verificação em 3 segundos
                handler.postDelayed(this, 3000);
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
                Log.i("STATUS", "Status recebido: " + result);

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


    private void detectarMarcadorAruco(Bitmap bitmap) {
        Log.i("Bitmap", "Bitmap: " + bitmap.toString());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Converter Bitmap para Mat
        Mat mat = new Mat(height,width, CvType.CV_8UC4);
        org.opencv.android.Utils.bitmapToMat(bitmap, mat);

        // Converter a imagem para escala de cinza
        Mat grayMat = new Mat();
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

        Dictionary dictionary = Aruco.getPredefinedDictionary(definirMarcadorEscolhido());
        Log.i("Dicionario", "Dicionario: "+ dictionary);

        DetectorParameters parameters = DetectorParameters.create();

        // Listas para armazenar os cantos e IDs detectados
        List<Mat> corners = new ArrayList<>();
        Mat ids = new Mat();

        // Detectar os marcadores ArUco
        Aruco.detectMarkers(grayMat, dictionary, corners, ids, parameters);

        if (ids.total() > 0) {
            // Parâmetros da câmera (novos parâmetros de calibração e distorção)
            Mat cameraMatrix = new Mat(3, 3, CvType.CV_64FC1);
            cameraMatrix.put(0, 0, 1893.25899, 0, 836.453929, 0, 1896.27302, 609.898826, 0, 0, 1); // Nova matriz de calibração

            MatOfDouble distCoeffs = new MatOfDouble();
            distCoeffs.fromArray(-0.304195144, 6.71272605, 0.0105837198, 0.00208301339, -42.8677103); // Novos coeficientes de distorção

            float markerLength = 0.1f; // Ajuste para o tamanho real do marcador

            Mat rvecs = new Mat(); // Vetores de rotação
            Mat tvecs = new Mat(); // Vetores de translação
            Aruco.estimatePoseSingleMarkers(corners, markerLength, cameraMatrix, distCoeffs, rvecs, tvecs);

            StringBuilder idsText = new StringBuilder("Marcadores Detectados: ");
            StringBuilder poseText = new StringBuilder();

            for (int i = 0; i < ids.total(); i++) {
                // Acessar o ID de cada marcador
                int markerId = (int) ids.get(i, 0)[0];
                idsText.append(markerId).append(" ");

                // Acessar o vetor de rotação e translação de cada marcador
                double[] rvec = rvecs.get(i, 0);
                double[] tvec = tvecs.get(i, 0);


                // Projeção do centro do marcador (0, 0, 0) para coordenadas 2D
                MatOfPoint3f objectPoints = new MatOfPoint3f(new Point3(0.0, 0.0, 0.0));
                MatOfPoint2f imagePoints = new MatOfPoint2f();

                Calib3d.projectPoints(objectPoints, new MatOfDouble(rvec), new MatOfDouble(tvec),
                        cameraMatrix, distCoeffs, imagePoints);

                xObstaculo = new float[(int) ids.total()];
                yObstaculo = new float[(int) ids.total()];

                if(markerId == pegarIdRobo()){
                    // Acessar as coordenadas 2D projetadas no MatOfPoint2f
                    Point[] projectedPoints = imagePoints.toArray();
                    xBitmapRobo = (float) projectedPoints[0].x;
                    yBitmapRobo = (float) projectedPoints[0].y;

                }else{
                    Point[] projectedPoints = imagePoints.toArray();
                    xObstaculo[i] = (float) projectedPoints[0].x;
                    yObstaculo[i] = (float) projectedPoints[0].y;
                    xObstaculoList.add(xObstaculo[i]);
                    yObstaculoList.add(yObstaculo[i]);

                }


                // Converter rvec em uma matriz de rotação
                Mat rotationMatrix = new Mat();
                Calib3d.Rodrigues(new MatOfDouble(rvec), rotationMatrix);

                // Extrair ângulos de Euler (pitch, yaw, roll) da matriz de rotação
                double thetaX = Math.atan2(rotationMatrix.get(2, 1)[0], rotationMatrix.get(2, 2)[0]);
                double thetaY = Math.atan2(-rotationMatrix.get(2, 0)[0],
                        Math.sqrt(Math.pow(rotationMatrix.get(2, 1)[0], 2) + Math.pow(rotationMatrix.get(2, 2)[0], 2)));

                if(markerId == pegarIdRobo()){
                    thetaZ = Math.atan2(rotationMatrix.get(1, 0)[0], rotationMatrix.get(0, 0)[0]);
                    thetaZ = Math.toDegrees(thetaZ);
                }


                // Converta os ângulos de radianos para graus, se necessário
                thetaX = Math.toDegrees(thetaX);
                thetaY = Math.toDegrees(thetaY);


                // Adicionar a posição e rotação ao texto de saída
                poseText.append(String.format("Marcador %d - Posição (x, y, z): [%.2f, %.2f, %.2f]\nPosição Bitmap (x, y): [%.2f, %.2f]\nRotação (pitch, yaw, roll): [%.2f, %.2f, %.2f]\n",
                        markerId, tvec[0], tvec[1], tvec[2], xBitmapRobo, yBitmapRobo, thetaX, thetaY, thetaZ));

                // Desenhar os eixos de cada marcador (opcional)
                drawCustomAxis(mat, cameraMatrix, distCoeffs, rvecs.row(i), tvecs.row(i), 0.05f);
            }

            // Exibir a imagem com os eixos desenhados
            Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            org.opencv.android.Utils.matToBitmap(mat, resultBitmap);
            binding.imageViewImagem.setImageBitmap(resultBitmap);

        }

    }

    private int pegarIdRobo() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("IdRobo", MODE_PRIVATE);
        String idRobo = sharedPreferences.getString("id", "");
        return  Integer.parseInt(idRobo);
    }

    public void drawCustomAxis(Mat image, Mat cameraMatrix, MatOfDouble distCoeffs, Mat rvec, Mat tvec, float length) {
        // Define os pontos dos eixos no espaço 3D
        MatOfPoint3f axisPoints = new MatOfPoint3f(
                new Point3(0, 0, 0),  // Origem
                new Point3(length, 0, 0),  // Eixo X
                new Point3(0, length, 0),  // Eixo Y
                new Point3(0, 0, length)   // Eixo Z
        );

        // Projeta os pontos 3D para a imagem 2D
        MatOfPoint2f imagePoints = new MatOfPoint2f();
        Calib3d.projectPoints(axisPoints, rvec, tvec, cameraMatrix, distCoeffs, imagePoints);

        // Converte os pontos projetados para um array de pontos 2D
        Point[] pts = imagePoints.toArray();

        // Desenha os eixos com cores personalizadas
        Imgproc.line(image, pts[0], pts[1], new Scalar(255, 0, 0), 10);  // Eixo X em azul
        Imgproc.line(image, pts[0], pts[2], new Scalar(0, 255, 0), 2);  // Eixo Y em verde
        Imgproc.line(image, pts[0], pts[3], new Scalar(0, 0, 255), 2);  // Eixo Z em vermelho
    }


    private int definirMarcadorEscolhido() {
        SharedPreferences preferences = getContext().getSharedPreferences("DicionarioDeMarcadores", MODE_PRIVATE);
        String selectedItem = preferences.getString("dicionario", null);
        switch (selectedItem) {
            case "ARUCO_ORIGINAL":
                return Aruco.DICT_ARUCO_ORIGINAL;
            case "4X4_50":
                return Aruco.DICT_4X4_50;
            case "4X4_100":
                return Aruco.DICT_4X4_100;
            case "4X4_250":
                return Aruco.DICT_4X4_250;
            case "4X4_1000":
                return Aruco.DICT_4X4_1000;
            case "5X5_50":
                return Aruco.DICT_5X5_50;
            case "5X5_100":
                return Aruco.DICT_5X5_100;
            case "5X5_250":
                return Aruco.DICT_5X5_250;
            case "5X5_1000":
                return Aruco.DICT_5X5_1000;
            case "6X6_50":
                return Aruco.DICT_6X6_50;
            case "6X6_100":
                return Aruco.DICT_6X6_100;
            case "6X6_250":
                return Aruco.DICT_6X6_250;
            case "6X6_1000":
                return Aruco.DICT_6X6_1000;
            case "7X7_50":
                return Aruco.DICT_7X7_50;
            case "7X7_100":
                return Aruco.DICT_7X7_100;
            case "7X7_250":
                return Aruco.DICT_7X7_250;
            case "7X7_1000":
                return Aruco.DICT_7X7_1000;
            default:
                return -Aruco.DICT_ARUCO_ORIGINAL;}
    }


    private void mostrarDialog(TipoFragment tipoFragment) {
        LayoutInflater inflater = this.getLayoutInflater();
        View viewDialogCustomizada = null;
        if(tipoFragment == TipoFragment.TRACAR_ROTA){
            viewDialogCustomizada = inflater.inflate(R.layout.dialog_tracar_rota, null);
        } else if (tipoFragment == TipoFragment.DEFINIR_OBJETIVO) {
            viewDialogCustomizada = inflater.inflate(R.layout.dialog_definir_objetivo, null);
            editTextIdRobo = viewDialogCustomizada.findViewById(R.id.textInputEditText_idMarcador);
            configurarIdRobo(editTextIdRobo);
        }

        editTextComprimento = viewDialogCustomizada.findViewById(R.id.textInputEditText_comprimento);
        editTextLargura = viewDialogCustomizada.findViewById(R.id.textInputEditText_largura);
        configurarEditText(editTextComprimento, editTextLargura);
        spinner = viewDialogCustomizada.findViewById(R.id.spinner_dicionarios);
        configurarSpinner(spinner);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyleCustomizado);
        builder.setView(viewDialogCustomizada);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();


        editTextComprimento.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable endDrawable = editTextComprimento.getCompoundDrawablesRelative()[2];
                    if (endDrawable != null && event.getRawX() >= (editTextComprimento.getRight() - endDrawable.getBounds().width())) {
                        habilitarEditText(editTextComprimento);
                        return true;
                    }
                }
                return false;
            }
        });

        editTextLargura.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable endDrawable = editTextLargura.getCompoundDrawablesRelative()[2];
                    if (endDrawable != null && event.getRawX() >= (editTextLargura.getRight() - endDrawable.getBounds().width())) {
                        habilitarEditText(editTextLargura);
                        return true;
                    }
                }
                return false;
            }
        });

        if(editTextIdRobo!=null){
            editTextIdRobo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Drawable endDrawable = editTextIdRobo.getCompoundDrawablesRelative()[2];
                        if (endDrawable != null && event.getRawX() >= (editTextIdRobo.getRight() - endDrawable.getBounds().width())) {
                            habilitarEditText(editTextIdRobo);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        Button buttonOk = viewDialogCustomizada.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Você escolheu OK!", Toast.LENGTH_LONG).show();
                if(String.valueOf(editTextComprimento.getText()).isEmpty() && String.valueOf(editTextLargura.getText()).isEmpty()){
                    editTextComprimento.setError("Por favor, insira um valor");
                    editTextLargura.setError("Por favor, insira um valor");
                }else if(String.valueOf(editTextComprimento.getText()).isEmpty()){
                    editTextComprimento.setError("Por favor, insira um valor");
                } else if (String.valueOf(editTextLargura.getText()).isEmpty()) {
                    editTextLargura.setError("Por favor, insira um valor");
                } else if (editTextIdRobo != null && String.valueOf(editTextIdRobo.getText()).isEmpty()) {
                    editTextIdRobo.setError("Por favor, insira um valor");
                } else{
                    String comprimento = String.valueOf(editTextComprimento.getText());
                    String largura = String.valueOf(editTextLargura.getText());
                    if(editTextIdRobo != null){
                        String idRobo = String.valueOf(editTextIdRobo.getText());
                        salvarIdRobo(idRobo);
                    }
                    salvarDicionarioMarcador(spinner);
                    salvarDimensoesAmbiente(comprimento, largura);
                    dialog.dismiss();
                }
            }
        });

        Button buttonCancelar = viewDialogCustomizada.findViewById(R.id.button_cancelar);
        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Você escolheu CANCELAR!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    private void salvarDicionarioMarcador(Spinner spinner) {
        String selected = spinner.getSelectedItem().toString();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("DicionarioDeMarcadores", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dicionario", selected);
        editor.apply();
    }

    private void salvarIdRobo(String idRobo) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("IdRobo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", idRobo);
        editor.apply();
    }

    private void configurarIdRobo(TextInputEditText editTextIdRobo) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("IdRobo", MODE_PRIVATE);
        String idRobo = sharedPreferences.getString("id", "");
        editTextIdRobo.setText(idRobo);
        if (!idRobo.isEmpty()) {
            desabilitarEditText(editTextIdRobo);
        }
    }

    private void configurarSpinner(Spinner spinner) {
        String[] dicionarios = adicionarDicionarios();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dicionarios);
        adapter.setDropDownViewResource(R.layout.spinner_item_background);
        spinner.setAdapter(adapter);

        SharedPreferences preferences = getContext().getSharedPreferences("DicionarioDeMarcadores", MODE_PRIVATE);
        String selectedItem = preferences.getString("dicionario", null);
        if (selectedItem != null) {
            int spinnerPosition = adapter.getPosition(selectedItem);
            spinner.setSelection(spinnerPosition);
        }
    }

    private String[] adicionarDicionarios() {
        return new String[]{
                "ARUCO_ORIGINAL",
                "4X4_50",
                "4X4_100",
                "4X4_250",
                "4X4_1000",
                "5X5_50",
                "5X5_100",
                "5X5_250",
                "5X5_1000",
                "6X6_50",
                "6X6_100",
                "6X6_250",
                "6X6_1000",
                "7X7_50",
                "7X7_100",
                "7X7_250",
                "7X7_1000"
        };
    }

    private void salvarDimensoesAmbiente(String comprimento, String largura) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("DimensoesAmbiente", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("comprimento", comprimento);
        editor.putString("largura", largura);
        editor.apply();
    }

    private void configurarEditText(TextInputEditText editTextComprimento, TextInputEditText editTextLargura) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("DimensoesAmbiente", MODE_PRIVATE);
        String comprimento = sharedPreferences.getString("comprimento", "");
        String largura = sharedPreferences.getString("largura", "");

        editTextComprimento.setText(comprimento);
        editTextLargura.setText(largura);

        if (!comprimento.isEmpty()) {
            desabilitarEditText(editTextComprimento);
        }
        if (!largura.isEmpty()) {
            desabilitarEditText(editTextLargura);
        }
    }

    private void desabilitarEditText(TextInputEditText editText) {
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_edit,0);
        editText.setFocusable(false);
        editText.setClickable(false);
        editText.setCursorVisible(false);
    }

    private void habilitarEditText(TextInputEditText editText) {
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setCursorVisible(true);
        editText.requestFocus();
    }
}

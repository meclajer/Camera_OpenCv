package com.example.camera_opencv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView vista;
    Bitmap respaldo;
    ImageButton btnCamara, btnGaleria, btnGrayScale, btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btnCamara = findViewById(R.id.btn_camera);
        btnGaleria = findViewById(R.id.btn_galeria);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnGrayScale = findViewById(R.id.btn_conv);
        vista = findViewById(R.id.imageView);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tomarFoto();
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargar();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        btnGrayScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escalaDeGrises();
            }
        });

    }

    private void tomarFoto() {
        Intent iCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (iCamara.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(iCamara, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void cargar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE: // Cargar imagen
                    try {
                        Uri selectedImage = data.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        final Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                        vista.setImageBitmap(bmp);
                        respaldo = bmp;
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE: // Tomar foto
                    try {
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        vista.setImageBitmap(imageBitmap);
                        respaldo = imageBitmap;
                    } catch (Exception e) {
                        Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void escalaDeGrises(){
        try {
            Mat rgb = new Mat();
            Mat gray = new Mat();

            Bitmap imageBitmat = ((BitmapDrawable) vista.getDrawable()).getBitmap();
            int width = imageBitmat.getWidth();
            int height = imageBitmat.getHeight();

            Bitmap grayImage = Bitmap.createBitmap(width, height, imageBitmat.getConfig());

            Utils.bitmapToMat(imageBitmat, rgb);
            Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);
            Utils.matToBitmap(gray, grayImage);

            vista.setImageBitmap(grayImage);
            respaldo = grayImage;
        }catch (Exception e){
            Toast.makeText(this, "Cargue primero una imagen", Toast.LENGTH_LONG).show();
        }
    }

    private void guardar() {

        try {
            BitmapDrawable draw = (BitmapDrawable) vista.getDrawable();
            Bitmap bitmap = draw.getBitmap();

            FileOutputStream outStream = null;
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/CamaraOpenCV");
            dir.mkdirs();
            String fileName = String.format("%d.jpeg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);

            try {
                outStream = new FileOutputStream(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            try {
                outStream.flush();
                Toast.makeText(this, "Guardando", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            try {
                outStream.close();
                Toast.makeText(this, "Imagen guardada con exito", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Cargue primero una imagen", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        vista.setImageBitmap(respaldo);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}


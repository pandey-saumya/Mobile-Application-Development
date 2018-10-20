package com.example.xunhu.mycameraapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    ImageButton btnTakePhoto;
    ImageButton gallery;
    ImageButton btnFlashAuto;
    Button btnSavePhoto;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    Camera.Parameters parameters;
    Intent galleryIntent, camIntent;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.cameraSurface);
        surfaceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                parameters = camera.getParameters();
                if (parameters.getMaxNumDetectedFaces()>0){
                    camera.startFaceDetection();
                    Toast.makeText(getApplicationContext(),"Face detection started",Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        initialize();
    }

    public void initialize() {

        btnTakePhoto = (ImageButton) findViewById(R.id.btnButton);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        btnSavePhoto = (Button) findViewById(R.id.btnSave);
        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImagePublic();
            }
        });


        btnFlashAuto = (ImageButton) findViewById(R.id.btn_Flash_Off);
        btnFlashAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashAuto();
            }
        });

        gallery = (ImageButton) findViewById(R.id.btnGallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    public void openGallery() {
            galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(galleryIntent,"Select Image from Gallery"),2);
    }

    private void takePhoto() {
        camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
  /**
    public void takePhoto() {
        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                photo = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                camera.stopPreview();
                btnSavePhoto.setVisibility(View.VISIBLE);
            }
        });
    }
   **/

    public void switchToBackFacingCamera() {
        camera.stopPreview();
        if (camera!=null){
            camera.release();
            camera=null;
        }
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    public void flashAuto() {
        camera = Camera.open();
        parameters = camera.getParameters();
        parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
        camera.startPreview();

    }

    public void downloadImagePublic() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MyCameraApp");
        if (!dir.exists()){
            dir.mkdir();
        }
      String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(dir.getPath()+File.separator+"IMG_"+timestamp+".jpg");
        FileOutputStream outputStream =null;
        try {
            outputStream = new FileOutputStream(mediaFile);
            photo.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (outputStream!=null){
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
       sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile)));
       Toast.makeText(getApplicationContext(),"download successfully",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera  = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                if (faces.length>0){
                    System.out.println("@ Location X "+faces[0].rect.centerX()+ "Location Y: "+faces[0].rect.centerY());
                }
            }
        });
        parameters = camera.getParameters();
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}

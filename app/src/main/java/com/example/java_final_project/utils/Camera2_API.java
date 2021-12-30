package com.example.java_final_project.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Camera2_API implements MediaScanning, BitmapUri{
    private Context mcontext;
    private TextView tv_path;
    private TextureView mtextureView;
    private ImageView iv_pic;
    private String cameraId;
    private CameraDevice cameraDevices;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size size;
    private ImageReader imageReader;
    private File file;
    private Bitmap bitmap;
    int MULTI_PERMISSION = 999;
    private Handler backhandler;
    private HandlerThread backthread;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private CameraDevice.StateCallback stateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            cameraDevices = cameraDevice;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
        }
    };
    public Camera2_API(Context context, TextureView textureView,
                       ImageView iv_capture_pic, TextView tv_path_image) {
        mcontext = context;
        mtextureView = textureView;
        iv_pic = iv_capture_pic;
        tv_path = tv_path_image;
    }
    public void takePicture() {
        if (cameraDevices == null) {
            return;
        }
        CameraManager manager = (CameraManager) mcontext.getSystemService(Context.CAMERA_SERVICE);
        /*CameraManager manager = (CameraManager) getSystemService
                (Context.CAMERA_SERVICE);*/
        try {
            CameraCharacteristics cameraCharacteristics =
                    manager.getCameraCharacteristics(cameraDevices.getId());
            Size[] jpegsizes = null;
            if (cameraCharacteristics != null) {
                jpegsizes = cameraCharacteristics.get(CameraCharacteristics
                        .SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);
                int width = 1000;
                int height = 500;
                if (jpegsizes != null && jpegsizes.length > 0) {
                    width = jpegsizes[0].getWidth();
                    height = jpegsizes[0].getHeight();
                }
                ImageReader reader = ImageReader.newInstance(
                        width, height, ImageFormat.JPEG, 1
                );
                List<Surface> outputSurface = new ArrayList<>(2);
                outputSurface.add(reader.getSurface());
                outputSurface.add(new Surface(mtextureView.getSurfaceTexture()));
                CaptureRequest.Builder captureBuilder =
                        cameraDevices.createCaptureRequest(CameraDevice
                                .TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(reader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE,
                        CameraMetadata.CONTROL_MODE_AUTO);
                int rotation = ((Activity) mcontext).getWindowManager()
                        .getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION
                        , ORIENTATIONS.get(rotation));
                String filename = new SimpleDateFormat
                        ("yyyyMMdd HHmmss", Locale.getDefault())
                        .format(Calendar.getInstance().getTime());
                File uploadcamera2 =
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DCIM + "/처방전"
                        );
                if (!uploadcamera2.exists()) {
                    uploadcamera2.mkdirs();
                }
                file = new File(
                        Environment.getExternalStorageDirectory() +
                        "/DCIM/처방전/",
                        filename + ".jpg");
                ImageReader.OnImageAvailableListener readerListener =
                        new ImageReader.OnImageAvailableListener() {
                            @Override
                            public void onImageAvailable(ImageReader imageReader) {
                                Image image = null;
                                try {
                                    image = reader.acquireLatestImage();
                                    ByteBuffer byteBuffer = image.getPlanes()[0]
                                            .getBuffer();
                                    byte[] bytes = new byte[byteBuffer.capacity()];
                                    byteBuffer.get(bytes);
                                    save(bytes);
                                } finally {
                                    if (image != null) {
                                        image.close();
                                    }
                                }
                            }

                            private void save(byte[] bytes) {
                                OutputStream outputStream = null;
                                try {
                                    outputStream = new
                                            FileOutputStream(file);
                                    outputStream.write(bytes);
                                    iv_pic.setVisibility(View.VISIBLE);
                                    mtextureView.setVisibility(View.GONE);
                                    tv_path.setVisibility(View.VISIBLE);
                                    tv_path.setText(file.getAbsolutePath());
                                    Bitmap bitmap = BitmapFactory.decodeFile
                                            (tv_path.getText().toString());
                                    tv_path.setVisibility(View.GONE);
                                    Matrix rotate = new Matrix();
                                    rotate.preRotate(90);
                                    Bitmap resizebitmap =
                                            Bitmap.createBitmap(
                                                    bitmap, 0, 0,  bitmap.getWidth(),
                                                    bitmap.getHeight(), rotate, false
                                            );
                                    iv_pic.setImageBitmap(resizebitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                reader.setOnImageAvailableListener
                        (readerListener, backhandler);
                CameraCaptureSession.CaptureCallback captureCallback =
                        new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureCompleted
                                    (@NonNull CameraCaptureSession session,
                                     @NonNull CaptureRequest request,
                                     @NonNull TotalCaptureResult result) {
                                super.onCaptureCompleted
                                        (session, request, result);
                                Toast.makeText(mcontext.getApplicationContext(),
                                        "Saved ",
                                        Toast.LENGTH_LONG).show();
                                onSave(file);
                                createCameraPreview();
                            }
                        };
                cameraDevices.createCaptureSession(outputSurface,
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured
                                    (@NonNull CameraCaptureSession cameraCaptureSession) {
                                try {
                                    cameraCaptureSession.capture(
                                            captureBuilder.build(), captureCallback,
                                            backhandler);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed
                                    (@NonNull CameraCaptureSession cameraCaptureSession) {

                            }
                        }, backhandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    public Uri bitmapToUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage
                (mcontext.getContentResolver(), bitmap,
                        "Title", null);
        return Uri.parse(path);
    }
    private void createCameraPreview() {
        try {
            SurfaceTexture texture = mtextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(size.getWidth(), size.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevices.createCaptureRequest
                    (CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevices.createCaptureSession
                    (Arrays.asList(surface),
                            new CameraCaptureSession.StateCallback() {
                                @Override
                                public void onConfigured
                                        (@NonNull CameraCaptureSession cameraCaptureSession) {
                                    if (cameraDevices == null) {
                                        return;
                                    }
                                    cameraCaptureSessions = cameraCaptureSession;
                                    updatePreview();
                                }

                                @Override
                                public void onConfigureFailed
                                        (@NonNull CameraCaptureSession cameraCaptureSession) {
                                    Toast.makeText(mcontext.getApplicationContext(), "Changed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (cameraDevices == null) {
            Toast.makeText(mcontext.getApplicationContext(), "Error",
                    Toast.LENGTH_SHORT).show();
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest
                    (captureRequestBuilder.build(), null, backhandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public void openCamera() {
        CameraManager manager = (CameraManager) mcontext.getSystemService
                (Context.CAMERA_SERVICE);
        /*CameraManager manager = (CameraManager) getSystemService
                (Context.CAMERA_SERVICE);*/
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics =
                    manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map =
                    characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            size = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission
                    (mcontext, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mcontext,
                        new String[] {
                                Manifest.permission.CAMERA
                        }, MULTI_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallBack, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void onSave(File filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(filePath));
        mcontext.sendBroadcast(intent);
    }
}

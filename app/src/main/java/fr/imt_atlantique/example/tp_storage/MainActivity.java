package fr.imt_atlantique.example.tp_storage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button cameraBtn;       // A button for turning camera on
    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SAVE_PHOTO_INTO_GALLERY = 2;
    private static final String TAG = "MainActivity";


    private String currentPhotoPath;
    private Uri currentPhotoUri;
    private File currentPhotoFile;
    private String currentPhotoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraBtn = findViewById(R.id.camera_btn);
        imageView = findViewById(R.id.image_view);
        // Button onclick function
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startCamera() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
//            File photoFile = null;
            try {
//                photoFile = createImageFile();
                currentPhotoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (currentPhotoFile != null) {
                currentPhotoUri = FileProvider.getUriForFile(this,
                        "fr.imt_atlantique.example.tp_storage.provider",
                        currentPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);   // Define photo's storage location
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);       // Starts to take photo
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Finished taking photos");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "requestCode: " + requestCode + "  resultCode: " + resultCode + "  data：" + data);
            Bitmap bitmap = null;
            try {
                // Once user finished taking photo, the photo has been stored in "currentPhotoUri", so now gets the photo back
                bitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(),
                        currentPhotoUri
                );

//                    File thumbnailFile = createFile(getFilesDir(), "thumbnails");
//                    FileOutputStream fos = new FileOutputStream(thumbnailFile);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                    fos.flush();
//                    fos.close();

                imageView.setImageBitmap(bitmap);   // shows photo on the screen
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        galleryAddPic();
    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(currentPhotoUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        currentPhotoName = imageFileName;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!(storageDir.exists())) {
            storageDir.mkdir();
        }
        System.out.println(storageDir.toString());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    public static File createFile(File storageDir, String folderName) throws IOException {
//        // Create a file name
//        String fileName = createUniqueFileName();
//        // Use an existing directory or create it if necessary
//        File picturesDir = new File(storageDir, folderName);
//        if (!(picturesDir.exists())) {
//            picturesDir.mkdir();
//        }
//
//        // Create the name of the file with suffix .jpg
//        File pathToFile = File.createTempFile(
//                fileName,
//                ".jpg",
//                picturesDir
//        );
//
//        System.out.println(pathToFile.toString());
//        return pathToFile;
//    }
//
//    private static String createUniqueFileName() throws IOException {
//        // Create a unique file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String fileName = "JPEG_" + timeStamp + "_";
//        return fileName;
//    }

}
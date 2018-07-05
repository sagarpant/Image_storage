package com.example.abc.image_storage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    public static final int Camera_image_request_id =0;
    public static final int Select_image_from_gallery = 1;


    public String text = "";

    ImageView Img_view_for_image;
    Button Load_button_for_image;
    Button Process_button_for_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , Camera_image_request_id);
        }

        Load_button_for_image = (Button) findViewById(R.id.Load_btn);
        Img_view_for_image = (ImageView) findViewById(R.id.img_view);
        Process_button_for_text = (Button) findViewById(R.id.Process);

        Load_button_for_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,Select_image_from_gallery);
            }
        });

        Process_button_for_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Camera_image_request_id){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Storage Permission Granted!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission not Ganted ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Select_image_from_gallery ){
            if(resultCode == RESULT_OK){
                Uri selected_image = data.getData();
                String[] filepath_of_image ={ MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                if (selected_image != null) {
                    cursor = getContentResolver().query(selected_image , filepath_of_image ,null ,null , null);
                }
                else{
                    Toast.makeText(this, "Selected Image is null", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (cursor != null) {
                    cursor.moveToFirst();
                }
                else{
                    Toast.makeText(this, "Cursor is null", Toast.LENGTH_SHORT).show();
                    return ;

                }
                int columnIndex = cursor.getColumnIndex(filepath_of_image[0]);
                String picturePath = cursor.getString(columnIndex);

                FirebaseVisionImage image=null;
                try {
                    image = FirebaseVisionImage.fromFilePath(this, selected_image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                        .getVisionTextDetector();

                Task<FirebaseVisionText> result =
                        detector.detectInImage(image)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                        // Task completed successfully
                                        // ...

                                        for(FirebaseVisionText.Block block :firebaseVisionText.getBlocks() ){
                                            text = text  + block.getText();
                                        }

                                        Intent intent11  = new Intent(MainActivity.this , Detected_text.class );
                                        intent11.putExtra("text" ,text);
                                        startActivity(intent11);
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                e.printStackTrace();
                                            }
                                        });





                cursor.close();
                Img_view_for_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            }
            else{
                Toast.makeText(this , "The image couldn't be picked", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

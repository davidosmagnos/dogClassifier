package com.example.imageup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.List;

public class DogDetector extends AppCompatActivity {
    ImageView imageview;
    Button process;
    Uri img;
    ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(1)
            .setScoreThreshold(0.5f)
            .build();

    ObjectDetector detector = null;
    Bitmap bitmap;
    List<Detection> result=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_detector);
        try {
            detector = ObjectDetector.createFromFileAndOptions(this,"Dog_Detector_metadata.tflite",options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        img = Uri.parse(getIntent().getStringExtra("str_img"));
        imageview = (ImageView) findViewById(R.id.imageView);
        process = (Button) findViewById(R.id.process);


        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),img);
            result= detector.detect(TensorImage.fromBitmap(bitmap));
            if(!result.isEmpty()) {
                imageview.setImageBitmap(draw(bitmap, result));
                process.setText("Proceed");
            }
            else {
                imageview.setImageBitmap(bitmap);
                Toast.makeText(this, "No Dogs Detected", Toast.LENGTH_LONG).show();
                process.setText("GO Back");
                process.setEnabled(false);
            }
        } catch (IOException e) {
            Toast.makeText(this,"No such Image",Toast.LENGTH_LONG).show();
        }

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!result.isEmpty()){
                    proceed();
                }
            }
        });


    }

    public Bitmap draw(Bitmap bm,List<Detection> dog){
        Bitmap b = bm.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRoundRect(dog.get(0).getBoundingBox().left,dog.get(0).getBoundingBox().top,dog.get(0).getBoundingBox().right,dog.get(0).getBoundingBox().bottom,15f,15f,paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(50f);

        String txt = "Dog "+Math.round(dog.get(0).getCategories().get(0).getScore()*100)+"%";
        canvas.drawText(txt,dog.get(0).getBoundingBox().left,dog.get(0).getBoundingBox().bottom+40,paint);
        return b;
    }

    public void proceed(){
        String str_img;
        Intent intent = new Intent(this, DogClassifier.class);
        intent.putExtra("str_img",img.toString());
        startActivity(intent);
    }


}


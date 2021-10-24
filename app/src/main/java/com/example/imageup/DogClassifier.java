package com.example.imageup;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.classifier.Classifications;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;
import org.tensorflow.lite.task.vision.detector.Detection;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DogClassifier extends AppCompatActivity {
    Uri image;
    ImageView imgRes;
    TextView txtRes;
    ImageClassifier.ImageClassifierOptions options = null;
    ImageClassifier classifier =  null;
    List<Classifications> result;
    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_classsifier);
        image = Uri.parse(getIntent().getStringExtra("str_img"));
        imgRes = findViewById(R.id.classifierRes);
        txtRes =  findViewById(R.id.textRes);
        options = ImageClassifier.ImageClassifierOptions.builder()
                .setMaxResults(3)
                .build();
        imgRes.setImageURI(image);
        try {
            classifier = ImageClassifier.createFromFileAndOptions(this,"Dog_Classifier_metadata.tflite",options);
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
            result = classifier.classify(TensorImage.fromBitmap(bitmap));
            String str = "";
            for(Classifications c:result){
                for(Category cat :c.getCategories()) {
                    str += String.format("%.1f%s %s \n",cat.getScore()*100,"%",cat.getLabel());
                }
                txtRes.setText("Your Dog is \n"+str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}
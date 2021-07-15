package com.naffi.useofmobilenetmlmodel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.naffi.useofmobilenetmlmodel.ml.MobilenetV110224Quant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_displayImage;
    private Button btn_getImage;
    private Button btn_predict;
    private TextView tv_result;
    private Bitmap img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        iv_displayImage = findViewById(R.id.iv_diplayImage);
        btn_getImage = findViewById(R.id.btn_getImage);
        btn_predict = findViewById(R.id.btn_predict);
        tv_result = findViewById(R.id.tv_resultDisplay);
        String townList[];

        String fileName = "labels.txt";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("wifi2.txt")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        String contents = "";
        InputStream is = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
            contents = reader.readLine();
            String line = null;
            while ((line = reader.readLine()) != null) {
                contents += '\n' + line;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        Log.d("results", contents);
//        tv_result.setText(contents);
        townList = contents.split("\n");

        //////////////////////////////

        btn_getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("images/*");
                startActivityForResult(intent,100);

            }
        });
        btn_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img = Bitmap.createScaledBitmap(img,224,224,true);
                try {
                    MobilenetV110224Quant model = MobilenetV110224Quant.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                    TensorImage tensorImage = new TensorImage(DataType.UINT8);
                    tensorImage.load(img);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();
                    inputFeature0.loadBuffer(byteBuffer);

                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    // Releases model resources if no longer used.
                    model.close();
//                    int max = getMax(inputFeature0.getFloatArray());
//                    float output = outputFeature0.getFloatArray()[max];
//                    String s=String.valueOf(getMax(outputFeature0.getFloatArray()));
                    String res = townList[getMax(outputFeature0.getFloatArray())];
                    tv_result.setText(res);
                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        iv_displayImage.setImageURI(data.getData());
        Uri uri = data.getData();
        try {
            img = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getMax(float arr[]){
        int ind =0;
        float max = 0.0f;
        for (Integer i = 0; i < 1000; i++ ) {
            if (arr[i]>max){
                ind = i;
                max = arr[i];
            }
        }
        return ind;
    }
}
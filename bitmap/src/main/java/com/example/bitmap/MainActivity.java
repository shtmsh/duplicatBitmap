package com.example.bitmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    public File externalDumpFile;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        Bitmap picture = BitmapFactory.decodeResource(getResources(), R.drawable.search);

        ((ImageView) findViewById(R.id.bitmap)).setImageBitmap(picture);

        Bitmap picture2 = BitmapFactory.decodeResource(getResources(), R.drawable.search);

        ((ImageView) findViewById(R.id.bitmap2)).setImageBitmap(picture2);

        findViewById(R.id.dump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dump();
            }
        });

    }

    private void dump() {
        //手动触发GC
        Runtime.getRuntime().gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.runFinalization();

        File file = new File(externalDumpFile.getAbsolutePath(), "dump.hprof");

        if (file.exists()) {
            file.delete();
        }

        try {
            boolean newFile = file.createNewFile();
            if (newFile) {
                Debug.dumpHprofData(file.getAbsolutePath());
                Toast.makeText(this, "path: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_GSERVICES) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            initExternalPath();
        }

    }

    private void initExternalPath() {
        externalDumpFile = new File(this.getExternalFilesDir(null), "bitmapAnalyzer");
        if (!externalDumpFile.exists()) {
            externalDumpFile.mkdirs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 1 && grantResults[0] == PERMISSION_GRANTED) {
            initExternalPath();
        } else {
            Log.e(TAG, "request permission failed ");

        }

        initExternalPath();
    }
}

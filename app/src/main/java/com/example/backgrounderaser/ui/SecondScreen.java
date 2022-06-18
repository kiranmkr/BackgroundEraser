package com.example.backgrounderaser.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.backgrounderaser.R;
import com.example.backgrounderaser.asyncWorking.MLCropAsyncTask;
import com.example.backgrounderaser.utils.Constant;
import com.example.backgrounderaser.utils.ImageUtils;
import com.example.backgrounderaser.utils.Utils;

public class SecondScreen extends AppCompatActivity {

    private Bitmap selectedBit;
    private ImageView setimg;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        setimg = findViewById(R.id.setimg);

        setimg.setOnTouchListener((view, motionEvent) -> {
            Utils.viewTransformation(view, motionEvent);
            return true;
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> setimg.post(() -> {
            if (Constant.getMainBitmap() != null) {
                selectedBit = Constant.getMainBitmap();
                setimg.setImageBitmap(Constant.getMainBitmap());
                initBMPNew();
            }
        }), 1000);
    }

    private void initBMPNew() {
        cutmaskNew();
    }

    public int mCount = 0;
    private Bitmap cutBit;

    public void cutmaskNew() {

        final ProgressBar progressBar = findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.GONE);
        new CountDownTimer(5000, 1000) {

            public void onFinish() {
            }

            public void onTick(long j) {
                mCount++;
                if (progressBar.getProgress() <= 90) {
                    progressBar.setProgress(mCount * 5);
                }
            }
        }.start();

        new MLCropAsyncTask((bitmap, bitmap2, i, i2) -> {
            SecondScreen.this.selectedBit.getWidth();
            SecondScreen.this.selectedBit.getHeight();
            int width = SecondScreen.this.selectedBit.getWidth();
            int height = SecondScreen.this.selectedBit.getHeight();
            int i3 = width * height;
            SecondScreen.this.selectedBit.getPixels(new int[i3], 0, width, 0, 0, width, height);
            int[] iArr = new int[i3];
            Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
            SecondScreen spiralSerpActivity = SecondScreen.this;
            spiralSerpActivity.cutBit = ImageUtils.getMask(SecondScreen.this.selectedBit, createBitmap, width, height);
            SecondScreen.this.cutBit = Bitmap.createScaledBitmap(bitmap, SecondScreen.this.cutBit.getWidth(), SecondScreen.this.cutBit.getHeight(), false);
            SecondScreen.this.runOnUiThread(() -> {
                if (Palette.from(SecondScreen.this.cutBit).generate().getDominantSwatch() == null) {
                    Toast.makeText(SecondScreen.this, "Human detection is failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
                SecondScreen.this.setimg.setImageBitmap(SecondScreen.this.cutBit);
            });
        }, this, progressBar).execute();
    }


}
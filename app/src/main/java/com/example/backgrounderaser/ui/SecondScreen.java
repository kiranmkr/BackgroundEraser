package com.example.backgrounderaser.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.backgrounderaser.R;
import com.example.backgrounderaser.asyncWorking.MLCropAsyncTask;
import com.example.backgrounderaser.interfaceCallBack.StickerClick;
import com.example.backgrounderaser.recyclerAdapter.ShapeAdapter;
import com.example.backgrounderaser.utils.Constant;
import com.example.backgrounderaser.utils.ImageUtils;
import com.example.backgrounderaser.utils.StoreManager;
import com.example.backgrounderaser.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SecondScreen extends AppCompatActivity implements StickerClick {

    private Bitmap selectedBit;
    private ImageView setimg;
    private ImageView setBackImg;
    private final ShapeAdapter shapeAdapter = new ShapeAdapter(this);
    private RelativeLayout savingRoot;
    private ProgressBar progressBar;

    Handler workerHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        findIdClick();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void findIdClick() {

        setimg = findViewById(R.id.setimg);
        setBackImg = findViewById(R.id.setback);
        ImageView btnBack = findViewById(R.id.imageView);
        RecyclerView reBackGround = findViewById(R.id.re_back);
        ImageView btnDownload = findViewById(R.id.imageView4);
        savingRoot = findViewById(R.id.mContentRootView);
        progressBar = findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.GONE);

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

        btnBack.setOnClickListener(view -> finish());

        reBackGround.setHasFixedSize(true);
        reBackGround.setAdapter(shapeAdapter);

        btnDownload.setOnClickListener(view -> {
            Log.d("myPath", String.valueOf(StoreManager.getPath()));
            progressBar.setVisibility(View.VISIBLE);
            saveUserData();
        });

        updateBackImage();

    }

    private Bitmap imageBitmap = null;

    private void saveUserData() {

        if (imageBitmap != null) {
            imageBitmap.recycle();
            imageBitmap = null;
        }

        imageBitmap = Utils.getLayoutBitmap(savingRoot);

        if (imageBitmap != null) {

            //Generating a file name
            String filename = "JPEG_" + System.currentTimeMillis() + ".jpg";

            //Output stream
            OutputStream fos = null;

            try {
                fos = new FileOutputStream(new File(String.valueOf(StoreManager.getPath()), filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (fos != null) {

                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                workerHandler.postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    Utils.showToast(this, "Photo Is save");
                }, 1000);

            } else {
                progressBar.setVisibility(View.GONE);
                Utils.showToast(this, getString(R.string.something_went_wrong));
            }

        } else {
            progressBar.setVisibility(View.GONE);
            Utils.showToast(this, getString(R.string.something_went_wrong));
        }
    }

    private void updateBackImage() {

        String path = "file:///android_asset/bgScr/" + 1 + ".webp";

        Log.d("myShapePath", path);

        Glide.with(SecondScreen.this)
                .load(path)
                .dontAnimate().into(setBackImg);
    }

    private void initBMPNew() {
        cutmaskNew();
    }

    public int mCount = 0;
    private Bitmap cutBit;

    public void cutmaskNew() {

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

    @Override
    public void setOnStickerClickListener(int position, boolean isShapeOrNot) {

        String path = "file:///android_asset/bgScr/" + position + ".webp";

        Log.d("myShapePath", path);

        Glide.with(SecondScreen.this)
                .load(path)
                .dontAnimate().into(setBackImg);

    }
}
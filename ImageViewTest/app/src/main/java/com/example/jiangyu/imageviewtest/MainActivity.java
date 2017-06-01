package com.example.jiangyu.imageviewtest;

/*
* author :jiangyu
*
* */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<ImageViewInfo> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageList = new ArrayList<>();
        Window window = MainActivity.this.getWindow();

        // 获取屏幕像素
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        if (window != null) {
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            ViewGroup contentView = (ViewGroup) decorView.findViewById(android.R.id.content);

            if (contentView != null) {
                List<ImageViewInfo> list = findImageViewInViewTree(contentView);
                for (ImageViewInfo imageViewInfo : list) {
                    Log.v("haha", "ImageView bitmap size：" + imageViewInfo.imageSize / 1024f + "KB" + "height:" + imageViewInfo.imageHeight + "width:" +
                            imageViewInfo.imageWidth + "screen height:" + display.getHeight() + "screen width:" + display.getWidth());
                }
            }
        }
    }

    private static class ImageViewInfo {
        int imageNum;
        int imageSize;
        int imageHeight;
        int imageWidth;
    }

    private List<ImageViewInfo> findImageViewInViewTree(View curNode) {
        if (curNode.getVisibility() != View.VISIBLE) {
            return null;
        }
        if (curNode instanceof ViewGroup) {
            ViewGroup curNodeGroup = (ViewGroup) curNode;
            for (int i = 0; i < curNodeGroup.getChildCount(); i++) {
                findImageViewInViewTree(curNodeGroup.getChildAt(i));
            }
        } else {
            if (curNode instanceof ImageView) {
                ImageViewInfo imageViewInfo = new ImageViewInfo();
                ImageView curImage = (ImageView) curNode;
                Drawable drawable = curImage.getDrawable();
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    imageViewInfo.imageHeight = bitmap.getHeight();
                    imageViewInfo.imageWidth = bitmap.getWidth();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {     //API 19
                        imageViewInfo.imageSize = bitmap.getAllocationByteCount();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
                        imageViewInfo.imageSize = bitmap.getByteCount();
                    } else {
                        imageViewInfo.imageSize = bitmap.getRowBytes() * bitmap.getHeight();
                    }
                }
                imageList.add(imageViewInfo);
            }
        }
        return imageList;
    }
}

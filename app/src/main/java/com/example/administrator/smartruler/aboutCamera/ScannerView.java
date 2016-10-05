package com.example.administrator.smartruler.aboutCamera;

import android.content.Context;
import android.hardware.Camera;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2016/10/5.
 */

public class ScannerView extends FrameLayout {
    protected Camera mCamera;
    protected CameraPreview mPreview;

    private ScannerView(Context context){
        super(context);
      //  addView(mPreview = new CameraPreview(getContext()));
    }

    public void setContentView(int res) {
        try {
            View showPanel = View.inflate(getContext(), res, null);
            addView(showPanel);
        } catch (Exception e) {
            return;
        }
    }
}

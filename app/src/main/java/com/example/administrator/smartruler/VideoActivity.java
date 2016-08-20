package com.example.administrator.smartruler;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.VideoView;

import java.io.File;

/**
 * Created by Administrator on 2016/8/20.
 */
public class VideoActivity extends Activity {

    private VideoView videoView;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.video_layout);

        videoView = (VideoView)findViewById(R.id.video_view);
        initVideoPath();
        videoView.start();
    }

    private void  initVideoPath(){
//        File file = new File(Environment.getExternalStorageDirectory(),"movie.3gp");
//        videoView.setVideoPath(file.getPath());
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.my_video_file;
        videoView.setVideoURI(Uri.parse(uri));
    }
}

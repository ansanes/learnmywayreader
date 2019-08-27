package org.readium.sdk.android.launcher;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Date;

public class BookshelfActivity extends AppCompatActivity {
    private static final int STOPSPLASH = 0;
    private static final long SPLASHTIME = 500;
    private final String testPath = "epubtest";

    private static final String TAG = "learnmyway";
    private ImageButton petHelperButton;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);
        petHelperButton = (ImageButton) findViewById(R.id.petHelperButton);
        petHelperButton.setImageResource(((LearnMyWayApplication) getApplication()).getImageIdForPetHelper());
        View portaObertaImageView = findViewById(R.id.portaobertaImageView);
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        portaObertaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playStartSound();
                launchReadium();
            }

        });
        petHelperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = STOPSPLASH;
                splashHandler.sendMessageDelayed(msg, SPLASHTIME);
            }
        });
    }

    protected void playStartSound(){
        mMediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.activity_start);
        mMediaPlayer.start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void launchReadium() {
        Intent listIntent = new Intent(getApplicationContext(),
                ContainerList.class);
        listIntent.putExtra("porta_aberta_epub","true");
        startActivity(listIntent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
//            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
//                // For JellyBean and above
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    ClipData clip = data.getClipData();
//
//                    if (clip != null) {
//                        for (int i = 0; i < clip.getItemCount(); i++) {
//                            Uri uri = clip.getItemAt(i).getUri();
//                            // Do something with the URI
//                        }
//                    }
//                    // For Ice Cream Sandwich
//                } else {
//                    ArrayList<String> paths = data.getStringArrayListExtra
//                            (FilePickerActivity.EXTRA_PATHS);
//
//                    if (paths != null) {
//                        for (String path: paths) {
//                            Uri uri = Uri.parse(path);
//                            // Do something with the URI
//                        }
//                    }
//                }
//
//            }

            Uri uri = data.getData();

            Intent listIntent = new Intent(getApplicationContext(),
                    ContainerList.class);
            listIntent.putExtra("epubFolder", uri.getPath());
            startActivity(listIntent);
            //MainActivity.this.finish();
        }
    }


    // handler for splash screen
    private Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOPSPLASH:

                    File sdcard = Environment.getExternalStorageDirectory();
                    File epubpath = new File(sdcard, testPath);
                    epubpath.mkdirs();

                    //String path = epubpath.getPath();
                    //Uri uri = Uri.parse(path);

                    Intent i = new Intent(BookshelfActivity.this.getApplicationContext(), FilePickerActivity.class);
                    // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                    // Set these depending on your use case. These are the defaults.
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);

                    i.putExtra(FilePickerActivity.EXTRA_START_PATH, epubpath.getPath());

                    startActivityForResult(i, 0);

                    break;
            }
            super.handleMessage(msg);
        }
    };


}
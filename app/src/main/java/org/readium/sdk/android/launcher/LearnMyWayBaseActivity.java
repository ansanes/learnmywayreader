package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import org.readium.sdk.android.launcher.util.VideoViewScaleListener;

public class LearnMyWayBaseActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    protected LearnMyWayApplication learnMyWayApplication;
    protected int voiceOverSoundId;
    protected int onOptionSoundId;
    protected int offOptionSoundId;
    protected LearnMyWayUserOptions userOptions;
    protected ImageButton signLanguageVideoButton;
    protected MutedVideoView signLanguageVideoView;
    private boolean scalingVideo;
    private float mScaleFactor = 1;
    protected Integer signLanguageVideoId;
    private ConstraintLayout containerConstraintLayout;
    private VideoViewScaleListener videoViewScaleListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        learnMyWayApplication = (LearnMyWayApplication) getApplication();
        this.userOptions = learnMyWayApplication.getLearnMyWayUserOptions();
    }

    protected void setupSignLanguageVideoButton()
    {
        signLanguageVideoButton = new ImageButton(this);
        signLanguageVideoButton.setContentDescription("Sign language video toggle");
        signLanguageVideoButton.setImageResource(R.mipmap.sign_language_on_enabled);
        signLanguageVideoButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        containerConstraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        containerConstraintLayout.addView(signLanguageVideoButton);
        ConstraintSet set = new ConstraintSet();
        set.connect(signLanguageVideoButton.getId(),ConstraintSet.TOP,containerConstraintLayout.getId(),ConstraintSet.TOP,0);
        set.connect(signLanguageVideoButton.getId(),ConstraintSet.END,containerConstraintLayout.getId(),ConstraintSet.END,0);
        set.constrainWidth(signLanguageVideoButton.getId(), 150);
        set.constrainHeight(signLanguageVideoButton.getId(), 150);
        set.applyTo(containerConstraintLayout);
        signLanguageVideoButton.bringToFront();
        if (learnMyWayApplication.isSignLanguageVideoInOptionsScreenEnabled()) {
            initAndStartVideo();
        }else{
            signLanguageVideoButton.setBackgroundColor(Color.parseColor("#cccccc"));
        }
        signLanguageVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                learnMyWayApplication.setSignLanguageVideoInOptionsScreenEnabled(!learnMyWayApplication.isSignLanguageVideoInOptionsScreenEnabled());
                if (learnMyWayApplication.isSignLanguageVideoInOptionsScreenEnabled()) {
                    initAndStartVideo();
                }else{
                    signLanguageVideoButton.setBackgroundColor(Color.parseColor("#cccccc"));
                    if (signLanguageVideoView != null) {
                        signLanguageVideoView.stopPlayback();
                        signLanguageVideoView.setVisibility(View.GONE);
                    }
                }
            }


        });
    }

    private void initAndStartVideo() {
        signLanguageVideoButton.setBackgroundColor(Color.parseColor("#ffa300"));
        String path = "android.resource://" + getPackageName() + "/" + signLanguageVideoId;
        if (signLanguageVideoView == null) {
            signLanguageVideoView = new MutedVideoView(LearnMyWayBaseActivity.this);
            signLanguageVideoButton.setBackgroundColor(Color.parseColor("#ffa300"));
            containerConstraintLayout.addView(signLanguageVideoView);
            signLanguageVideoView.bringToFront();
            signLanguageVideoView.setClickable(true);
            signLanguageVideoView.setFocusable(true);
            videoViewScaleListener = new VideoViewScaleListener(signLanguageVideoView);
            final ScaleGestureDetector mScaleDetector = new ScaleGestureDetector(LearnMyWayBaseActivity.this, videoViewScaleListener);
            signLanguageVideoView.setTag("videoviewtag");
            Float videoX = learnMyWayApplication.getVideoViewX();
            Float videoY = learnMyWayApplication.getVideoViewY();
            Integer width = learnMyWayApplication.getVideoViewWidth();
            Integer heigh = learnMyWayApplication.getVideoViewHeight();
            Double scaleFactor = learnMyWayApplication.getVideoScaleFactor();
            if (videoX!= null){
                videoViewScaleListener.setmScaleFactor(scaleFactor);
                signLanguageVideoView.animate()
                        .x(videoX)
                        .y(videoY)
                        .setDuration(0)
                        .start();
                ViewGroup.LayoutParams layoutParams = signLanguageVideoView.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = heigh;
                signLanguageVideoView.setLayoutParams(layoutParams);
            }

            signLanguageVideoView.setOnTouchListener(new View.OnTouchListener() {
                float dX, dY;
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    mScaleDetector.onTouchEvent(event);
                    if (videoViewScaleListener.isScalingVideo()) {
                        return false;
                    }

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            break;

                        case MotionEvent.ACTION_MOVE:

                            view.animate()
                                    .x(event.getRawX() + dX)
                                    .y(event.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                            break;
                        default:
                            return false;
                    }
                    return true;

                }
            });
        }
        signLanguageVideoView.stopPlayback();
        signLanguageVideoView.setVisibility(View.VISIBLE);
        signLanguageVideoView.setVideoURI(Uri.parse(path));
        signLanguageVideoView.start();
        signLanguageVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                signLanguageVideoView.setVisibility(View.GONE);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (signLanguageVideoView != null){
            signLanguageVideoView.stopPlayback();
            signLanguageVideoView.setVisibility(View.GONE);
            learnMyWayApplication.setVideoViewX(signLanguageVideoView.getX());
            learnMyWayApplication.setVideoViewY(signLanguageVideoView.getY());
            learnMyWayApplication.setVideoViewWidth(signLanguageVideoView.getWidth());
            learnMyWayApplication.setVideoViewHeight(signLanguageVideoView.getHeight());
            learnMyWayApplication.setVideoScaleFactor(videoViewScaleListener.getmScaleFactor());
        }
    }



    public void stopVoiceOver() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void playVoiceOverSound() {
        stopVoiceOver();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(),voiceOverSoundId);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopVoiceOver();
            }
        });
        mMediaPlayer.start();
    }


    public void playOnOptionSound() {
        stopVoiceOver();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(),onOptionSoundId);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopVoiceOver();
            }
        });
        mMediaPlayer.start();
    }

    public void playOffOptionSound() {
        stopVoiceOver();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(),offOptionSoundId);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopVoiceOver();
            }
        });
        mMediaPlayer.start();
    }

    protected void playNextSound(){
        stopVoiceOver();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.next_pop_sound);
        mMediaPlayer.start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void playBackSound() {
        stopVoiceOver();
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.back_pop_sound);
        mMediaPlayer.start();

    }

}
package org.readium.sdk.android.launcher.util;

import android.os.Handler;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import org.readium.sdk.android.launcher.MutedVideoView;

public class VideoViewScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    private boolean scalingVideo=false;
    private double mScaleFactor = 1.0;
    private MutedVideoView videoView;

    public VideoViewScaleListener(MutedVideoView videoView) {
        this.videoView = videoView;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        this.scalingVideo = true;
        return true;
    }
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        mScaleFactor *= detector.getScaleFactor();
        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
        // Don't let the object get too small or too large.
        int prevWidth = layoutParams.width;
        int prevHeight = layoutParams.height;
        int newWidth = Math.max(300, (int) (layoutParams.width * mScaleFactor));
        int newHeight = Math.max(300, (int) (layoutParams.height * mScaleFactor));
        layoutParams.width = newWidth;
        layoutParams.height = newHeight;
        int diffX = newWidth - prevWidth;
        int diffY = newHeight - prevHeight;
        videoView.animate()
                .x(videoView.getX() - diffX / 2)
                .y(videoView.getY() - diffY / 2)
                .setDuration(0)
                .start();
        mScaleFactor = 1.f;
        Log.d("scaling", "diffx:" + diffX + " diffy:" + diffY);
        videoView.setLayoutParams(layoutParams);
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        super.onScaleEnd(detector);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scalingVideo = false;
            }
        }, 200);
    }

    public boolean isScalingVideo() {
        return scalingVideo;
    }

    public double getmScaleFactor() {
        return mScaleFactor;
    }

    public void setmScaleFactor(double mScaleFactor) {
        this.mScaleFactor = mScaleFactor;
    }
}

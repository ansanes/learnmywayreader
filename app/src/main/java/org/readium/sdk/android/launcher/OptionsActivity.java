package org.readium.sdk.android.launcher;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class OptionsActivity extends LearnMyWayBaseActivity {
    protected Button leftOptionButton;
    protected Button rightOptionButton;
    protected int leftOptionEnabledImageId;
    protected int rightOptionEnabledImageId;
    protected int leftOptionDisabledImageId;
    protected int rightOptionDisabledImageId;
    protected Boolean leftOptionEnabled;
    private Drawable leftEnabledDrawable;
    private Drawable leftDisabledDrawable;
    private Drawable rightEnabledDrawable;
    private Drawable rightDisabledDrawable;
    protected Integer leftButtonSignLanguageVideoId;
    protected Integer rightButtonSignLanguageVideoId;
    private ImageView petHelperImageView;
    private Button backButton;
    private Button nextButton;

    protected void configureButtonsInitialState() {
        leftOptionButton = (Button) findViewById(R.id.leftButton);
        rightOptionButton = (Button) findViewById(R.id.rightButton);
        petHelperImageView = (ImageView) findViewById(R.id.petHelperImageView);
        if (petHelperImageView != null) {
            petHelperImageView.setImageResource(((LearnMyWayApplication) getApplication()).getImageIdForPetHelper());
        }
        leftEnabledDrawable = ContextCompat.getDrawable(this,leftOptionEnabledImageId);
        leftDisabledDrawable = ContextCompat.getDrawable(this,leftOptionDisabledImageId);
        rightEnabledDrawable = ContextCompat.getDrawable(this,rightOptionEnabledImageId);
        rightDisabledDrawable = ContextCompat.getDrawable(this,rightOptionDisabledImageId);
        leftOptionButton.setCompoundDrawablesWithIntrinsicBounds(null, leftDisabledDrawable, null, null);
        rightOptionButton.setCompoundDrawablesWithIntrinsicBounds(null, rightDisabledDrawable, null, null);
        Drawable rightBackground = rightOptionButton.getBackground();
        ((GradientDrawable)rightBackground).setColor(Color.WHITE);
        rightOptionButton.setTextColor(Color.BLACK);
        Drawable leftBackground = leftOptionButton.getBackground();
        ((GradientDrawable)leftBackground).setColor(Color.WHITE);
        leftOptionButton.setTextColor(Color.BLACK);
        backButton= (Button) findViewById(R.id.backbutton);
        nextButton= (Button) findViewById(R.id.nextbutton);
        Drawable backButtonBackground = backButton.getBackground();
        ((GradientDrawable)backButtonBackground).setColor(Color.WHITE);
        backButton.setTextColor(Color.BLACK);
        Drawable nextButtonBackground = nextButton.getBackground();
        ((GradientDrawable)nextButtonBackground).setColor(Color.WHITE);
        nextButton.setTextColor(Color.BLACK);
        Drawable forwardArrowDrawable = ContextCompat.getDrawable(this, R.mipmap.forwardarrow_black);
        nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, forwardArrowDrawable, null);
    }

    public void leftOptionClicked(View view) {

        leftOptionButton.setCompoundDrawablesWithIntrinsicBounds(null, leftEnabledDrawable, null, null);
        rightOptionButton.setCompoundDrawablesWithIntrinsicBounds(null, rightDisabledDrawable, null, null);
        Drawable rightBackground = rightOptionButton.getBackground();
        ((GradientDrawable)rightBackground).setColor(Color.WHITE);
        rightOptionButton.setTextColor(Color.BLACK);
        Drawable leftBackground = leftOptionButton.getBackground();
        ((GradientDrawable)leftBackground).setColor(Color.parseColor("#0099ff"));
        leftOptionButton.setTextColor(Color.WHITE);
        enableNextButton();
        playOnOptionSound();
        if (learnMyWayApplication.isSignLanguageVideoInOptionsScreenEnabled()){
            playSingLanguageVideoForLeftOption(true);
        }
    }

    private void playSingLanguageVideoForLeftOption(boolean leftButton) {
        String path = "android.resource://" + getPackageName() + "/" + (leftButton?leftButtonSignLanguageVideoId:rightButtonSignLanguageVideoId);
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


    public void rightOptionClicked(View view) {
        leftOptionButton.setCompoundDrawablesWithIntrinsicBounds(null, leftDisabledDrawable, null, null);
        rightOptionButton.setCompoundDrawablesWithIntrinsicBounds(null, rightEnabledDrawable, null, null);
        Drawable rightBackground = rightOptionButton.getBackground();
        ((GradientDrawable)rightBackground).setColor(Color.parseColor("#0099ff"));
        rightOptionButton.setTextColor(Color.WHITE);
        Drawable leftBackground = leftOptionButton.getBackground();
        ((GradientDrawable)leftBackground).setColor(Color.WHITE);
        leftOptionButton.setTextColor(Color.BLACK);
        enableNextButton();
        playOffOptionSound();
        if (learnMyWayApplication.isSignLanguageVideoInOptionsScreenEnabled()){
            playSingLanguageVideoForLeftOption(false);
        }
    }

    private void enableNextButton() {
        Drawable newxtButtonBackground = nextButton.getBackground();
        ((GradientDrawable)newxtButtonBackground).setColor(Color.parseColor("#3AD003"));
        nextButton.setTextColor(Color.WHITE);
        Drawable forwardArrowDrawable = ContextCompat.getDrawable(this, R.mipmap.forwardarrow_white);
        nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, forwardArrowDrawable, null);
    }
}

package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TutorialOptionsActivity extends LearnMyWayBaseActivity {

    private ImageView petHelperImageView;
    private Button backButton;
    private Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_options);
        petHelperImageView = (ImageView) findViewById(R.id.petHelperImageView);
        petHelperImageView.setImageResource(((LearnMyWayApplication) getApplication()).getImageIdForPetHelper());
        backButton = (Button) findViewById(R.id.backbutton);
        nextButton = (Button) findViewById(R.id.nextbutton);
        Drawable rightBackground = backButton.getBackground();
        ((GradientDrawable) rightBackground).setColor(Color.WHITE);
        backButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        backButton.setTextColor(Color.BLACK);
        Drawable forwardArrowDrawable = ContextCompat.getDrawable(this, R.mipmap.forwardarrow_white);
        nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, forwardArrowDrawable, null);
        backButton.setText("No, Skip");
        this.voiceOverSoundId = R.raw.voice_over_do_you_want_to_see_a_tutorial;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_27;
        setupSignLanguageVideoButton();
    }

    public void nextClicked(View view) {
        stopVoiceOver();playNextSound();
        Intent intent = new Intent(this, BookshelfActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopVoiceOver();
    }
    public void backClicked(View view) {
        stopVoiceOver();playBackSound();
        Intent intent = new Intent(this, BookshelfActivity.class);
        startActivity(intent);
    }
}

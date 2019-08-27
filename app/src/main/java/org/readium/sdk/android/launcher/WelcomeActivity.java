package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends LearnMyWayBaseActivity {


    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button backButton = (Button) findViewById(R.id.backbutton);
        //backButton.setVisibility(View.INVISIBLE);
        backButton.setText("Skip");
        backButton.setCompoundDrawablesWithIntrinsicBounds( null, null, null, null );
        this.voiceOverSoundId = R.raw.voice_over_hello_welcome_to;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_01;
        setupSignLanguageVideoButton();

    }



    @Override
    protected void onPause() {
        super.onPause();
        stopVoiceOver();
    }

    public void nextClicked(View view){
        stopVoiceOver();playNextSound();
        Intent intent = new Intent(this, ChooseAPetTextActivity.class);
        startActivity(intent);
    }


    public void backClicked(View view){
        stopVoiceOver();playNextSound();
        Intent intent = new Intent(this, BookshelfActivity.class);
        startActivity(intent);
    }
}

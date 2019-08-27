package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class WellDoneOptionsActivity extends LearnMyWayBaseActivity {

    private ImageView petHelperImageView;
    private ImageView miniPetHelperImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_well_done_options);
        petHelperImageView = (ImageView) findViewById(R.id.petHelperImageView);
        petHelperImageView.setImageResource(((LearnMyWayApplication)getApplication()).getImageIdForPetHelper());
        miniPetHelperImageView = (ImageView) findViewById(R.id.minipethelperimageView);
        miniPetHelperImageView.setImageResource(((LearnMyWayApplication)getApplication()).getImageIdForPetHelper());
        this.voiceOverSoundId = R.raw.voice_over_well_done_we_saved_the_way_you_like_to_learn;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_26;
        setupSignLanguageVideoButton();
    }

    public void nextClicked(View view){
        stopVoiceOver();playNextSound();
        Intent intent = new Intent(this, TutorialOptionsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVoiceOver();
    }
    public void backClicked(View view){
        playBackSound();
        finish();
    }
}

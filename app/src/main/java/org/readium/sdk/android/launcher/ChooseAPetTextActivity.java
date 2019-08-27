package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseAPetTextActivity extends LearnMyWayBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_a_pet_text);
        this.voiceOverSoundId = R.raw.voice_over_choose_a_pet_helper_desc;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_02;
        setupSignLanguageVideoButton();
    }




    public void nextClicked(View view){
        stopVoiceOver();playNextSound();
        Intent intent = new Intent(this, ChooseAPetHelperActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopVoiceOver();
    }
    public void backClicked(View view){
        stopVoiceOver();playBackSound();
        finish();
    }
}

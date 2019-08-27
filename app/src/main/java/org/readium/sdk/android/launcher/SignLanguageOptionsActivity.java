package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SignLanguageOptionsActivity extends OptionsActivity {

    private Boolean signLanguageOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_language_options);
        this.leftOptionEnabledImageId=R.mipmap.sign_language_on_enabled;
        this.leftOptionDisabledImageId=R.mipmap.sign_language_on_disabled;
        this.rightOptionEnabledImageId = R.mipmap.sign_language_off_enabled;
        this.rightOptionDisabledImageId = R.mipmap.sign_language_off_disabled;
        configureButtonsInitialState();
        leftOptionButton.setText("Sign Language On");
        rightOptionButton.setText("Sign Language Off");
        this.voiceOverSoundId = R.raw.voice_over_do_you_want_to_turn_on_sign_language_video;
        this.onOptionSoundId = R.raw.acc_sign_lang_on;
        this.offOptionSoundId = R.raw.acc_sign_lang_off;
        this.leftButtonSignLanguageVideoId = R.raw.s01_18;
        this.rightButtonSignLanguageVideoId = R.raw.s01_19;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_17;
        setupSignLanguageVideoButton();
    }
    @Override
    public void leftOptionClicked(View view) {
        super.leftOptionClicked(view);
        signLanguageOn = true;
    }

    @Override
    public void rightOptionClicked(View view) {
        super.rightOptionClicked(view);
        signLanguageOn = false;

    }


    public void nextClicked(View view){
        stopVoiceOver();playNextSound();
        if (signLanguageOn != null) {
            this.userOptions.setSignLanguageVideo(signLanguageOn);
            Intent intent = new Intent(this, ExtraTipsOptionsActivity.class);
            startActivity(intent);
        }
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

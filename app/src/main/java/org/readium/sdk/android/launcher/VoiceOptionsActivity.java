package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class VoiceOptionsActivity extends OptionsActivity {
    private Boolean voiceOverOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_options);
        this.leftOptionEnabledImageId = R.mipmap.voice_over_on_enabled;
        this.leftOptionDisabledImageId = R.mipmap.voice_over_on_disabled;
        this.rightOptionEnabledImageId = R.mipmap.voice_over_off_enabled;
        this.rightOptionDisabledImageId = R.mipmap.voice_over_off_disabled;
        configureButtonsInitialState();
        this.voiceOverSoundId = R.raw.voice_over_do_you_want_a_voice_to_read_out_loud_to_you;
        this.onOptionSoundId = R.raw.acc_voice_over_on;
        this.offOptionSoundId = R.raw.acc_voice_over_off;
        this.leftButtonSignLanguageVideoId = R.raw.s01_15;
        this.rightButtonSignLanguageVideoId = R.raw.s01_16;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_14;
        setupSignLanguageVideoButton();
    }



    @Override
    public void leftOptionClicked(View view) {
        super.leftOptionClicked(view);
        voiceOverOn = true;
    }

    @Override
    public void rightOptionClicked(View view) {
        super.rightOptionClicked(view);
        voiceOverOn = false;

    }

    public void nextClicked(View view) {
        stopVoiceOver();
        playNextSound();
        if (voiceOverOn != null) {
            this.userOptions.setVoiceOver(voiceOverOn);
            Intent intent = new Intent(this, SignLanguageOptionsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVoiceOver();
    }

    public void backClicked(View view) {
        playBackSound();
        finish();
    }

}

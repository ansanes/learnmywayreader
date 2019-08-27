package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ExtraTipsOptionsActivity extends OptionsActivity {

    private Boolean extraHintsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_tips_options);
        this.leftOptionEnabledImageId=R.mipmap.hints_on_enabled;
        this.leftOptionDisabledImageId=R.mipmap.hints_on_disabled;
        this.rightOptionEnabledImageId = R.mipmap.hints_off_enabled;
        this.rightOptionDisabledImageId = R.mipmap.hints_off_disabled;
        configureButtonsInitialState();
        leftOptionButton.setText("Extra Tips On");
        rightOptionButton.setText("Extra Tips Off");
        this.voiceOverSoundId = R.raw.voice_over_extra_tips;
        this.onOptionSoundId = R.raw.acc_tips_on;
        this.offOptionSoundId = R.raw.acc_tips_off;
        this.leftButtonSignLanguageVideoId = R.raw.s01_21;
        this.rightButtonSignLanguageVideoId = R.raw.s01_22;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_20;
        setupSignLanguageVideoButton();
    }

    @Override
    public void leftOptionClicked(View view) {
        super.leftOptionClicked(view);
        extraHintsEnabled = true;
    }

    @Override
    public void rightOptionClicked(View view) {
        super.rightOptionClicked(view);
        extraHintsEnabled = false;

    }


    public void nextClicked(View view){
        stopVoiceOver();playNextSound();
        if (extraHintsEnabled != null) {
            this.userOptions.setExtraHints(extraHintsEnabled);
            Intent intent = new Intent(this, BookOptionsActivity.class);
            startActivity(intent);
            stopVoiceOver();
        }
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

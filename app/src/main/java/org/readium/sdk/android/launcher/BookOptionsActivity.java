package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BookOptionsActivity extends OptionsActivity {

    private Boolean readLikeABook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_options);
        this.leftOptionEnabledImageId=R.mipmap.likeabook;
        this.leftOptionDisabledImageId=R.mipmap.likeabook;
        this.rightOptionEnabledImageId = R.mipmap.likeawebpage;
        this.rightOptionDisabledImageId = R.mipmap.likeawebpage;
        configureButtonsInitialState();
        leftOptionButton.setText("Like a Book");
        rightOptionButton.setText("Like a Webpage");
        this.voiceOverSoundId = R.raw.voice_over_do_you_want_to_read_like_a_book_or_like_a_webpage;
        this.onOptionSoundId = R.raw.acc_like_book;
        this.offOptionSoundId = R.raw.acc_like_webpage;
        this.leftButtonSignLanguageVideoId = R.raw.s01_24;
        this.rightButtonSignLanguageVideoId = R.raw.s01_25;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_23;
        setupSignLanguageVideoButton();
    }

    @Override
    public void leftOptionClicked(View view) {
        super.leftOptionClicked(view);
        readLikeABook = true;
    }

    @Override
    public void rightOptionClicked(View view) {
        super.rightOptionClicked(view);
        readLikeABook = false;

    }


    public void nextClicked(View view){
        stopVoiceOver();playNextSound();
        if (readLikeABook != null) {
            this.userOptions.setLikeABook(readLikeABook);
            Intent intent = new Intent(this, WellDoneOptionsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopVoiceOver();
    }

    public void backClicked(View view)
    {
        stopVoiceOver();playBackSound();
        finish();
    }
}

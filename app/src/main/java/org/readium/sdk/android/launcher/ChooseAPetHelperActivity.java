package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ChooseAPetHelperActivity extends LearnMyWayBaseActivity {
    private LearnMyWayUserOptions.PET_HELPER selectedPetHelper;
    private ImageButton currentButton;
    private Button backButton;
    private Button nextButton;
    private MediaPlayer mMediaPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_a_pet_helper);
        backButton= (Button) findViewById(R.id.backbutton);
        nextButton= (Button) findViewById(R.id.nextbutton);
        Drawable rightBackground = backButton.getBackground();
        ((GradientDrawable)rightBackground).setColor(Color.WHITE);
        backButton.setTextColor(Color.BLACK);
        Drawable leftBackground = nextButton.getBackground();
        ((GradientDrawable)leftBackground).setColor(Color.WHITE);
        nextButton.setTextColor(Color.BLACK);
        Drawable forwardArrowDrawable = ContextCompat.getDrawable(this, R.mipmap.forwardarrow_black);
        nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, forwardArrowDrawable, null);
        this.voiceOverSoundId = R.raw.voice_over_choose_your_pet_helper;
        ((ImageButton)findViewById(R.id.dog_helper_imageButton)).setBackgroundColor(Color.WHITE);
        ((ImageButton)findViewById(R.id.cat_helper_imageButton)).setBackgroundColor(Color.WHITE);
        ((ImageButton)findViewById(R.id.owl_helper_imageButton)).setBackgroundColor(Color.WHITE);
        ((ImageButton)findViewById(R.id.fish_helper_imageButton)).setBackgroundColor(Color.WHITE);
        ((ImageButton)findViewById(R.id.cactus_helper_imageButton)).setBackgroundColor(Color.WHITE);
        ((ImageButton)findViewById(R.id.bird_helper_imageButton)).setBackgroundColor(Color.WHITE);
        ((ImageButton)findViewById(R.id.lion_helper_imageButton)).setBackgroundColor(Color.WHITE);
        ((ImageButton)findViewById(R.id.monkey_helper_imageButton)).setBackgroundColor(Color.WHITE);
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_03;
        setupSignLanguageVideoButton();
    }

    public void dogClicked(View view){
        selectedPetHelper = LearnMyWayUserOptions.PET_HELPER.DOG;
        switchButton((ImageButton) view);
    }

    public void catClicked(View view){
        selectedPetHelper = LearnMyWayUserOptions.PET_HELPER.CAT;
        switchButton((ImageButton) view);
    }

    public void cactusClicked(View view){
        selectedPetHelper = LearnMyWayUserOptions.PET_HELPER.CACTUS;
        switchButton((ImageButton) view);
    }

    private void switchButton(ImageButton button){
        resetCurrentButton();
        currentButton =  button;
        highlightCurrentButton();
        enableNextButton();
        playPetSound();
        if (learnMyWayApplication.isSignLanguageVideoInOptionsScreenEnabled()){
            playPetSignLanguageVideo();
        }
    }

    private void playPetSignLanguageVideo() {
        String path = "android.resource://" + getPackageName() + "/" + getPetSignLanguageVideoId();
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

    private void enableNextButton() {
        Drawable leftBackground = nextButton.getBackground();
        ((GradientDrawable)leftBackground).setColor(Color.parseColor("#3AD003"));
        nextButton.setTextColor(Color.WHITE);
        Drawable forwardArrowDrawable = ContextCompat.getDrawable(this, R.mipmap.forwardarrow_white);
        nextButton.setCompoundDrawablesWithIntrinsicBounds(null, null, forwardArrowDrawable, null);
    }

    private void highlightCurrentButton() {
        currentButton.setBackgroundColor(Color.parseColor("#0099ff"));
    }


    private void resetCurrentButton() {
        if (currentButton != null){
            currentButton.setBackgroundColor(Color.WHITE);
        }
    }

    public void fishClicked(View view){
        selectedPetHelper = LearnMyWayUserOptions.PET_HELPER.FISH;
        switchButton((ImageButton) view);
    }

    public void owlClicked(View view){
        selectedPetHelper = LearnMyWayUserOptions.PET_HELPER.OWL;
        switchButton((ImageButton) view);
    }

    public void birdClicked(View view){
        selectedPetHelper = LearnMyWayUserOptions.PET_HELPER.BIRD;
        switchButton((ImageButton) view);
    }


    public void lionClicked(View view){
        selectedPetHelper = LearnMyWayUserOptions.PET_HELPER.LION;
        switchButton((ImageButton) view);
    }

    public void monkeyClicked(View view){
        selectedPetHelper = LearnMyWayUserOptions.PET_HELPER.MONKEY;
        switchButton((ImageButton) view);
    }

    public void nextClicked(View view){
        stop();stopVoiceOver();playNextSound();
        if (selectedPetHelper != null) {
            ((LearnMyWayApplication) getApplicationContext()).setPetHelper(selectedPetHelper);
            Intent intent = new Intent(this, ChooseHowToLearnActivity.class);
            startActivity(intent);
        }
    }
    public void stop() {
        if (mMediaPlayer2 != null) {
            mMediaPlayer2.release();
            mMediaPlayer2 = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        stop();stopVoiceOver();
    }
    public void playPetSound() {
            stop();
            mMediaPlayer2 = MediaPlayer.create(getApplicationContext(),getPetSoundId());
            mMediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (mMediaPlayer2 != null) {
                        mMediaPlayer2.release();
                        mMediaPlayer2 = null;
                    }
                    mMediaPlayer2 = MediaPlayer.create(getApplicationContext(),getPetSoundNameId());
                    mMediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (mMediaPlayer2 != null) {
                                mMediaPlayer2.release();
                                mMediaPlayer2 = null;
                            }
                        }
                    });
                    mMediaPlayer2.start();
                }
            });
            mMediaPlayer2.start();

    }

    private int getPetSignLanguageVideoId(){
        switch (selectedPetHelper){
            case MONKEY:return R.raw.s01_11;
            case LION:return R.raw.s01_10;
            case FISH:return R.raw.s01_07;
            case OWL:return R.raw.s01_08;
            case BIRD:return R.raw.s01_09;
            case DOG:return R.raw.s01_04;
            case CAT:return R.raw.s01_05;
            case CACTUS:return R.raw.s01_12;
        }
        return R.raw.lion_helper;
    }

    public int getPetSoundId() {
        switch (selectedPetHelper){
            case MONKEY:return R.raw.monkey_helper;
            case LION:return R.raw.lion_helper;
            case FISH:return R.raw.fish_helper;
            case OWL:return R.raw.owl_helper;
            case BIRD:return R.raw.bird_helper;
            case DOG:return R.raw.dog_helper;
            case CAT:return R.raw.cat_helper;
            case CACTUS:return R.raw.cactus_helper;
        }
        return R.raw.lion_helper;
    }

    public int getPetSoundNameId() {
        switch (selectedPetHelper){
            case MONKEY:return R.raw.acc_helper_monkey;
            case LION:return R.raw.acc_helper_lion;
            case FISH:return R.raw.acc_helper_fish;
            case OWL:return R.raw.acc_helper_owl;
            case BIRD:return R.raw.acc_helper_bird;
            case DOG:return R.raw.acc_helper_dog;
            case CAT:return R.raw.acc_helper_cat;
            case CACTUS:return R.raw.acc_helper_cactus;
        }
        return R.raw.lion_helper;
    }


    public void backClicked(View view){
        stop();stopVoiceOver();playBackSound();
        finish();
    }

}

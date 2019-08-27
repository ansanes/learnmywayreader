package org.readium.sdk.android.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseHowToLearnActivity extends LearnMyWayBaseActivity {
    private ImageView petHelperImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_how_to_learn);
        TextView chooseHowToLearnTextView = (TextView) findViewById(R.id.chooseHowToLearnTextView);
        String text = chooseHowToLearnTextView.getText().toString();
        switch (((LearnMyWayApplication)getApplication()).getLearnMyWayUserOptions().getPetHelper()){
            case CACTUS:text=text.replaceAll("Roooar!!","Boing!");break;
            case CAT:text=text.replaceAll("Roooar!!","Meow!");break;
            case DOG:text=text.replaceAll("Roooar!!","Woof!");break;
            case BIRD:text=text.replaceAll("Roooar!!","Tweet!");break;
            case OWL:text=text.replaceAll("Roooar!!","Hoot!");break;
            case FISH:text=text.replaceAll("Roooar!!","Splash!");break;
            case LION:text=text.replaceAll("Roooar!!","Rooar!");break;
            case MONKEY:text=text.replaceAll("Roooar!!","Ook!");break;
        }
        chooseHowToLearnTextView.setText(text);
        petHelperImageView = (ImageView) findViewById(R.id.petHelperImageView);
        petHelperImageView.setImageResource(((LearnMyWayApplication)getApplication()).getImageIdForPetHelper());
        this.voiceOverSoundId = R.raw.voice_over_now_lets_choose_how_youd_like;
        this.onOptionSoundId = R.raw.acc_like_book;
        this.offOptionSoundId = R.raw.acc_like_webpage;
        playVoiceOverSound();
        signLanguageVideoId = R.raw.s01_13;
        setupSignLanguageVideoButton();
    }

    public void
    nextClicked(View view){
        stopVoiceOver();playNextSound();
        Intent intent = new Intent(this, VoiceOptionsActivity.class);
        startActivity(intent);
        stopVoiceOver();
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

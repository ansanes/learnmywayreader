package org.readium.sdk.android.launcher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationOptionsActivity extends Activity {


    enum OPTION {
        SIGNLANGUAGEVIDEO,
        VOICEOVER,
        EXTRAHINTS,
        LIKEABOOK,
        FONTTYPE,
        FONTSIZE,
        NARRATIONSPEED,
        AUTOPLAY,

    }

    private LearnMyWayApplication learnMyWayApplication;
    private LearnMyWayUserOptions userOptions;

    private List<OptionConfig> optionsConfig = new ArrayList<>();
    private List<OptionConfig> optionsConfigPage2 = new ArrayList<>();
    private LinearLayout optionsPage1LinearLayout;
    private LinearLayout optionsPage2LinearLayout;
    private Button moreOptionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.85);
        int screenHeigth = (int) (metrics.heightPixels * 0.85);
        setContentView(R.layout.activity_configuration_options);
        moreOptionsButton = findViewById(R.id.moreOptionsButton);
        optionsPage1LinearLayout = findViewById(R.id.optionsPage1LinearLayout);
        optionsPage2LinearLayout = findViewById(R.id.optionsPage2LinearLayout);
        this.setFinishOnTouchOutside(false);
        getWindow().setLayout(screenWidth, screenHeigth);
        ImageView petHelperImageView = findViewById(R.id.optionsPethelperImageView);
        learnMyWayApplication = (LearnMyWayApplication) getApplication();
        petHelperImageView.setImageResource(learnMyWayApplication.getImageIdForPetHelper());
        userOptions = learnMyWayApplication.getLearnMyWayUserOptions();
        optionsConfig.add(new OptionConfig(R.mipmap.options_sign_language_on_enabled, R.mipmap.options_sign_language_on_disabled, "Sign Language", OPTION.SIGNLANGUAGEVIDEO, (Button) findViewById(R.id.optionSignLanguageButton), userOptions.getSignLanguageVideo(), null,0,null));
        optionsConfig.add(new OptionConfig(R.mipmap.options_voiceover_enabled, R.mipmap.options_voiceover_disabled, "Voice Over", OPTION.VOICEOVER, (Button) findViewById(R.id.optionVoiceOverButton), userOptions.getVoiceOver(), null,0,null));
        optionsConfig.add(new OptionConfig(R.mipmap.hints_on_enabled, R.mipmap.hints_on_disabled, "Extra Hints", OPTION.EXTRAHINTS, (Button) findViewById(R.id.optionExtraHintsButton), userOptions.getExtraHints(), null,0,null));
        optionsConfig.add(new OptionConfig(R.mipmap.likeabook, R.mipmap.likeawebpage, "Like a Book", OPTION.LIKEABOOK, (Button) findViewById(R.id.optionLikeBookButton), userOptions.getLikeABook(), null,0,null));
        optionsConfig.get(0).styleButton();
        optionsConfig.get(1).styleButton();
        optionsConfig.get(2).styleButton();
        optionsConfig.get(3).styleButton();
        List<String> fontTypeOptions = new ArrayList<>();
        fontTypeOptions.add("Open Dyslexic");
        fontTypeOptions.add("Roboto");
        int currentFontTypeIndex = 0;
        optionsConfigPage2.add(new OptionConfig(R.mipmap.fontchange_on_white, R.mipmap.fontchange_on_black, "Font Change", OPTION.FONTTYPE, (Button) findViewById(R.id.optionFontTypeButton), true, fontTypeOptions,currentFontTypeIndex,null));
        List<String> fontSizeOptions = new ArrayList<>();
        fontSizeOptions.add("Big");
        fontSizeOptions.add("Small");
        int currentFontSizeIndex = 0;
        optionsConfigPage2.add(new OptionConfig(R.mipmap.textsize_on_white, R.mipmap.textsize_off_black, "Font Size Over", OPTION.FONTSIZE, (Button) findViewById(R.id.optionFontSizeButton), true, fontSizeOptions,currentFontSizeIndex,null));
        List<String> narrationSpeedOptions = new ArrayList<>();
        narrationSpeedOptions.add("Normal");
        narrationSpeedOptions.add("Fast");
        narrationSpeedOptions.add("Faster");
        narrationSpeedOptions.add("Fastest");
        int currentNarrationSpeedIndex = 0;
        optionsConfigPage2.add(new OptionConfig(R.mipmap.speed_1, R.mipmap.speed_2, "Narration Speed", OPTION.NARRATIONSPEED, (Button) findViewById(R.id.optionNarrationSpeedButton), true, narrationSpeedOptions,currentNarrationSpeedIndex,new int[]{R.mipmap.speed_1, R.mipmap.speed_1_5,R.mipmap.speed_2, R.mipmap.speed_4}));
        optionsConfigPage2.add(new OptionConfig(R.mipmap.autoplay_on_enabled, R.mipmap.autoplay_on_disabled, "Autoplay", OPTION.AUTOPLAY, (Button) findViewById(R.id.optionAutoplayButton), userOptions.getAutoplay(), null,0,null));
        optionsConfigPage2.get(0).styleButton();
        optionsConfigPage2.get(1).styleButton();
        optionsConfigPage2.get(2).styleButton();
        optionsConfigPage2.get(3).styleButton();
        final MediaPlayer mMediaPlayer2 = MediaPlayer.create(getApplicationContext(), learnMyWayApplication.getPetSoundId());
        mMediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mMediaPlayer2 != null) {
                    mMediaPlayer2.release();
                }
            }
        });
        mMediaPlayer2.start();
        optionsPage2LinearLayout.setVisibility(View.GONE);
    }

    public void closeClicked(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void optionButtonClicked(View view) {
        OptionConfig optionConfig = findOptionConfig(view);
        optionConfig.switchOptionButton();
    }


    private OptionConfig findOptionConfig(View view) {
        for (OptionConfig optionConfig : optionsConfig) {
            if (optionConfig.optionButton == view) {
                return optionConfig;
            }
        }
        for (OptionConfig optionConfig : optionsConfigPage2) {
            if (optionConfig.optionButton == view) {
                return optionConfig;
            }
        }
        return null;
    }

    public void homeClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("navigation", "toHome");
        setResult(RESULT_OK, intent);
        finish();

    }

    public void contentsClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("navigation", "toContent");
        setResult(RESULT_OK, intent);
        finish();
    }


    private class OptionConfig {
        private final int optionEnabledImageId;
        private final int optionDisabledImageId;
        private int[] imageResourcesIds = null;
        private Button optionButton;
        private String optionText;
        private OPTION option;
        private Boolean optionEnabled = false;
        private List optionValues;
        private int currentOptionValueIndex = 0;

        public OptionConfig(int enabledImageId, int disbledImageId, String optionText, OPTION option, Button optionButton, Boolean optionEnabled, List optionValues,int currentOptionValueIndex,int[] imageResourcesIds) {
            this.optionEnabled = optionEnabled;
            this.optionButton = optionButton;
            this.optionEnabledImageId = enabledImageId;
            this.optionDisabledImageId = disbledImageId;
            this.optionText = optionText;
            this.option = option;
            this.optionButton.setCompoundDrawablesWithIntrinsicBounds(null, getOptionEnabledDrawable(), null, null);
            this.optionValues = optionValues;
            this.currentOptionValueIndex = currentOptionValueIndex;
            this.imageResourcesIds = imageResourcesIds;
        }

        private Drawable resize(Drawable image) {
            Bitmap b = ((BitmapDrawable) image).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 140, 140, false);
            return new BitmapDrawable(getResources(), bitmapResized);
        }


        public Drawable getOptionEnabledDrawable() {
            return resize(ContextCompat.getDrawable(ConfigurationOptionsActivity.this, optionEnabledImageId));
        }

        public Drawable getOptionDisabledDrawable() {
            return resize(ContextCompat.getDrawable(ConfigurationOptionsActivity.this, optionDisabledImageId));
        }


        public void switchOptionButton() {
            optionEnabled = !optionEnabled;
            if (optionValues != null){
                currentOptionValueIndex = (currentOptionValueIndex + 1) % optionValues.size();
            }
            styleButton();
            switch (option) {
                case EXTRAHINTS:
                    userOptions.setExtraHints(optionEnabled);
                    break;
                case LIKEABOOK:
                    userOptions.setLikeABook(optionEnabled);
                    break;
                case AUTOPLAY:
                    userOptions.setAutoplay(optionEnabled);
                    break;
                case VOICEOVER:
                    userOptions.setVoiceOver(optionEnabled);
                    break;
                case SIGNLANGUAGEVIDEO:
                    userOptions.setSignLanguageVideo(optionEnabled);
                    break;
                case FONTSIZE:
                    switch (currentOptionValueIndex) {
                        case 0:
                            userOptions.setFontSize(LearnMyWayUserOptions.FONT_SIZE.FONT_SIZE_SMALL);
                            break;
                        case 1:
                            userOptions.setFontSize(LearnMyWayUserOptions.FONT_SIZE.FONT_SIZE_BIG);
                            break;
                    }
                    break;
                case FONTTYPE:
                    switch (currentOptionValueIndex) {
                        case 0:
                            userOptions.setFontType(LearnMyWayUserOptions.FONT_TYPE.ROBOTO);
                            break;
                        case 1:
                            userOptions.setFontType(LearnMyWayUserOptions.FONT_TYPE.OPEN_DYSLEXIC);
                            break;
                    }
                    break;

                case NARRATIONSPEED:
                    switch (currentOptionValueIndex) {
                        case 0:
                            userOptions.setNarrationSpeed(LearnMyWayUserOptions.NARRATION_SPEED.NORMAL);
                            break;
                        case 1:
                            userOptions.setNarrationSpeed(LearnMyWayUserOptions.NARRATION_SPEED.FAST);
                            break;
                        case 2:
                            userOptions.setNarrationSpeed(LearnMyWayUserOptions.NARRATION_SPEED.FASTER);
                            break;
                        case 3:
                            userOptions.setNarrationSpeed(LearnMyWayUserOptions.NARRATION_SPEED.FASTEST);
                            break;
                    }
                    break;

            }

        }

        public void styleButton() {
            Drawable leftBackground = optionButton.getBackground();
            if (optionEnabled) {
                optionButton.setAlpha(1f);
                optionButton.setCompoundDrawablesWithIntrinsicBounds(null, getOptionEnabledDrawable(), null, null);
                ((GradientDrawable) leftBackground).setColor(Color.parseColor("#0099ff"));
                optionButton.setTextColor(Color.WHITE);
            } else {
                optionButton.setAlpha(0.6f);
                optionButton.setCompoundDrawablesWithIntrinsicBounds(null, getOptionDisabledDrawable(), null, null);
                ((GradientDrawable) leftBackground).setColor(Color.parseColor("#fafafa"));
                optionButton.setTextColor(Color.BLACK);
            }
            switch (option) {
                case LIKEABOOK:
                    optionButton.setText((optionEnabled ? "Like a Book" : "Like a Webpage"));
                    break;
                case NARRATIONSPEED:
                    optionButton.setAlpha(1f);
                    optionButton.setCompoundDrawablesWithIntrinsicBounds(null, getDrawableForCurrentIndex(), null, null);
                    ((GradientDrawable) leftBackground).setColor(Color.parseColor("#0099ff"));
                    optionButton.setTextColor(Color.WHITE);
                    optionButton.setText(optionValues.get(currentOptionValueIndex).toString());
                    break;
                default:
                    if (optionValues == null) {
                        optionButton.setText(Html.fromHtml(optionText + "<br/>" + (optionEnabled ? "ON" : "Off")));
                        break;
                    } else {
                        optionButton.setText(optionValues.get(currentOptionValueIndex).toString());
                        break;
                    }
            }
        }

        private Drawable getDrawableForCurrentIndex() {
            return resize(ContextCompat.getDrawable(ConfigurationOptionsActivity.this, imageResourcesIds[currentOptionValueIndex]));
        }
    }

    public void moreOptionsClicked(View view) {
        if (optionsPage1LinearLayout.getVisibility() == View.GONE) {
            moreOptionsButton.setText("More");
            optionsPage2LinearLayout.setVisibility(View.GONE);
            optionsPage1LinearLayout.setVisibility(View.VISIBLE);
        } else {
            moreOptionsButton.setText("Back");
            optionsPage2LinearLayout.setVisibility(View.VISIBLE);
            optionsPage1LinearLayout.setVisibility(View.GONE);
        }

    }

    public void accesibilityClicked(View view){
        startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0);
    }

}

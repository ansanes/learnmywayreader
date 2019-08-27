package org.readium.sdk.android.launcher;

import android.app.Application;

public class LearnMyWayApplication extends Application {
    public static int LION_PET_IMAGE_ID = R.mipmap.pet_lion_transbk;
    public static int CAT_PET_IMAGE_ID = R.mipmap.pet_cat_transbk;
    public static int FISH_PET_IMAGE_ID = R.mipmap.pet_fish_transbk;
    public static int DOG_PET_IMAGE_ID = R.mipmap.pet_dog_transbk;
    public static int OWL_PET_IMAGE_ID = R.mipmap.pet_owl_transbk;
    public static int BIRD_PET_IMAGE_ID = R.mipmap.pet_bird_transbk;
    public static int MONKEY_PET_IMAGE_ID = R.mipmap.pet_monkey_transbk;
    public static int CACTUS_PET_IMAGE_ID = R.mipmap.pet_cactus_transbk;

    private LearnMyWayUserOptions learnMyWayUserOptions=new LearnMyWayUserOptions();
    private boolean signLanguageVideoInOptionsScreenEnabled = false;

    private Float videoViewX;
    private Float videoViewY;
    private Integer videoViewWidth;
    private Integer videoViewHeight;
    private Double videoScaleFactor;



    public void setPetHelper(LearnMyWayUserOptions.PET_HELPER petHelper) {
        learnMyWayUserOptions.setPetHelper(petHelper);
    }

    public int getImageIdForPetHelper(){
        switch (learnMyWayUserOptions.getPetHelper()){
            case CAT: return CAT_PET_IMAGE_ID;
            case DOG: return DOG_PET_IMAGE_ID;
            case OWL: return OWL_PET_IMAGE_ID;
            case BIRD: return BIRD_PET_IMAGE_ID;
            case FISH: return FISH_PET_IMAGE_ID;
            case LION: return LION_PET_IMAGE_ID;
            case CACTUS: return CACTUS_PET_IMAGE_ID;
            case MONKEY: return MONKEY_PET_IMAGE_ID;
        }
        return 0;
    }

    public int getPetSoundId() {
        switch (learnMyWayUserOptions.getPetHelper()){
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

    public boolean isSignLanguageVideoInOptionsScreenEnabled() {
        return signLanguageVideoInOptionsScreenEnabled;
    }

    public void setSignLanguageVideoInOptionsScreenEnabled(boolean signLanguageVideoInOptionsScreenEnabled) {
        this.signLanguageVideoInOptionsScreenEnabled = signLanguageVideoInOptionsScreenEnabled;
    }

    public LearnMyWayUserOptions getLearnMyWayUserOptions() {
        return learnMyWayUserOptions;
    }

    public Float getVideoViewX() {
        return videoViewX;
    }

    public void setVideoViewX(Float videoViewX) {
        this.videoViewX = videoViewX;
    }

    public Float getVideoViewY() {
        return videoViewY;
    }

    public void setVideoViewY(Float videoViewY) {
        this.videoViewY = videoViewY;
    }

    public Integer getVideoViewWidth() {
        return videoViewWidth;
    }

    public void setVideoViewWidth(Integer videoViewWidth) {
        this.videoViewWidth = videoViewWidth;
    }

    public Integer getVideoViewHeight() {
        return videoViewHeight;
    }

    public void setVideoViewHeight(Integer videoViewHeight) {
        this.videoViewHeight = videoViewHeight;
    }

    public Double getVideoScaleFactor() {
        return videoScaleFactor;
    }

    public void setVideoScaleFactor(Double videoScaleFactor) {
        this.videoScaleFactor = videoScaleFactor;
    }
}

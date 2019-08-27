package org.readium.sdk.android.launcher;

public class LearnMyWayUserOptions {


    public enum FONT_TYPE{
        ROBOTO,
        OPEN_DYSLEXIC
    }

    public enum FONT_SIZE{
        FONT_SIZE_BIG,
        FONT_SIZE_SMALL,

    }


    public enum NARRATION_SPEED{
        NORMAL,
        FAST,
        FASTER,
        FASTEST
    }

    public enum PET_HELPER{
        BIRD,
        CACTUS,
        CAT,
        DOG,
        FISH,
        LION,
        MONKEY,
        OWL
    }
    private Boolean autoplay=false;
    private Boolean extraHints=false;
    private Boolean signLanguageVideo=true;
    private Boolean voiceOver=true;
    private Boolean likeABook=true;
    private FONT_SIZE fontSize;
    private FONT_TYPE fontType;
    private NARRATION_SPEED narrationSpeed=NARRATION_SPEED.NORMAL;


    private PET_HELPER petHelper=PET_HELPER.CACTUS;

    public PET_HELPER getPetHelper() {
        return petHelper;
    }

    public void setPetHelper(PET_HELPER petHelper) {
        this.petHelper = petHelper;
    }


    public Boolean getAutoplay() {
        return autoplay;
    }

    public void setAutoplay(Boolean autoplay) {
        this.autoplay = autoplay;
    }

    public Boolean getSignLanguageVideo() {
        return signLanguageVideo;
    }

    public void setSignLanguageVideo(Boolean signLanguageVideo) {
        this.signLanguageVideo = signLanguageVideo;
    }

    public Boolean getVoiceOver() {
        return voiceOver;
    }

    public void setVoiceOver(Boolean voiceOver) {
        this.voiceOver = voiceOver;
    }

    public Boolean getLikeABook() {
        return likeABook;
    }

    public void setLikeABook(Boolean likeABook) {
        this.likeABook = likeABook;
    }

    public  LearnMyWayUserOptions(){

    }

    public FONT_SIZE getFontSize() {
        return fontSize;
    }

    public void setFontSize(FONT_SIZE fontSize) {
        this.fontSize = fontSize;
    }

    public FONT_TYPE getFontType() {
        return fontType;
    }

    public void setFontType(FONT_TYPE fontType) {
        this.fontType = fontType;
    }

    public NARRATION_SPEED getNarrationSpeed() {
        return narrationSpeed;
    }

    public void setNarrationSpeed(NARRATION_SPEED narrationSpeed) {
        this.narrationSpeed = narrationSpeed;
    }

    public Boolean getExtraHints() {
        return extraHints;
    }

    public void setExtraHints(Boolean extraHints) {
        this.extraHints = extraHints;
    }

    public LearnMyWayUserOptions(LearnMyWayUserOptions learnMyWayUserOptions) {
        this.petHelper = learnMyWayUserOptions.petHelper;
        this.autoplay=learnMyWayUserOptions.autoplay;
        this.signLanguageVideo=learnMyWayUserOptions.signLanguageVideo;
        this.voiceOver=learnMyWayUserOptions.voiceOver;
        this.likeABook=learnMyWayUserOptions.likeABook;
        this.fontSize=learnMyWayUserOptions.fontSize;
        this.fontType=learnMyWayUserOptions.fontType;
        this.narrationSpeed=learnMyWayUserOptions.narrationSpeed;
    }

    public float getNarrationSpeedFloat() {
        switch (narrationSpeed){
            case NORMAL:return 1.0f;
            case FAST:return 1.5f;
            case FASTER:return 2.0f;
            case FASTEST:return 3.0f;
        }
        return 0;
    }

}

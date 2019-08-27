package org.readium.sdk.android.launcher.model;

public class SignLanguageVideoElement {
    private String parId;
    private String textSrc;
    private String videoFileName;
    private Integer clipBegin;
    private Integer clipEnd;
    private boolean lastPageElement = false;

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }


    public String getTextSrc() {
        return textSrc;
    }

    public void setTextSrc(String textSrc) {
        this.textSrc = textSrc;
    }

    public Integer getClipEnd() {
        return clipEnd;
    }

    public void setClipEnd(Integer clipEnd) {
        this.clipEnd = clipEnd;
    }

    public Integer getClipBegin() {
        return clipBegin;
    }

    public void setClipBegin(Integer clipBegin) {
        this.clipBegin = clipBegin;
    }

    public String getParId() {
        return parId;
    }

    public void setParId(String parId) {
        this.parId = parId;
    }

    public Integer getDuration(){
        return clipEnd-clipBegin;
    }


    public boolean isLastPageElement() {
        return lastPageElement;
    }

    public void setLastPageElement(boolean lastPageElement) {
        this.lastPageElement = lastPageElement;
    }
}

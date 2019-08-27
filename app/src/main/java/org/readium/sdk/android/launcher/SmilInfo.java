package org.readium.sdk.android.launcher;

public class SmilInfo {
    private Double playPosition;
    private Integer smilIndex;
    private Integer parIndex;
    public String parId;

    public Double getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(Double playPosition) {
        this.playPosition = playPosition;
    }

    public Integer getSmilIndex() {
        return smilIndex;
    }

    public void setSmilIndex(Integer smilIndex) {
        this.smilIndex = smilIndex;
    }

    public Integer getParIndex() {
        return parIndex;
    }

    public void setParIndex(Integer parIndex) {
        this.parIndex = parIndex;
    }

    @Override
    public String toString() {
        return "SmilInfo{" +
                "playPosition=" + playPosition +
                ", smilIndex=" + smilIndex +
                ", parIndex=" + parIndex +
                '}';
    }

}

package com.riddlesvillage.core.title;

/**
 * Created by Matthew E on 6/14/2017.
 */

public class TitleBuilder {
    protected String title;
    protected String subTitle;
    protected int fadeIn;
    protected int fadeOut;
    protected int duration;

    public TitleBuilder() {
    }

    public Title build() {
        return new Title(title, subTitle, fadeIn, fadeOut, duration);
    }

    public TitleBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TitleBuilder withSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public TitleBuilder withFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public TitleBuilder withFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public TitleBuilder withDuration(int duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Creates the title like {@link net.minecraft.server.v1_11_R1.PacketPlayOutTitle}
     * @return
     */
    public static TitleBuilder createTitle() {
        return new TitleBuilder();
    }
}

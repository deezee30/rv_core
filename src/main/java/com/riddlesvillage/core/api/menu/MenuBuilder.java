package com.riddlesvillage.core.api.menu;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by matt1 on 3/21/2017.
 */
public class MenuBuilder {

    private String title;
    private int slots;
    private JavaPlugin javaPlugin;

    public MenuBuilder(String title, int slots, JavaPlugin javaPlugin) {
        this.title = title;
        this.javaPlugin = javaPlugin;
        this.slots = slots;
    }

    public MenuBuilder() {
    }


    public String getTitle() {
        return title;
    }

    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public MenuBuilder setJavaPlugin(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        return this;
    }

    public static Menu fastMenu(String title, int slots, JavaPlugin javaPlugin) {
        return new MenuBuilder()
                .withTitle(title)
                .withSlots(slots)
                .withJavaPlugin(javaPlugin)
                .build();
    }

    public MenuBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getSlots() {
        return slots;
    }

    public MenuBuilder setSlots(int slots) {
        this.slots = slots;
        return this;
    }

    public Menu build() {
        return new MenuImpl(title, slots, javaPlugin);
    }

    public MenuBuilder withJavaPlugin(JavaPlugin javaPlugin) {
        return setJavaPlugin(javaPlugin);
    }

    public MenuBuilder javaPlugin(JavaPlugin javaPlugin) {
        return setJavaPlugin(javaPlugin);
    }

    public MenuBuilder slots(int slots) {
        return setSlots(slots);
    }

    public MenuBuilder withTitle(String title) {
        return setTitle(title);
    }

    public MenuBuilder title(String title) {
        return setTitle(title);
    }

    public MenuBuilder withSlots(int slots) {
        return setSlots(slots);
    }
}

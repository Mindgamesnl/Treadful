package com.craftmend.treadful.plugin.enums;

import com.craftmend.treadful.plugin.features.CommandFeature;
import com.craftmend.treadful.plugin.interfaces.VariableFeature;
import org.bukkit.Material;

public enum OptionalFeature {

    COMMAND(Material.COMMAND, "Command Proxy", "Detects poorly made plugins and wraps their commands to prevent performance or stability issues", new CommandFeature()),;

    private Material visualizer;
    private String name;
    private String description;
    private VariableFeature feature;

    OptionalFeature(Material v, String name, String about, VariableFeature feature) {
        this.visualizer = v;
        this.name = name;
        this.description = about;
        this.feature = feature;
    }

    public Material getVisualizer() {
        return visualizer;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public VariableFeature getFeature() {
        return feature;
    }
}

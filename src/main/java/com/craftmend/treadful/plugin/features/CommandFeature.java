package com.craftmend.treadful.plugin.features;

import com.craftmend.treadful.Treadful;
import com.craftmend.treadful.command.CommandMiddleware;
import com.craftmend.treadful.plugin.enums.Message;
import com.craftmend.treadful.plugin.interfaces.VariableFeature;

public class CommandFeature implements VariableFeature {

    private boolean isEnabled = false;
    private CommandMiddleware commandMiddleware;

    @Override
    public void init() {
        isEnabled = Treadful.getInstance().getConfig().getBoolean("feature.command.isEnabled");
        if (isEnabled) enable();
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void enable() {
        if (isEnabled) return;

        Message.toOp(Message.PREFIX.getMessage() + "Enabling the command proxy feature");

        try {
            commandMiddleware = new CommandMiddleware();
        } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        isEnabled = true;
        Treadful.getInstance().getConfig().set("feature.command.isEnabled", true);
    }

    @Override
    public void disable() {
        if (isEnabled && commandMiddleware != null) {
            Message.toOp(Message.PREFIX.getMessage() + "Unloading the command proxy feature");
            commandMiddleware.disable();
            isEnabled = false;
            Treadful.getInstance().getConfig().set("feature.command.isEnabled", false);
        }
    }
}

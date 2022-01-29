package com.rukukun.mutekey;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class Mutekey implements ModInitializer {

    private static KeyBinding keyBinding;
    MutekeyConfig config;
    public Identifier muteTexture = new Identifier("mutekey", "gui/muted.png");
    boolean isFocused = true;
    boolean isUserMuted = false;
    boolean isAutoMuted = false;
    @Override
    public void onInitialize() {


        AutoConfig.register(MutekeyConfig.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(MutekeyConfig.class).getConfig();



        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mutekey.togglemute", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_M, // The keycode of the key
                "category.mutekey.keys" // The translation key of the keybinding's category.
        ));

        HudRenderCallback.EVENT.register(this::onHudRender);

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            if(client.options.getSoundVolume(SoundCategory.MASTER) == 0)
                isUserMuted = true;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(!MinecraftClient.getInstance().isWindowFocused())
            {
                if(isFocused)
                {
                    OnUnfocus();
                    isFocused = false;
                }
            } else {
                if(!isFocused)
                {
                    OnFocus();
                    isFocused = true;
                }
            }
            while (keyBinding.wasPressed()) {
                if(client.options.getSoundVolume(SoundCategory.MASTER) == 0f)
                {
                    UnmuteGame(false);
                }
                else
                {
                    MuteGame(false);
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
        });
    }

    private void OnFocus() {
        if(this.client == null)
            return;
        if(config.autoMuteOnLostFocus && isAutoMuted && !isUserMuted)
        {
            UnmuteGame(true);
        }
    }

    private void OnUnfocus() {
        if(this.client == null)
            return;
        if(config.autoMuteOnLostFocus& !isUserMuted)
        {
            MuteGame(true);
        }
    }

    private void UnmuteGame(boolean automatic) {
        if(client == null)
            return;
        if(automatic)
            isAutoMuted = false;
        else
            isUserMuted = false;
        client.options.setSoundVolume(SoundCategory.MASTER, config.uservolume);
        if(config.showUnmuteMessage && client.player != null)
            client.player.sendMessage(new LiteralText(formatMessage(config.unmuteMessageFormat)), true);
    }

    private void MuteGame(boolean automatic) {
        if(client == null)
            return;
        if(automatic)
            isAutoMuted = true;
        else if(!automatic)
            isUserMuted = true;
        config.SetUserVolume(client.options.getSoundVolume(SoundCategory.MASTER));
        client.options.setSoundVolume(SoundCategory.MASTER, 0f);
        if(config.showMuteMessage && client.player != null)
            client.player.sendMessage(new LiteralText(formatMessage(config.muteMessageFormat)), true);
    }

    private String formatMessage(String unformattedMessage)
    {
        return unformattedMessage.replace("%", formatVolume());
    }





    private MinecraftClient client;
    private void onHudRender(MatrixStack matrixStack, float v) {

        if(!config.iconEnabled)
            return;
        if(client == null)
            client = MinecraftClient.getInstance();
        if(client.options.getSoundVolume(SoundCategory.MASTER) > 0)
            return;
        matrixStack.push();
            RenderSystem.setShaderTexture(0, muteTexture);
            client.inGameHud.drawTexture(
                    matrixStack,
                    10 + config.iconXOffset, 10 + config.iconYOffset,
                    0, 0,
                    16, 16,
                    16, 16
            );
        matrixStack.pop();
    }

    private String formatVolume()
    {
        return (int)Math.round(config.uservolume * 100) + "%";
    }
}

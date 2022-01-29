package com.rukukun.mutekey;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
@Config(name = "mutekey")
public class MutekeyConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    @ConfigEntry.Category("mutekey.general")
    public float uservolume  = 1;

    @ConfigEntry.Category("mutekey.general")
    public boolean iconEnabled = true;

    @ConfigEntry.Category("mutekey.general")
    @ConfigEntry.Gui.Tooltip
    public int iconXOffset = 0;

    @ConfigEntry.Category("mutekey.general")
    @ConfigEntry.Gui.Tooltip
    public int iconYOffset = 0;

    @ConfigEntry.Category("mutekey.general")
    @ConfigEntry.Gui.Tooltip
    public boolean showMuteMessage = true;

    @ConfigEntry.Category("mutekey.general")
    @ConfigEntry.Gui.Tooltip
    public String muteMessageFormat = "Sound Muted";

    @ConfigEntry.Category("mutekey.general")
    @ConfigEntry.Gui.Tooltip
    public boolean showUnmuteMessage = true;

    @ConfigEntry.Category("mutekey.general")
    @ConfigEntry.Gui.Tooltip
    public String unmuteMessageFormat = "Sound restored to %";

    @ConfigEntry.Category("mutekey.general")
    @ConfigEntry.Gui.Tooltip
    public boolean autoMuteOnLostFocus = false;

    public void SetUserVolume(float targetVal)
    {
       this.uservolume = targetVal;
    }

}

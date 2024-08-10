package me.waterarchery.littournaments.configurations;

import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litlibs.configuration.ConfigManager;
import me.waterarchery.litlibs.configuration.ConfigPart;

import java.util.Arrays;
import java.util.List;

public class TournamentMenuFile extends ConfigManager {

    public TournamentMenuFile(LitLibs litLibs, String folder, String name, boolean saveAfterLoad) {
        super(litLibs, folder, name, saveAfterLoad);
    }

    @Override
    public void initializeDefaults() {
        addDefault(ConfigPart.noComment("Title", "&3Tournaments"));
        addDefault(ConfigPart.noComment("Size", 45));
        addDefault(ConfigPart.noComment("FillMenu.Enabled", true));
        addDefault(ConfigPart.noComment("FillMenu.FillItem.Name", "&8"));
        addDefault(ConfigPart.noComment("FillMenu.FillItem.Material", "GRAY_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("FillMenu.FillItem.CustomModelData", -1));
        addDefault(ConfigPart.noComment("FillMenu.FillItem.HideAttributes", true));

        addOptional(ConfigPart.of("Items.block_break", null, Arrays.asList(
                "Use tournament id only.",
                "The tournament id is the file name in the tournaments folder.",
                "You need to remove .yml extension whilere using them here.",
                "For example, if you have a tournament named block_break.yml in your tournaments folder,",
                "you need to use block_break here."
        )));
        addOptional(ConfigPart.noComment("Items.block_break.Name", "&bBlock Break Tournament"));
        addOptional(ConfigPart.noComment("Items.block_break.Material", "COBBLESTONE"));
        addOptional(ConfigPart.noComment("Items.block_break.Slot", 10));
        addOptional(ConfigPart.noComment("Items.block_break.HideAttributes", true));
        addOptional(ConfigPart.of("Items.block_break.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addOptional(ConfigPart.noComment("Items.block_break.Lore", Arrays.asList(
                "",
                "&7You can &bbreak blocks &7to become",
                "&7the champion of &bthis tournament!",
                "",
                "&b&lYOUR STATS",
                "&b| &7Broken Blocks: &b%stat%",
                "&b| &7Your Position: &b%position%",
                "",
                "&b&lLEADERBOARD",
                "&b| &4\uD83D\uDD25 #1 &f%leader_name_1% &8(&4%leader_score_formatted_1%&8)",
                "&b| &c\uD83D\uDD25 #2 &f%leader_name_2% &8(&c%leader_score_formatted_2%&8)",
                "&b| &e\uD83D\uDD25 #3 &f%leader_name_3% &8(&e%leader_score_formatted_3%&8)",
                "&b| &6⭐ #4 &f%leader_name_4% &8(&6%leader_score_formatted_4%&8)",
                "&b| &3⭐ #5 &f%leader_name_5% &8(&3%leader_score_formatted_5%&8)",
                "&b| &8   #6 &f%leader_name_6% &8(&8%leader_score_formatted_6%&8)",
                "&b| &8   #7 &f%leader_name_7% &8(&8%leader_score_formatted_7%&8)",
                "&b| &8   #8 &f%leader_name_8% &8(&8%leader_score_formatted_8%&8)",
                "&b| &8   #9 &f%leader_name_9% &8(&8%leader_score_formatted_9%&8)",
                "&b| &8   #10 &f%leader_name_10% &8(&8%leader_score_formatted_10%&8)",
                "",
                "&b&lREMAINING TIME",
                "&b| &7There is &c%remaining_time%",
                "&b| &7to this tournament finish."
        )));

        addOptional(ConfigPart.noComment("Items.block_place.Name", "&bBlock Place Tournament"));
        addOptional(ConfigPart.noComment("Items.block_place.Material", "OAK_LOG"));
        addOptional(ConfigPart.noComment("Items.block_place.Slot", 13));
        addOptional(ConfigPart.noComment("Items.block_place.HideAttributes", true));
        addOptional(ConfigPart.of("Items.block_place.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addOptional(ConfigPart.noComment("Items.block_place.Lore", Arrays.asList(
                "",
                "&7You can &bplace blocks &7to become",
                "&7the champion of &bthis tournament!",
                "",
                "&b&lYOUR STATS",
                "&b| &7Placed Blocks: &b%stat%",
                "&b| &7Your Position: &b%position%",
                "",
                "&b&lLEADERBOARD",
                "&b| &4\uD83D\uDD25 #1 &f%leader_name_1% &8(&4%leader_score_formatted_1%&8)",
                "&b| &c\uD83D\uDD25 #2 &f%leader_name_2% &8(&c%leader_score_formatted_2%&8)",
                "&b| &e\uD83D\uDD25 #3 &f%leader_name_3% &8(&e%leader_score_formatted_3%&8)",
                "&b| &6⭐ #4 &f%leader_name_4% &8(&6%leader_score_formatted_4%&8)",
                "&b| &3⭐ #5 &f%leader_name_5% &8(&3%leader_score_formatted_5%&8)",
                "&b| &8   #6 &f%leader_name_6% &8(&8%leader_score_formatted_6%&8)",
                "&b| &8   #7 &f%leader_name_7% &8(&8%leader_score_formatted_7%&8)",
                "&b| &8   #8 &f%leader_name_8% &8(&8%leader_score_formatted_8%&8)",
                "&b| &8   #9 &f%leader_name_9% &8(&8%leader_score_formatted_9%&8)",
                "&b| &8   #10 &f%leader_name_10% &8(&8%leader_score_formatted_10%&8)",
                "",
                "&b&lREMAINING TIME",
                "&b| &7There is &c%remaining_time%",
                "&b| &7to this tournament finish."
        )));

        addOptional(ConfigPart.noComment("Items.item_craft.Name", "&bCraft Item Tournament"));
        addOptional(ConfigPart.noComment("Items.item_craft.Material", "CRAFTING_TABLE"));
        addOptional(ConfigPart.noComment("Items.item_craft.Slot", 16));
        addOptional(ConfigPart.noComment("Items.item_craft.HideAttributes", true));
        addOptional(ConfigPart.of("Items.item_craft.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addOptional(ConfigPart.noComment("Items.item_craft.Lore", Arrays.asList(
                "",
                "&7You can &bcraft items &7to become",
                "&7the champion of &bthis tournament!",
                "",
                "&b&lYOUR STATS",
                "&b| &7Placed Blocks: &b%stat%",
                "&b| &7Your Position: &b%position%",
                "",
                "&b&lLEADERBOARD",
                "&b| &4\uD83D\uDD25 #1 &f%leader_name_1% &8(&4%leader_score_formatted_1%&8)",
                "&b| &c\uD83D\uDD25 #2 &f%leader_name_2% &8(&c%leader_score_formatted_2%&8)",
                "&b| &e\uD83D\uDD25 #3 &f%leader_name_3% &8(&e%leader_score_formatted_3%&8)",
                "&b| &6⭐ #4 &f%leader_name_4% &8(&6%leader_score_formatted_4%&8)",
                "&b| &3⭐ #5 &f%leader_name_5% &8(&3%leader_score_formatted_5%&8)",
                "&b| &8   #6 &f%leader_name_6% &8(&8%leader_score_formatted_6%&8)",
                "&b| &8   #7 &f%leader_name_7% &8(&8%leader_score_formatted_7%&8)",
                "&b| &8   #8 &f%leader_name_8% &8(&8%leader_score_formatted_8%&8)",
                "&b| &8   #9 &f%leader_name_9% &8(&8%leader_score_formatted_9%&8)",
                "&b| &8   #10 &f%leader_name_10% &8(&8%leader_score_formatted_10%&8)",
                "",
                "&b&lREMAINING TIME",
                "&b| &7There is &c%remaining_time%",
                "&b| &7to this tournament finish."
        )));

        addOptional(ConfigPart.noComment("Items.mob_kill.Name", "&bMob Kill Tournament"));
        addOptional(ConfigPart.noComment("Items.mob_kill.Material", "DIAMOND_SWORD"));
        addOptional(ConfigPart.noComment("Items.mob_kill.Slot", 28));
        addOptional(ConfigPart.noComment("Items.mob_kill.HideAttributes", true));
        addOptional(ConfigPart.of("Items.mob_kill.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addOptional(ConfigPart.noComment("Items.mob_kill.Lore", Arrays.asList(
                "",
                "&7You can &bkill mobs &7to become",
                "&7the champion of &bthis tournament!",
                "",
                "&b&lYOUR STATS",
                "&b| &7Killed Mobs: &b%stat%",
                "&b| &7Your Position: &b%position%",
                "",
                "&b&lLEADERBOARD",
                "&b| &4\uD83D\uDD25 #1 &f%leader_name_1% &8(&4%leader_score_formatted_1%&8)",
                "&b| &c\uD83D\uDD25 #2 &f%leader_name_2% &8(&c%leader_score_formatted_2%&8)",
                "&b| &e\uD83D\uDD25 #3 &f%leader_name_3% &8(&e%leader_score_formatted_3%&8)",
                "&b| &6⭐ #4 &f%leader_name_4% &8(&6%leader_score_formatted_4%&8)",
                "&b| &3⭐ #5 &f%leader_name_5% &8(&3%leader_score_formatted_5%&8)",
                "&b| &8   #6 &f%leader_name_6% &8(&8%leader_score_formatted_6%&8)",
                "&b| &8   #7 &f%leader_name_7% &8(&8%leader_score_formatted_7%&8)",
                "&b| &8   #8 &f%leader_name_8% &8(&8%leader_score_formatted_8%&8)",
                "&b| &8   #9 &f%leader_name_9% &8(&8%leader_score_formatted_9%&8)",
                "&b| &8   #10 &f%leader_name_10% &8(&8%leader_score_formatted_10%&8)",
                "",
                "&b&lREMAINING TIME",
                "&b| &7There is &c%remaining_time%",
                "&b| &7to this tournament finish."
        )));

        addOptional(ConfigPart.noComment("Items.item_break.Name", "&bItem Break Tournament"));
        addOptional(ConfigPart.noComment("Items.item_break.Material", "ANVIL"));
        addOptional(ConfigPart.noComment("Items.item_break.Slot", 31));
        addOptional(ConfigPart.noComment("Items.item_break.HideAttributes", true));
        addOptional(ConfigPart.of("Items.item_break.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addOptional(ConfigPart.noComment("Items.item_break.Lore", Arrays.asList(
                "",
                "&7You can &bbreak your items &7to become",
                "&7the champion of &bthis tournament!",
                "",
                "&b&lYOUR STATS",
                "&b| &7Broken Items: &b%stat%",
                "&b| &7Your Position: &b%position%",
                "",
                "&b&lLEADERBOARD",
                "&b| &4\uD83D\uDD25 #1 &f%leader_name_1% &8(&4%leader_score_formatted_1%&8)",
                "&b| &c\uD83D\uDD25 #2 &f%leader_name_2% &8(&c%leader_score_formatted_2%&8)",
                "&b| &e\uD83D\uDD25 #3 &f%leader_name_3% &8(&e%leader_score_formatted_3%&8)",
                "&b| &6⭐ #4 &f%leader_name_4% &8(&6%leader_score_formatted_4%&8)",
                "&b| &3⭐ #5 &f%leader_name_5% &8(&3%leader_score_formatted_5%&8)",
                "&b| &8   #6 &f%leader_name_6% &8(&8%leader_score_formatted_6%&8)",
                "&b| &8   #7 &f%leader_name_7% &8(&8%leader_score_formatted_7%&8)",
                "&b| &8   #8 &f%leader_name_8% &8(&8%leader_score_formatted_8%&8)",
                "&b| &8   #9 &f%leader_name_9% &8(&8%leader_score_formatted_9%&8)",
                "&b| &8   #10 &f%leader_name_10% &8(&8%leader_score_formatted_10%&8)",
                "",
                "&b&lREMAINING TIME",
                "&b| &7There is &c%remaining_time%",
                "&b| &7to this tournament finish."
        )));

        addOptional(ConfigPart.noComment("Items.player_kill.Name", "&bPlayer Kill Tournament"));
        addOptional(ConfigPart.noComment("Items.player_kill.Material", "SHIELD"));
        addOptional(ConfigPart.noComment("Items.player_kill.Slot", 34));
        addOptional(ConfigPart.noComment("Items.player_kill.HideAttributes", true));
        addOptional(ConfigPart.of("Items.player_kill.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addOptional(ConfigPart.noComment("Items.player_kill.Lore", Arrays.asList(
                "",
                "&7You can &bkill players &7to become",
                "&7the champion of &bthis tournament!",
                "",
                "&b&lYOUR STATS",
                "&b| &7Killed Players: &b%stat%",
                "&b| &7Your Position: &b%position%",
                "",
                "&b&lLEADERBOARD",
                "&b| &4\uD83D\uDD25 #1 &f%leader_name_1% &8(&4%leader_score_formatted_1%&8)",
                "&b| &c\uD83D\uDD25 #2 &f%leader_name_2% &8(&c%leader_score_formatted_2%&8)",
                "&b| &e\uD83D\uDD25 #3 &f%leader_name_3% &8(&e%leader_score_formatted_3%&8)",
                "&b| &6⭐ #4 &f%leader_name_4% &8(&6%leader_score_formatted_4%&8)",
                "&b| &3⭐ #5 &f%leader_name_5% &8(&3%leader_score_formatted_5%&8)",
                "&b| &8   #6 &f%leader_name_6% &8(&8%leader_score_formatted_6%&8)",
                "&b| &8   #7 &f%leader_name_7% &8(&8%leader_score_formatted_7%&8)",
                "&b| &8   #8 &f%leader_name_8% &8(&8%leader_score_formatted_8%&8)",
                "&b| &8   #9 &f%leader_name_9% &8(&8%leader_score_formatted_9%&8)",
                "&b| &8   #10 &f%leader_name_10% &8(&8%leader_score_formatted_10%&8)",
                "",
                "&b&lREMAINING TIME",
                "&b| &7There is &c%remaining_time%",
                "&b| &7to this tournament finish."
        )));
    }

}

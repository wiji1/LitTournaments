package me.waterarchery.littournaments.configurations;

import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litlibs.configuration.ConfigManager;
import me.waterarchery.litlibs.configuration.ConfigPart;

import java.util.Arrays;
import java.util.List;

public class LeaderboardMenuFile extends ConfigManager {

    public LeaderboardMenuFile(LitLibs litLibs, String folder, String name, boolean saveAfterLoad) {
        super(litLibs, folder, name, saveAfterLoad);
    }

    @Override
    public void initializeDefaults() {
        addDefault(ConfigPart.noComment("Title", "&3%tournament% Leaderboard"));
        addDefault(ConfigPart.noComment("Size", 45));

        addDefault(ConfigPart.noComment("Decoration.item1.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item1.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item1.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item1.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item1.Slot", 0));
        addDefault(ConfigPart.noComment("Decoration.item1.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item2.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item2.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item2.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item2.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item2.Slot", 1));
        addDefault(ConfigPart.noComment("Decoration.item2.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item3.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item3.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item3.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item3.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item3.Slot", 2));
        addDefault(ConfigPart.noComment("Decoration.item3.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item4.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item4.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item4.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item4.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item4.Slot", 3));
        addDefault(ConfigPart.noComment("Decoration.item4.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item5.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item5.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item5.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item5.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item5.Slot", 5));
        addDefault(ConfigPart.noComment("Decoration.item5.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item7.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item7.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item7.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item7.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item7.Slot", 6));
        addDefault(ConfigPart.noComment("Decoration.item7.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item8.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item8.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item8.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item8.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item8.Slot", 7));
        addDefault(ConfigPart.noComment("Decoration.item8.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item9.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item9.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item9.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item9.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item9.Slot", 8));
        addDefault(ConfigPart.noComment("Decoration.item9.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item10.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item10.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item10.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item10.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item10.Slot", 36));
        addDefault(ConfigPart.noComment("Decoration.item10.Lore", List.of()));

        addDefault(ConfigPart.noComment("Decoration.item11.Name", "&8"));
        addDefault(ConfigPart.noComment("Decoration.item11.Material", "GREEN_STAINED_GLASS_PANE"));
        addDefault(ConfigPart.noComment("Decoration.item11.CustomModelData", -1));
        addDefault(ConfigPart.noComment("Decoration.item11.HideAttributes", true));
        addDefault(ConfigPart.noComment("Decoration.item11.Slot", 44));
        addDefault(ConfigPart.noComment("Decoration.item11.Lore", List.of()));

        addDefault(ConfigPart.of("Items.playerTemplate", null, Arrays.asList(
                "Don't change this id.",
                "This item will be displayed in /tournament leaderboard <tournament name>.",
                "This item is the template item of all leaderboard players."
        )));
        addDefault(ConfigPart.noComment("Items.playerTemplate.Name", "&b%player%"));
        addDefault(ConfigPart.of("Items.playerTemplate.Material", "PLAYER", List.of(
                "You can use custom head values like this:",
                "HEAD-eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MjJhM2Q1M2NjYzc4NDM2YzBmNjYwMDRjYjRiNzcyOWM0NjZlYTEwMDY1ZTgzOWEwNmI2Mjg4YmZkYTk4NiJ9fX0=",
                "or you can use PLAYER to display player's head."
        )));
        addDefault(ConfigPart.noComment("Items.playerTemplate.HideAttributes", true));
        addDefault(ConfigPart.of("Items.playerTemplate.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addDefault(ConfigPart.noComment("Items.playerTemplate.Lore", Arrays.asList(
                "",
                "&b&lPLAYER STATS",
                "&b| &7Player's Name: &b%name%",
                "&b| &7Score: &b%stat%",
                "&b| &7Position: &b%position%"
        )));

        addDefault(ConfigPart.of("Items.ownPlayer", null, Arrays.asList(
                "Don't change this id.",
                "This item will be displayed in /tournament leaderboard <tournament name>.",
                "This item is the template item of the player that runs the command."
        )));
        addDefault(ConfigPart.noComment("Items.ownPlayer.Name", "&b%player%"));
        addDefault(ConfigPart.noComment("Items.ownPlayer.Material", "PLAYER"));
        addDefault(ConfigPart.noComment("Items.ownPlayer.Slot", 4));
        addDefault(ConfigPart.noComment("Items.ownPlayer.HideAttributes", true));
        addDefault(ConfigPart.of("Items.playerTemplate.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addDefault(ConfigPart.noComment("Items.ownPlayer.Lore", Arrays.asList(
                "",
                "&b&lYOUR STATS",
                "&b| &7Score: &b%stat%",
                "&b| &7Position: &b%position%"
        )));

        addDefault(ConfigPart.noComment("Items.previousPage.Name", "&cPrevious Page"));
        addDefault(ConfigPart.noComment("Items.previousPage.Material", "ARROW"));
        addDefault(ConfigPart.noComment("Items.previousPage.Slot", 37));
        addDefault(ConfigPart.noComment("Items.previousPage.HideAttributes", true));
        addDefault(ConfigPart.of("Items.previousPage.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addDefault(ConfigPart.noComment("Items.previousPage.Lore", Arrays.asList(
                "",
                "&7Click here to navigate",
                "&bprevious page."
        )));

        addDefault(ConfigPart.noComment("Items.nextPage.Name", "&cNext Page"));
        addDefault(ConfigPart.noComment("Items.nextPage.Material", "ARROW"));
        addDefault(ConfigPart.noComment("Items.nextPage.Slot", 43));
        addDefault(ConfigPart.noComment("Items.nextPage.HideAttributes", true));
        addDefault(ConfigPart.of("Items.nextPage.CustomModelData", -1,
                List.of("You can set it -1 to disable")));
        addDefault(ConfigPart.noComment("Items.nextPage.Lore", Arrays.asList(
                "",
                "&7Click here to navigate",
                "&bnext page."
        )));
    }

}

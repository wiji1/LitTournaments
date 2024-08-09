package me.waterarchery.littournaments.guis;

import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litlibs.configuration.ConfigManager;
import me.waterarchery.litlibs.libs.gui.builder.item.ItemBuilder;
import me.waterarchery.litlibs.libs.gui.guis.Gui;
import me.waterarchery.litlibs.libs.gui.guis.GuiItem;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.handlers.*;
import me.waterarchery.littournaments.models.Tournament;
import me.waterarchery.littournaments.models.TournamentPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TournamentGUI {

    private static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

    public static Gui of(Player player) {
        GUIHandler guiHandler = GUIHandler.getInstance();
        ConfigManager manager = FileHandler.getTournamentMenu();
        LitLibs libs = LitTournaments.getLitLibs();
        FileConfiguration yml = manager.getYml();
        PlayerHandler playerHandler = PlayerHandler.getInstance();
        TournamentHandler tournamentHandler = TournamentHandler.getInstance();
        TournamentPlayer tournamentPlayer = playerHandler.getPlayer(player.getUniqueId());

        String title = guiHandler.getMenuTitle(manager);
        int size = guiHandler.getMenuSize(manager);

        Gui gui = Gui.gui()
                .title(Component.text(title))
                .rows(size)
                .disableAllInteractions()
                .create();

        if (yml.getBoolean("FillMenu.Enabled", true)) {
            ItemStack itemStack = guiHandler.craftItemStack(manager, "FillItem", "FillMenu");
            GuiItem guiItem = ItemBuilder.from(itemStack).asGuiItem();
            gui.getFiller().fill(guiItem);
        }

        for (String tournamentName : Objects.requireNonNull(yml.getConfigurationSection("Items")).getKeys(false)) {
            Tournament tournament = tournamentHandler.getTournament(tournamentName);

            if (tournament == null) {
                libs.getLogger().error(String.format(
                        "You used %s in the tournament menu but there is no tournament that has its id", tournamentName));
                continue;
            }

            ItemStack itemStack = guiHandler.craftItemStack(manager, tournamentName, "Items");
            int slot = yml.getInt("Items." + tournamentName + ".Slot");
            parseLore(itemStack, tournamentPlayer, tournament);

            boolean glowItemIfJoined = yml.getBoolean("GlowItemIfJoined", true);

            GuiItem guiItem = ItemBuilder.from(itemStack)
                    .glow(glowItemIfJoined && tournamentPlayer.isRegistered(tournament))
                    .asGuiItem(event -> {
                        if (tournamentPlayer.isRegistered(tournament)) {
                            libs.getMessageHandler().sendLangMessage(player, "AlreadyJoined");
                            libs.getSoundHandler().sendSound(player, "Sounds.AlreadyJoined");
                        }
                        else {
                            tournamentPlayer.join(tournament);
                            libs.getSoundHandler().sendSound(player, "Sounds.SuccessfullyJoined");
                            libs.getMessageHandler().sendLangMessage(player, "SuccessfullyRegistered");
                            gui.update();
                        }
            });

            gui.setItem(slot, guiItem);
        }

        return gui;
    }

    private static void parseLore(ItemStack itemStack, TournamentPlayer tournamentPlayer, Tournament tournament) {
        ValueHandler valueHandler = ValueHandler.getInstance();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null && itemMeta.hasLore()) {
            List<String> lore = new ArrayList<>();

            for (String part : Objects.requireNonNull(itemMeta.getLore())) {
                part = part.replace("%position%", valueHandler.getPlayerPosition(tournamentPlayer, tournament))
                        .replace("%stat%", valueHandler.getPlayerScore(tournamentPlayer, tournament))
                        .replace("%remaining_time%", valueHandler.getRemainingTime(tournament));

                List<String> otherPlaceholders = getPlaceholders(part);

                for (String placeholder : otherPlaceholders) {
                    if (placeholder.contains("leader_score_formatted_")) {
                        int pos = Integer.parseInt(placeholder
                                .replace("leader_score_formatted_", "")
                                .replace("%", ""));

                        String score = numberFormat.format(valueHandler.getPlayerScoreWithPosition(pos, tournament));
                        part = part.replace(placeholder, score);
                    }
                    else if (placeholder.contains("leader_score_")) {
                        int pos = Integer.parseInt(placeholder
                                .replace("leader_score_", "")
                                .replace("%", ""));

                        String score = String.valueOf(valueHandler.getPlayerScoreWithPosition(pos, tournament));
                        part = part.replace(placeholder, score);
                    }
                    if (placeholder.contains("leader_name_")) {
                        int pos = Integer.parseInt(placeholder
                                .replace("leader_name_", "")
                                .replace("%", ""));

                        part = part.replace(placeholder, valueHandler.getPlayerNameWithPosition(pos, tournament));
                    }
                }

                lore.add(part);
            }

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
    }

    private static List<String> getPlaceholders(String part) {
        List<String> placeholders = new ArrayList<>();
        boolean found = false;
        StringBuilder placeholder = new StringBuilder();

        for (char c : part.toCharArray()) {
            if (c == '%') {
                if (found) {
                    placeholder.append(c);
                    placeholders.add(placeholder.toString());
                    found = false;
                    placeholder = new StringBuilder();
                }
                else {
                    found = true;
                }
            }

            if (!found) continue;
            placeholder.append(c);
        }

        return placeholders;
    }

}

package me.waterarchery.littournaments.handlers;

import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litlibs.configuration.ConfigManager;
import me.waterarchery.litlibs.handlers.MessageHandler;
import me.waterarchery.litlibs.libs.xseries.XMaterial;
import me.waterarchery.littournaments.LitTournaments;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIHandler {

    private static GUIHandler instance;

    public static GUIHandler getInstance() {
        if (instance == null) instance = new GUIHandler();
        return instance;
    }

    private GUIHandler() { }

    public String getMenuTitle(ConfigManager menu) {
        LitLibs libs = LitTournaments.getLitLibs();
        FileConfiguration yml = menu.getYml();
        String title = yml.getString("Title");

        return libs.getMessageHandler().updateColors(title);
    }

    public int getMenuSize(ConfigManager menu) {
        FileConfiguration yml = menu.getYml();
        int size = yml.getInt("Size");

        return size / 9;
    }

    public ItemStack craftItemStack(ConfigManager menu, String itemName, String path) {
        LitLibs libs = LitTournaments.getLitLibs();
        FileConfiguration yml = menu.getYml();
        MessageHandler mesHandler = libs.getMessageHandler();

        String name = yml.getString(path + "." + itemName + ".Name");
        String materialName = yml.getString(path + "." + itemName + ".Material");
        List<String> rawLore = yml.getStringList(path + "." + itemName + ".Lore");
        List<String> lore = new ArrayList<>();
        int customModelData = yml.getInt(path + "." + itemName + ".CustomModelData", -1);

        ItemStack itemStack = XMaterial.matchXMaterial(materialName)
                .map(XMaterial::parseItem)
                .orElse(new ItemStack(Material.STONE));
        ItemMeta itemMeta = itemStack.getItemMeta();

        for (String part : rawLore) {
            lore.add(mesHandler.updateColors(part));
        }

        itemMeta.setDisplayName(mesHandler.updateColors(name));
        itemMeta.setLore(lore);
        if (customModelData != -1) itemMeta.setCustomModelData(customModelData);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}

package me.waterarchery.littournaments.configurations;

import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.litlibs.configuration.ConfigManager;
import me.waterarchery.litlibs.configuration.ConfigPart;

import java.util.Arrays;
import java.util.Collections;

public class ConfigFile extends ConfigManager {

    public ConfigFile(LitLibs litLibs, String folder, String name, boolean saveAfterLoad) {
        super(litLibs, folder, name, saveAfterLoad);
    }

    @Override
    public void initializeDefaults() {
        addDefault(ConfigPart.noComment("Prefix", "&7[&bTournaments&7] "));
        addDefault(ConfigPart.of("Language", "en",
                Collections.singletonList("Currently only en and tr available")));

        addDefault(ConfigPart.of("LeaderboardRefresh", 60, Arrays.asList(
                "Its in seconds.",
                "Please don't set it under 60 seconds."
        )));

        addDefault(ConfigPart.noComment("SoundVolume", 2));
        addDefault(ConfigPart.of("Sounds.MenuOpen", "BLOCK_ANVIL_BREAK", Collections.singletonList("# You can set sounds \"\" to disable them.")));
        addDefault(ConfigPart.noComment("Sounds.InvalidCommand", "ENTITY_ARROW_HIT"));
        addDefault(ConfigPart.noComment("Sounds.NoPermission", "ENTITY_ARROW_HIT"));
        addDefault(ConfigPart.noComment("Sounds.TournamentFinish", "BLOCK_ANVIL_BREAK"));
        addDefault(ConfigPart.noComment("Sounds.AlreadyJoined", "ENTITY_ARROW_HIT"));
        addDefault(ConfigPart.noComment("Sounds.SuccessfullyJoined", "BLOCK_ANVIL_BREAK"));

        addDefault(ConfigPart.of("Database.DatabaseType", "sqlite", Arrays.asList("You can use these options:", "MySQL", "SQLite")));
        addDefault(ConfigPart.noComment("Database.MySQL.host", "localhost"));
        addDefault(ConfigPart.noComment("Database.MySQL.port", "3306"));
        addDefault(ConfigPart.noComment("Database.MySQL.database", "db"));
        addDefault(ConfigPart.noComment("Database.MySQL.user", "user"));
        addDefault(ConfigPart.noComment("Database.MySQL.password", "mypassword"));
    }

}

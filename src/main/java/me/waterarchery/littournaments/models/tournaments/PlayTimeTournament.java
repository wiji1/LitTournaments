package me.waterarchery.littournaments.models.tournaments;

import me.waterarchery.littournaments.models.Tournament;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayTimeTournament extends Tournament {

    public PlayTimeTournament(String identifier, YamlConfiguration yamlConfiguration) {
        super(identifier, yamlConfiguration);
    }

}

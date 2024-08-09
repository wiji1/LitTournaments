package me.waterarchery.littournaments.models.tournaments;

import me.waterarchery.littournaments.models.Tournament;
import org.bukkit.configuration.file.YamlConfiguration;

public class MoneyReceiveTournament extends Tournament {

    public MoneyReceiveTournament(String identifier, YamlConfiguration yamlConfiguration) {
        super(identifier, yamlConfiguration);
    }

}

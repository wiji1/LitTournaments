package me.waterarchery.littournaments.models.tournaments;

import me.waterarchery.littournaments.models.Tournament;
import org.bukkit.configuration.file.YamlConfiguration;

public class MoneySpendTournament extends Tournament {

    public MoneySpendTournament(String identifier, YamlConfiguration yamlConfiguration) {
        super(identifier, yamlConfiguration);
    }

}

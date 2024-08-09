package me.waterarchery.littournaments.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.handlers.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQL extends Database {

    MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();

    public MySQL(LitTournaments instance){
        super(instance);
    }

    public Connection getSQLConnection() {
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            connection = dataSource.getConnection();
            return connection;
        } catch (SQLException ex) {
            instance.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        }
        return null;
    }

    @Override
    public void initialize() {
        FileConfiguration yml = FileHandler.getConfig().getYml();;

        dataSource.setServerName(yml.getString("Database.MySQL.host"));
        dataSource.setPortNumber(yml.getInt("Database.MySQL.port"));
        dataSource.setDatabaseName(yml.getString("Database.MySQL.database"));
        dataSource.setUser(yml.getString("Database.MySQL.user"));
        dataSource.setPassword(yml.getString("Database.MySQL.password"));
    }

}

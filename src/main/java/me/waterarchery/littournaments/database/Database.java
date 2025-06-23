package me.waterarchery.littournaments.database;

import me.waterarchery.litlibs.LitLibs;
import me.waterarchery.littournaments.LitTournaments;
import me.waterarchery.littournaments.models.RewardInstance;
import me.waterarchery.littournaments.models.Tournament;
import me.waterarchery.littournaments.models.TournamentLeaderboard;
import me.waterarchery.littournaments.models.TournamentValue;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class Database {

    protected final LitTournaments instance;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(20);
    private final ConcurrentHashMap<UUID, String> playerNameCache = new ConcurrentHashMap<>();

    public String rewardsTableName = "tournament_rewards";
    public String playersTableName = "tournament_players";

    public String createTableToken = "CREATE TABLE IF NOT EXISTS {{TOURNAMENT_NAME}} (" +
            "`player` varchar(36) NOT NULL PRIMARY KEY," +
            "`score` int(11) NOT NULL" +
            ");";

    public String createRewardsTableToken = "CREATE TABLE IF NOT EXISTS {{TABLE_NAME}} (" +
            "`player` varchar(36) NOT NULL," +
            "`command` varchar(255) NOT NULL" +
            ");";

    public String createPlayersTableToken = "CREATE TABLE IF NOT EXISTS {{TABLE_NAME}} (" +
            "`uuid` varchar(36) NOT NULL PRIMARY KEY," +
            "`name` varchar(16) NOT NULL" +
            ");";

    public Database(LitTournaments instance){
        this.instance = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void initialize();

    public void load(List<Tournament> tournaments) {
        try (Connection connection = getSQLConnection()){
            tournaments.forEach(tournament -> {
                try {
                    String tableName = tournament.getIdentifier();
                    String query = createTableToken.replace("{{TOURNAMENT_NAME}}", tableName);
                    Statement s = connection.createStatement();
                    s.executeUpdate(query);
                    s.close();

                    reloadLeaderboard(tournament);
                }
                catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            try {
                String query = createRewardsTableToken.replace("{{TABLE_NAME}}", rewardsTableName);
                Statement s = connection.createStatement();
                s.executeUpdate(query);
                s.close();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            try {
                String query = createPlayersTableToken.replace("{{TABLE_NAME}}", playersTableName);
                Statement s = connection.createStatement();
                s.executeUpdate(query);
                s.close();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void addPoint(UUID uuid, Tournament tournament, long point) {
        Runnable runnable = () -> {
            String query = String.format("INSERT INTO %s (player, score) VALUES (?, ?)" +
                    "ON DUPLICATE KEY UPDATE score = score + ?;", tournament.getIdentifier());

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);

                stmt.setString(1, uuid.toString());
                stmt.setLong(2, point);
                stmt.setLong(3, point);
                stmt.setString(4, uuid.toString());

                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        threadPool.submit(runnable);
    }

    public void setPoint(UUID uuid, Tournament tournament, long point) {
        Runnable runnable = () -> {
            String query = String.format("REPLACE INTO %s (player, score) VALUES(?, ?);", tournament.getIdentifier());

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, point);

                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        threadPool.submit(runnable);
    }

    public long getPoint(UUID uuid, Tournament tournament) {
        String query = String.format("SELECT score FROM %s WHERE player = ?;", tournament.getIdentifier());

        try (Connection connection = getSQLConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, uuid.toString());

            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                return rs.getLong("score");
            else
                return -9999;
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void registerToTournament(UUID uuid, Tournament tournament) {
        Runnable runnable = () -> {
            String QUERY = String.format("INSERT INTO %s (player, score) VALUES(?, ?);", tournament.getIdentifier());

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(QUERY);
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, 0L);

                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        threadPool.submit(runnable);
    }

    public void deleteFromTournament(UUID uuid, Tournament tournament) {
        Runnable runnable = () -> {
            String query = String.format("DELETE FROM %s WHERE player = ?;", tournament.getIdentifier());

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, uuid.toString());

                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        threadPool.submit(runnable);
    }

    public void reloadLeaderboard(Tournament tournament) {
        threadPool.submit(getReloadTournamentRunnable(tournament));
    }

    public Runnable getReloadTournamentRunnable(Tournament tournament) {
        return () -> {
            String query = String.format("SELECT * FROM %s ORDER BY score DESC;", tournament.getIdentifier());
            TournamentLeaderboard leaderboard = tournament.getLeaderboard();

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                int pos = 1;
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("player"));
                    long score = rs.getLong("score");

                    TournamentValue tournamentValue = new TournamentValue(uuid, score);
                    leaderboard.setPosition(tournamentValue, pos);
                    pos++;
                }
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    public void clearTournament(Tournament tournament) {
        Runnable runnable = () -> {
            String query = String.format("DELETE FROM %s;", tournament.getIdentifier());

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        threadPool.submit(runnable);
    }

    public void addReward(RewardInstance rewardInstance) {
        Runnable runnable = () -> {
            String query = String.format("INSERT INTO %s (player, command) VALUES (?, ?);", rewardsTableName);

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, rewardInstance.getPlayer().toString());
                stmt.setString(2, rewardInstance.getCommand());
                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        threadPool.submit(runnable);
    }

    public CompletableFuture<List<String>> getRewardCommands(UUID player) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> rewardCommands = new ArrayList<>();
            String query = String.format("SELECT command FROM %s WHERE player = ?;", rewardsTableName);

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, player.toString());

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String command = rs.getString("command");
                    rewardCommands.add(command);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            return rewardCommands;
        }, threadPool);
    }

    public void clearRewards(UUID player) {
        Runnable runnable = () -> {
            String query = String.format("DELETE FROM %s WHERE player = ?;", rewardsTableName);

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, player.toString());
                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        threadPool.submit(runnable);
    }

    public void setPlayerName(Player player) {
        playerNameCache.put(player.getUniqueId(), player.getName());
        
        Runnable runnable = () -> {
            String query = String.format(
                    "INSERT INTO %s (uuid, name) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE name = VALUES(name);",
                    playersTableName
            );

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setString(2, player.getName());
                stmt.executeUpdate();
            }
            catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };

        threadPool.submit(runnable);
    }

    public CompletableFuture<String> getPlayerName(UUID uuid) {
        String cachedName = playerNameCache.get(uuid);
        if (cachedName != null) {
            return CompletableFuture.completedFuture(cachedName);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            String query = String.format("SELECT name FROM %s WHERE uuid = ?;", playersTableName);

            try (Connection connection = getSQLConnection()) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, uuid.toString());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    playerNameCache.put(uuid, name);
                    return name;
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            return null;
        }, threadPool);
    }


    public void shutdownPool(){
        try {
            LitLibs libs = LitTournaments.getLitLibs();
            libs.getLogger().log("Shutting down thread pool");
            threadPool.shutdownNow();
            boolean closed = threadPool.awaitTermination(20, TimeUnit.SECONDS);
            if (!closed)
                libs.getLogger().error("Shutting down thread pool failed");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
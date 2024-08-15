package me.waterarchery.littournaments.api.events;

import me.waterarchery.littournaments.models.Tournament;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PointAddEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;
    private final Tournament tournament;
    private final UUID player;
    private int amount;
    private final String actionName;

    public PointAddEvent(Tournament tournament, UUID player, int amount, String actionName) {
        this.tournament = tournament;
        this.player = player;
        this.amount = amount;
        this.actionName = actionName;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getActionName() {
        return actionName;
    }

}

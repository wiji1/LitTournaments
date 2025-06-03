package me.waterarchery.littournaments.api.events;

import me.waterarchery.littournaments.enums.RedisMessage;
import me.waterarchery.littournaments.models.Tournament;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

public class RedisUpdateEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final RedisMessage messageType;
    private final List<String> args;

    public RedisUpdateEvent(RedisMessage messageType, List<String> args) {
        this.messageType = messageType;
        this.args = args;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public RedisMessage getMessageType() {
        return messageType;
    }

    public List<String> getArgs() {
        return args;
    }

}

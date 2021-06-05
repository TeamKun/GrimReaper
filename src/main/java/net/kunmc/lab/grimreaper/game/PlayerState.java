package net.kunmc.lab.grimreaper.game;

import org.bukkit.entity.Player;

public class PlayerState {
    private Player player;

    public PlayerState(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
package ru.kredwi.qa.game.request;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public record RequestInfo(String gameName, Player sender, Location startLocation) {

}

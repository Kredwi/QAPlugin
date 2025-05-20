package ru.kredwi.qa.game.impl;

import org.bukkit.entity.Player;

public record GameInfo(String name, Player owner, int blocksToWin) {}

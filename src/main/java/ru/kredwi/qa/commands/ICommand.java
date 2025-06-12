package ru.kredwi.qa.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.permissions.Permissible;

import ru.kredwi.qa.exceptions.QAException;

public interface ICommand extends TabCompleter{
	public String getName();
	public int needArgs();
	public boolean isSkipCheckArgs();
	public boolean isCommandOnlyForPlayer();
	public boolean isHaveNeedsPermissions(Permissible permissible);
	public void run(ICommandController commandController,
			CommandSender sender, Command cmd, String[] args)
		throws QAException;
}

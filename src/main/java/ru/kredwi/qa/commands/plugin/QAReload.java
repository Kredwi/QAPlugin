package ru.kredwi.qa.commands.plugin;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.exceptions.QAException;

public class QAReload extends CommandAbstract{

	public QAReload() {
		super("qareload", false, "qaplugin.plugin.base");
	}

	@Override
		public void run(ICommandController commandController, CommandSender sender, Command cmd, String[] args)
			throws QAException {
		
		commandController.getPlugin().reloadConfig();
		
		commandController.getConfigManager().setConfig(commandController.getPlugin().getConfig());
		
		sender.sendMessage(commandController.getConfigManager().getAsString(ConfigKeys.PLUGIN_SUCCESS_RELOADED));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!isHaveNeedsPermissions(sender)) return Collections.emptyList();
		return null;
	}

}

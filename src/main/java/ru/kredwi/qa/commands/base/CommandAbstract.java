package ru.kredwi.qa.commands.base;

import java.util.Objects;

import org.bukkit.permissions.Permissible;

public abstract class CommandAbstract implements ICommand {

	private static final String PERMISSION_FOR_ALL = "qaplugin.*";
	
	private final String name;
	private final int needArgs;
	private final boolean skipCheckArgs;
	private final boolean commandOnlyForPlayer;
	private String[] permissions;
	
	
	public CommandAbstract(String name, boolean commandOnlyForPlayer, String...permissions) {
		this(name, 0, commandOnlyForPlayer, permissions);
	}
	
	public CommandAbstract(String name, int needArgs, boolean commandOnlyForPlayer, String...permissions) {
		this(name, needArgs, false, commandOnlyForPlayer, permissions);
	}
	
	public CommandAbstract(String name, int needArgs, boolean skipCheckArgs, boolean commandOnlyForPlayer, String...permissions) {
		
		Objects.requireNonNull(name);
		Objects.requireNonNull(needArgs);
		Objects.requireNonNull(commandOnlyForPlayer);
		
		this.name = name;
		this.needArgs=needArgs;
		this.skipCheckArgs = skipCheckArgs;
		this.commandOnlyForPlayer = commandOnlyForPlayer;
		this.permissions =
				permissions == null
				? new String[0]
				: permissions;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int needArgs() {
		return needArgs;
	}

	@Override
	public boolean isCommandOnlyForPlayer() {
		return commandOnlyForPlayer;
	}

	@Override
	public boolean isHaveNeedsPermissions(Permissible permissible) {
		if (permissions.length == 0 || permissible.isOp() || permissible.hasPermission(PERMISSION_FOR_ALL))
			return true;
		
		for (String permission : permissions) {
			
			// sender dont have permissions
			if (!permissible.hasPermission(permission))
				return false;
		}
		
		// sender have permissions
		return true;
	}

	@Override
	public boolean isSkipCheckArgs() {
		return skipCheckArgs;
	}

}

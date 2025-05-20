# QAPlugin - Question and Answer Plugin for Minecraft

## Description
QAPlugin will add a Question/Answer minigame to your server.
**1.19**+

## Teams

### Basic Commands
- `/question <question text>` - Ask the players a question (for the game owner only)
- `/answer <answer>` - Answer the current question

### Game Management
- `/creategame <name> <blocks to win>` - Create a new game
- `/startgame <name>` - Start the game
- `/deletegame <name>` - Delete the game
- `/path <game name> [player]` - Invite the player to the game or join the game yourself
- `/acceptgame [game name]` - Accept the invitation to the game
- `/deleteplayer <game name> <player>` - Remove a player from the game

## Configuration
The plugin settings are located in the `plugins/QAPlugin/config.yml` file. Basic parameters:

- `enabled-blocks` - List of blocks used for construction
- `win-sound` - Sound when winning
- `block-place-sound` - The sound when placing the block
- `build-delay` - Delay before the start of construction (in ticks)
- `build-period` - Construction period (in ticks)

## Links
- [GitHub](https://github.com/Kredwi/QAPlugin)
- [List of Bukkit materials](https://helpch.at/docs/1.19.4/org/bukkit/Material.html)
- [List of Bukkit sounds](https://helpch.at/docs/1.19.4/org/bukkit/Sound.html)

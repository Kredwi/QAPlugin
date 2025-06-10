# QAPlugin - Question and Answer Plugin for Minecraft

## Description
QAPlugin will add a Question/Answer minigame to your server.
Tested in: **NONE**

## How to use
1.  A player with game creation permissions uses the command `/creategame <game name>`, specifying the required arguments.
2.  The player who created the game must then create paths for the players using the command `/path <game name> [player]`.
 - **NOTE**: The direction and starting location are set based on the *command sender's* current position.
3.  After setting paths for all players, the command `/startgame` must be executed. This will initialize the game, create the necessary blocks, and teleport all players to their starting points.

## Commands

### Basic Commands
- `/question <question text>` - Ask the players a question (for the game owner only)
- `/answer <answer>` - Answer the current question

### Game Management
- `/creategame <name> <blocks to win>` - Create a new game
- `/startgame <name>` - Start the game
- `/deletegame <name>` - Delete the game
- `/path <game name> [player]` - Invite the player to the game or join the game yourself
- `/acceptgame [game name]` - Accept the invitation to the game
- `/denygame [game name]` - Deny the invitation to the game
- `/deleteplayer <game name> <player>` - Remove a player from the game
- `/deleteblock <playername> [count of layer]` - Delete layers from the path for Player
- `/qareload` - Reload the plugin configuration

## Configuration
The plugin settings are located in the `plugins/QAPlugin/config.yml` file. Basic parameters:

- `enabled-blocks` - List of blocks used for construction
 - `win-sound` - Sound when winning
 - `block-place-sound` - The sound when placing the block
 - `build-delay` - Delay before the start of construction (in ticks)
 - `build-period` - Construction period (in ticks)
 - `max-requests-size` - Max number of requests to game
 - `allow-destroy-any-block` - Allow breaking blocks by paths **(DANGEROUS FOR THE WORLD!! BLOCKS CANNOT BE RESTORED)**
 - `spawn-display-texts` - Enables spawning of display texts around blocks (can cause high server load (**1** block = **5** entites of `DisplayText`))
 - `indestructible-block-encountered` - What to do if the block cannot be broken (paths cannot continue to be built because the block cannot be broken)

- `end-game-winner-firework` - The fireworks sector, which at the end of the game, the winners will receive a fireworks display.
 - `enable` - Enables this effect in side effects
 - `flicker` - Set whether the firework effect should flicker.
 - `trail` - Set whether the firework effect should have a trail.
 - `type` - The type or shape of the effect.
 - `colors` - An array within an array that contains rgb primary colors for a fireworks explosion
 - `fades` - An array within an array that contains the primary colors for fading the fireworks

- `database`
 - `enable` - enable using database
 - `username` - username for database account
 - `password` - password for database account
 - `host` - host of database (ip)
 - `port` - port of database
 - `database` - database name for plugin

*and other...*

### Why Spawn Display Texts not recommended?
Because everything happens in one tick of the server, which means that 3 blocks are created (the main block and two perpendicular blocks on the left and right) in one tick of the server, and 5 objects are created on the same tick of the server. This can create a significant load on the server, especially if several players are playing at the same time. Therefore, we strongly recommend against enabling this feature.

### Indestructible Block Encountered
- `IGNORE` - Skips this block and does not process it.
- `END_GAME` - Calculates the winner and ends the game. (have more bugs (dont use))

## Links
- [Plugin issues](https://github.com/Kredwi/QAPlugin/issues)
- [List of Bukkit materials](https://helpch.at/docs/1.19.4/org/bukkit/Material.html)
- [List of Bukkit sounds](https://helpch.at/docs/1.19.4/org/bukkit/Sound.html)

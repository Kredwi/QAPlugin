# QAPlugin - Question and Answer Plugin for Minecraft

## Description
QAPlugin will add a Question/Answer minigame to your server.
Tested in: **1.19.4**

## How to use
1.  A player with game creation permissions uses the command `/creategame <game name>`, specifying the required arguments. (Corrected grammar and clarity.  Using "uses" is more natural.)
2.  The player who created the game must then create paths for the players using the command `/path <game name> [player]`.
 - **NOTE**: The direction and starting location are set based on the *command sender's* current position. (Replaced "command sender" with "command sender's" to show possession. Replaced "place of start" with "starting location" for better flow.)
3.  After setting paths for all players, the command `/startgame` must be executed. This will initialize the game, create the necessary blocks, and teleport all players to their starting points. (Replaced "proinitsiliziruet" with a proper spelling. Replaced "to start points" with "to their starting points" for better flow. Changed order of sentences for better flow)

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
- `/deleteplayer <game name> <player>` - Remove a player from the game

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

### Indestructible Block Encountered
- `IGNORE` - Skips this block and does not process it.
- `END_GAME` - Calculates the winner and ends the game.

## Links
- [Plugin issues](https://github.com/Kredwi/QAPlugin) *(pls create issues pls)*
- [List of Bukkit materials](https://helpch.at/docs/1.19.4/org/bukkit/Material.html)
- [List of Bukkit sounds](https://helpch.at/docs/1.19.4/org/bukkit/Sound.html)

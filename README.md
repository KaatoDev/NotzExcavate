<div align="center">
  
##### **NotzPlugins**
<img src="https://repo.kaato.dev/images/plugins/NotzExcavate2.png" alt="Notz Excavate" width="150"/>
<a href="https://modrinth.com/plugin/notzscoreboard">
<img src="https://repo.kaato.dev/images/plugins/NotzScoreboard2.png" alt="Notz Scoreboard" width="150"/>
</a>
<a href="https://modrinth.com/plugin/notzwarps">
<img src="https://repo.kaato.dev/images/plugins/NotzWarps2.png" alt="Notz Warps" width="150"/>
</a>

#
<img src="https://repo.kaato.dev/images/plugins/NotzExcavate2.png" alt="Notz Warps"/>

#
NotzExcavator is the best plugin for excavating terrain in PlotSquared; it's simple and fast, with customizable and synchronized timing, starting at a minimum of 1 minute.

<br/>

## Information

### `Excavator`
A unique excavation system for each plot that saves all information such as: number of blocks, allowed and prohibited blocks, total and remaining time, and current status (not started, started, paused, or complete).

### `Shovel`
A unique, unmistakable item system that activates the excavator. It features special lore, a duration display, and a replacement system when you change its duration or display.

</div>

<br/>

## Dependencies
- PlotSquared

<br/>

## Placeholders
 - `{default}` - A default placeholder for an internal message string.

<br/>

## Permissions
- `notzexcavate.admin` - Enables the player to use the /nex admin command.
- `notzexcavate.useany` - Allows the player to use the Excavator on any plot.

<br/>

## Commands
### `/status`-`/excavate`-`/excavator`
 - Displays the Excavator's status on the current plot.

### `/nex`
 - `create` \<name> \<display> - Creates a new Shovel; 
 - `excavator` - Enters the Excavator command menu;
 - `list` - Views the list of existing Shovels;
 - `restart` - Restart the Excavators;
 - `save` - Saves the Excavators and Shovels;
 - `status` - Views the Excavator status for the plot;
 - `excavator`
   - `all` - Views the list of all existing Excavators;
   - `list` - Shows the list of incompleted Excavators;
   - `completed` - Shows the list of completed Excavators;
   - `remove` - Removes the existing Excavator from the plot;
   - `stop` - Stops the Excavator from the plot;
 - `<shovel>`
   - `addAllowed` \<block> - Adds a block to the allowed list;
   - `addBlocked` \<block> - Adds a block to the blocked list;
   - `clearAllowed` - Resets the allowed list;
   - `clearBlocked` - Resets the blocked list;
   - `delete` - Deletes the Shovel;
   - `get` \<player> - Gives the Shovel item;
   - `give` \<player> (quantity) - Gives the Shovel item to a player (only in console);
   - `remAllowed` \<block> - Removes a block from the allowed list;
   - `remBlocked` \<block> - Removes a block from the blocked list;
   - `setDisplay` \<display> - Changes the Shovel display;
   - `setDuration` \<minutes> - Changes the Shovel duration (in minutes);
   - `setMaterial` - Changes the Shovel material item to the material of the item in hand;
   - `updateItem` - Updates the Shovel item in the inventory of all online players.

<br/>
<sub> | <> required argument. | ( ) optional argument. | </sub>

#

<sub> Tested versions: 1.8 - 1.12.2 </sub>


# LottaLogs

![LottaLogs](LottaLogs.png)

LottaLogs is a moderation tool designed to keep track of dozens of actions that happen on the server as well as to offer users easy access to this and any other log related data.  
This plugin was originally made for the [Altitude](https://alttd.com/) server and it was meant to be used along with [CoreProtect](https://github.com/PlayPro/CoreProtect). It doesn't provide some things that CoreProtect provides (like blocks placed/broken, entities killed, etc.) and the logs are specific to what Altitude needed. Getting CoreProtect is strongly suggested as it is an amazingly useful moderation tool.  
If you require any changes/additions to the plugin contact me at `daki2117` on Discord.

* [Features](#features)
* [For Users](#for-users)
* [Configuration](#configuration)
* [Permissions](#permissions)
* [searchlogs](#searchlogs)
* [searchlogs normal](#searchlogs-normal)
* [searchlogs special](#searchlogs-special)
* [Types of Special Logs](#types-of-special-logs)
* [searchlogs additional](#searchlogs-additional)
* [For Developers](#for-developers)
* [Building the Plugin](#building-the-plugin)

## Features

- Saves dozens of different actions done on the server to easy to read log files
- Allows users to search through the server's, this plugin's or any other plugin's logs with easy to use in-game commands
- Makes everything easily accessible with the option of sending results to Discord
- Reliable - nothing remains a he-said-she-said, know everything that happened on the server
- Multi-threaded - server's performance is never impacted
- Easily configurable - ready to go out of the box but offers great configurability
- Storage concious - by using easily compressable txt files the plugin saves valuable storage resources

# For Users
## Configuration

The plugin is ready to go out of the box but it also offers per-log configurability for all of your needs.

Each log has the following options:
* Enabled (true/false) - Whether or not that log is enabled
* DaysOfLogsToKeep (integer) - At midnight local time, the logs of that day are compressed and moved to compressed-logs. The plugin automatically deletes these logs after the set number of days. Set to -1 if you want these logs to never be deleted
* BlackListedString (list of strings) - If a line being written to this log contains any of the provided strings, it won't be written

Other config options:

* Logging.PlayerLocationLog.WriteFrequencySeconds (integer) - How often in seconds all of the player's locations are saved to PlayerLocationLog
* Logging.CreatingBlankFiles (all/enabled/none) - Should the plugin create blank log files at midnight, startup or reload. all - it will create blank files for all logs, enabled - it will create blank files only for the enabled logs, none - it won't create any blank files, files will be created automatically as they need to be written to
* SearchLogs.OutputPath (string) - A path to where the search result files should be saved. If you have multiple worlds/machines running you can save results to a shared folder, each result includes the name of the world
* SearchLogs.FileSizeUploadLimitMB (integer) - Number of megabytes that a result file can be before it gets compressed. If the compressed file is above the limit it won't be sent to Discord
* SearchLogs.DiscordChannelWebhook (string) - Webhook URL of the Discord channel in which you want the result files to be sent. To create it press Edit Channel -> Integrations -> Create Webhook -> Copy Webhook URL
* SearchLogs.NormalSearchBlacklistedStrings (list of strings) - If any of the provided strings appear in a line that is being read by the normal search, the line won't be saved to the result.
* SearchLogs.ProgressTextColor (any [ChatColor](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html)) - Color of the search progress bar text
* SearchLogs.ProgressBarColor (any [BarColor](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/boss/BarColor.html)) - Color of the search progress bar
* AdditionalLogs (Name: string Path: string) - any other logs that you want the plugin to have access to through /searchlogs additional. These log's names need to start with the date (yyyy-mm-dd)

## Permissions

```
/searchlogs normal - lottalogs.searchlogs.normal
/searchlogs special - lottalogs.searchlogs.special
/searchlogs additional - lottalogs.searchlogs.additional
/reload - lottalogs.reload
```

## searchlogs

This command allows you to search the server's (normal), this plugin's (special) or any other plugin's (additional) logs. It creates a result file that gets sent to the Discord channel if a Webhook URL is provided in the config. If a webhook is configured but you don't want that particular search to get sent add `-silent` to the command (works for all searches).  
Using Notepad++ for reading the result file is recommended, as well as CTRL+F. If a result file gets compressed (.gz) because of it's size, you can use any compression software (7-Zip, WinZip, WinRAR) to uncompress it.  
All searches support the use of regular expressions. The number argument in all searches represents the number of days to search for, 0 means today (midnight local time to now), 1 means yesterday and today and so on.

## searchlogs normal

`/searchlogs normal <numberOfDays> <searchString>`

To search through the server's log use this command.

Some examples of how to use it (these examples depend on the setup of your server):

```
To get everything about a player:
/searchlogs normal 7 Player1

To get all commands that a player has used:
/searchlogs normal 7 Player1.*command

To get all chat messages tha a player has sent:
/searchlogs normal 7 chat.*Player1

A specific command that a player has used (this case /tpa):
/searchlogs normal 7 Player1.*/tpa

Regex allows us to search for multiple players and multiple commands at the same time:
/searchlogs normal 7 (Player1|Player2|Player3).*(/command1|/command2|/command3)
/searchlogs normal 7 chat.*(Player1|Player2|Player3)
```

## searchlogs special

`/searchlogs special <numberOfDays> <arguments>`

To search through this plugin's logs use this command. Unlike the normal logs, these logs don’t use terms like “issued server command” to explain things. You can see what the log does from it’s name and inside it stores data separated by the ‘|’ symbol. You need at least one argument when using it, but you can also use multiple to narrow down the search.

Using tab complete when writing this command is highly advised. Some things to keep in mind when searching. You need a space after the Argument: and the data you give it. Log names are case sensitive. The Time: argument isn’t the actual amount of time to look back for, it’s the exact moment that log entry is made. You can use it to define a specific day/hour/minute/etc. that you want to search.

You can use a -radius: argument when searching the logs which have a Location: or Area:

You can search through multiple of the special log types at once. It’s done by listing all the log names with a ‘,’ between them. Don’t put a space after that comma. Some logs don’t have the same arguments as others e.g. DroppedItemsLog doesn’t have a Cause argument, PickedUpItemsLog doesn’t have Entity etc. Think about which ones you’re combining in one search.

Examples:
```
/searchlogs special DroppedItemsLog 3 User: Player1 Item: diamond -radius: 50
/searchlogs special GriefPreventionClaimsCreatedLog 100 User: Player1
/searchlogs special CrazyCratesCratePrizesLog 14 User: Player1 Items: bow Crate: supercrate
/searchlogs special DroppedItemsLog,PickedUpItemsLog 7 User: Player1 Time: Sep 20
```

## Types of Special Logs

* ChatWithLocationLog - chat messages that players send and their location
* CommandsWithLocationLog - commands that players send and their location
* CrazyCratesCratePrizesLog - crate prizes from the CrazyCrates plugin
* DroppedItemsLog - items that players drop
* DroppedItemsOnDeathLog - items that players drop when they different
* EggsThrownLog - players throwing eggs
* FarmLimiterLog - entities deleted by the FarmLimiter plugin
* GriefPreventionClaimsCreatedLog - claims created by the GriefPrevention plugin
* GriefPreventionClaimsDeletedLog - claims deleted by the GriefPrevention plugin
* GriefPreventionClaimsExpiredLog - claims modified by the GriefPrevention plugin
* GriefPreventionClaimsResizedLog - claims resized by the GriefPrevention plugin
* InventoryOnJoinLog - items in the player's inventory when they join
* InventoryOnQuitLog - items in the player's inventory when they quit
* ItemsBrokenLog - items that break due to losing durability
* ItemsDespawnedLog - dropped items that despawn
* ItemsDestroyedLog - dropped items that get destroyed, items destroyed by the void are not logged
* ItemsPlacedInItemFramesLog - items placed in item frames
* ItemsTakenOutOfItemFramesLog - items taken out of item frames
* LightningStrikesLog - lightning strikes
* MCMMORepairUseLog - items that players right click while looking at an iron block
* MinecartsDestroyedLog - minecarts that get destroyed
* MyPetItemPickupLog - pets from the mypet plugin picking up items
* PickedUpItemsLog- items that players pick up
* PlayerLocationLog - all of the player's locations every X seconds
* TridentsLog - players throwing, picking up or hitting something with a trident
* UIClicksLog - inventory clicks that players make

## searchlogs additional

`/searchlogs additional <logName> <numberOfDays> <searchString>`

Works the same as the normal log search except it searches any additional logs that you've added.

# For Developers
## Building the plugin

To build LottaLogs simply clone it and use `mvn package`. You'll encounter an error saying that you are missing system scope dependencies for plugins in the lib directory. If you need these, download them yourself and put them in the directory, if you don't, remove their dependencies, their APIs.java entries and their logs.

To add a new log simply add it to com.daki.lottalogs.logs and make it extend Log. Copy the format from other logs and you're good to go. Everything else like event registering, config adding, blank file creation, tab completes, searching is done automatically.
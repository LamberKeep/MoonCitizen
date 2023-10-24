# MoonCitizen

## Description

Is [Paper](https://papermc.io/software/paper) plugin working on latest versions on Minecraft and allows to create NPCs on your server by using [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/). 

## To Do list

There are still a few shortcomings in this plugin that I would add if I weren't lazy, such as:
- [x] Message localization
- [ ] NPC head rotation when following a player
- [ ] NPC gravity
- [ ] NPCs following the player (pets)
- [ ] More NPC interaction and actions
- [ ] Action conditions (like `click on the cat with the fish to feed it` or `hit the npc 10 times to get the achievement`)
- [ ] Events and API

# Permissions

| Permission         | Default | Description                              |
|--------------------|---------|:-----------------------------------------|
| mooncitizen.npc    | false   | Allows you to control NPCs               |
| mooncitizen.plugin | false   | Allows to control the configuration file |
| mooncitizen.*      | op      | Allows to control plugin                 |

# Commands

## Plugin command

| Command             | Description                 |
|---------------------|-----------------------------|
| /mooncitizen        | Shows command usage         |
| /mooncitizen reload | Reloads NPCs                |
| /mooncitizen save   | Save NPCs and configuration |

## NPC command

| Command                          | Description                     | Note                                                                                                                |
|----------------------------------|---------------------------------|---------------------------------------------------------------------------------------------------------------------|
| /npc                             | Shows command usage             |                                                                                                                     |
| /npc <id> info                   | Shows NPCs information          |                                                                                                                     |
| /npc <id> create                 | Creates new NPCs                | You can use any number as the entity id, as long as it is not repeated in the configuration file                    |
| /npc <id> remove                 | Removes new NPCs                |                                                                                                                     |
| /npc <id> move                   | Teleports an NPC to you         |                                                                                                                     |
| /npc <id> name <name>            | Changes the NPC's display name  | To remove the display name, use `""`                                                                                |
| /npc <id> type <type>            | Changes the NPC's entity type   | Check [entity types list](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html) before use it |
| /npc <id> invisible <true/false> | Makes NPC invisible             | To make a hologram (text in the air) you need to set the entity type to `ARMOR_STAND` and set display name          |
| /npc <id> follow <true/false>    | Makes the NPC's head follow you |                                                                                                                     |

## NPC action command

| Command                                         | Description                                                      | Note                                                                                                            |
|-------------------------------------------------|------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| /npc <id> action                                | Shows command usage                                              | As with NPCs, you can use any number as an action ID, just so that it is not repeated in the configuration file |
| /npc <id> action <action_id> create <name>      | Creates new NPC's action by specified name                       | Check [action section](/README.md#actions) before use it                                                        |
| /npc <id> action <action_id> remove             | Removes new NPC's action                                         |                                                                                                                 |
| /npc <id> action <action_id> right <true/false> | Allows/denies interactions with NPCs with the right mouse button |                                                                                                                 |
| /npc <id> action <action_id> left <true/false>  | Allows/denies interactions with NPCs with the left mouse button  |                                                                                                                 |
| /npc <id> action <action_id> data <data>        | Changes NPC's action data                                        | Check [action section](/README.md#actions) before use it                                                        |
| /npc <id> action <action_id> info               | Shows NPC's action data                                          |                                                                                                                 |

## Actions

| Name        | Data                 | Description                              | Note                                                                                                                                                          |
|-------------|----------------------|------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| empty       | String               | Does nothing (what a coincidence)        | Created when you create a non-existent action. To avoid making mistakes, create action in accordance with the name column                                     |
| message     | Colored chat message | Sends message to player                  | Use ampersand symbol and [minecraft color codes](https://www.digminecraft.com/lists/color_list_pc.php) to make message prettier (example: `&cHello &aWorld!`) |
| run_command | String               | Enters a command on behalf of the player |                                                                                                                                                               |
| teleport    | Location             | Teleports player to location             | To change this type of data, you do not need to enter anything in the chat, just write a command and the data will adjust to your current position            |
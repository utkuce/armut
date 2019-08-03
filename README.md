# armut
Downloads the necessary mods to join a Minecraft server, given that the server also has the armut mod installed.

## Usage

Downlaod the [universal jar file](https://github.com/utkuce/armut/releases/download/v0.2.1/armut-0.2.1.jar).

### Client
- Put the jar in your mods folder
- Launch the game and set the address of the minecraft server you want to join in mod options and restart the game. Mods installed on the server will be downloaded to the minecraft mods folder from now on with every launch.
  - Note that, for the newly downloaded mods to be initialized game needs to be restarted again.
- Optionally set downloadModConfigs to true to download the config files of the mods as well
  - Note that this will override the client configs with the configs from the server everytime the game is launched
### Server
- Put the jar under mods folder with the others
- Make sure the port 27688 is open
- Launch the server

# LitTournaments

LitTournaments is a Minecraft plugin designed to manage and run tournaments on your server. It leverages the Spigot API and integrates with various libraries and databases to provide a seamless experience.

## Features

- Tournament management
- Leaderboard tracking
- Command handling
- Database integration
- Multi server support
- Easy to implement Tournament API

## Requirements

- Java 16
- LitLibs

## Configuration

Configure the plugin by editing the `config.yml` file located in the `plugins/LitTournaments` directory. Here you can set various options such as leaderboard refresh intervals and database connection details.

## Usage

### Commands

- `/tournament` - Shows the main GUI.
- `/tournament join` - Joins to a tournaments
- `/tournament leave` - Leaves from a tournaments
- `/tournament reload` - Reloads the plugin.

## Integrating

  Integrating is very easy. Just add LitMinions to local dependency or import it with Maven. After that you should softdepend LitMinions to your in your plugin.yml. It should be like this ```softdepend: [ LitMinions ]```

  Latest version: ![Release](https://jitpack.io/v/WaterArchery/LitTournaments.svg)
  
  Maven Repository
  
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>

  Dependency

	<dependency>
	    <groupId>com.github.WaterArchery</groupId>
	    <artifactId>LitTournaments</artifactId>
	    <version>1.0.7</version>
	    <scope>provided</scope>
	</dependency>
 
## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.

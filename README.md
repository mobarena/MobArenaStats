# MobArenaStats [![Build Status](https://github.com/garbagemule/MobArenaStats/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/garbagemule/MobArenaStats/actions/workflows/build.yml)

MobArenaStats is a _plugin extension_ for [MobArena](https://github.com/garbagemule/MobArena).
The extension collects stats from MobArena sessions into persistent storage such as MySQL, MariaDB, and SQLite databases.
It hooks into MobArena's command handler to provide commands for querying and managing the stats.


## Getting Started

Download a copy of the latest MobArenaStats build and place it in your server's `plugins` folder.
You can grab a build from the _Artifacts_ section of the latest run of the [build workflow](https://github.com/garbagemule/MobArenaStats/actions/workflows/build.yml) in GitHub Actions, or you can join the MobArena Discord server and grab one from the `#test-builds` channel.

By default, MobArenaStats uses a local SQLite database called `stats.db` located in the MobArenaStats plugin folder.
To set up a different data store, please refer to the [Configuration](#configuration) section below.


## Configuration

Upon first run, a `config.yml` file will be generated in the MobArenaStats plugin folder.
It consists of a single section, the `store` section, which is used to set up the data store.

MobArenaStats _natively_ supports a couple of different data stores:

- SQLite
- MySQL
- MariaDB
- CSV files

The following sections describe the store-specific config properties.


### SQLite

[SQLite](https://www.sqlite.org/) is a database engine embedded in a library.
It uses a single file on disk to store all of its data.

By default, MobArenaStats uses the filename `stats.db` inside its own plugin folder, but this can be altered with the `filename` property, which is the _relative path_ from the plugin folder.

#### Example 
An example SQLite database configuration that stores its data in a file called `mobarena_stats.db` in the server root folder (`../..` is the server root relative to the plugin folder).

```yml
store:
  type: sqlite
  filename: ../../mobarena_stats.db
```


### MySQL

[MySQL](https://www.mysql.com/) is one of the most well-known relational databases and a common sight in database offerings from various providers.

MobArenaStats requires a `host`, a `port`, a `database` name, and credentials in the form of a `username` and a `password` to connect to a MySQL database.
By default, the plugin tries to connect to `localhost` on port 3306 with a database called `mobarena_stats`, but these can all be altered.
There are no defaults for the `username` and `password`.

**Note:** The `database` must be created manually!

MobArenaStats is tested with MySQL 5.7.

#### Example
An example MySQL database configuration that connects to a database called `mastats` on localhost port 1337 with username `bob` and password `saget`.

```yml
store:
  type: mysql
  host: localhost
  port: 1337
  database: mastats
  username: bob
  password: saget
```


### MariaDB

[MariaDB](https://mariadb.org/) is a fork of MySQL by the original MySQL authors that aims to be free and open-source forever.

MobArenaStats supports MariaDB via the [drop-in replacement](https://en.wikipedia.org/wiki/Drop-in_replacement) compatibility with MySQL.
This means that the configuration of MariaDB databases is _exactly_ the same as for [MySQL](#mysql) databases (including setting `type: mysql`).

MobArenaStats is tested with MariaDB 10.4.


### CSV files

MobArenaStats can persist session stats on the file system in the CSV format.
It stores two files, `sessions.csv` and `players.csv`, with overall and player-specific session data, respectively.
By default, the plugin stores the data files in a local `data` subfolder in the plugin folder, and it uses semicolons (`;`) to separate values.

**Note:** CSV stores _do not_ support data queries!

#### Example
An example of a CSV configuration that stores the data files in a `mobarena_stats` folder in the server root (`../..` is the server root relative to the plugin folder) and uses commas (`,`) to separate values.

```yml
store:
  type: csv
  folder: ../../mobarena_stats
  separator: ,
```


## Commands

MobArenaStats introduces new subcommands to the `/ma` base command in MobArena.
These include player commands for querying the data store, and server admin commands for exporting from and importing into data stores.

The following sections describe each command (and the permission required to run it) and its arguments, if any.


### Queries

Query commands dip into the data store to fetch global stats, arena-specific stats, or player-specific stats.

#### Global stats

- `/ma global-stats`
- `mobarenastats.command.global-stats`

Get a summary of session stats across all arenas and all players:

- Total sessions: total number of unique arena sessions
- Total duration: sum of all session durations
- Total kills: sum of all kills made in arena sessions
- Total waves: total number of waves spawned in arena sessions

#### Arena stats

- `/ma arena-stats <arena-slug>`
- `mobarenastats.command.arena-stats`

Get a summary of stats across all sessions in the arena denoted by the given `<arena-slug>`:

- Highest wave: the highest wave number spawned in the arena
- Longest duration: the duration of the longest session in the arena
- Most kills: the highest number of total kills in a session in the arena
- Total sessions: total number of unique sessions in the arena
- Total duration: sum of durations for all sessions in the arena
- Total kills: sum of all kills made in the arena
- Total waves: total number of waves spawned in the arena

#### Player stats

- `/ma player-stats <player-name>`
- `mobarenastats.command.player-stats`

Get a summary of session stats for the player with the given `<player-name>`:

- Total sessions: total number of unique arena sessions for the player
- Total duration: sum of all session durations for the player
- Total kills: sum of all kills made in arena sessions by the player
- Total waves: total number of waves spawned in arena sessions with the player


### Import & Export

All data stores can export their stats, and all data stores can import stats exported from other data stores.
The plugin always exports data as SQLite database files, and it can only import files that follow the same format and naming convention.

#### Export

- `/ma export-stats`
- `mobarenastats.command.export-stats`

Export the stats in the current data store to an SQLite database file.
All data exports follow the naming convention `stats.export-<timestamp>.db`, where `<timestamp>` is a UNIX timestamp of the time that the export was started.

**Note:** Exporting stats is a slow operation and will take time proportional to the amount of data in the current store.
The operation runs _off_ the main thread, so it should not impact performance.

#### Import

- `/ma import-stats <filename>`
- `mobarenastats.command.import-stats`

Import the stats from the data export denoted by the given `<filename>`.
The file must be an SQLite database created with the [export command](#export).

**Note:** It is not recommended to import stats into a non-empty data store.
Always import into an empty store unless you know what you are doing.

**Note:** Importing stats is a very slow operation and will take time proportional to the amount of data in the export file.
The operation runs _off_ the main thread, so it should not impact performance.


## Getting Help

If you run into problems or need help with something, feel free to hop on the MobArena Discord server: [Instant Invite](https://discord.gg/5tnwQvC)

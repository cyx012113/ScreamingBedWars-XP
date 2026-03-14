# ScreamingBedWars-XP

ScreamingBedWars-XP is a **modified fork** of [ScreamingBedWars](https://github.com/ScreamingSandals/BedWars) (branch `ver/0.2.x`).  
It directly modifies the original plugin source to introduce an **experience-based economy**, replacing the classic resource economy. This is a complete BedWars implementation and does **not** require the original ScreamingBedWars plugin to run.

Forked by [@cyx012113](https://github.com/cyx012113). Original source code by [@Misat11](https://github.com/Misat11).

[![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0)

## ✨ Core Features (ScreamingBedWars-XP)

*   **💎 Experience Economy**: Picking up iron, gold, emeralds, and copper automatically converts them into **Vault economy balance** (your experience currency). The player's Minecraft experience level visually reflects their balance.
*   **💀 Death & Reward System**: When a player dies, a quarter of their balance is transferred to the killer, and another quarter is lost from the victim, creating a high-stakes gameplay loop.
*   All original ScreamingBedWars features (teams, beds, shops, generators, etc.) remain fully functional and are now powered by the experience economy.

## 🔥 Fire – Independent World Protection Plugin (included in this repository)

[Fire](https://github.com/cyx012113/screamingbedwars-xp/tree/main/Fire) is a separate, lightweight plugin built with Maven. It is **not required** for ScreamingBedWars-XP but is recommended to enhance server protection and lobby management.

### Features of Fire

*   **🛡️ Fire Immunity**: Completely disables fire damage, entity combustion, and block burning.
*   **🏞️ Terrain Protection**: Players can only break blocks they have personally placed. Native terrain is fully protected from players, TNT, and other explosions.
*   **🏛️ Lobby Balance Reset**: Non-admin players in the designated lobby world will have their balance automatically cleared at regular intervals, ensuring a fair start for everyone.

Fire is distributed as a separate JAR file (`fire.jar`). See the [Installation](#installation) section for details.

## 📋 Requirements

*   **Java**: 21 or higher
*   **Server**: Paper 1.21.11 (or other 1.21.x forks)
*   **Dependencies**:
    *   [Vault](https://www.spigotmc.org/resources/vault.34315/) (with a compatible economy plugin like EssentialsX)
    *   [Multiverse-Core](https://dev.bukkit.org/projects/multiverse-core) (for world management, required by ScreamingBedWars-XP)

## ⚙️ Installation

### Installing ScreamingBedWars-XP
1.  Download the latest `ScreamingBedWars-XP.jar` from the [Releases](https://github.com/cyx012113/screamingbedwars-xp/releases) page.
2.  Place the JAR file into your server's `plugins` folder.
3.  Ensure Vault and Multiverse-Core are installed and configured.
4.  Restart your server.

### Installing Fire (Optional)
1.  Download the latest `fire.jar` from the same [Releases](https://github.com/cyx012113/screamingbedwars-xp/releases) page.
2.  Place `fire.jar` into your server's `plugins` folder.
3.  Restart your server. You can customize Fire's settings in `plugins/Fire/config.yml`.

## 🚀 Configuration

ScreamingBedWars-XP will generate a `config.yml` file in its data folder on the first run.

```yaml
# Resource conversion rates (balance gained per item)
rates:
  copper: 50.0
  iron: 5.0
  gold: 10.0
  emerald: 200.0
# ... other options (inherited from original ScreamingBedWars)
```

(All original ScreamingBedWars configuration options are still available and work as expected.)

## 🔨 Compiling from Source

This project uses **Gradle (ScreamingBedWars) & Maven (Fire)**. The repository contains two separate plugins that can be built independently.

> To build **ScreamingBedWars-XP** at once, run:
>
> **On Linux / PowerShell:**
> ```bash
> ./gradlew clean build
> ```
>
> **On Windows cmd:**
> ```bat
> gradlew.bat clean build
> ```
>
> After building, you will find two JAR files in the following locations:
> - `./plugin/build/libs/BedWars-<version>.jar` \\ 
>   (This is the main plugin – **not** the `-unshaded` version!)

> To build **Fire** at once, run:
>
> ```bash
> mvn clean package
> ```
>
> After building, you will find a JAR files in the following locations:
> - `./target/Fire-<version>.jar`

## 📄 License

This project is licensed under the **GNU Lesser General Public License v3.0**. See the [LICENSE](LICENSE) file for details.

Both the modified ScreamingBedWars-XP and the independent Fire plugin are distributed under the same LGPL v3 license.

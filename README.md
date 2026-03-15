# ScreamingBedWars-XP

ScreamingBedWars-XP is a **modified fork** of [ScreamingBedWars](https://github.com/ScreamingSandals/BedWars) (branch ver/0.2.x).  
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

## 🪨 Obsidian_Change – Special Item Plugin (included in this repository)

[Obsidian_Change](https://github.com/cyx012113/screamingbedwars-xp/tree/main/Obsidian_Change) is a separate, lightweight plugin built with Maven. It adds a **special item** that allows players to convert obsidian into wood planks, perfect for breaking through bed defenses.

### Features of Obsidian_Change

*   **⏱️ 3-Second Conversion**: Right-click an obsidian block while holding the powder. After a 3-second wait, the obsidian transforms into oak planks.
*   **❄️ Cooldown & Consumption**: The powder is consumed on use, and each player has a 5-second cooldown.
*   **🔧 Admin Commands**: Use `/obsidian give <player> obsidian_change_powder [amount]` to give the item.
*   **💰 Shop Integration**: Easily add the item to your ScreamingBedWars shop (see configuration below).

Obsidian_Change is distributed as a separate JAR file (`obsidian-change-1.0.0.jar`). See the [Installation](#installation) section for details.

### Integrating Obsidian_Change into ScreamingBedWars Shop

To make the powder purchasable in-game, add the following entry to your `shop.yml` (usually located in the arena folder or global shop):

```yaml
- price: 300 of gold
  stack:
    type: REDSTONE
    amount: 1
    display-name: "&dObsidian Change Powder"
    lore:
      - "&7Right-click obsidian"
      - "&7Wait 3 seconds to convert to planks"
      - "&7Cooldown: 5 seconds"
    # If you use custom model data for resource packs:
    # custom-model-data: 10001
```

If you are using Vault economy for purchases, replace `price: 300 of gold` with a Vault price (e.g., `price: 300` with `price-type: vault`).

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

### Installing Obsidian_Change (Optional)
1.  Download the latest `obsidian-change-1.0.0.jar` from the same [Releases](https://github.com/cyx012113/screamingbedwars-xp/releases) page.
2.  Place the JAR file into your server's `plugins` folder.
3.  Restart your server. No configuration file is required, but you can adjust the conversion time or cooldown by modifying the source code (see [Compiling from Source](#-compiling-from-source)).

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

This project uses **Gradle (ScreamingBedWars-XP)** and **Maven (Fire & Obsidian_Change)**. The repository contains three separate plugins that can be built independently.

### Building ScreamingBedWars-XP

**On Linux / PowerShell:**
```bash
./gradlew clean build
```

**On Windows cmd:**
```bat
gradlew.bat clean build
```

After building, you will find the JAR file at:
- `./plugin/build/libs/BedWars-<version>.jar`  
  (This is the main plugin – **not** the `-unshaded` version!)

### Building Fire

```bash
cd Fire
mvn clean package
```

The JAR will be located at `./Fire/target/fire-<version>.jar`.

### Building Obsidian_Change

```bash
cd Obsidian_Change
mvn clean package
```

The JAR will be located at `./Obsidian_Change/target/obsidian-change-<version>.jar`.

## 📄 License

This project is licensed under the **GNU Lesser General Public License v3.0**. See the [LICENSE](LICENSE) file for details.

All three plugins – the modified ScreamingBedWars-XP, Fire, and Obsidian_Change – are distributed under the same LGPL v3 license.
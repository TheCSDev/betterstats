<h1 align="center">
	<a href="https://modrinth.com/project/n6PXGAoM" target="_blank">
		<img width="1366" height="705" alt="thumbnail" src="https://github.com/user-attachments/assets/62714f3f-9d63-420e-869f-05e21adb013d"/>
	</a>
</h1>

<p align="center">
	<a href="https://github.com/sponsors/TheCSDev/" target="_blank">
		<img alt="Sponsors" src="https://img.shields.io/github/sponsors/TheCSDev?style=for-the-badge"/>
	</a>
	<a href="https://github.com/TheCSDev/betterstats/issues" target="_blank">
		<img alt="Issues" src="https://img.shields.io/github/issues/TheCSDev/betterstats?style=for-the-badge"/>
	</a>
	<a href="https://curseforge.com/projects/667464" target="_blank">
		<img alt="Issues" src="https://img.shields.io/curseforge/dt/667464?style=for-the-badge&label=CurseForge"/>
	</a>
	<a href="https://modrinth.com/project/n6PXGAoM" target="_blank">
		<img alt="Issues" src="https://img.shields.io/modrinth/dt/n6PXGAoM?style=for-the-badge&label=Modrinth"/>
	</a>
</p>

<p align="center">This repository contains the source-code of "Better Statistics Screen", a Minecraft mod that improves thestatistics screen and makes it more useful. To download this mod, please visit its CurseForge or Modrinth page.</p>

<p align="center">(The thumbnail screenshot features <a href="https://github.com/Glitchfiend/BiomesOPlenty" target="_blank">Biomes O' Plenty</a>, <a href="https://github.com/ComplementaryDevelopment/ComplementaryReimagined" target="_blank">Complementary Shaders</a>, and <a href="https://modrinth.com/project/rox3U8B6" target="_blank">Bare Bones</a>)</p>

> [!IMPORTANT]
> This repository contains versions starting from `v5.0`. If you’re looking for legacy releases (`v4.0` and earlier), please see the archived repository: https://github.com/TheCSDev/betterstats-v4

## Introduction

The vanilla statistics screen is a mess, harder to read and navigate, and lacks features like proper search and filtering functionalities, making it harder to find what you're looking for. The goal of this mod is to make it easier to navigate and find the specific statistics you are looking for, especially when your world has dozens or hundreds of entries to keep track of.

**Key features**
- 🔍 Searching, filtering, sorting, and grouping statistics
- 📊 Item and mob statistics are shown in a nice and clean visual grid
- 🖥️ Mobs are visually rendered on the screen because it looks nicer und makes finding them faster
- 📂 Saving and loading statistics files, as well as a tabbed interface for viewing multiple files
- 🌐 Sharing statistics with your friends and anyone else you'd like
- ⚙️ Optional `/statistics` command, for integrating statistics into commands and data-packs

## Dependencies

This mod depends on some other mods that first need to be installed before this mod can be installed. Those dependencies are as follows:
- 🖥 [TCDCommons API](https://github.com/TheCSDev/tcdcommons) - Powers the GUI interface of this mod
- 🏗 [Architectury API](https://github.com/architectury/architectury-api) - Allows this mod to run on `Fabric` and `NeoForge`

## Building

Follow these steps to build the project from source.

### Prerequisites
* **Java 21**: Ensure you have the Java Development Kit (JDK) 21 installed.
* **Git**: Required to clone this repository.

### Instructions
1. **Clone the repository**
```bash
git clone https://github.com/TheCSDev/betterstats.git
cd betterstats
```

2. **Build the mod**
```bash
./gradlew clean build
```
*Note: Use `gradlew.bat clean build` **if** you are on Windows.*

The compiled `.jar` file will be located in `build/libs/`.

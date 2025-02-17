# MojanglesDataGenerator
Minecraft mod that generates data related to the Mojangles and Unifont fonts into storage data through mcfunctions.

# Data
Upon running the functions in-game, the following data will be stored:
- `storage juloos:data char.widths[{k:"<character>",v:<width>}]` if running the normal function.
- `storage juloos:data char.bold_widths[{k:"<character>",v:<width>}]` if running the bold variant.

The [Mojangles font](https://minecraft.wiki/w/Mojangles) is the default Minecraft font used in-game that supports up to 2400+ characters.
The [Unifont font](https://minecraft.wiki/w/GNU_Unifont) is a shipped GNU Unifont that supports almost all the other Unicode characters.

PS: The Unifont function will also contain the Mojangles defined characters, but it is recommended not to used it since the file size and datapack load delay is consequent.

# How to use
Download the mcfunction files directly from the releases page.

OR

1. Download and setup this repository.
2. Compile the mod with `gradlew build`.
3. Install the mod.
4. Run the game.
5. Game will stop after loading.
6. mcfunction files will be generated in the minecraft folder.

NOT AFFILIATED IN ANY WAY WITH MINECRAFT, MOJANG, OR MICROSOFT.

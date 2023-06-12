<p align="center">
	<img width=256px src="GitHub/logo.png" />
</p>

# Dynamic FPS

Dynamic FPS automatically reduces the speed at which minecraft renders when it's not focused (to 1 FPS) or hidden (no renders at all). It also fixes a bug in Vanilla Minecraft that makes it take much more performance in the background than it should.

Download in [the releases section](https://github.com/juliand665/Dynamic-FPS/releases), on [Modrinth](https://modrinth.com/mod/dynamic-fps), or on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/dynamic-fps).

## Developer Info

Dynamic FPS can optimize the game while a screen is actively obscuring the world.  
To make use of this feature your screen must call [`renderDirtBackground`](# "Mojang Mappings") / [`renderBackgroundTexture`](# "Quilt Mappings / Yarn") during rendering.

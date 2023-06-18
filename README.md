<p align="center">
	<img width=256px src="GitHub/logo.png" />
</p>

# Dynamic FPS

Dynamic FPS automatically reduces the speed at which Minecraft renders when it's not focused (to 1 FPS) or hidden (no renders at all). It also fixes a bug in Vanilla Minecraft that makes it take much more performance in the background than it should.

Download in [the releases section](https://github.com/juliand665/Dynamic-FPS/releases), on [Modrinth](https://modrinth.com/mod/dynamic-fps), or on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/dynamic-fps).

## Developer Info

Dynamic FPS can optimize the game while a screen is actively obscuring the world.  
There are two ways of opting in or out of this feature, either of which may be more applicable to your situation:

- Screens calling [`renderDirtBackground`](# "Mojang Mappings") / [`renderBackgroundTexture`](# "Quilt Mappings / Yarn") during rendering are opted in automatically
- Mods can provide a list of screens they'd like to opt in or out via their mod metadata ([Fabric Example] / [Quilt Example])

[Fabric Example]: https://gist.github.com/LostLuma/3058c596e15e70671750fe348ff3aff1#file-fabric-mod-json-L42-L49
[Quilt Example]: https://gist.github.com/LostLuma/3058c596e15e70671750fe348ff3aff1#file-quilt-mod-json-L40-L47

<p align="center">
    <img width=256px src="GitHub/logo.png" />
</p>

# Dynamic FPS

Reduce resource usage while Minecraft is in the background or idle.

## Features

Dynamic FPS will detect whether the Minecraft window is currently active, being hovered over, unfocused, or invisible.  
For each of these states you're able to adjust the frame rate, volume, and whether [toast notifications](https://minecraft.wiki/w/Toasts) are temporarily paused.

There's also an option to enter an idle state while the window is active but hasn't received any input in a certain amount of time.

---

In addition to this Dynamic FPS fixes a vanilla bug causing higher-than-necessary background CPU usage and stops
rendering the world while it's being obscured by resource loading overlay, helping especially on low-end systems.

## Installation

Dynamic FPS is available for download on [GitHub](https://github.com/juliand665/Dynamic-FPS/releases), [Modrinth](https://modrinth.com/mod/dynamic-fps), and [CurseForge](https://www.curseforge.com/minecraft/mc-mods/dynamic-fps).  
To access the in-game config screen you'll also need to install [Mod Menu](https://modrinth.com/mod/modmenu) and [Cloth Config](https://modrinth.com/mod/cloth-config).

## Frequently Asked Questions

- Why is Minecraft still running at 15 FPS?

Dynamic FPS will only reduce the client / render loop to a minimum of 15 cycles per second.
Lower frame rates are achieved by then cancelling the rendering of all superfluous frames, e.g. 14 out of 15 frames are cancelled for 1 FPS.

This lets you resume playing almost instantly after switching back to the game:
Instead of having to wait for up to a second until the next rendered frame comes along, the game checks back within 1/15th of a second.

## Developer Info

> [!IMPORTANT]  
> These features are currently only accurate for the Fabric and Quilt versions of Dynamic FPS!

If Dynamic FPS' optimizations conflict with a feature of your mod you can disable them.  
This is done by adding a `dynamic_fps` object to your mod's metadata (inside `quilt`/`fabric.mod.json`).

Force disable the loading overlay optimization:

```json
    "dynamic_fps": {
        "optimized_overlay": false
    },
```

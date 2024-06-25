<p align="center">
    <img width=256px src="GitHub/logo.png" />
</p>

# Dynamic FPS

Reduce resource usage while Minecraft is in the background or idle.

## Features

Dynamic FPS will detect whether the Minecraft window is currently active, being hovered over, unfocused, or invisible.  
For each of these states you're able to adjust the frame rate, volume, and whether [toast notifications](https://minecraft.wiki/w/Toasts) are temporarily paused.

You can also configure these settings for when you're idling (with a custom timeout) or while your laptop / handheld is on battery.  
Optionally you may also display the current battery status on the in-game HUD and receive toast notifications about battery activity.

---

In addition to this Dynamic FPS fixes a vanilla bug causing higher-than-necessary background CPU usage and stops
rendering the world while it's being obscured by resource loading overlay, helping especially on low-end systems.

## Installation

Dynamic FPS is available for download on [GitHub](https://github.com/juliand665/Dynamic-FPS/releases), [Modrinth](https://modrinth.com/mod/dynamic-fps), and [CurseForge](https://www.curseforge.com/minecraft/mc-mods/dynamic-fps).  
To access the in-game config screen you'll also need to install [Mod Menu](https://modrinth.com/mod/modmenu) and [Cloth Config](https://modrinth.com/mod/cloth-config).

## Frequently Asked Questions

- Why is Minecraft still running at 15 FPS?

Dynamic FPS will only slow the client render loop to a minimum of 15 cycles per second.
Lower frame rates are achieved by then cancelling the rendering of all superfluous frames, e.g. 14 out of 15 frames are cancelled for 1 FPS.

This lets you resume playing almost instantly after switching back to the game:
Instead of having to wait for up to a second until the next rendered frame comes along, the game checks back within 1/15th of a second.

## Disclaimer

Enabling the battery integration requires downloading an additional library at runtime.  
The mod contains the hashes for these files ahead of time and will verify them before usage.

You may disable this behavior in the mod's settings, or [install the library yourself](docs/manual-natives-install.md) if you wish.

## License

Dynamic FPS' code and translations are available under the [MIT license](LICENSE).  
Other assets included in this repository may not be released under an open source license.

## Developer Info

If Dynamic FPS' optimizations conflict with a feature of your mod you can request to disable them.  
The process of doing so is as simple as adding some additional metadata Dynamic FPS reads to your mod metadata.

**Disable the loading overlay optimization:**

Fabric / Quilt:

```json
    "dynamic_fps": {
        "optimized_overlay": false
    },
```

Forge / NeoForge

```toml
[modproperties.your_mod_id]
dynamic_fps = {optimized_overlay = false}
```

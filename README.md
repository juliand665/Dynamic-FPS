<p align="center">
    <img width=256px src="GitHub/logo.png" />
</p>

# Dynamic FPS

Dynamic FPS can reduce the frame rate, volume, and pause toasts while Minecraft is not focused or hidden.  
In addition to this it also fixes a bug in vanilla Minecraft making it take too many resources in the background.

Download on [GitHub Releases](https://github.com/juliand665/Dynamic-FPS/releases), [Modrinth](https://modrinth.com/mod/dynamic-fps), or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/dynamic-fps).

## Features

Dynamic FPS will detect when the window is not focused, but instead either hovered over, unfocused, or invisible.  
For each mode you can adjust the maximum FPS, master volume, toast visibility, and whether to invoke garbage collection.

In addition to this Dynamic FPS also reduces background CPU usage and stops rendering the world while either the loading overlay is active or a screen is obscuring the world (you're in eg. the keybinds or any detected screen).

Other than that Dynamic FPS is kept very light-weight - no update checker, no telemetry, or anything else unneeded!

## Questions

- Why is Minecraft still running at 15 FPS?

Dynamic FPS will only reduce the client / render loop to a minimum of 15 cycles per second.
Lower frame rates are achieved by then cancelling the rendering of all superfluous frames, eg. 14 out of 15 frames are cancelled for 1 FPS.

This is done to make resume playing feel pretty much instantaneous - instead of being able to
go back to full rendering speed once a second from 1 FPS we can resume from any of those 15th of a second intervals (every 66 ms).

## Developer Info

If Dynamic FPS' optimizations conflict with a feature of your mod you can disable them.  
This is done by adding a `dynamic_fps` object to your mod's metadata (inside `quilt/fabric.mod.json`).

Force disable the loading overlay optimization:

```json5
    "dynamic_fps": {
        "optimized_overlay": false
    },
```

Screen optimization is enabled for screens calling [`renderDirtBackground`](# "Mojang Mappings")/[`renderBackgroundTexture`](# "Quilt Mappings / Yarn").  
Other screens can opt in or out by adding their package and class name to the `optimized_screens` object instead:

```json5
    "dynamic_fps": {
        "optimized_screens": {
            "enabled": [
                "com.example.example_mod.gui.screens.ExampleScreen"
            ],
            "disabled": []
        }
    },
```

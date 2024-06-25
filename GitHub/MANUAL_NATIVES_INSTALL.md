# Manual Natives Installation

How to install the battery library without having the mod do it on your behalf:

- Visit the [releases section](https://github.com/LostLuma/battery/releases) of the library and find the right version:
  - Currently Dynamic FPS uses version 1.0.1
- Download the correct dynamic library for your system:
  - You will want to download the file called `libbattery-jni-1.0.0+<arch>.<os>.<ext>`
  - If you're not sure which one to use simply download all of them, redundant ones are ignored at runtime
- Locate Dynamic FPS' cache directory in your Minecraft instance
  - This is currently `<instance>/.cache/dynamic_fps/` for every mod loader
- Drop the dynamic library into this folder
- Start the game! The battery features should now be working

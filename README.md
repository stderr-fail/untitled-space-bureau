# jvm debug and other args

https://godot-kotl.in/en/stable/user-guide/advanced/commandline-args/

Godot project settings > [advanced] > Editor > Run

### wait for debugger

`--wait-for-debugger` is enabled by default

```
--jvm-debug-port=5005
```

### don't wait for debugger

```
--jvm-debug-port=5005 --wait-for-debugger=false
```
    

# run Godot from Asahi Fedora

1. Custom compile Godot + Godot/Kotlin (read instructions on Godot/Kotlin website, it's pretty easy)
2. Launch Godot with flag `--rendering-driver opengl3` since Asahi Fedora doesn't have a Vulkan driver (yet)
3. Update the Godot project settings for Rendering > Renderer > Rendering Method to `gl_compatability`

## Using Renderdoc on Asahi Fedora

This presents some challenges since it's all KDE Plasma on Wayland

But this appears to work (see [Arch Wiki](https://wiki.archlinux.org/title/Wayland#Qt)):

```bash
QT_QPA_PLATFORM=xcb qrenderdoc
```

And then you can configure the launcher in Renderdoc like so:

|Executable Path|`/path/to/godot`|
|Working Directory|`/whatever/you/want`|
|Command-line Arguments|`--rendering-driver vulkan /path/to/your/scene/main.tscn`

Note that you must pass a Godot scene file argument so that Renderdoc will run the scene in the process it launches

## RenderDoc shader debugging

Supposedly Godot's `--generate-spirv-debug-info` option from [this PR](https://github.com/godotengine/godot/pull/77975) should satisfy what RenderDoc needs (see RenderDoc docs [here](https://renderdoc.org/docs/how/how_debug_shader.html#including-debug-info-in-shaders))

But this is not working on Asahi Fedora as of right now (2024-07-27), probably because Vulkan is (i think?) emulated and not hardware accelerated (yet).


# Sandboxes

## orbital-lines-stuff

The **orbital-lines-stuff** sandbox holds PoC code for rendering orbital lines and Godot Camera3D code for 
moving the camera along with mouse movements

# Name Ideas

Blobbernauts
Blobonauts

BSA - Blobonaut Space Agency
BASA - Blobonaut Astral Space Agency
BASA - Blobonaut Aeronautics Space Agency

BSOD - blob space organization/operations? department/destination/?
        blob space operations department?

GELO - gelatinous exploration league operations

GASP - Gelatinous Astronauts' Space Program

GOO - Galactic Operations Organization

GLOP - Gelatin Launch and Orbital Program

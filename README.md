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


# Sandboxes

## orbital-lines-stuff

The **orbital-lines-stuff** sandbox holds PoC code for rendering orbital lines and Godot Camera3D code for 
moving the camera along with mouse movements
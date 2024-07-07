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


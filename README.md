# MP3
Repository for MP3 - SE 4367

## Part 0: Getting Started
Using the VM arguments: `-javaagent:lib/iagent.jar -cp bin/`
With the provided `Transformer` and `HelloThread`, the output is as follows.

```transforming class java/lang/invoke/MethodHandleImpl
transforming class java/lang/invoke/MethodHandleImpl$1
transforming class java/lang/invoke/MethodHandleImpl$2
transforming class java/util/function/Function
transforming class java/lang/invoke/MethodHandleImpl$3
transforming class java/lang/invoke/MethodHandleImpl$4
transforming class java/lang/ClassValue
transforming class java/lang/ClassValue$Entry
transforming class java/lang/ClassValue$Identity
transforming class java/lang/ClassValue$Version
transforming class java/lang/invoke/MemberName$Factory
transforming class java/lang/invoke/MethodHandleStatics
transforming class java/lang/invoke/MethodHandleStatics$1
transforming class sun/misc/PostVMInitHook
transforming class sun/misc/PostVMInitHook$2
transforming class jdk/internal/util/EnvUtils
transforming class sun/misc/PostVMInitHook$1
transforming class sun/usagetracker/UsageTrackerClient
transforming class java/util/concurrent/atomic/AtomicBoolean
transforming class sun/usagetracker/UsageTrackerClient$1
transforming class sun/usagetracker/UsageTrackerClient$4
transforming class sun/usagetracker/UsageTrackerClient$2
transforming class sun/usagetracker/UsageTrackerClient$3
transforming class java/lang/StringCoding$StringEncoder
transforming class java/io/FileOutputStream$1
transforming class sun/launcher/LauncherHelper
transforming class HelloThread
transforming class sun/launcher/LauncherHelper$FXHelper
transforming class java/lang/Class$MethodArray
transforming class HelloThread$TestThread
transforming class java/lang/Void
transforming class Log
transforming class java/lang/Shutdown
transforming class java/lang/Shutdown$Lock
```

### Quick Start with ASM
Using the VM arguments: `java -javaagent:lib/iagent.jar -cp lib/asm-7.1.jar:bin/`
The message will be printed out is as follow.
```Thread main start new Thread Thread-0```


## Part 1: Instrumenting Thread Synchronizations
`MethodAdapter` and `Transformer` were tweaked in a way so that it can appropreiately print out desired output. Please regard the source files for updates.
After filling `visitMethodInsn()` and `visitInsn()`, the output of this part is as follows.
```
Thread main start new Thread Thread-0
Thread Thread-0 lock object 1253353779
Thread Thread-0 wait signal on object 1253353779
Thread main start new Thread Thread-1
Thread main unlock object 1950409828
Thread Thread-1 lock object 1253353779
Thread Thread-1 notify signal on object 1253353779
Thread Thread-1 unlock object 1253353779
Thread Thread-0 unlock object 1253353779
```


## Part 2: Instrumenting Field and Array Accesses
According to this section, `MethodAdapter` was modified with completion of `visitFieldInsn()` and `visitInsn()`. The output is as follows.
```Thread main wrote instance field y of object HelloThread2
Thread main wrote instance field b of object HelloThread2
Thread main wrote instance field c of object HelloThread2
Thread main wrote instance field val$hello of object HelloThread2$1
Thread main start new Thread Thread-0
Thread main wrote static field x
Thread main wrote static field z
Thread main wrote instance field y of object HelloThread2
Thread main read instance field c of object HelloThread2
Thread Thread-0 read instance field val$hello of object HelloThread2$1
Thread Thread-0 read instance field c of object HelloThread2
Thread main wrote array [index] HelloThread2@355da254 [1]
Thread main read instance field c of object HelloThread2
Thread Thread-0 read array [index] HelloThread2$1@442d2736 [0]
Thread Thread-0 read instance field val$hello of object HelloThread2$1
Thread Thread-0 read instance field c of object HelloThread2
Thread main wrote array [index] HelloThread2@355da254 [1]
Thread main wrote instance field b of object HelloThread2
Thread main read static field z
Thread Thread-0 wrote array [index] HelloThread2$1@442d2736 [1]
Thread Thread-0 read static field x
Thread main wrote array [index] HelloThread2@355da254 [1]
Thread main read static field z
Thread main read static field x
Thread Thread-0 read instance field val$hello of object HelloThread2$1
Thread Thread-0 wrote instance field y of object HelloThread2
Thread Thread-0 read instance field val$hello of object HelloThread2$1
Thread Thread-0 read instance field b of object HelloThread2
Thread Thread-0 wrote static field x
Thread main wrote array [index] HelloThread2@355da254 [1]
```

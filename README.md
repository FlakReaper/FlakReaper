# FlakReaper

This repository contains the reproducing tool FlakReaper and the flaky tests evaluated in our experiments.


### How to use FlakReaper
1. Prerequisites
    - Android emulator with API 30 with EdXposed Framework installed
    - Android Studio(Hedgehog | 2023.1.1 Canary 10) with YAMP plugin installed
    - Java 17 && Gradle 8.0 && Android Gradle Plugin 8.0.0

2. Usage
    - Install the app module and the timelord module of FlakReaper to an Android emulator with API 30 with EdXposed Framework installed. You may check if the app module and the timelord module are correctly installed in the EdXposedManager module list.

    - To trace the messageQueue and the GUI related operations during an instrumentation test, make sure the control switch of the app module on and the timelord module off in the EdXposedManager. 

    - Run the instrumentation test in the Android Studio with YAMP plugin on to record the runtime method call stack with an output of .trace file. The log of GUI related operations should be generated under the /sdcard directory in the emulator with the format of {timestamp}.txt.

    - Run the Analyzer module with the input of the .trace file and the {timestamp}.txt. It will generate an static_id_list.txt file under the /sdcard directory in the emulator, which contains the static id list.

    - To inject a single delay in the instrumentation test, make sure the control switch of the app module on and the timelord module off in the EdXposedManager. Then run the instrumentation test again and the delay will be injected to reproduce the failure of flaky test.



### [Artifacts: Benchmark, RootCauses, and RunTime Data](https://flakytestreproducer.github.io/)


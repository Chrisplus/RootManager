# Root Manager [![Build Status](https://travis-ci.org/Chrisplus/RootManager.svg?branch=master)](https://travis-ci.org/Chrisplus/RootManager)

RootManager is a library which enable developers access the root utilities on Android devices. RootManager provides functions including checking if the device is rooted, trying to obtain superuser privileges, installing/uninstalling applications *silently*, capturing screen shot, recording screen (4.4. and upper), etc..

The project is based on [*ROOTTOOLS*](https://github.com/Stericson/RootTools). RootManager slims ROOTTOOLS and extends various functions for common cases.

<p align="center">
  <img src="./icons/LostVikings.jpg"/>
</p>
### Features

##### root access
* check if the device is rooted.
* obtain the root privileges, run command as the superuser.

##### package management
* install packages *silently* [*].
* uninstall packages *silently*.
* uninstall system applications *silently*.

[\*]: *silently* means operation will be done at the background without any popups or prompts.

##### command execution
* run commands.
* install binary executable files.
* remove binary executable files.

##### others
* remount file system in RW.
* copy files.
* capture screenshots.
* screen record on 4.4 and upper.
* check if a process is running.
* kill a process by its PID or package name.
* restart the device.

### Usage
* Install RootManager by gradle:
```groovy
dependencies {
    compile 'com.chrisplus.rootmanager:library:2.0.5@aar'
}
```
* All functions you can access via (`RootManager`)
    + <code>RootManager.getInstance()</code>.
* A suggestion to call RootManager:
    1. check if this device is rooted (`RootManager.getInstance().hasRooted`). If yes, this device might be rooted so that we can obtain superuser permission, otherwise we cannot.
    2. try to get root permission via (`RootManager.getInstance.obtainPermission()`). Then, for most cases, a dialog will be shown to users, *GRAND* or *DENY* the full access to the device. This dialog is controlled by SU app such as SuperSU and KingUser, etc.
    3. RootManager will return the result according to the user choice.
    4. once obtaining the root access, developers can run commands or call functions.
* Access some sample code, check the sample app please.

### License

RootManager is released under [GPL v2 license](https://www.gnu.org/licenses/gpl-2.0.html)

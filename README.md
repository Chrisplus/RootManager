# Root Manager [![Build Status](https://travis-ci.org/Chrisplus/RootManager.svg?branch=master)](https://travis-ci.org/Chrisplus/RootManager)

RootManager is a library that developers can use to access root utilities on Android devices. RootManager provides functions including checking if the device is rooted, trying to obtain superuser privileges, installing/uninstalling applications *silently*, capturing screen shot, recording screen and so on.

The project is based on [*ROOTTOOLS*](https://github.com/Stericson/RootTools). RootManager slims ROOTTOOLS and extends various functions for common cases.

<p align="center">
  <img src="./icons/LostVikings.jpg"/>
</p>
## Features

##### Access Root Privileges

* Check if the device is rooted.
* Obtain the root privileges, run command as a superuser.

##### Manage Packages

* Install packages *silently* [*].
* Uninstall packages *silently*.
* Uninstall system applications *silently*.

[\*]: *silently* means operation will be done at the background without any popups or prompts.

##### Execute Commands

* Run commands.
* Install binary executable files.
* Remove binary executable files.

##### Utilities

* Remount file system in RW.
* Copy files.
* Capture screenshots.
* Record screen.
* Check whether a process is running.
* Kill process by PID or package name.
* Restart Device.

## Usage & Example
* Install RootManager by gradle:
```groovy
dependencies {
    compile 'com.chrisplus.rootmanager:library:2.0.4@aar'
}
```
* All functions you can access via (`RootManager`)
    + <code>RootManager.getInstance()</code>.
* A suggested procedure to conduct ROOT operations:
    1. Check if this device is rooted, hence check if *SU* files exits (`RootManager.getInstance().hasRooted`). If yes, this device might be rooted so that we can obtain superuser permission, otherwise we cannot.
    2. Try to get superuser/root permission via (`RootManager.getInstance.obtainPermission()`). Then, for most cases, a dialog will be shown to users, *GRAND* or *DENY* the full access to the device. This dialog is controlled by permission control applications such as SuperSU and KingUser, etc.
    3. RootManager will return the result of user authorization.
    4. Once obtaining superuser permission, developers can run commands or call functions as root.
* Access some sample code, check *sample* please.

## License

RootManager is released under [GPL v2 license](https://www.gnu.org/licenses/gpl-2.0.html)

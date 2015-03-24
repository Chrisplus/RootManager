# Root Manager

RootManager is a library that developers can use to access root utilities on Android devices. Currently RootManager provides functions including checking if the device is rooted, trying to obtain superuser privileges, installing/uninstalling applications *silently*, capturing screen shot, recording screen and so on.

The project is based on [*ROOTTOOLS*](https://github.com/Stericson/RootTools). RootManager slims ROOTTOOLS and extends various functions for common cases.


## Main Features

#### Access Root Privileges

* Check if the device is rooted.
* Obtain the root privileges, run command as a superuser.

#### Manage Packages

* Install packages *silently* [*].
* Uninstall packages *silently*.
* Uninstall system applications *silently*.

[\*]: *silently* means operation will be done at the background without any popups or prompts.

#### Execute Commands

* Run commands.
* Install binary executable files.
* Remove binary executable files.

#### Utilities

* Remount file system in RW.
* Copy files.
* Capture screenshots.
* Record screen.
* Check whether a process is running.
* Kill process by PID or package name.
* Restart Device.

## Usage and Sample code

* All functions you can access via (`RootManager`)
    + <code>RootManager.getInstance()</code>.
* A process suggested to do ROOT operation is that:
    + Check if this device is rooted, hence check if *SU* file exits. (`RootManager.getIntance().hasRooted`). If yes, this device may be rooted and *_maybe_* we can get super user permisson, otherwise we nerver can.
    + Try to get Super User Permission. (`RootManager.getInstance().grantPermission()`). If yes, it means we has become *SU*, failed otherwise. In this step, generally speaking, a popup dilog will be shown for users to choose *Allow* or *Deny*. This dialog is controlled by the Super User Application in your phone.
    + Run command, files or call functions as a *SU*.
* Access some sample code, check *_RootManagerExample_* please.

## License

*RootManager* is released under *GNU GPL v2 license*.

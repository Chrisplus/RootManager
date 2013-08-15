# Root Manager

This is a lib for android developers to do things as a root user, such as judging whether the device has rooted, try to granted root premission, install app without pop dialog (just like Google Play do), uninstall apps silently, capture screen programmly and so on.  

This project bases on the project [*ROOTTOOLS*](https://code.google.com/p/roottools/) . *RootManager* slims *roottools* and extends more various utilities for common cases.

Download the lastest JAR File [HERE](https://github.com/Chrisplus/RootManager/releases)

## Features

#### Root Access

* Check if the Device has been rooted.
* Grant Super User Permission.

#### App Management

* Install Package *silently*.
* Uninstall Package *silently*.
* Uninstall System Applications and Packages *silently*.
* *silently* means operation will be done at the background, without popup dialog or other things.

#### Execute Commands or Files

* Run Command.
* Install Binary File.
* Remove Binary File.
* Execute Binary File.

#### Utilities

* Remount File System.
* Copy files.
* Screen Shot.
* Kill Process by process name or PID.
* Restart Device.

## Usage and Sample code

* All useful functions you can access via (`RootManager`)
    + <code>RootManager.getInstance()</code>.
* A process suggested to do ROOT operation is that:
    + Check if this device is rooted, hence check if *SU* file exits. (`RootManager.getIntance().hasRooted`). If yes, this device may be rooted and *_maybe_* we can get super user permisson, otherwise we nerver can.
    + Try to get Super User Permission. (`RootManager.getInstance().grantPermission()`). If yes, it means we has become *SU*, failed otherwise. In this step, generally speaking, a popup dilog will be shown for users to choose *Allow* or *Deny*. This dialog is controlled by the Super User Application in your phone.
    + Run command, files or call functions as a *SU*. 
* Access some sample code, check *_RootManagerExample_* please.

## License

*RootManager* is released under *GNU GPL v2 license*.


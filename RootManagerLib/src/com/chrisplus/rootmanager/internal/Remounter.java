/*
 * This file is part of the RootKit Project: http://code.google.com/p/RootKit/
 * Copyright (c) 2012 Stephen Erickson, Chris Ravenscroft, Dominik Schuermann, Adam Shanks
 * This code is dual-licensed under the terms of the Apache License Version 2.0 and
 * the terms of the General Public License (GPL) Version 2.
 * You may use this code according to either of these licenses as is most appropriate
 * for your project on a case-by-case basis.
 * The terms of each license can be found in the root directory of this project's repository as well
 * as at:
 * * http://www.apache.org/licenses/LICENSE-2.0
 * * http://www.gnu.org/licenses/gpl-2.0.txt
 * Unless required by applicable law or agreed to in writing, software
 * distributed under these Licenses is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See each License for the specific language governing permissions and
 * limitations under that License.
 */

package com.chrisplus.rootmanager.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;
import com.wandoujia.phoenix2.rootkit.RootKitConstants;
import com.wandoujia.phoenix2.rootkit.RootKitController;
import com.wandoujia.phoenix2.rootkit.containers.Mount;
import com.wandoujia.phoenix2.rootkit.execution.CommandCapture;
import com.wandoujia.phoenix2.rootkit.execution.Shell;

public class Remounter {

  public boolean remount(String file, String mountType) {

    // if the path has a trailing slash get rid of it.
    if (file.endsWith("/") && !file.equals("/")) {
      file = file.substring(0, file.lastIndexOf("/"));
    }
    // Make sure that what we are trying to remount is in the mount list.
    boolean foundMount = false;
    while (!foundMount) {
      try {
        for (Mount mount : RootKitController.getMounts()) {
          RootKitController.log(mount.getMountPoint().toString());

          if (file.equals(mount.getMountPoint().toString())) {
            foundMount = true;
            break;
          }
        }
      } catch (Exception e) {
        return false;
      }
      if (!foundMount) {
        try {
          file = (new File(file).getParent()).toString();
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
      }
    }
    Mount mountPoint = findMountPointRecursive(file);

    RootKitController.log("Remounting " + mountPoint.getMountPoint().getAbsolutePath() + " as "
        + mountType.toLowerCase());
    final boolean isMountMode = mountPoint.getFlags().contains(mountType.toLowerCase());

    if (!isMountMode) {
      // grab an instance of the internal class
      try {
        CommandCapture command =
            new CommandCapture(0,
                "busybox mount -o remount," + mountType.toLowerCase() + " "
                    + mountPoint.getDevice().getAbsolutePath() + " "
                    + mountPoint.getMountPoint().getAbsolutePath(),
                "toolbox mount -o remount," + mountType.toLowerCase() + " "
                    + mountPoint.getDevice().getAbsolutePath() + " "
                    + mountPoint.getMountPoint().getAbsolutePath(),
                "mount -o remount," + mountType.toLowerCase() + " "
                    + mountPoint.getDevice().getAbsolutePath() + " "
                    + mountPoint.getMountPoint().getAbsolutePath(),
                "/system/bin/toolbox mount -o remount," + mountType.toLowerCase() + " "
                    + mountPoint.getDevice().getAbsolutePath() + " "
                    + mountPoint.getMountPoint().getAbsolutePath()
            );

        Shell.startRootShell().add(command);
        command.waitForFinish();

      } catch (Exception e) {}

      mountPoint = findMountPointRecursive(file);
    }

    Log.i(RootKitConstants.TAG, mountPoint.getFlags() + " AND " + mountType.toLowerCase());
    if (mountPoint.getFlags().contains(mountType.toLowerCase())) {
      RootKitController.log(mountPoint.getFlags().toString());
      return true;
    } else {
      RootKitController.log(mountPoint.getFlags().toString());
      return false;
    }
  }

  private Mount findMountPointRecursive(String file) {
    try {
      ArrayList<Mount> mounts = RootKitController.getMounts();
      for (File path = new File(file); path != null;) {
        for (Mount mount : mounts) {
          if (mount.getMountPoint().equals(path)) {
            return mount;
          }
        }
      }
      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.meefik.linuxdeploy.simpleprotocolplayer;

import android.media.AudioManager;
import android.util.Log;
import java.lang.reflect.Method;

/**
 * Contains methods to handle registering/unregistering remote control
 * clients.  These methods only
 * run on ICS devices.  On previous devices, all methods are no-ops.
 */
@SuppressWarnings({"rawtypes"})
public class RemoteControlHelper {
  private static final String TAG = "RemoteControlHelper";

  private static final boolean sHasRemoteControlAPIs;

  private static Method sRegisterRemoteControlClientMethod;
  private static Method sUnregisterRemoteControlClientMethod;

  static {
    boolean hasRemoteControlAPIs = false;
    try {
      ClassLoader classLoader =
          RemoteControlHelper.class.getClassLoader();
      if (classLoader != null) {
        Class sRemoteControlClientClass =
            RemoteControlClientCompat
                .getActualRemoteControlClientClass(classLoader);
        sRegisterRemoteControlClientMethod = AudioManager.class.getMethod(
            "registerRemoteControlClient",
            sRemoteControlClientClass);
        sUnregisterRemoteControlClientMethod = AudioManager.class.getMethod(
            "unregisterRemoteControlClient",
            sRemoteControlClientClass);
        hasRemoteControlAPIs = true;
      }
    } catch (ClassNotFoundException e) {
      // Silently fail when running on an OS before ICS.
    } catch (NoSuchMethodException e) {
      // Silently fail when running on an OS before ICS.
    } catch (IllegalArgumentException e) {
      // Silently fail when running on an OS before ICS.
    } catch (SecurityException e) {
      // Silently fail when running on an OS before ICS.
    }
    sHasRemoteControlAPIs = hasRemoteControlAPIs;
  }

  public static void registerRemoteControlClient(AudioManager audioManager,
      RemoteControlClientCompat remoteControlClient) {
    if (!sHasRemoteControlAPIs) {
      return;
    }

    try {
      sRegisterRemoteControlClientMethod.invoke(audioManager,
          remoteControlClient.getActualRemoteControlClientObject());
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }


  public static void unregisterRemoteControlClient(AudioManager audioManager,
      RemoteControlClientCompat remoteControlClient) {
    if (!sHasRemoteControlAPIs) {
      return;
    }

    try {
      sUnregisterRemoteControlClientMethod.invoke(audioManager,
          remoteControlClient.getActualRemoteControlClientObject());
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }
}


/*
 * Copyright 2018 Google LLC
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

package com.example.ardemo;

import android.Manifest;

import com.google.ar.sceneform.ux.ArFragment;


public class WritingArFragment extends ArFragment {
  @Override
  public String[] getAdditionalPermissions() {
    String[] additionalPermissions = super.getAdditionalPermissions();
    int permissionLength = additionalPermissions != null ? additionalPermissions.length : 0;
    String[] permissions = new String[permissionLength + 1];
    permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    if (permissionLength > 0) {
      System.arraycopy(additionalPermissions, 0, permissions, 1, additionalPermissions.length);
    }
    return permissions;
  }
}

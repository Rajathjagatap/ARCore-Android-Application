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

import android.util.Log;
import android.util.SparseArray;

import com.google.ar.sceneform.rendering.ModelRenderable;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;

public class ModelLoader {
  private static final String TAG = "ModelLoader";
  private final SparseArray<CompletableFuture<ModelRenderable>> futureSet = new SparseArray<>();
  private final WeakReference<RouterAnimation> owner;

  ModelLoader(RouterAnimation owner) {
    this.owner = new WeakReference<>(owner);
  }



  boolean loadModel(int id, int resourceId) {
    RouterAnimation activity = owner.get();
    if (activity == null) {
      Log.d(TAG, "Activity is null.  Cannot load model.");
      return false;
    }
    CompletableFuture<ModelRenderable> future =
        ModelRenderable.builder()
            .setSource(owner.get(), resourceId)
            .build()
            .thenApply(renderable -> this.setRenderable(id, renderable))
            .exceptionally(throwable -> this.onException(id, throwable));
    if (future != null) {
      futureSet.put(id, future);
    }
    return future != null;
  }

  ModelRenderable onException(int id, Throwable throwable) {
    RouterAnimation activity = owner.get();
    if (activity != null) {
      activity.onException(id, throwable);
    }
    futureSet.remove(id);
    return null;
  }

  ModelRenderable setRenderable(int id, ModelRenderable modelRenderable) {
    RouterAnimation activity = owner.get();
    if (activity != null) {
      activity.setRenderable(id, modelRenderable);
    }
    futureSet.remove(id);
    return modelRenderable;
  }
}

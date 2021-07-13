package com.example.ardemo;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class AugmentedImageRouter extends AppCompatActivity {

    ArFragment arFragment1;
    boolean shouldAddModel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augmented_image_router);
        arFragment1 = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment_1);
        arFragment1.getPlaneDiscoveryController().hide();
        arFragment1.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void placeObject(ArFragment arFragment1, Anchor anchor, Uri uri) {
        ModelRenderable.builder()
                .setSource(arFragment1.getContext(), uri)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(arFragment1, anchor, modelRenderable))
                .exceptionally(throwable -> {
                            Toast.makeText(arFragment1.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            return null;
                        }

                );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment1.getArSceneView().getArFrame();

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : augmentedImages) {
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
                if (augmentedImage.getName().equals("tplink2") && shouldAddModel) {
                    placeObject(arFragment1, augmentedImage.createAnchor(augmentedImage.getCenterPose()), Uri.parse("new_router_3.sfb"));
                    shouldAddModel = false;
                }
            }
        }
    }

    public boolean setupAugmentedImagesDb(Config config, Session session) {
        AugmentedImageDatabase augmentedImageDatabase;
        Bitmap bitmap = loadAugmentedImage();
        if (bitmap == null) {
            return false;
        }

        augmentedImageDatabase = new AugmentedImageDatabase(session);
        augmentedImageDatabase.addImage("tplink2", bitmap);
        augmentedImageDatabase.addImage("tplink10", bitmap);
        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }

    private Bitmap loadAugmentedImage() {
        try (InputStream is = getAssets().open("tplink2.jpg") ) {
            return BitmapFactory.decodeStream(is);
        }

        catch (IOException e) {
            Log.e("ImageLoad", "IO Exception", e);
        }
        return null;
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable)
    {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }
}

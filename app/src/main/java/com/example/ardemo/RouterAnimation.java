package com.example.ardemo;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.SkeletonNode;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

public class RouterAnimation extends AppCompatActivity {

    private static final String TAG = "AnimationSample";
    private static final int ROUTER_RENDERABLE = 1;
    MediaPlayer mediaPlayer;

    private ArFragment arFragment_2;
    private ModelLoader modelLoader;
    private ModelRenderable routerRenderable;
    private AnchorNode anchorNode;
    private SkeletonNode routercomputer;
    private int nextAnimation;
    private ModelAnimator animator;
    private FloatingActionButton animationButton;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_animation);

        arFragment_2 = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment_2);
        arFragment_2.getPlaneDiscoveryController().hide();
        arFragment_2.getPlaneDiscoveryController().setInstructionView(null);
        modelLoader = new ModelLoader(this);

        modelLoader.loadModel(ROUTER_RENDERABLE, R.raw.new_router_1_with_3d);

        mediaPlayer = MediaPlayer.create(this, R.raw.router_mp3);


        arFragment_2.setOnTapArPlaneListener(this::onPlaneTap);


        arFragment_2.getArSceneView().getScene().addOnUpdateListener(this::onFrameUpdate);


        animationButton = findViewById(R.id.animate);
        animationButton.setEnabled(false);
        animationButton.setOnClickListener(this::onPlayAnimation);


    }

    private void onPlaneTap(HitResult hitResult, Plane unusedPlane, MotionEvent unusedMotionEvent) {
        if (routerRenderable == null) {
            return;
        }

        Anchor anchor = hitResult.createAnchor();

        if (anchorNode == null) {
            anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment_2.getArSceneView().getScene());


            routercomputer = new SkeletonNode();

            routercomputer.setParent(anchorNode);
            routercomputer.setRenderable(routerRenderable);
        }

    }


    private void onPlayAnimation(View unusedView) {
        if (animator == null || !animator.isRunning()) {


          /* AnimationData data  = routerRenderable.getAnimationData("new_router");
            numAnimations = routerRenderable.getAnimationDataCount();
            ModelAnimator andyAnimator = new ModelAnimator(data, routerRenderable);
            andyAnimator.start();*/

            AnimationData data = routerRenderable.getAnimationData(nextAnimation);
            nextAnimation = (nextAnimation + 1) % routerRenderable.getAnimationDataCount();
            animator = new ModelAnimator(data, routerRenderable);

            mediaPlayer.start();

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            animator.start();
            animator.setRepeatCount(5);
            Toast toast = Toast.makeText(this, data.getName(), Toast.LENGTH_SHORT);
            Log.d(TAG,
                    String.format(
                            "Starting animation %s - %d ms long", data.getName(), data.getDurationMs()));
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

        }
    }


    private void onFrameUpdate(FrameTime unusedframeTime) {
        // If the model has not been placed yet, disable the buttons.
        if (anchorNode == null) {
            if (animationButton.isEnabled()) {
                animationButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                animationButton.setEnabled(false);

            }
        } else {
            if (!animationButton.isEnabled()) {
                animationButton.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
                animationButton.setEnabled(true);

            }
        }
    }


    void setRenderable(int id, ModelRenderable renderable) {
        {
            this.routerRenderable = renderable;

        }
    }

    void onException(int id, Throwable throwable) {
        Toast toast = Toast.makeText(this, "Unable to load renderable: " + id, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Log.e(TAG, "Unable to load andy renderable", throwable);
    }
}
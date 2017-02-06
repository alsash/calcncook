package com.alsash.reciper.ui.animator;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.alsash.reciper.R;

/**
 * Helper class for flipping two children of a {@link FrameLayout}
 * with animation like a poker card flip
 * Uses modern {@link AnimatorSet} class instead of an old {@link android.view.animation.Animation}
 */
public class FlipAnimatorHelper {

    private final int frontIndex = 1;
    private final int backIndex = 0;

    private final AnimatorSet flipLeftIn;
    private final AnimatorSet flipLeftOut;
    private final AnimatorSet flipRightIn;
    private final AnimatorSet flipRightOut;

    private FrameLayout flipContainer;
    private boolean isBackVisible;

    private Animator.AnimatorListener listener;

    public FlipAnimatorHelper(Context context) {
        flipLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in);
        flipLeftOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out);
        flipRightIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in);
        flipRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out);
    }

    public FlipAnimatorHelper setFlipContainer(FrameLayout flipContainer) {
        this.flipContainer = flipContainer;
        return this;
    }

    public FlipAnimatorHelper setBackVisible(boolean backVisible) {
        isBackVisible = backVisible;
        return this;
    }

    public FlipAnimatorHelper setListener(Animator.AnimatorListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Flip two first children of a flipContainer
     * added into {@link #setFlipContainer(FrameLayout)}
     */
    public boolean flip() {
        if (flipContainer.getChildCount() < 2) return isBackVisible;

        View frontView = flipContainer.getChildAt(frontIndex);
        View backView = flipContainer.getChildAt(backIndex);

        if (isBackVisible) {
            return flip(backView, frontView, flipRightIn, flipRightOut);
        } else {
            return flip(frontView, backView, flipLeftIn, flipLeftOut);
        }
    }

    private boolean flip(final View front, final View back, AnimatorSet in, AnimatorSet out) {
        if (in.isStarted()) in.end();
        if (out.isStarted()) out.end();

        out.removeAllListeners();
        if (listener != null) {
            out.addListener(listener);
        }

        in.setTarget(back);
        out.setTarget(front);
        in.start();
        out.start();

        isBackVisible = !isBackVisible;
        return isBackVisible;
    }
}

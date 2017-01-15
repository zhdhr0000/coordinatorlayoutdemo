package com.github.zhdhr0000.demoapp;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import java.lang.ref.WeakReference;

import static android.content.ContentValues.TAG;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by zhangyh on 2017/1/16.
 */

public class DemoBehavior extends CoordinatorLayout.Behavior<RecyclerView> {

    boolean isExpanded = false;
    boolean isScrolling = false;

    WeakReference<View> dependentView;
    Scroller scroller;
    Handler handler;
    Context context;

    public DemoBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        scroller = new Scroller(context);
        handler = new Handler();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
        if (dependency != null && dependency.getId() == R.id.image) {
            dependentView = new WeakReference<View>(dependency);
            return true;
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.height == MATCH_PARENT) {
            child.layout(0, 0, parent.getWidth(), (int) (parent.getHeight() - getDependentViewCollapsedHeight()));
            return true;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
        float progress = 1.f - Math.abs(dependency.getTranslationY() / (dependency.getHeight() - getDependentViewCollapsedHeight()));
        child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());

        float scale = 1 + 0.4f * (1.f - progress);

        dependency.setScaleY(scale);

        dependency.setAlpha(progress);
        return true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
        scroller.abortAnimation();
        isScrolling = false;
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dx, int dy, int[] consumed) {
        if (dy < 0) {
            return;
        }
        View view = dependentView.get();
        float newTranstionY = view.getTranslationY() - dy;
        float minTranslate = getDependentViewCollapsedHeight() - view.getHeight();

        if (newTranstionY > minTranslate) {
            view.setTranslationY(newTranstionY);
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        View view = dependentView.get();
        if (dyUnconsumed > 0) {
            if (dyUnconsumed > DimenUtils.dp2px(context, 200)) {
                float translateY = view.getTranslationY() - DimenUtils.dp2px(context, 200);
                view.setTranslationY(translateY);
            } else {
                float translateY = view.getTranslationY() - dyUnconsumed;
                view.setTranslationY(translateY);
            }
            return;
        }
        float newTranslateY = view.getTranslationY() - dyUnconsumed;
        float maxHeaderTranslate = 0;
        if (newTranslateY < maxHeaderTranslate) {
            view.setTranslationY(newTranslateY);
        }
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, float velocityX, float velocityY, boolean consumed) {
        return onUserStopDragging(velocityY);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target) {
        if (!isScrolling) {
            onUserStopDragging(500);
        }
    }

    public boolean onUserStopDragging(float velocity) {
        Log.e(TAG, "onUserStopDragging: " + velocity);
        View view = dependentView.get();
        float translateY = view.getTranslationY();
        float minHeaderTranslate = getDependentViewCollapsedHeight() - view.getHeight();
        if (translateY == 0 || translateY == minHeaderTranslate) {
            return false;
        }

        boolean targetState;
        if (Math.abs(velocity) <= 500) {
            targetState = Math.abs(translateY) >= Math.abs(translateY - minHeaderTranslate);
            velocity = 500;
        } else {
            targetState = velocity > 0;
        }

        float targetTranslateY = targetState ? minHeaderTranslate : 0;
        scroller.startScroll(0, (int) translateY, 0, (int) (targetTranslateY - translateY), (int) (1000000 / Math.abs(velocity)));
        handler.post(flingRunnable);
        isScrolling = true;

        return true;
    }

    private Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                dependentView.get().setTranslationY(scroller.getCurrY());
                handler.post(this);
            } else {
                isExpanded = dependentView.get().getTranslationY() != 0;
                isScrolling = false;
            }
        }
    };

    private float getDependentViewCollapsedHeight() {
        return DimenUtils.dp2px(context, 0f);

    }
}

package com.jonduran.circularrevealdropdown;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.RotateDrawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jonathanduran on 12/4/15.
 */
public class CircularRevealDropdown extends FrameLayout {

    private RelativeLayout dropdownLayout;
    private TextView expandText;
    private ImageButton expandButton;
    private LinearLayout menuLayout;
    private ImageButton collapseButton;
    private ListView menu;

    private final int[] coordinates = new int[2];
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    public CircularRevealDropdown(Context context) {
        super(context);
        init();
    }

    public CircularRevealDropdown(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularRevealDropdown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.circular_reveal_dropdown, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        dropdownLayout = (RelativeLayout) findViewById(R.id.dropdown_layout);

        expandText = (TextView) dropdownLayout.findViewById(R.id.expand_text);

        expandButton = (ImageButton) dropdownLayout.findViewById(R.id.expand_button);
        expandButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        circularReveal();
                    }
                }, 200);

            }
        });

        dropdownLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        dropdownLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        expandButton.getLocationInWindow(coordinates);
                    }
                });

        menuLayout = (LinearLayout) findViewById(R.id.menu_layout);

        collapseButton = (ImageButton) menuLayout.findViewById(R.id.collapse_button);
        LayerDrawable parent = (LayerDrawable) collapseButton.getBackground();
        GradientDrawable drawable = (GradientDrawable) parent.getDrawable(0);
        drawable.setColor(Color.WHITE);
        RippleDrawable rippleDrawable = (RippleDrawable) parent.getDrawable(1);
        rippleDrawable.setColor(ColorStateList.valueOf(Color.WHITE));
        RotateDrawable imageDrawable = (RotateDrawable) collapseButton.getDrawable();
        imageDrawable.setLevel(5000);
        collapseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideButton();
            }
        });

        menu = (ListView) menuLayout.findViewById(R.id.menu);
    }

    public void setItems(String[] items) {
        if (items != null && !(items.length == 0)) {
            expandText.setText(items[0]);
            menu.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_view, items));
        }
    }

    public void setItems(List<String> items) {
        if (items != null && !items.isEmpty()) {
            expandText.setText(items.get(0));
            menu.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_view, items));
        }
    }

    private void displayButton() {
        collapseButton.setVisibility(VISIBLE);
        ViewCompat.animate(collapseButton)
                .scaleX(1.0F)
                .scaleY(1.0F)
                .alpha(1.0F)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        displayMenu();
                    }
                })
                .setListener(null)
                .start();
    }

    private void displayMenu() {
        menu.setVisibility(VISIBLE);
        ViewCompat.animate(menu)
                .scaleX(1.0F)
                .scaleY(1.0F)
                .alpha(1.0F)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(null)
                .start();
    }

    private void circularReveal() {
        // get the center for the clipping circle
        int cx = coordinates[0];
        int cy = coordinates[1];

        Log.d("DISPLAY", "Center X = " + cx + ", Center Y = " + cy);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(menuLayout.getWidth(), menuLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(menuLayout, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        menuLayout.setVisibility(View.VISIBLE);

        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                displayButton();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private void hideButton() {
        ViewCompat.animate(collapseButton)
                .scaleX(0.0F)
                .scaleY(0.0F)
                .alpha(0.0F)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        hideMenu();
                    }
                })
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        circularHide();
                    }
                })
                .setListener(null)
                .start();
    }

    private void hideMenu() {
        ViewCompat.animate(menu)
                .scaleX(0.0F)
                .scaleY(0.0F)
                .alpha(0.0F)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(null)
                .start();
    }

    private void circularHide() {
        // get the center for the clipping circle
        int cx = coordinates[0];
        int cy = coordinates[1];

        Log.d("HIDE", "Center X = " + cx + ", Center Y = " + cy);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(menuLayout.getWidth(), menuLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(menuLayout, cx, cy, finalRadius, 0);

        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }
}

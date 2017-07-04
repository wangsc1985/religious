package com.wang17.religiouscalendar.util;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.wang17.religiouscalendar.R;

/**
 * Created by Administrator on 2017/6/30.
 */

public class AnimationUtils {

    /**
     * 闪烁动画
     *
     * @param targetView 动画对象
     */
    public static void setFlickerAnimation(View targetView, long... duration) {
        Animation animation = new AlphaAnimation(1, 0);
        if (duration.length == 0)
            animation.setDuration(700);//闪烁时间间隔
        else if (duration.length == 1)
            animation.setDuration(duration[0]);//闪烁时间间隔
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        targetView.setAnimation(animation);
    }

    public static void setRorateAnimation(Context context, View target, long... duration) {
        Animation rorateAnimation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
        if (duration.length == 1)
            rorateAnimation.setDuration(duration[0]);
        LinearInterpolator lin = new LinearInterpolator();
        rorateAnimation.setInterpolator(lin);
        target.startAnimation(rorateAnimation);
    }
}

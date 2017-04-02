package com.wang17.religiouscalendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.helper._Helper;
import com.wang17.religiouscalendar.helper._Session;
import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.model.Setting;

public class WelcomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_welcome);

            DataContext context = new DataContext(WelcomeActivity.this);
            int softVersion = 13;
            int currentVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            int latestVersionCode = Integer.parseInt(context.getSetting(Setting.KEYS.latestVersionCode,0).getValue());
            if(softVersion>latestVersionCode){
                GuideActivity.btnText="立即体验";
                startActivity(new Intent(WelcomeActivity.this,GuideActivity.class));
                context.editSetting(Setting.KEYS.latestVersionCode,softVersion);
            }

            ImageView welcomeImg = (ImageView) this.findViewById(R.id.imageBuddha);
            Setting setting = context.getSetting(Setting.KEYS.welcome);
            int itemPosition = 0;
            if (setting != null) {
                itemPosition = Integer.parseInt(setting.getValue());
            } else {
                context.addSetting(Setting.KEYS.welcome, itemPosition + "");
            }
            if (itemPosition >= _Session.welcomes.size()) {
                itemPosition = 0;
                context.editSetting(Setting.KEYS.welcome, itemPosition + "");
            }
            welcomeImg.setImageResource(_Session.welcomes.get(itemPosition).getResId());

            AlphaAnimation animation = new AlphaAnimation(1, 1);
            animation.setDuration(2500);// 设置动画显示时间
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Log.i("wangsc", "WelcomeActivity animation start ...");
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.i("wangsc", "WelcomeActivity animation end ...");
                    startActivity(new Intent(getApplication(), MainActivity.class));
                    WelcomeActivity.this.finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Log.i("wangsc", "WelcomeActivity animation repeat ...");
                }
            });
            welcomeImg.startAnimation(animation);
        } catch (Exception ex) {
            _Helper.printExceptionSycn(WelcomeActivity.this,uiHandler,ex);
        }
    }

    private Handler uiHandler = new Handler();
    class splashhandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));
            WelcomeActivity.this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}

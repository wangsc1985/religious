package com.wang17.religiouscalendar.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.fragment.ActionBarFragment;
import com.wang17.religiouscalendar.util._Utils;

public class UpdateIntroduceActivity extends AppCompatActivity {

    private TextView textView_guide, textView_update;
    private Handler uiHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_introduce);
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_action, ActionBarFragment.newInstance()).commit();

            textView_guide = (TextView) findViewById(R.id.textView_guide);
            textView_update = (TextView) findViewById(R.id.textView_update);

            textView_guide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuideActivity.btnText = "返回软件";
                    startActivity(new Intent(UpdateIntroduceActivity.this, GuideActivity.class));
                }
            });

            TextView textViewVersion = (TextView) findViewById(R.id.textView_Version);
            textViewVersion.setText("寿康宝鉴日历 " + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            _Utils.printExceptionSycn(UpdateIntroduceActivity.this, uiHandler, e);
        }
    }
}

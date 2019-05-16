package com.wang17.religiouscalendar.util;

import android.support.v7.app.AppCompatActivity;

        import com.wang17.religiouscalendar.R;
        import com.wang17.religiouscalendar.model.PicNameRes;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by 阿弥陀佛 on 2016/9/28.
 */
public class _Session extends AppCompatActivity {
    public static List<PicNameRes> welcomes;
    public static List<PicNameRes> banners;
//    public static AppInfo newVersionAppInfo;

    static {
        welcomes = new ArrayList<PicNameRes>();
        welcomes.add(new PicNameRes(R.drawable.welcome01, "观世音菩萨一"));
        welcomes.add(new PicNameRes(R.drawable.welcome02, "观世音菩萨二"));
        welcomes.add(new PicNameRes(R.drawable.welcome03, "观世音菩萨三"));
        welcomes.add(new PicNameRes(R.drawable.welcome04, "观世音菩萨四"));
        welcomes.add(new PicNameRes(R.drawable.welcome05, "观世音菩萨五"));
        welcomes.add(new PicNameRes(R.drawable.amtf, "南无阿弥陀佛"));
        welcomes.add(new PicNameRes(R.drawable.jyt, "阿弥陀佛接引图"));

        banners = new ArrayList<PicNameRes>();
        banners.add(new PicNameRes(R.drawable.banner01, "释迦牟尼佛"));

        banners.add(new PicNameRes(R.drawable.banner02, "弥勒菩萨"));
        banners.add(new PicNameRes(R.drawable.banner03, "大智度论"));
        banners.add(new PicNameRes(R.drawable.banner04, "善导大师"));
        banners.add(new PicNameRes(R.drawable.banner06, "佛法会"));
    }

    public static int[] duration = new int[]{0,3000,4000,5000,6000,7000};
}

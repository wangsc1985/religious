package com.wang17.religiouscalendar.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wang17.religiouscalendar.R;
import com.wang17.religiouscalendar.emnu.MDrelation;
import com.wang17.religiouscalendar.emnu.MDtype;
import com.wang17.religiouscalendar.emnu.Zodiac;
import com.wang17.religiouscalendar.fragment.ActionBarFragment;
import com.wang17.religiouscalendar.fragment.ActionBarFragment.OnActionFragmentBackListener;
import com.wang17.religiouscalendar.helper._Helper;
import com.wang17.religiouscalendar.helper._Session;
import com.wang17.religiouscalendar.helper._String;
import com.wang17.religiouscalendar.model.DataContext;
import com.wang17.religiouscalendar.model.DateTime;
import com.wang17.religiouscalendar.model.LunarDate;
import com.wang17.religiouscalendar.model.MemorialDay;
import com.wang17.religiouscalendar.model.Setting;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SettingActivity extends AppCompatActivity implements OnActionFragmentBackListener {

    private LinearLayout layoutBirthday;
    private Spinner spinner_zodiac1, spinner_zodiac2, spinner_mdtype, spinner_mdrelation, spinner_month, spinner_day, spinner_welcome, spinner_duration;
    private Button btn_addMD, btnRecordStatus, btnWay, btnBirthday, btnTarget;
    private CheckBox checkBox_szr, checkBox_lzr, checkBox_gyz;
    private TextView textView_guide, textView_update;

    public static boolean isCalenderChanged, isRecordSetChanged;
    private DataContext dataContext;
    private MDlistdAdapter mdListAdapter;
    private List<HashMap<String, String>> mdListItems;

    //    private static final String BUTTON_STATUS_TEXT_OFF = "已关闭";
//    private static final String BUTTON_STATUS_TEXT_ON = "已开启";
    private static final String BUTTON_WAY_TEXT_AUTO = "自动";
    private static final String BUTTON_WAY_TEXT_CUSTOM = "自定义";
    private static final String BUTTON_TARGET_TEXT = "设定行房周期";
    private static final String BUTTON_TARGET_AUTO_TEXT = "自动生成";
    private static final String BUTTON_BIRTHDAY_TEXT = "设定生日";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("wangsc", "SettingActivity is loading ...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_action, ActionBarFragment.newInstance()).commit();

        try {
            spinner_zodiac1 = (Spinner) findViewById(R.id.spinner_zodiac1);
            spinner_zodiac2 = (Spinner) findViewById(R.id.spinner_zodiac2);
            spinner_mdtype = (Spinner) findViewById(R.id.spinner_mdtype);
            spinner_mdrelation = (Spinner) findViewById(R.id.spinner_mdrelation);
            spinner_month = (Spinner) findViewById(R.id.spinner_month);
            spinner_day = (Spinner) findViewById(R.id.spinner_day);
            spinner_welcome = (Spinner) findViewById(R.id.spinner_welcome);
            spinner_duration = (Spinner) findViewById(R.id.spinner_duration);

            btn_addMD = (Button) findViewById(R.id.button_addMD);
            textView_guide = (TextView) findViewById(R.id.textView_guide);
            textView_update = (TextView) findViewById(R.id.textView_update);

            checkBox_szr = (CheckBox) findViewById(R.id.checkBox_szr);
            checkBox_lzr = (CheckBox) findViewById(R.id.checkBox_lzr);
            checkBox_gyz = (CheckBox) findViewById(R.id.checkBox_gyz);

            layoutBirthday = (LinearLayout) findViewById(R.id.layout_birthday);
//            layoutOpened = (LinearLayout) findViewById(R.id.layout_opened);

            btnRecordStatus = (Button) findViewById(R.id.button_recordStatus);
            btnWay = (Button) findViewById(R.id.button_targetWay);
            btnBirthday = (Button) findViewById(R.id.button_birthday);
            btnTarget = (Button) findViewById(R.id.button_customTarget);

            this.initializeFields();
            this.initializeEvents();
            Log.i("wangsc", "SettingActivity have loaded ...");
        } catch (Exception ex) {
            _Helper.printExceptionSycn(SettingActivity.this, uiHandler, ex);
        }
    }

    private void initializeFields() {
        try {
            dataContext = new DataContext(SettingActivity.this);
            isCalenderChanged = false;
            isRecordSetChanged = false;

//        if (UpdateManager.isUpdate())
//            textView_update.setVisibility(View.VISIBLE);

            TextView textViewVersion = (TextView) findViewById(R.id.textView_Version);
            textViewVersion.setText("寿康宝鉴日历 " + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);

            if (Boolean.parseBoolean(dataContext.getSetting(Setting.KEYS.recordIsOpened, false).getValue()) == true) {
                btnRecordStatus.setBackgroundResource(R.drawable.on);
//                layoutOpened.setVisibility(View.VISIBLE);


            } else {
                btnRecordStatus.setBackgroundResource(R.drawable.off);
//                layoutOpened.setVisibility(View.GONE);
            }

            btnBirthday.setText(BUTTON_BIRTHDAY_TEXT);
            btnTarget.setText(BUTTON_TARGET_TEXT);
            if (Boolean.parseBoolean(dataContext.getSetting(Setting.KEYS.targetAuto, true).getValue()) == true) {
                btnWay.setText(BUTTON_WAY_TEXT_AUTO);
                layoutBirthday.setVisibility(View.VISIBLE);
                autoWayDataInit();
            } else {
                btnWay.setText(BUTTON_WAY_TEXT_CUSTOM);
                layoutBirthday.setVisibility(View.GONE);
                customWayDataInit();
            }

            /**
             * 开关记录
             */
            btnRecordStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataContext.getSetting(Setting.KEYS.recordIsOpened, false).getBoolean() == false) {
                        if (dataContext.getSetting(Setting.KEYS.targetAuto).getBoolean() == true) {
                            if (dataContext.getSetting(Setting.KEYS.birthday) == null) {
                                new AlertDialog.Builder(SettingActivity.this).setMessage("请先设定生日！").show();
                                return;
                            }
                        } else {
                            if (dataContext.getSetting(Setting.KEYS.targetInMillis) == null) {
                                new AlertDialog.Builder(SettingActivity.this).setMessage("请先设定行房周期！").show();
                                return;
                            }
                        }
                        btnRecordStatus.setBackgroundResource(R.drawable.on);
//                        layoutOpened.setVisibility(View.VISIBLE);
                        dataContext.editSetting(Setting.KEYS.recordIsOpened, true);
                    } else {
                        btnRecordStatus.setBackgroundResource(R.drawable.off);
//                        layoutOpened.setVisibility(View.GONE);
                        dataContext.editSetting(Setting.KEYS.recordIsOpened, false);
                    }
                    isRecordSetChanged = true;
                }
            });

            /**
             * 间隔方式：自动、自定义。
             */
            btnWay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnWay.getText().equals(BUTTON_WAY_TEXT_AUTO)) {
                        btnWay.setText(BUTTON_WAY_TEXT_CUSTOM);
                        layoutBirthday.setVisibility(View.GONE);
                        dataContext.editSetting(Setting.KEYS.targetAuto, false);
                        if (dataContext.getSetting(Setting.KEYS.recordIsOpened, false).getBoolean() == true && dataContext.getSetting(Setting.KEYS.targetInMillis) == null) {
                            showTargetDialog(SettingActivity.this);
                        } else {
                            customWayDataInit();
                        }
                    } else {
                        btnWay.setText(BUTTON_WAY_TEXT_AUTO);
                        layoutBirthday.setVisibility(View.VISIBLE);
                        dataContext.editSetting(Setting.KEYS.targetAuto, true);
                        if (dataContext.getSetting(Setting.KEYS.recordIsOpened, false).getBoolean() == true && dataContext.getSetting(Setting.KEYS.birthday) == null) {
                            showBirthdayDialog(SettingActivity.this);
                        } else {
                            autoWayDataInit();
                        }
                    }
                    isRecordSetChanged = true;
                }
            });
            /**
             * 设置生日按钮
             */
            btnBirthday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataContext.getSetting(Setting.KEYS.targetAuto, true).getBoolean() == true)
                        showBirthdayDialog(SettingActivity.this);
                }
            });
            /**
             * 自定义间隔
             */
            btnTarget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataContext.getSetting(Setting.KEYS.targetAuto, true).getBoolean() == false)
                        showTargetDialog(SettingActivity.this);
                }
            });

            /**
             * 十斋日、六斋日、观音斋
             */
            Setting szr = dataContext.getSetting(Setting.KEYS.szr, false);
            checkBox_szr.setChecked(Boolean.parseBoolean(szr.getValue()));
            Setting lzr = dataContext.getSetting(Setting.KEYS.lzr, false);
            checkBox_lzr.setChecked(Boolean.parseBoolean(lzr.getValue()));
            Setting gyz = dataContext.getSetting(Setting.KEYS.gyz, false);
            checkBox_gyz.setChecked(Boolean.parseBoolean(gyz.getValue()));

            /**
             * 太岁日
             */
            this.initializeZodiac(spinner_zodiac1);
            this.initializeZodiac(spinner_zodiac2);
            Setting zodiac1 = dataContext.getSetting(Setting.KEYS.zodiac1);
            Setting zodiac2 = dataContext.getSetting(Setting.KEYS.zodiac2);
            if (zodiac1 != null) {
                spinner_zodiac1.setSelection(Zodiac.fromString(zodiac1.getValue()).toInt(), true);
            }
            if (zodiac2 != null) {
                spinner_zodiac2.setSelection(Zodiac.fromString(zodiac2.getValue()).toInt(), true);
            }

            /**
             * 纪念日
             */
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < MDtype.count(); i++) {
                list.add(MDtype.fromInt(i).toString());
            }
            this.fillSpinner(spinner_mdtype, list);
            List<String> list2 = new ArrayList<String>();
            for (int i = 0; i < MDrelation.count(); i++) {
                list2.add(MDrelation.fromInt(i).toString());
            }
            this.fillSpinner(spinner_mdrelation, list2);

            this.initializeLunarMonth(spinner_month);
            this.initializeLunarDay(spinner_day);

            List<MemorialDay> memorialDays = dataContext.getMemorialDays();
            mdListItems = new ArrayList<HashMap<String, String>>();
            for (MemorialDay md : memorialDays) {
                this.addListItem(md);
            }
            refreshMdList();

            /**
             * 欢迎界面
             */
            this.initializeWelcome();
            spinner_welcome.setSelection(Integer.parseInt(dataContext.getSetting(Setting.KEYS.welcome, 0).getValue()), true);

            this.initializeDuration();
            spinner_duration.setSelection(Integer.parseInt(dataContext.getSetting(Setting.KEYS.welcome_duration, 1).getValue()), true);

        } catch (Exception e) {
            _Helper.printExceptionSycn(this, uiHandler, e);
        }
//        mdListAdapter = new MDlistdAdapter();
//        listViewMD.setAdapter(mdListAdapter);
    }

    private void customWayDataInit() {
        try {
            Setting settingCustom = dataContext.getSetting(Setting.KEYS.targetInMillis);
            if (settingCustom != null) {
                long targetInMillis = Integer.parseInt(settingCustom.getValue());
                btnTarget.setText(DateTime.toSpanString(targetInMillis, 4, 3));
            } else {
                btnTarget.setText(BUTTON_TARGET_TEXT);
            }
        } catch (Exception e) {
            _Helper.printException(this, e);
        }
    }

    private void autoWayDataInit() {
        try {
            Setting settingBirthday = dataContext.getSetting(Setting.KEYS.birthday);
            if (settingBirthday != null) {
                DateTime birthday = settingBirthday.getDateTime();
                btnBirthday.setText(birthday.toShortDateString());
                btnTarget.setText(DateTime.toSpanString(_Helper.getTargetInMillis(birthday), 4, 3));
            } else {
                btnBirthday.setText(BUTTON_BIRTHDAY_TEXT);
                btnTarget.setText(BUTTON_TARGET_AUTO_TEXT);
            }
        } catch (Exception e) {
            _Helper.printException(this, e);
        }
    }

    private void addListItem(MemorialDay md) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", md.getId().toString());
        map.put("relation", md.getRelation().toString());
        map.put("type", md.getType().toString());
        map.put("lunarDate", md.getLunarDate().toString());
        mdListItems.add(map);
    }

    private void refreshMdList() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mdList);
        linearLayout.removeAllViews();
        for (int position = 0; position < mdListItems.size(); position++) {
            final int index = position;
            View convertView = View.inflate(SettingActivity.this, R.layout.inflate_md_list_item, null);
            HashMap<String, String> map = mdListItems.get(position);
            TextView textViewRelation = (TextView) convertView.findViewById(R.id.textView_relation);
            TextView textViewType = (TextView) convertView.findViewById(R.id.textView_type);
            TextView textViewLunarDate = (TextView) convertView.findViewById(R.id.textView_lunarDate);
            LinearLayout btnDel = (LinearLayout) convertView.findViewById(R.id.linear_delete);
            final String relation = map.get("relation");
            final String type = map.get("type");
            String lunarDate = map.get("lunarDate");
            textViewRelation.setText(relation);
            textViewType.setText(type);
            textViewLunarDate.setText(lunarDate);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("删除确认");
                    builder.setMessage(_String.concat("是否要删除【", relation, "\t", type, "】?"));
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataContext.deleteMemorialDay(UUID.fromString(mdListItems.get(index).get("id")));
                            mdListItems.remove(index);
                            refreshMdList();
                            isCalenderChanged = true;
                            dialog.cancel();
                            snackbar("删除成功");
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
            });
            linearLayout.addView(convertView, 0);
        }
    }

    private Handler uiHandler = new Handler();

    private void initializeEvents() {
//        textView_update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UpdateManager manager = new UpdateManager(SettingActivity.this);
//                manager.startDownload();
//            }
//        });
        textView_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideActivity.btnText = "返回软件";
                startActivity(new Intent(SettingActivity.this, GuideActivity.class));
            }
        });
        checkBox_szr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    dataContext.editSetting(Setting.KEYS.szr, isChecked);
                    isCalenderChanged = true;
                    snackbarSaved();
                }
            }
        });
        checkBox_lzr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    dataContext.editSetting(Setting.KEYS.lzr, isChecked);
                    isCalenderChanged = true;
                    snackbarSaved();
                }
            }
        });
        checkBox_gyz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    dataContext.editSetting(Setting.KEYS.gyz, isChecked);
                    isCalenderChanged = true;
                    snackbarSaved();
                }
            }
        });
        btn_addMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MemorialDay md = new MemorialDay();
                    String relation = spinner_mdrelation.getSelectedItem().toString();
                    String type = spinner_mdtype.getSelectedItem().toString();
                    String month = spinner_month.getSelectedItem().toString();
                    String day = spinner_day.getSelectedItem().toString();
                    md.setRelation(MDrelation.fromString(relation));
                    md.setType(MDtype.fromString(type));
                    md.setLunarDate(new LunarDate(month, day));
                    dataContext.addMemorialDay(md);

                    //
                    SettingActivity.this.addListItem(md);
                    refreshMdList();
                    isCalenderChanged = true;
                    snackbar("添加成功");
                } catch (Exception ex) {
                    _Helper.printExceptionSycn(SettingActivity.this, uiHandler, ex);
                }
            }
        });
        spinner_zodiac1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Setting setting = dataContext.getSetting(Setting.KEYS.zodiac1);
                String zodiac = spinner_zodiac1.getItemAtPosition(position).toString();
//                if (setting != null && !setting.getValue().equals(zodiac)) {
                dataContext.editSetting(Setting.KEYS.zodiac1, zodiac);
                isCalenderChanged = true;
                snackbarSaved();

//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_zodiac2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Setting setting = dataContext.getSetting(Setting.KEYS.zodiac2);
                String zodiac = spinner_zodiac2.getItemAtPosition(position).toString();
//                if (setting != null && !setting.getValue().equals(zodiac)) {
                dataContext.editSetting(Setting.KEYS.zodiac2, zodiac);
                isCalenderChanged = true;
                snackbarSaved();
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_welcome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                      @Override
                                                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                          Setting setting = dataContext.getSetting(Setting.KEYS.welcome, 0);
//                                                          if (!setting.getValue().equals(position + "")) {
                                                          dataContext.editSetting(Setting.KEYS.welcome, spinner_welcome.getSelectedItemPosition());
                                                          snackbarSaved();
//                                                          }
                                                      }

                                                      @Override
                                                      public void onNothingSelected(AdapterView<?> parent) {

                                                      }
                                                  }

        );
        spinner_duration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Setting setting = dataContext.getSetting(Setting.KEYS.welcome_duration, 1);
//                if (!setting.getValue().equals(position + "")) {
                dataContext.editSetting(Setting.KEYS.welcome_duration, spinner_duration.getSelectedItemPosition());
                snackbarSaved();
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeDuration() {
        List<String> list = new ArrayList<String>();
        list.add("2秒");
        list.add("3秒");
        list.add("4秒");
        list.add("5秒");
        list.add("6秒");
        list.add("7秒");
        this.fillSpinner(spinner_duration, list);
    }

    private void initializeWelcome() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < _Session.welcomes.size(); i++) {
            list.add(_Session.welcomes.get(i).getListItemString());
        }
        this.fillSpinner(spinner_welcome, list);
    }

    private void initializeZodiac(Spinner spinner) {
        List<String> mItems = new ArrayList<String>();
        for (int i = 0; i < Zodiac.count(); i++) {
            mItems.add(Zodiac.fromInt(i).toString());
        }
        this.fillSpinner(spinner, mItems);
    }

    private void initializeLunarMonth(Spinner spinner) {
        this.fillSpinner(spinner, LunarDate.Months);
    }

    private void initializeLunarDay(Spinner spinner) {
        this.fillSpinner(spinner, LunarDate.Days);
    }

    private void fillSpinner(Spinner spinner, List<String> values) {
        ArrayAdapter<String> aspn = new ArrayAdapter<String>(SettingActivity.this, R.layout.inflate_spinner, values);
        aspn.setDropDownViewResource(R.layout.inflate_spinner_dropdown);
        spinner.setAdapter(aspn);
    }

    @Override
    public void onBackListener() {
        this.finish();
    }

    /**
     *
     */
    protected class MDlistdAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mdListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int index = position;
            final View vew = convertView;
            getApplicationContext().getResources();
            convertView = View.inflate(SettingActivity.this, R.layout.inflate_md_list_item, null);
            HashMap<String, String> map = mdListItems.get(position);
            TextView textViewRelation = (TextView) convertView.findViewById(R.id.textView_relation);
            TextView textViewType = (TextView) convertView.findViewById(R.id.textView_type);
            TextView textViewLunarDate = (TextView) convertView.findViewById(R.id.textView_lunarDate);
            ImageView btnDel = (ImageView) convertView.findViewById(R.id.imageView_Del);
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("删除确认");
                    builder.setMessage("是否要删除此纪念日?");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataContext.deleteMemorialDay(UUID.fromString(mdListItems.get(index).get("id")));
                            mdListItems.remove(index);
                            mdListAdapter.notifyDataSetChanged();
                            isCalenderChanged = true;
                            dialog.cancel();
                            snackbar("删除成功");
//                            Toast.makeText(SettingActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create().show();
                }
            });
            textViewRelation.setText(map.get("relation"));
            textViewType.setText(map.get("type"));
            textViewLunarDate.setText(map.get("lunarDate"));
            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void snackbarSaved() {
        snackbar("设置已保存");
    }

    private void snackbar(String message) {
        RelativeLayout root = (RelativeLayout) findViewById(R.id.layout_setting_root);
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }

    public void showBirthdayDialog(final Context context) {

        View view = View.inflate(context, R.layout.inflate_dialog_date_picker, null);
        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(context).setView(view).create();
        dialog.setTitle("设定生日");
        final NumberPicker npYear = (NumberPicker) view.findViewById(R.id.npYear);
        final NumberPicker npMonth = (NumberPicker) view.findViewById(R.id.npMonth);
        final NumberPicker npDay = (NumberPicker) view.findViewById(R.id.npDay);
        final NumberPicker npHour = (NumberPicker) view.findViewById(R.id.npHour);

        Setting setting = dataContext.getSetting(Setting.KEYS.birthday);
        DateTime date = null;
        if (setting == null) {
            date = new DateTime();
        } else {
            date = setting.getDateTime();
        }
        final int cyear = new DateTime().getYear();
        final int year = date.getYear();
        int month = date.getMonth();
        final int day = date.getDay();


        try {
            String[] yearNumbers = new String[100];
            for (int i = cyear - 99; i <= cyear; i++) {
                yearNumbers[i - cyear + 99] = i + "年";
            }
            String[] monthNumbers = new String[12];
            for (int i = 0; i < 12; i++) {
                monthNumbers[i] = i + 1 + "月";
            }
            String[] dayNumbers = new String[31];
            for (int i = 0; i < 31; i++) {
                dayNumbers[i] = i + 1 + "日";
            }
            npHour.setVisibility(View.GONE);
            npYear.setMinValue(cyear - 99);
            npYear.setMaxValue(cyear);
            npYear.setValue(year);
            npYear.setDisplayedValues(yearNumbers);
            npYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
            npMonth.setMinValue(1);
            npMonth.setMaxValue(12);
            npMonth.setDisplayedValues(monthNumbers);
            npMonth.setValue(month + 1);
            npMonth.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
            npDay.setMinValue(1);
            npDay.setMaxValue(31);
            npDay.setDisplayedValues(dayNumbers);
            npDay.setValue(day);
            npDay.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
        } catch (Exception e) {
            _Helper.printException(SettingActivity.this, e);
        }


        npMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                DateTime selected = new DateTime(npYear.getValue(), npMonth.getValue() - 1, 1);
                int max = selected.getActualMaximum(Calendar.DAY_OF_MONTH);

                int day = npDay.getValue();
                npDay.setMaxValue(max);
                if (day > max) {
                    npDay.setValue(1);
                } else {
                    npDay.setValue(day);
                }
            }
        });

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int year = npYear.getValue();
                    int month = npMonth.getValue() - 1;
                    int day = npDay.getValue();
                    DateTime selectedDateTime = new DateTime(year, month, day, 0, 0, 0);
                    dataContext.editSetting(Setting.KEYS.birthday, selectedDateTime.getTimeInMillis());
                    isRecordSetChanged = true;
                    autoWayDataInit();
                    dialog.dismiss();
                } catch (Exception e) {
                    _Helper.printException(context, e);
                }
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                    if (dataContext.getSetting(Setting.KEYS.recordIsOpened, false).getBoolean() == true
                            && dataContext.getSetting(Setting.KEYS.targetAuto, true).getBoolean() == true
                            && dataContext.getSetting(Setting.KEYS.birthday) == null) {
                        dataContext.editSetting(Setting.KEYS.recordIsOpened, false);
                        btnRecordStatus.setBackgroundResource(R.drawable.off);
                    }
                } catch (Exception e) {
                    _Helper.printException(context, e);
                }
            }
        });
        dialog.show();
    }


    public void showTargetDialog(final Context context) {

        try {
            View view = View.inflate(context, R.layout.inflate_dialog_date_picker, null);
            android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(context).setView(view).create();
            dialog.setTitle("自定义间隔");

            long target = 5 * 24 * 3600000;
            Setting setting = dataContext.getSetting(Setting.KEYS.targetInMillis);
            if (setting != null) {
                target = setting.getLong();
            }
            int aaa = (int) (target / 3600000 / 24);
            int bbb = (int) (target % (3600000 * 24) / 3600000);
            String[] dayNumbers = new String[99];
            for (int i = 0; i < 99; i++) {
                dayNumbers[i] = i + 2 + "天";
            }
            String[] hourNumbers = new String[24];
            for (int i = 0; i < 24; i++) {
                hourNumbers[i] = i + "小时";
            }
            final NumberPicker npYear = (NumberPicker) view.findViewById(R.id.npYear);
            npYear.setVisibility(View.GONE);
            final NumberPicker npMonth = (NumberPicker) view.findViewById(R.id.npMonth);
            npMonth.setVisibility(View.GONE);
            final NumberPicker npDays = (NumberPicker) view.findViewById(R.id.npDay);
            final NumberPicker npHours = (NumberPicker) view.findViewById(R.id.npHour);
            npDays.setMinValue(2);
            npDays.setMaxValue(100);
            npDays.setDisplayedValues(dayNumbers);
            npDays.setValue(aaa);
            npDays.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
            npHours.setMinValue(0);
            npHours.setMaxValue(23);
            npHours.setDisplayedValues(hourNumbers);
            npHours.setValue(bbb);
            npHours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        int days = npDays.getValue();
                        int hours = npHours.getValue();
                        long target = (days * 24 + hours) * 60 * 60000;
                        dataContext.editSetting(Setting.KEYS.targetInMillis, target);
                        btnTarget.setText(DateTime.toSpanString(target, 4, 3));
                        isRecordSetChanged = true;
                        customWayDataInit();
                        dialog.dismiss();
                    } catch (Exception e) {
                        _Helper.printException(context, e);
                    }
                }
            });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.dismiss();
                        if (dataContext.getSetting(Setting.KEYS.recordIsOpened, false).getBoolean() == true
                                && dataContext.getSetting(Setting.KEYS.targetAuto, true).getBoolean() == false
                                && dataContext.getSetting(Setting.KEYS.targetInMillis) == null) {
                            dataContext.editSetting(Setting.KEYS.recordIsOpened, false);
                            btnRecordStatus.setBackgroundResource(R.drawable.off);
                        }
                    } catch (Exception e) {
                        _Helper.printException(context, e);
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
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

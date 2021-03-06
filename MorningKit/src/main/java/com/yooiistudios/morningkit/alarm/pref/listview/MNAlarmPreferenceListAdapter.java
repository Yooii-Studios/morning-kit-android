package com.yooiistudios.morningkit.alarm.pref.listview;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.yooiistudios.morningkit.alarm.model.MNAlarm;
import com.yooiistudios.morningkit.alarm.pref.listview.item.maker.MNAlarmPrefItemMaker;
import com.yooiistudios.morningkit.alarm.pref.listview.item.maker.MNAlarmPrefLabelItemMaker;
import com.yooiistudios.morningkit.alarm.pref.listview.item.maker.MNAlarmPrefRepeatItemMaker;
import com.yooiistudios.morningkit.alarm.pref.listview.item.maker.MNAlarmPrefSoundItemMaker;
import com.yooiistudios.morningkit.common.bus.MNAlarmPrefBusProvider;
import com.yooiistudios.stevenkim.alarmsound.OnAlarmSoundClickListener;
import com.yooiistudios.stevenkim.alarmsound.SKAlarmSound;

/**
 * Created by StevenKim in MorningKit from Yooii Studios Co., LTD. on 2013. 12. 7.
 *
 * MNAlarmPreferenceListAdapter
 *  알람설정 리스트뷰를 초기화하는 어댑터
 */
public class MNAlarmPreferenceListAdapter extends BaseAdapter implements OnAlarmSoundClickListener {
    private Context context;
    private MNAlarm alarm;

    @SuppressWarnings("unused")
    private MNAlarmPreferenceListAdapter() {}
    public MNAlarmPreferenceListAdapter(Context context, MNAlarm alarm) {
        this.context = context;
        this.alarm = alarm;
        MNAlarmPrefBusProvider.getInstance().register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean hasVibrator = true;
        if (Build.VERSION.SDK_INT > 10) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            hasVibrator = vibrator.hasVibrator();
        }
        // 바이브레이터가 없을 때는 표시하지 않게 구현
        MNAlarmPrefListItemType indexType = MNAlarmPrefListItemType.valueOf(position);
        if (indexType == MNAlarmPrefListItemType.VIBRATE && !hasVibrator) {
            indexType = MNAlarmPrefListItemType.valueOf(position + 1);
        }
        switch (indexType) {
            case REPEAT:
                convertView = MNAlarmPrefRepeatItemMaker.makeRepeatItem(context, parent, alarm);
                break;
            case LABEL:
                convertView = MNAlarmPrefLabelItemMaker.makeLabelItem(context, parent, alarm);
                break;
            case SOUND:
                convertView = MNAlarmPrefSoundItemMaker.makeSoundItem(context, parent, alarm, this);
                break;
            case SNOOZE:
                convertView = MNAlarmPrefItemMaker.makeSnoozeItem(context, parent, alarm);
                break;
            case VIBRATE:
                convertView = MNAlarmPrefItemMaker.makeVibrateItem(context, parent, alarm);
                break;
            case TIME:
                convertView = MNAlarmPrefItemMaker.makeTimeItem(context, parent, alarm);
                break;
            case VOLUME:
                convertView = MNAlarmPrefItemMaker.makeVolumeItem(context, parent, alarm);
        }
        convertView.setBackgroundColor(Color.WHITE);
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        int vibratorOffset = 0;
        if (Build.VERSION.SDK_INT > 10) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (!vibrator.hasVibrator()) {
                vibratorOffset = 1;
            }
        }
        return MNAlarmPrefListItemType.values().length - vibratorOffset;
    }

    /**
     * Otto: MNAlarmPrefBusProvider
     */
    @Subscribe
    public void onLabelChanged(String labelString) {
        if (labelString != null) {
            if (labelString.length() != 0) {
                alarm.setAlarmLabel(labelString);
            } else {
                alarm.setAlarmLabel("Alarm");
            }
            notifyDataSetChanged();
        } else {
            throw new AssertionError("labelString must be null!");
        }
    }

    @Subscribe
    public void onRepeatChanged(boolean[] repeats) {
        if (alarm != null && repeats.length == 7) {
            alarm.setRepeatOn(false);
            for (int i = 0; i < repeats.length; i++) {
                if (repeats[i]) {
                    alarm.setRepeatOn(true);
                }
                alarm.getAlarmRepeatList().set(i, repeats[i]);
            }
            notifyDataSetChanged();
        } else {
            throw new AssertionError("alarm must not be null!");
        }
    }

    @Override
    public void onAlarmSoundSelected(SKAlarmSound alarmSound) {
        MNAlarmPrefBusProvider.getInstance().post(context);
        if (alarm != null) {
            if (alarmSound != null) {
                alarm.setAlarmSound(alarmSound);
                notifyDataSetChanged();
            } else {
                throw new AssertionError("alarmSound must not be null!");
            }
        } else {
            throw new AssertionError("alarm must not be null!");
        }
    }

    @Override
    public void onAlarmSoundSelectCanceled() {
        MNAlarmPrefBusProvider.getInstance().post(context);
    }

    @Override
    public void onAlarmSoundSelectFailedDueToUsbConnection() {
        MNAlarmPrefBusProvider.getInstance().post(context);
        Toast.makeText(context, "Can't access due to USB connection", Toast.LENGTH_SHORT).show();
    }
}

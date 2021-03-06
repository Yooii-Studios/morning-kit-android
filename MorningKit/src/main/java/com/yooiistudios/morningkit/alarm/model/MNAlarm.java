package com.yooiistudios.morningkit.alarm.model;

import android.content.Context;

import com.yooiistudios.morningkit.alarm.model.string.MNAlarmToast;
import com.yooiistudios.morningkit.main.MNMainActivity;
import com.yooiistudios.stevenkim.alarmmanager.SKAlarmManager;
import com.yooiistudios.stevenkim.alarmsound.SKAlarmSound;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by StevenKim on 2013. 11. 11..
 *
 * MNAlarm
 *  알람 자료구조
 */
public class MNAlarm implements Serializable, Cloneable {
    private static final String TAG = "MNAlarm";

    // 로직의 변경이 있을 때 같은 클래스임을 명시적으로 알려 주는 코드
    private static final long serialVersionUID = 1L;

    /**
     * Variables
     */
    @Getter @Setter private boolean             isAlarmOn;
    @Getter @Setter private boolean             isSnoozeOn;
    @Getter @Setter private boolean             isRepeatOn;
    @Getter @Setter private boolean             isVibrateOn;

    @Getter @Setter private ArrayList<Boolean>  alarmRepeatList;

    @Getter @Setter private String              alarmLabel;

    // 한 알람당 8개 할당. n+0번 ~ n+6번: 미반복/월(0번이 월요일이거나 미반복) ~ 일, n+7번: 스누즈
    @Getter @Setter private int                 alarmId;

    @Getter @Setter private Calendar            alarmCalendar;

    @Getter @Setter private SKAlarmSound        alarmSound;

    @Getter @Setter private int                 alarmVolume;

    /**
     * Methods
     */
    // 이 메서드 호출을 방지, 일반적으로 MNAlarmMaker에서 생성해 사용할 것
    private MNAlarm() {}

    public static MNAlarm newInstance() {
        MNAlarm alarm = new MNAlarm();
        alarm.alarmRepeatList = new ArrayList<Boolean>(7);
        return alarm;
    }

    public void stopAlarm(Context context) {
        isAlarmOn = false;

        if (isRepeatOn) {
            for (int i = 0; i < alarmRepeatList.size(); i++) {
                SKAlarmManager.cancelAlarm(alarmId + i, context, MNMainActivity.class);
            }
        } else {
            SKAlarmManager.cancelAlarm(alarmId, context, MNMainActivity.class);
        }

        // 스누즈 알람도 제거
        SKAlarmManager.cancelAlarm(alarmId + 7, context, MNMainActivity.class);
    }

    public void startAlarm(Context context) {
        isAlarmOn = true;

        // 캘린더 시간 보정
        alarmCalendar = SKAlarmManager.adjustCalendar(alarmCalendar);

        if (isRepeatOn) {
            startRepeatAlarm(context, true);
        } else {
            startNonRepeatAlarm(context, true);
        }
    }

    public void startAlarmWithNoToast(Context context) {
        isAlarmOn = true;

        alarmCalendar = SKAlarmManager.adjustCalendar(alarmCalendar);

        if (isRepeatOn) {
            startRepeatAlarm(context, false);
        } else {
            startNonRepeatAlarm(context, false);
        }
    }

    private void startNonRepeatAlarm(Context context, boolean isToastOn) {
        SKAlarmManager.setAlarm(alarmId, alarmId, alarmCalendar, context, MNMainActivity.class);
        if (isToastOn) {
            MNAlarmToast.show(context, alarmCalendar);
        }
    }

    private void startRepeatAlarm(Context context, boolean isToastOn) {
        boolean isToastShown = false;

        for (int i = 0; i < alarmRepeatList.size(); i++) {
            Calendar repeatCalendar = (Calendar) alarmCalendar.clone();
            repeatCalendar.add(Calendar.DATE, i);

            // Calendar DayOfWeek : 1 ~ 7 : Sun ~ Sat
            // RepeatList         : 0 ~ 6 : Mon ~ Sun
            int convertedDayOfWeek = repeatCalendar.get(Calendar.DAY_OF_WEEK) - 2;
            if (convertedDayOfWeek < 0) {
                convertedDayOfWeek += 7;
            }

            if (alarmRepeatList.get(convertedDayOfWeek)) {
                SKAlarmManager.setAlarm(alarmId + i, alarmId, repeatCalendar, context, MNMainActivity.class);
                if (isToastOn && !isToastShown) {
                    MNAlarmToast.show(context, repeatCalendar);
                    isToastShown = true;
                }
            }
        }
    }

    public void snoozeAlarm(Context context) {
        Calendar snoozeCalendar = Calendar.getInstance();
        snoozeCalendar.set(Calendar.SECOND, 1);
        snoozeCalendar.add(Calendar.MINUTE, 10);

        SKAlarmManager.setAlarm(alarmId + 7, alarmId, snoozeCalendar, context, MNMainActivity.class);

        MNAlarmToast.show(context, snoozeCalendar);
    }

    // 혹시나 깊은 복사를 사용할 경우를 대비해서 가져옴
    // 아래 주석은 Eclipse에서 그대로 가져옴
    @SuppressWarnings("unchecked")
    public MNAlarm clone() throws CloneNotSupportedException {
        MNAlarm obj = (MNAlarm)super.clone();
        obj.alarmCalendar = (Calendar) alarmCalendar.clone();
        obj.alarmRepeatList = (ArrayList<Boolean>) alarmRepeatList.clone();

        return obj;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        return String.format("-alarmId: %d\n" +
                        "-alarmCalendar: %s\n" +
                        "-alarmLabel: %s\n" +
                        "-on: %s\n" +
                        "-repeat: %s\n" +
                        "-snooze: %s",
                alarmId,
                dateFormat.format(alarmCalendar.getTime()),
                alarmLabel,
                isAlarmOn ? "Yes" : "No",
                isRepeatOn ? "Yes: " + getRepeatString(0) + "/" + getRepeatString(1) + "/"
                                    + getRepeatString(2) + "/" + getRepeatString(3) + "/"
                                    + getRepeatString(4) + "/" + getRepeatString(5) + "/"
                                    + getRepeatString(6)
                           : "No",
                isSnoozeOn ? "Yes" : "No");
    }

    private String getRepeatString(int i) {
        return alarmRepeatList.get(i) ? "t" : "f";
    }
}

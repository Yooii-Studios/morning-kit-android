package com.yooiistudios.morningkit.common.log;

import android.util.Log;

import com.testflightapp.lib.TestFlight;

/**
 * Created by StevenKim in MorningKit from Yooii Studios Co., LTD. on 2014. 2. 18.
 *
 * MNLog
 *  앱의 전체적인 로그를 관리. 출시시엔 끌 수 있음
 */
public class MNLog {
    private MNLog() { throw new AssertionError("You MUST not create this class!"); }
    private static final boolean isDebug = true;

    public static void now(String message) {
        if (isDebug) {
            Log.i("MNLog", message);
        }
    }
    public static void i(String TAG, String message) {
        if (isDebug) {
            Log.i(TAG, message);
        }
    }
    public static void d(String TAG, String message) {
        if (isDebug) {
            Log.d(TAG, message);
        }
    }
    public static void testFlight(String message) {
        if (isDebug) {
            TestFlight.log(message);
        }
    }
}

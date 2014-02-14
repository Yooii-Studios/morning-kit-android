package com.yooiistudios.morningkit.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.yooiistudios.morningkit.R;
import com.yooiistudios.morningkit.common.sound.MNSoundEffectsPlayer;
import com.yooiistudios.morningkit.main.MNMainActivity;
import com.yooiistudios.morningkit.setting.store.util.IabHelper;
import com.yooiistudios.morningkit.setting.theme.soundeffect.MNSound;

import lombok.Setter;

/**
 * Created by StevenKim in Morning Kit from Yooii Studios Co., LTD. on 2014. 1. 6.
 *
 * MNSettingActivity
 *  세팅 액티비티: 액션바가 바탕이 되고 안드로이드 기본 코드를 이용해 구현
 */
public class MNSettingActivity extends ActionBarActivity implements ActionBar.TabListener {

    private static final String TAG = "MNSettingActivity";
    private static final String SETTING_PREFERENCES = "SETTING_PREFERENCES";
    private static final String LATEST_TAB_SELECTION= "LATEST_TAB_SELECTION";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    MNSettingSectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    MNSettingViewPager mViewPager;

    @Setter IabHelper iabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Theme
        setTheme(R.style.MNSettingActionBarTheme_Light);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle("Main");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.status_bar_icon);

        int latestTabIndex;
        SharedPreferences prefs = getSharedPreferences(SETTING_PREFERENCES, MODE_PRIVATE);
        if (prefs != null) {
            latestTabIndex = prefs.getInt(LATEST_TAB_SELECTION, 0);
        } else {
            latestTabIndex = 0;
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new MNSettingSectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (MNSettingViewPager) findViewById(R.id.setting_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        mViewPager.setCurrentItem(latestTabIndex);
    }

    private void applyLocaledTabName() {
        final ActionBar actionBar = getSupportActionBar();

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.getTabAt(i).setText(mSectionsPagerAdapter.getPageTitle(i));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyLocaledTabName();
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode);

        if (iabHelper == null) return;
            // Pass on the activity result to the helper for handling
        if (!iabHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
//        } else {
//            Log.d("MNSettingActivity", "onActivityResult handled by IABUtil.");
//        }
        }
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (MNSound.isSoundOn(this)) {
                MNSoundEffectsPlayer.play(R.raw.effect_view_close, this);

                // test
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    startActivity(new Intent(this, MNMainActivity.class));
//                    return false;
//                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Catch the back button and make fragment animate
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if (MNSound.isSoundOn(this)) {
                MNSoundEffectsPlayer.play(R.raw.effect_view_close, this);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * TabListener
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

        // remember the selection
        SharedPreferences prefs = getSharedPreferences(SETTING_PREFERENCES, MODE_PRIVATE);
        prefs.edit().putInt(LATEST_TAB_SELECTION, tab.getPosition()).commit();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
}
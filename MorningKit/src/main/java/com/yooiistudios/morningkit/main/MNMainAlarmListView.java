package com.yooiistudios.morningkit.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.yooiistudios.morningkit.alarm.listview.MNAlarmListAdaptor;

/**
 * Created by yongbinbae on 13. 10. 22..
 */
public class MNMainAlarmListView extends ListView
{
    public MNMainAlarmListView(Context context) {
        super(context);
        init();
    }

    public MNMainAlarmListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MNMainAlarmListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

    }

    public void initWithListAdaptor(MNAlarmListAdaptor listAdaptor) {
        setAdapter(listAdaptor);
    }
}

package com.yooiistudios.morningkit.panel.calendar;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yooiistudios.morningkit.R;
import com.yooiistudios.morningkit.panel.calendar.model.MNCalendarSelectDialog;
import com.yooiistudios.morningkit.panel.calendar.model.MNCalendarUtils;
import com.yooiistudios.morningkit.panel.core.detail.MNPanelDetailFragment;

import org.json.JSONException;

import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.yooiistudios.morningkit.panel.calendar.MNCalendarPanelLayout.CALENDAR_DATA_SELECTED_CALEDNDARS;

/**
 * Created by StevenKim in MorningKit from Yooii Studios Co., LTD. on 2014. 4. 14.
 *
 * MNCalendarDetailFragment
 */
public class MNCalendarDetailFragment extends MNPanelDetailFragment implements MNCalendarSelectDialog.MNCalendarSelectDialogListner {

    private static final String TAG = "MNCalendarDetailFragment";

    @InjectView(R.id.calendar_detail_events_listview) ListView eventsListView;
    @InjectView(R.id.calendar_detail_select_calendars_button) Button selectCalendarsButton;

    boolean[] selectedArr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.panel_calendar_detail_fragment, container, false);
        if (rootView != null) {
            ButterKnife.inject(this, rootView);

            eventsListView.setBackgroundColor(Color.MAGENTA);

            // 패널 데이터 가져오기
            if (getPanelDataObject().has(CALENDAR_DATA_SELECTED_CALEDNDARS)) {
                String calendarModelsJsonString = null;
                try {
                    calendarModelsJsonString = getPanelDataObject().getString(CALENDAR_DATA_SELECTED_CALEDNDARS);
                    if (calendarModelsJsonString != null) {
                        Type type = new TypeToken<boolean[]>(){}.getType();
                        selectedArr = new Gson().fromJson(calendarModelsJsonString, type);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                try {
//                    // 기존 정보가 있다면 가져와서 표시
//                    // title
//                    titleEditText.setText(getPanelDataObject().getString(DATE_COUNTDOWN_DATA_TITLE));
//
//                    // date - JSONString에서 클래스로 캐스팅
//                    Type type = new TypeToken<MNDate>(){}.getType();
//                    String dateJsonString = getPanelDataObject().getString(DATE_COUNTDOWN_DATA_DATE);
//                    MNDate date = new Gson().fromJson(dateJsonString, type);
//                    // Calendar의 month는 -1을 해줘야 맞는다
//                    datePicker.init(date.getYear(), date.getMonth() - 1, date.getDay(), null);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            } else {
//                // 기존 정보가 없다면 새해 표시
//                titleEditText.setText(R.string.date_countdown_new_year);
//                MNDate date = MNDefaultDateMaker.getDefaultDate();
//                // Calendar의 month는 -1을 해줘야 맞는다
//                datePicker.init(date.getYear(), date.getMonth() - 1, date.getDay(), null);
            }
        }
        return rootView;
    }

    @Override
    protected void archivePanelData() throws JSONException {
        AlertDialog calendarSelectDialog = MNCalendarSelectDialog.makeDialog(getActivity(), this,
                MNCalendarUtils.loadCalendarModels(getActivity()));
        calendarSelectDialog.show();
    }

    @Override
    public void onSelectCalendars(boolean[] selectedArr) {
        try {
            getPanelDataObject().put(MNCalendarPanelLayout.CALENDAR_DATA_SELECTED_CALEDNDARS,
                    new Gson().toJson(selectedArr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

package com.yooiistudios.morningkit.panel.calendar.model;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.provider.BaseColumns._ID;
import static android.provider.CalendarContract.Events;
import static android.provider.CalendarContract.Events.ALL_DAY;
import static android.provider.CalendarContract.Events.CALENDAR_ID;
import static android.provider.CalendarContract.Events.CONTENT_URI;
import static android.provider.CalendarContract.Events.DESCRIPTION;
import static android.provider.CalendarContract.Events.DTEND;
import static android.provider.CalendarContract.Events.DTSTART;
import static android.provider.CalendarContract.Events.RRULE;
import static android.provider.CalendarContract.Events.TITLE;

/**
 * Created by StevenKim in GoogleCalendarTestApp from Yooii Studios Co., LTD. on 2014. 4. 3.
 * <p/>
 * MNCalendarUtils
 * 디바이스 내의 캘린더 ID들과, 해당 ID의 캘린더 이벤트들을 얻을 수 있는 유틸리티 클래스
 */
public class MNCalendarFetcher {
    private static final String TAG = "MNCalendarUtils";

    private MNCalendarFetcher() {
        throw new AssertionError("You MUST not create this class!");
    }

    public static ArrayList<MNCalendar> getCalendarModels(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        // Fetch a list of all calendars synced with the device, their display names and whether the
        // user has them selected for display.
        // content://com.android.calendar // 2.2(Froyo) or greater
        // content://calendar/calendars // below 2.2
        final Cursor cursor = contentResolver.query(getCalendarURI(false),
                (new String[]{"_id", "displayName", "selected"}), null, null, null);
        // For a full list of available columns see http://tinyurl.com/yfbg76w

        if (cursor != null) {
            ArrayList<MNCalendar> calendarModels = new ArrayList<MNCalendar>();

            while (cursor.moveToNext()) {

                MNCalendar calendarModel = new MNCalendar();

                final String _id = cursor.getString(0);
                final String displayName = cursor.getString(1);
                final Boolean selected = !cursor.getString(2).equals("0");

//                System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);

                calendarModel.calendarId = _id;
                calendarModel.displayName = displayName;
                calendarModel.selected = true;
                calendarModels.add(calendarModel);
            }
            cursor.close();
            return calendarModels;
        }
        return null;
    }

    @TargetApi(14)
    public static ArrayList<MNCalendar> getCalendarModel14(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        // Fetch a list of all calendars synced with the device, their display names and whether the
        // user has them selected for display.
        Cursor cursor;
        cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, new String[]
                        { CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME},
                null, null, null
        ); // 캘린더 이름으로 ASC 정렬이었으나 그냥 id 순서대로 정렬로 변경
        // CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " ASC");

        if (cursor != null) {
            ArrayList<MNCalendar> calendarModels = new ArrayList<MNCalendar>();

            while (cursor.moveToNext()) {
                MNCalendar calendarModel = new MNCalendar();

                final String _id = cursor.getString(0);
                final String displayName = cursor.getString(1);

                calendarModel.calendarId = _id;
                calendarModel.displayName = displayName;
                calendarModel.selected = true;

//                System.out.println("Id:" + _id + "/Display Name:" + displayName);

                calendarModels.add(calendarModel);
            }
            cursor.close();
            return calendarModels;
        }
        return null;
    }

    //for os version android below version 4(ICS)
    public static MNCalendarEventList getCalendarEvents(Context context, String calID) {
        ContentResolver cr = context.getContentResolver();

        Uri.Builder builder = getInstancesURI().buildUpon();

        // 오늘부터 1년 간의 이벤트를 얻기 - 나중에 현 시간부터 내일까지로 변경 필요
        // 오늘-종일, 오늘-일정 / 내일-종일, 내일-일정 총 4개의 ArrayList가 필요
        MNCalendarEventList calendarEventList = new MNCalendarEventList();

        // 필요한 시간 기준들을 미리 준비
        DateTime todayNowDateTime = DateTime.now();
        DateTime tomorrowDateTime = DateTime.now().plusDays(1);
        DateTime todayStartDateTime = new DateTime(todayNowDateTime.getYear(), todayNowDateTime.getMonthOfYear(),
                todayNowDateTime.getDayOfMonth(), 0, 0, 0);
        DateTime todayEndDateTime = new DateTime(tomorrowDateTime.getYear(), tomorrowDateTime.getMonthOfYear(),
                tomorrowDateTime.getDayOfMonth(), 0, 0, 0);

        DateTime tomorrowStartDateTime = new DateTime(tomorrowDateTime.getYear(),
                tomorrowDateTime.getMonthOfYear(), tomorrowDateTime.getDayOfMonth(), 0, 0, 0);
        DateTime tomorrowEndDateTime = tomorrowStartDateTime.plusDays(1);

        // 지금부터 내일 0시 0분 0초 미만의 시간(그 중에서도 all-day와 scheduled를 분리)
        Log.i(TAG, "today events");
        Log.i(TAG, "all-day events");
        calendarEventList.todayAlldayEvents = getEventsBetweenDates(context, calID, true,
                todayStartDateTime, todayEndDateTime);
        Log.i(TAG, "scheduled events");
        calendarEventList.todayScheduledEvents = getEventsBetweenDates(context, calID, false,
                todayNowDateTime, todayEndDateTime);

        // 내일 0시 0분 0초 이상 모레 0시 0분 0초 미만의 일정(그 중에서도 all-day와 scheduled를 분리)
        Log.i(TAG, "tomorrow events");
        Log.i(TAG, "all-day events");
        calendarEventList.tomorrowAlldayEvents = getEventsBetweenDates(context, calID, true,
                tomorrowStartDateTime, tomorrowEndDateTime);
        Log.i(TAG, "scheduled events");
        calendarEventList.tomorrowScheduledEvents = getEventsBetweenDates(context, calID, false,
                tomorrowStartDateTime, tomorrowEndDateTime);

        return calendarEventList;
    }

    private static ArrayList<MNCalendarEvent> getEventsBetweenDates(Context context, String calID,
                                                                    boolean isAllDayEvents,
                                                                    DateTime startDateTime,
                                                                    DateTime endDateTime) {
        ContentResolver cr = context.getContentResolver();

        Uri.Builder builder = getInstancesURI().buildUpon();
        ContentUris.appendId(builder, startDateTime.getMillis());
        ContentUris.appendId(builder, endDateTime.getMillis());

        String selection = "Calendars._id = " + calID + " AND (" +
                startDateTime.getMillis() + " <= " + "begin" + " AND " +
                "begin" + " < " + endDateTime.getMillis() + ")";
        if (isAllDayEvents) {
            selection += " AND " + "allDay = 1";
        } else {
            selection += " AND " + "allDay = 0";
        }

        Cursor eventCursor = cr.query(builder.build(),
                new String[]{"title", "begin"}, selection, null, "begin ASC");

        if (eventCursor != null) {

            ArrayList<MNCalendarEvent> calendarModelList = new ArrayList<MNCalendarEvent>();

            while (eventCursor.moveToNext()) {
                MNCalendarEvent calendarEvent = new MNCalendarEvent();

                // title
                String title = "";
                if (eventCursor.getString(0) != null) {
                    title = eventCursor.getString(0).trim();
                    calendarEvent.title = title;
                }

                // beginDate
                Date begin = new Date(eventCursor.getLong(1));
                calendarEvent.beginDate = begin;

                // all day
                calendarEvent.isAllDayEvent = isAllDayEvents;

                SimpleDateFormat sdfrr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String stimesr = sdfrr.format(begin);

                System.out.println("title:" + title + "/stimes:" + stimesr +
                        (calendarEvent.isAllDayEvent ? "/all-day" : ""));

                calendarModelList.add(calendarEvent);
            }
            eventCursor.close();
            return calendarModelList;
        } else {
            Log.e(TAG, "eventCursor is null");

            return null;
        }
    }

    //for os version android version 4(ICS) AND ABOVE
    @TargetApi(14)
    public static MNCalendarEventList getCalendarEvents14(Context context,
                                                          ArrayList<MNCalendar> calendarModels) {
        // 오늘부터 1년 간의 이벤트를 얻기 - 나중에 현 시간부터 내일까지로 변경 필요
        // 오늘-종일, 오늘-일정 / 내일-종일, 내일-일정 총 4개의 ArrayList가 필요
        MNCalendarEventList calendarEventList = new MNCalendarEventList();

        // 필요한 시간 기준들을 미리 준비
        DateTime todayNowDateTime = DateTime.now();
        DateTime tomorrowDateTime = DateTime.now().plusDays(1);
        DateTime todayStartDateTime = new DateTime(todayNowDateTime.getYear(), todayNowDateTime.getMonthOfYear(),
                todayNowDateTime.getDayOfMonth(), 0, 0, 0);
        DateTime todayEndDateTime = new DateTime(tomorrowDateTime.getYear(), tomorrowDateTime.getMonthOfYear(),
                tomorrowDateTime.getDayOfMonth(), 0, 0, 0);

        DateTime tomorrowStartDateTime = new DateTime(tomorrowDateTime.getYear(),
                tomorrowDateTime.getMonthOfYear(), tomorrowDateTime.getDayOfMonth(), 0, 0, 0);
        DateTime tomorrowEndDateTime = tomorrowStartDateTime.plusDays(1);

        // 지금부터 내일 0시 0분 0초 미만의 시간(그 중에서도 all-day와 scheduled를 분리)
        Log.i(TAG, "today events");
        Log.i(TAG, "all-day events");
        calendarEventList.todayAlldayEvents = getEventsBetweenDates14(context, calendarModels, true,
                todayStartDateTime, todayEndDateTime);
        Log.i(TAG, "scheduled events");
        calendarEventList.todayScheduledEvents = getEventsBetweenDates14(context, calendarModels, false,
                todayNowDateTime, todayEndDateTime);
//        sortCalendarEventListByBeginTime(calendarEventList.todayScheduledEvents);

        // 내일 0시 0분 0초 이상 모레 0시 0분 0초 미만의 일정(그 중에서도 all-day와 scheduled를 분리)
        Log.i(TAG, "tomorrow events");
//        new DateTime(tomorrowDateTime.getYear(),
//                tomorrowDateTime.getMonthOfYear(), tomorrowDateTime.getDayOfMonth(), 23, 59, 59);
        Log.i(TAG, "all-day events");
        calendarEventList.tomorrowAlldayEvents = getEventsBetweenDates14(context, calendarModels, true,
                tomorrowStartDateTime, tomorrowEndDateTime);
        Log.i(TAG, "scheduled events");
        calendarEventList.tomorrowScheduledEvents = getEventsBetweenDates14(context, calendarModels, false,
                tomorrowStartDateTime, tomorrowEndDateTime);
//        sortCalendarEventListByBeginTime(calendarEventList.tomorrowScheduledEvents);

        return calendarEventList;
    }

    //for os version android version 4(ICS) AND ABOVE
    @TargetApi(14)
    public static MNCalendarEventList getCalendarEvents14(Context context, String calID) {
        // 오늘부터 1년 간의 이벤트를 얻기 - 나중에 현 시간부터 내일까지로 변경 필요
        // 오늘-종일, 오늘-일정 / 내일-종일, 내일-일정 총 4개의 ArrayList가 필요
        MNCalendarEventList calendarEventList = new MNCalendarEventList();

        // 필요한 시간 기준들을 미리 준비
        DateTime todayNowDateTime = DateTime.now();
        DateTime tomorrowDateTime = DateTime.now().plusDays(1);
        DateTime todayStartDateTime = new DateTime(todayNowDateTime.getYear(), todayNowDateTime.getMonthOfYear(),
                todayNowDateTime.getDayOfMonth(), 0, 0, 0);
        DateTime todayEndDateTime = new DateTime(tomorrowDateTime.getYear(), tomorrowDateTime.getMonthOfYear(),
                tomorrowDateTime.getDayOfMonth(), 0, 0, 0);

        DateTime tomorrowStartDateTime = new DateTime(tomorrowDateTime.getYear(),
                tomorrowDateTime.getMonthOfYear(), tomorrowDateTime.getDayOfMonth(), 0, 0, 0);
        DateTime tomorrowEndDateTime = tomorrowStartDateTime.plusDays(1);

        // 지금부터 내일 0시 0분 0초 미만의 시간(그 중에서도 all-day와 scheduled를 분리)
        Log.i(TAG, "today events");
        Log.i(TAG, "all-day events");
        calendarEventList.todayAlldayEvents = getEventsBetweenDates14(context, calID, true,
                todayStartDateTime, todayEndDateTime);
        Log.i(TAG, "scheduled events");
        calendarEventList.todayScheduledEvents = getEventsBetweenDates14(context, calID, false,
                todayNowDateTime, todayEndDateTime);
//        sortCalendarEventListByBeginTime(calendarEventList.todayScheduledEvents);

        // 내일 0시 0분 0초 이상 모레 0시 0분 0초 미만의 일정(그 중에서도 all-day와 scheduled를 분리)
        Log.i(TAG, "tomorrow events");
//        new DateTime(tomorrowDateTime.getYear(),
//                tomorrowDateTime.getMonthOfYear(), tomorrowDateTime.getDayOfMonth(), 23, 59, 59);
        Log.i(TAG, "all-day events");
        calendarEventList.tomorrowAlldayEvents = getEventsBetweenDates14(context, calID, true,
                tomorrowStartDateTime, tomorrowEndDateTime);
        Log.i(TAG, "scheduled events");
        calendarEventList.tomorrowScheduledEvents = getEventsBetweenDates14(context, calID, false,
                tomorrowStartDateTime, tomorrowEndDateTime);
//        sortCalendarEventListByBeginTime(calendarEventList.tomorrowScheduledEvents);

        return calendarEventList;
    }

    @TargetApi(14)
    private static ArrayList<MNCalendarEvent> getSortedEventsBetweenDates14(Context context, String calID,
                                                                            boolean isAllDay,
                                                                            DateTime startDateTime,
                                                                            DateTime endDateTime) {
        ContentResolver contentResolver = context.getContentResolver();

        Uri builder = CONTENT_URI;

        String[] COLS = new String[]{TITLE, DESCRIPTION, ALL_DAY, DTSTART, DTEND, RRULE, _ID};

        String selection = CALENDAR_ID + " = " + calID + " AND " + Events.DELETED + " = 0";

        if (!isAllDay) {
//            selection = "(" + selection + " AND " +
//                    startDateTime.getMillis() + " < " + DTSTART + " AND " +
//                    DTEND + " < " + endDateTime.getMillis() + ")";
            selection = "(" + selection + " AND " + ALL_DAY + " = 0)";
        } else {
            selection = "(" + selection + " AND " + ALL_DAY + " = 1)";
        }

        Cursor eventCursor = contentResolver.query(builder,
                COLS,
                selection, null, DTSTART + " ASC");

        if (eventCursor != null) {
            int n = eventCursor.getCount();

            System.out.println("Calendar id: " + calID + " / No. of rows is = " + n);
            ArrayList<MNCalendarEvent> calendarModelList = new ArrayList<MNCalendarEvent>();

            while (eventCursor.moveToNext()) {
                MNCalendarEvent calendarEvent = new MNCalendarEvent();

                // beginDate
                Date beginDate = new Date(eventCursor.getLong(3));
                calendarEvent.beginDate = beginDate;

                // endDate
                Date endDate = new Date(eventCursor.getLong(4));

                // 해당 시간 안에 존재 해야만 컨테이너에 추가
                // all-day 옵션이라면 같은 날짜,
                // scheduled 옵션이라면 해당 DateTime 안에 들어가는지가 중요
                boolean isEventShouldBeAdded = false;
                if (isAllDay) {
                    DateTime beginDateTime = new DateTime(beginDate);
                    if (startDateTime.getYear() == beginDateTime.getYear() &&
                            startDateTime.getMonthOfYear() == beginDateTime.getMonthOfYear() &&
                            startDateTime.getDayOfMonth() == beginDateTime.getDayOfMonth()) {
                        isEventShouldBeAdded = true;
                    }
                } else {
                    if (eventCursor.getString(5) == null) {
                        if (startDateTime.getMillis() <= beginDate.getTime() &&
                                endDate.getTime() <= endDateTime.getMillis()) {
                            isEventShouldBeAdded = true;
                        }
                    } else {
                        // 반복 일정일 경우에는 RRULE -> DateTime 리스트를 만들어
                        // 타겟 시간 안에 드는지 확인이 필요
                        int eventId = eventCursor.getInt(6);

                        final String[] INSTANCE_PROJECTION = new String[]{
                                CalendarContract.Instances.EVENT_ID,      // 0
                                CalendarContract.Instances.BEGIN,         // 1
                                CalendarContract.Instances.END,           // 2
                                CalendarContract.Instances.TITLE          // 3
                        };

                        Cursor cur = null;
                        ContentResolver cr = context.getContentResolver();

                        // The ID of the recurring event whose instances you are searching
                        // for in the Instances table
//                        String repeatSelection = "(" + CalendarContract.Instances.EVENT_ID + " = ?" + " AND " +
//                                startDateTime.getMillis() + " < " + CalendarContract.Instances.BEGIN + " AND " +
//                                CalendarContract.Instances.END + " < " + endDateTime.getMillis() + ")";

                        String repeatSelection = "(" + CalendarContract.Instances.EVENT_ID + " = ?" + ")";

                        String[] repeatSelectionArgs = new String[]{String.valueOf(eventId)};

                        // Construct the query with the desired date range.
                        Uri.Builder repeatBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
                        ContentUris.appendId(repeatBuilder, startDateTime.getMillis());
                        ContentUris.appendId(repeatBuilder, endDateTime.getMillis());

                        // Submit the query
                        cur = cr.query(repeatBuilder.build(),
                                INSTANCE_PROJECTION,
                                repeatSelection,
                                repeatSelectionArgs,
                                null);

                        if (cur != null) {
                            while (cur.moveToNext()) {

                                String title = null;
                                long eventID = 0;
                                long beginVal = 0;

                                // Get the field values
                                eventID = cur.getLong(0);
                                beginVal = cur.getLong(1);
                                title = cur.getString(3);

                                // Do something with the values.
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(beginVal);
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Log.i(TAG, "Event:" + title + "/Date:" + formatter.format(calendar.getTime()) +
                                        "/event_id: " + eventID);

                                MNCalendarEvent repeatCalendarEvent = new MNCalendarEvent();
                                repeatCalendarEvent.title = title;
                                repeatCalendarEvent.isAllDayEvent = false;
                                repeatCalendarEvent.beginDate = new Date(beginVal);
                                calendarModelList.add(repeatCalendarEvent);
                            }
                            cur.close();
                        }
                    }
                }

                // 추가해야 할 이벤트라면
                if (isEventShouldBeAdded) {
                    // all day
                    if (eventCursor.getInt(2) == 1) {
                        calendarEvent.isAllDayEvent = true;
                    } else {
                        calendarEvent.isAllDayEvent = false;
                    }

                    // title
                    String title = "";
                    if (eventCursor.getString(0) != null) {
                        title = eventCursor.getString(0).trim();
                        calendarEvent.title = title;
                    }

                    SimpleDateFormat sdfrr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String stimesr = sdfrr.format(beginDate);

                    System.out.println("title:" + title + "/stimesr:" + stimesr +
                            (calendarEvent.isAllDayEvent ? "/all-day" : "" + "/event_id: " + eventCursor.getInt(6)));

                    calendarModelList.add(calendarEvent);
                }
            }
            eventCursor.close();

            return calendarModelList;
        } else {
            Log.e(TAG, "eventCursor is null");

            return null;
        }
    }

    @TargetApi(14)
    private static ArrayList<MNCalendarEvent> getEventsBetweenDates14(Context context,
                                                                      ArrayList<MNCalendar> calendarModels,
                                                                      boolean isAllDayEvents,
                                                                      DateTime startDateTime,
                                                                      DateTime endDateTime) {
        // Instnaces 를 이용해서 받아보자(동기화된 이벤트 정보를 얻기 위해서)
        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.BEGIN,         // 0
                CalendarContract.Instances.TITLE,         // 1
        };

        Cursor eventCursor;
        ContentResolver contentResolver = context.getContentResolver();

        // The ID of the recurring event whose instances you are searching
        // for in the Instances table
        // 괄호가 중요, 괄호에 따라 제대로 된 값이 안나올 가능성이 있음
        // 아주 새로운 개념. 오늘의 일정을 표시하기 위해서, begin과 end가 필요한 것이 아니라,
        // begin만을 가지고 오늘 예정된 일정이라는 것을 표시 가능! iOS도 수정 필요!
        String selection = null;
        for (MNCalendar calendarModel : calendarModels) {
            // 선택된 캘린더일 경우에만 로딩해 전체 캘린더에 더하기
            if (calendarModel.selected) {
                if (selection == null) {
                    selection = (CALENDAR_ID + " = " + calendarModel.calendarId);
                } else {
                    selection += (" OR " + CALENDAR_ID + " = " + calendarModel.calendarId);
                }
            }
        }

        selection = "(" + selection + ") AND (" +
                startDateTime.getMillis() + " <= " + CalendarContract.Instances.BEGIN + " AND " +
                CalendarContract.Instances.BEGIN + " < " + endDateTime.getMillis() + ")";

        // 종일 이벤트일 경우 쿼리문을 더 추가
        if (isAllDayEvents) {
            selection += " AND " + CalendarContract.Instances.ALL_DAY + " = 1";
        } else {
            selection += " AND " + CalendarContract.Instances.ALL_DAY + " = 0";
        }

        // Construct the query with the desired date range.
        Uri.Builder builder = getInstancesURI().buildUpon();
        ContentUris.appendId(builder, startDateTime.getMillis());
        ContentUris.appendId(builder, endDateTime.getMillis());

        // Submit the query
        eventCursor = contentResolver.query(builder.build(),
                INSTANCE_PROJECTION,
                selection,
                null,
                CalendarContract.Instances.BEGIN + " ASC");

        if (eventCursor != null) {
            ArrayList<MNCalendarEvent> calendarModelList = new ArrayList<MNCalendarEvent>();

            while (eventCursor.moveToNext()) {

                MNCalendarEvent calendarEvent = new MNCalendarEvent();

                // Get the field values
                long beginTimeInMillis = eventCursor.getLong(0);
                String title = eventCursor.getString(1);

                // Do something with the values.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(beginTimeInMillis);
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Log.i(TAG, "Event:" + title + "/Date:" + formatter.format(calendar.getTime()));

                calendarEvent.title = title;
                calendarEvent.isAllDayEvent = isAllDayEvents;
                calendarEvent.beginDate = new Date(beginTimeInMillis);
                calendarModelList.add(calendarEvent);
            }
            eventCursor.close();
            return calendarModelList;
        } else {
            Log.e(TAG, "eventCursor is null");
            return null;
        }
    }

    @TargetApi(14)
    private static ArrayList<MNCalendarEvent> getEventsBetweenDates14(Context context, String calID,
                                                                      boolean isAllDayEvents,
                                                                      DateTime startDateTime,
                                                                      DateTime endDateTime) {
        // Instnaces 를 이용해서 받아보자(동기화된 이벤트 정보를 얻기 위해서)
        final String[] INSTANCE_PROJECTION = new String[]{
                CalendarContract.Instances.BEGIN,         // 0
                CalendarContract.Instances.TITLE,         // 1
        };

        Cursor eventCursor;
        ContentResolver contentResolver = context.getContentResolver();

        // The ID of the recurring event whose instances you are searching
        // for in the Instances table
        // 괄호가 중요, 괄호에 따라 제대로 된 값이 안나올 가능성이 있음
        // 아주 새로운 개념. 오늘의 일정을 표시하기 위해서, begin과 end가 필요한 것이 아니라,
        // begin만을 가지고 오늘 예정된 일정이라는 것을 표시 가능! iOS도 수정 필요!
        String selection = CALENDAR_ID + " = ? AND (" +
                startDateTime.getMillis() + " <= " + CalendarContract.Instances.BEGIN + " AND " +
                CalendarContract.Instances.BEGIN + " < " + endDateTime.getMillis() + ")";

        // 종일 이벤트일 경우 쿼리문을 더 추가
        if (isAllDayEvents) {
            selection += " AND " + CalendarContract.Instances.ALL_DAY + " = 1";
        } else {
            selection += " AND " + CalendarContract.Instances.ALL_DAY + " = 0";
        }

        // Construct the query with the desired date range.
        Uri.Builder builder = getInstancesURI().buildUpon();
        ContentUris.appendId(builder, startDateTime.getMillis());
        ContentUris.appendId(builder, endDateTime.getMillis());

        // Submit the query
        eventCursor = contentResolver.query(builder.build(),
                INSTANCE_PROJECTION,
                selection,
                new String[]{ calID },
                CalendarContract.Instances.BEGIN + " ASC");

        if (eventCursor != null) {
            ArrayList<MNCalendarEvent> calendarModelList = new ArrayList<MNCalendarEvent>();

            while (eventCursor.moveToNext()) {

                MNCalendarEvent calendarEvent = new MNCalendarEvent();

                // Get the field values
                long beginTimeInMillis = eventCursor.getLong(0);
                String title = eventCursor.getString(1);

                // Do something with the values.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(beginTimeInMillis);
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Log.i(TAG, "Event:" + title + "/Date:" + formatter.format(calendar.getTime()));

                calendarEvent.title = title;
                calendarEvent.isAllDayEvent = isAllDayEvents;
                calendarEvent.beginDate = new Date(beginTimeInMillis);
                calendarModelList.add(calendarEvent);
            }
            eventCursor.close();
            return calendarModelList;
        } else {
            Log.e(TAG, "eventCursor is null");
            return null;
        }
    }

    private static Uri getCalendarURI(boolean eventUri) {
        Uri calendarURI;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            calendarURI = (eventUri) ? Uri.parse("content://calendar/events") : Uri.parse("content://calendar/calendars");
        } else {
            calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/events") : Uri.parse("content://com.android.calendar/calendars");
        }
        return calendarURI;
    }

    private static Uri getInstancesURI() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return Uri.parse("content://com.android.calendar/instances/when");
        } else {
            return CalendarContract.Instances.CONTENT_URI;
        }
    }
}

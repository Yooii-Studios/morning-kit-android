package com.yooiistudios.morningkit.setting.theme.themedetail;

import com.yooiistudios.morningkit.R;

/**
 * Created by StevenKim in Morning Kit from Yooii Studios Co., LTD. on 2014. 1. 17.
 *
 * MNSettingResources
 *  세팅 화면에서 테마에 적합한 이미지 리소스를 반환
 */
public class MNSettingResources {
    private MNSettingResources() { throw new AssertionError("You MUST not create this class!"); }

    // 설정 화면은 모든 테마에 통일되게 제공하자. 하지만 나중에 예외가 생길 가능성도 있음
    public static int getNormalItemResourcesId(MNThemeType themeType) {
        return R.drawable.shape_rounded_view_pastel_green_normal;
    }

    public static int getItemSelectorResourcesId(MNThemeType themeType) {
        return R.drawable.shape_rounded_view_pastel_green;

//        switch (themeType) {
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case WATER_LILY:
//                return R.drawable.shape_rounded_view_translucent;
//
//            case MODERNITY_WHITE:
//                return R.drawable.shape_rounded_view_classic_white;
//
//            case SLATE_GRAY:
//                return R.drawable.shape_rounded_view_classic_gray;
//
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.shape_rounded_view_skyblue;
//
//            case PASTEL_GREEN:
//                return R.drawable.shape_rounded_view_pastel_green;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getLockItemResourcesId(MNThemeType themeType) {
        return R.drawable.shape_rounded_view_classic_white_unlocked;
    }

    public static int getCheckResourceId(MNThemeType themeType) {
        return R.drawable.setting_theme_check_classic_white;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.setting_theme_check_classic_white;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.setting_theme_check_classic_gray;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getLockResourceId(MNThemeType themeType) {
        return R.drawable.setting_theme_lock_classic_white;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.setting_theme_lock_classic_white;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.setting_theme_lock_classic_gray;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getPanelSelectPagerLockResourceId(MNThemeType themeType) {
        return R.drawable.panel_lock_icon_classic_white;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.panel_lock_icon_classic_white;
//
//            case SLATE_GRAY:
//                return R.drawable.panel_lock_icon_classic_gray;
//
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.panel_lock_icon_skyblue;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    /**
     * SettingPanlMatrixItem Resources
     */

    public static int getWeatherResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_weather_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_weather_black_ipad;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_weather_white_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getDateResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_calendar_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_calendar_black_ipad;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_calendar_white_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getCalendarResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_reminder_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_reminder_black_ipad;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_reminder_white_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getWorldClockResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_worldclock_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_worldclock_black_ipad;
//
//            case SLATE_GRAY:
//                return R.drawable.widget_cover_worldclock_white_ipad;
//
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_worldclock_skyblue_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getQuotesResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_quotes_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_quotes_black_ipad;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_quotes_white_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getFlickrResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_flickr_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_flickr_black_ipad;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_flickr_white_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getExchangeRatesResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_exchangerates_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_exchangerates_black_ipad;
//
//            case SLATE_GRAY:
//                return R.drawable.widget_cover_exchangerates_white_ipad;
//
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_exchangerates_skyblue_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getMemoResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_memo_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_memo_black_ipad;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_memo_white_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }

    public static int getDateCountdownResourceId(MNThemeType themeType) {
        return R.drawable.widget_cover_datecountdown_black_ipad;

//        switch (themeType) {
//            case WATER_LILY:
//            case TRANQUILITY_BACK_CAMERA:
//            case REFLECTION_FRONT_CAMERA:
//            case PHOTO:
//            case MODERNITY_WHITE:
//            case PASTEL_GREEN:
//                return R.drawable.widget_cover_datecountdown_black_ipad;
//
//            case SLATE_GRAY:
//            case CELESTIAL_SKY_BLUE:
//                return R.drawable.widget_cover_datecountdown_white_ipad;
//
//            default: throw new IndexOutOfBoundsException("Undefined Enumeration Index");
//        }
    }
}

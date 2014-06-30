package com.yooiistudios.morningkit.panel.exchangerates.model;

import android.content.Context;

import com.yooiistudios.morningkit.setting.theme.language.MNLanguage;

/**
 * Created by StevenKim in MorningKit from Yooii Studios Co., LTD. on 2014. 3. 6.
 *
 * MNDefaultExchangeRatesInfo
 *  현재 디바이스의 언어에 따라서 기본 환율의 조합을 생성
 */
public class MNDefaultExchangeRatesInfo {
    private MNDefaultExchangeRatesInfo() { throw new AssertionError("You MUST not create this class!"); }

    public static MNExchangeRatesInfo newInstance(Context context) {
        String baseCurrencyCode;
        String targetCurrencyCode;
        double baseCurrenyMoney;

        //한국어: 1000원 -> 달러
        //일본어: 100엔 -> 달러
        //간체: 10위안 -> 달러
        //번체: 100대만달러 -> 달러
        //영어: $1 -> 유로
        //러시아: 100루블 -> 달러

        switch (MNLanguage.getCurrentLanguageType(context)) {
            case KOREAN:
                baseCurrencyCode = "KRW";
                targetCurrencyCode = "USD";
                baseCurrenyMoney = 1000;
                break;

            case ENGLISH:
                baseCurrencyCode = "USD";
                targetCurrencyCode = "EUR";
                baseCurrenyMoney = 1;
                break;

            case JAPANESE:
                baseCurrencyCode = "JPY";
                targetCurrencyCode = "USD";
                baseCurrenyMoney = 100;
                break;

            case SIMPLIFIED_CHINESE:
                baseCurrencyCode = "CNY";
                targetCurrencyCode = "USD";
                baseCurrenyMoney = 10;
                break;

            case TRADITIONAL_CHINESE:
                baseCurrencyCode = "TWD";
                targetCurrencyCode = "USD";
                baseCurrenyMoney = 100;
                break;

            case RUSSIAN:
                baseCurrencyCode = "RUB";
                targetCurrencyCode = "USD";
                baseCurrenyMoney = 100;
                break;

            default:
                baseCurrencyCode = "USD";
                targetCurrencyCode = "EUR";
                baseCurrenyMoney = 1;
        }
        MNExchangeRatesInfo newExchangeRatesInfo = new MNExchangeRatesInfo(baseCurrencyCode, targetCurrencyCode);
        newExchangeRatesInfo.setBaseCurrencyMoney(baseCurrenyMoney);
        newExchangeRatesInfo.setExchangeRate(1);

        return newExchangeRatesInfo;
    }
}
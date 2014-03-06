package com.yooiistudios.morningkit.panel.exchangerates;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;

import com.yooiistudios.morningkit.R;
import com.yooiistudios.morningkit.panel.exchangerates.model.CurrencyInfoListAdapter;
import com.yooiistudios.morningkit.panel.exchangerates.model.MNCurrencyInfo;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by StevenKim in MorningKit from Yooii Studios Co., LTD. on 2014. 3. 4.
 *
 * MNExchangeRatesSelectDialog
 *  디테일 프래그먼트에서 국가를 선택할 때 사용하는 다이얼로그
 */
public class MNExchangeRatesSelectDialog extends Dialog {
    protected MNExchangeRatesSelectDialog(Context context) {
        super(context);
        init();
    }

    protected MNExchangeRatesSelectDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    protected MNExchangeRatesSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        // 커스텀 뷰를 커스터마이징
        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                        R.layout.panel_exchange_rates_select_dialog_view, null, false);

        TabHost tabHost = (TabHost) tabLinearLayout.findViewById(R.id.exchange_tabhost);
        tabHost.setup();
        TabHost.TabSpec spec;

        // tab 1
        Resources resources = getContext().getResources();
        spec = tabHost.newTabSpec(resources.getString(R.string.exchange_rate_main_currencies));
        spec.setContent(R.id.exchange_tab1);
        spec.setIndicator(resources.getString(R.string.exchange_rate_main_currencies));
        tabHost.addTab(spec);

        // tab2
        spec = tabHost.newTabSpec(resources.getString(R.string.exchange_rate_all_currencies));
        spec.setContent(R.id.exchange_tab2);
        spec.setIndicator(resources.getString(R.string.exchange_rate_all_currencies));
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

        MNCurrencyInfo[] frequentCurrencies = MNCurrencyInfo.frequentCurrency;

        // tab list main
        ArrayList<MNCurrencyInfo> mainCurrencies = new ArrayList<MNCurrencyInfo>();
        // 아래 한줄로 줄여질 수 있음
//        for (MNCurrencyInfo frequentCurrency : frequentCurrencies) {
//            mainCurrency.add(frequentCurrency);
//        }
        Collections.addAll(mainCurrencies, frequentCurrencies);

        CurrencyInfoListAdapter mainAdapter = new CurrencyInfoListAdapter(getContext(), mainCurrencies);
        ListView flagMainListView = (ListView) tabHost.findViewById(R.id.exchange_list_maincurrency);
        flagMainListView.setAdapter(mainAdapter);
//        flagMainListView.setOnItemClickListener(this); // 나중에 구현
        mainAdapter.notifyDataSetInvalidated();

        // tab list all
        MNCurrencyInfo.loadAllCurrency(getContext()); // 스태틱으로 만들어 놔서;; 그냥 여기서 읽어줌
        ArrayList<MNCurrencyInfo> allCurrencies = MNCurrencyInfo.allCurrency;

        CurrencyInfoListAdapter allAdapter = new CurrencyInfoListAdapter(getContext(), allCurrencies);
        ListView flagAllListView = (ListView) tabHost.findViewById(R.id.exchange_list_allcurrency);
        flagAllListView.setAdapter(allAdapter);
//        flagAllListView.setOnItemClickListener(this); // 나중에 구현
        allAdapter.notifyDataSetInvalidated();

        EditText searchFlagEditText = (EditText) tabHost.findViewById(R.id.exchange_edit_allcurrency);
        searchFlagEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        searchFlagEditText.setSingleLine(true);

//        edit_flag_all.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                String searchingString = s.toString().toLowerCase();
//
//                allCurrency = MNCurrencyInfo.allCurrency;
//                ArrayList<MNCurrencyInfo> newAllCurrency = new ArrayList<MNCurrencyInfo>();
//
//                MNCurrencyInfo currentCurrency = null;
//                int lastIndex = 0;
//                for(int i=0; i < allCurrency.size(); ++i)
//                {
//                    currentCurrency = allCurrency.get(i);
//
//                    if( currentCurrency.currencyName.toLowerCase().startsWith(searchingString) )
//                        newAllCurrency.add(lastIndex++, currentCurrency);
//
//                    else if( currentCurrency.currencyName.toLowerCase().contains(searchingString) )
//                        newAllCurrency.add(currentCurrency);
//
//                }
//
//                CurrencyInfoListAdapter allAdapter = new CurrencyInfoListAdapter(MNExchangeRateModalActivity.this, newAllCurrency);
//                listView_flag_all.setAdapter(allAdapter);
//                allAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {}
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });

//        edit_flag_all.setOnEditorActionListener(new OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if ((actionId == EditorInfo.IME_ACTION_DONE) ||
//                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    return true;
//                }
//                return false;
//            }
//        });
////		edit_flag_all.setOnEditorActionListener(MNExchangeRateModalActivity.this);
//
//        //
        Button cancelBtn = (Button) tabLinearLayout.findViewById(R.id.exchange_btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(tabLinearLayout);
    }
}

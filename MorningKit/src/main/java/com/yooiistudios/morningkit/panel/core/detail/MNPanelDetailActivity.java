package com.yooiistudios.morningkit.panel.core.detail;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.yooiistudios.morningkit.MNApplication;
import com.yooiistudios.morningkit.R;
import com.yooiistudios.morningkit.common.analytic.MNAnalyticsUtils;
import com.yooiistudios.morningkit.common.log.MNFlurry;
import com.yooiistudios.morningkit.common.size.MNViewSizeMeasure;
import com.yooiistudios.morningkit.panel.core.MNPanel;
import com.yooiistudios.morningkit.panel.core.MNPanelType;
import com.yooiistudios.morningkit.panel.core.selectpager.MNPanelSelectPagerInterface;
import com.yooiistudios.morningkit.panel.core.selectpager.MNPanelSelectPagerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MNPanelDetailActivity extends AppCompatActivity implements MNPanelSelectPagerInterface,
        MNPanelDetailFragment.MNPanelDetailFragmentListener {

    private static final String TAG = "PanelDetailActivity";

    @InjectView(R.id.panel_detail_container) RelativeLayout containerLayout;
    @InjectView(R.id.panel_detail_select_pager_layout) MNPanelSelectPagerLayout panelSelectPagerLayout;
    private MNPanelDetailFragment panelDetailFragment;
    private int panelWindowIndex;
    private boolean isPanelChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OS에 의해서 kill 당할 경우 복구하지 말고 메인 액티비티를 새로 띄워줌 - panelObject 와 관련된 오류 해결
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_panel_detail);
        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            // 인텐트에서 패널 데이터를 가져오기
            Intent intent = getIntent();
            panelWindowIndex = intent.getIntExtra(MNPanel.PANEL_WINDOW_INDEX, 0);
            JSONObject panelDataObject;
            try {
                panelDataObject = new JSONObject(intent.getStringExtra(MNPanel.PANEL_DATA_OBJECT));
                MNPanelType panelType = MNPanelType.valueOfUniqueId(panelDataObject.getInt(MNPanel.PANEL_UNIQUE_ID));

                initActionBar(panelType);

                // 패널 타입을 확인해 프래그먼트 생성
                panelDetailFragment = MNPanelDetailFragment.newInstance(panelType, panelWindowIndex, this);

                // 데이터를 프래그먼트에 넣어주기
                panelDetailFragment.setPanelDataObject(panelDataObject);

                // 패널셀렉트페이저 로딩 및 색 설정
                panelSelectPagerLayout.loadPanelSelectPager(getSupportFragmentManager(), this);
                initPanelSelectPagerColor();

                // 프래그먼트 시작
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.panel_detail_fragment_container, panelDetailFragment)
                        .commit();

                MNAnalyticsUtils.startAnalytics((MNApplication) getApplication(), TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initPanelSelectPagerColor() {
        int panelUniqueId = -1;
        try {
            panelUniqueId = panelDetailFragment.getPanelDataObject().getInt(MNPanel.PANEL_UNIQUE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        if (panelUniqueId != -1) {
            final int currentPanelTypeIndex = MNPanelType.valueOfUniqueId(panelUniqueId).getIndex();
            MNViewSizeMeasure.setViewSizeObserver(panelSelectPagerLayout, new MNViewSizeMeasure.OnGlobalLayoutObserver() {
                @Override
                public void onLayoutLoad() {
                    panelSelectPagerLayout.applyTheme();
                    panelSelectPagerLayout.setColorOfPanelSelectPager(currentPanelTypeIndex, -1);
                }
            });
        }
    }

    private void initActionBarTitle(MNPanelType panelType) {
        String panelTypeName = MNPanelType.toString(panelType.getIndex(), this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getActionBar() != null) {
                getActionBar().setTitle(panelTypeName);
            }
        }else{
            getSupportActionBar().setTitle(panelTypeName); // 추후 구현 다시 하자
        }
    }

    private void initActionBar(MNPanelType panelType) {
        initActionBarTitle(panelType);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getActionBar() != null) {
                getActionBar().setDisplayShowHomeEnabled(false); // 추후 구현 다시 하자
            }
        }else{
            getSupportActionBar().setDisplayShowHomeEnabled(false); // 추후 구현 다시 하자
        }
    }

    /**
     * Action Bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.pref_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.pref_action_ok:
                onActionBarDoneButtonClicked();
                return true;

            case R.id.pref_action_cancel:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActionBarDoneButtonClicked() {
        // 변경사항 저장
        try {
            panelDetailFragment.archivePanelData();
            MNPanel.archivePanelData(this, panelDetailFragment.getPanelDataObject(), panelWindowIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 메인 액티비티에 리프레시 요청
        Intent intent = new Intent();
        intent.putExtra(MNPanel.PANEL_DATA_OBJECT,
                panelDetailFragment.getPanelDataObject().toString());
        setResult(RESULT_OK, intent);

        // 패널이 변경되었으면 교체 요청하기
        if (isPanelChanged) {
            intent.putExtra(MNPanel.PANEL_CHANGED, true);
        }
        finish();
    }

    /**
     * Panel Select Pager
     */
    @Override
    public void onPanelSelectPagerItemClick(int position) {

        // 선택한 패널 타입 체크
        int panelUniqueId = -1;
        try {
            if (panelDetailFragment.getPanelDataObject().has(MNPanel.PANEL_UNIQUE_ID)) {
                panelUniqueId = panelDetailFragment.getPanelDataObject().getInt(MNPanel.PANEL_UNIQUE_ID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (panelUniqueId != -1) {
            int previousPanelTypeIndex = MNPanelType.valueOfUniqueId(panelUniqueId).getIndex();

            if (previousPanelTypeIndex != position) {
                isPanelChanged = true;

                // title
                initActionBarTitle(MNPanelType.valueOf(position));

                // panelSelectPager forward 색상 변경
                panelSelectPagerLayout.setColorOfPanelSelectPager(position, previousPanelTypeIndex);

                // fragment
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                panelDetailFragment = MNPanelDetailFragment.newInstance(MNPanelType.valueOf(position),
                        panelWindowIndex, this);
                transaction.replace(R.id.panel_detail_fragment_container, panelDetailFragment);
                transaction.commit();
            }
        }
    }

    @Override
    public void onPanelSelectPagerUnlockItemClick(int position) {
    }

    @Override
    public void onPanelSelectPagerStoreItemClick(int position) {
    }

    @Override
    protected void onStart() {
        // Activity visible to user
        super.onStart();
        FlurryAgent.onStartSession(this, MNFlurry.KEY);
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // Activity no longer visible
        super.onStop();
        FlurryAgent.onEndSession(this);
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(containerLayout, R.string.permission_granted, Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(containerLayout, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
        }
    }
}

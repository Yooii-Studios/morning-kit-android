package com.yooiistudios.morningkit.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;
import com.yooiistudios.morningkit.common.log.MNFlurry;
import com.yooiistudios.morningkit.common.size.MNViewSizeMeasure;
import com.yooiistudios.morningkit.panel.core.MNPanel;
import com.yooiistudios.morningkit.panel.core.MNPanelLayout;
import com.yooiistudios.morningkit.panel.core.MNPanelLayoutFactory;
import com.yooiistudios.morningkit.panel.core.MNPanelType;
import com.yooiistudios.morningkit.panel.weather.MNWeatherPanelLayout;
import com.yooiistudios.morningkit.setting.theme.panelmatrix.MNPanelMatrix;
import com.yooiistudios.morningkit.setting.theme.panelmatrix.MNPanelMatrixType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * Created by yongbinbae on 13. 10. 22..
 * MNWidgetWindowLayout
 */
public class MNPanelWindowLayout extends LinearLayout {
//    private static final String TAG = "MNWidgetWindowLayout";

    // 현재 지원하는 최대의 매트릭스 row과 갯수
    private static final int PANEL_ROWS = 3;
    private static final int NUMBER_OF_PANELS = 6;

    @Getter private LinearLayout panelLineLayouts[];
    @Getter private MNPanelLayout panelLayouts[];

    private MNPanelMatrixType previousPanelMatrixType;

    public MNPanelWindowLayout(Context context) {
        super(context);
    }

    public MNPanelWindowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initWithPanelMatrix() {
        this.setOrientation(VERTICAL);

        panelLineLayouts = new LinearLayout[PANEL_ROWS];
        panelLayouts = new MNPanelLayout[NUMBER_OF_PANELS];
        MNPanelMatrixType panelMatrixType = MNPanelMatrix.getCurrentPanelMatrixType(getContext());

        // 패널 데이터 리스트 로드
        List<JSONObject> panelDataObjects = MNPanel.getPanelDataList(getContext());

        // 패널들이 있는 레이아웃을 추가
        for (int i = 0; i < PANEL_ROWS; i++) {
            panelLineLayouts[i] = new LinearLayout(getContext());
            panelLineLayouts[i].setOrientation(HORIZONTAL);
            panelLineLayouts[i].setWeightSum(2);

            LayoutParams layoutParams =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT, 1);
            panelLineLayouts[i].setLayoutParams(layoutParams);
            this.addView(panelLineLayouts[i]);

            // 각 패널 레이아웃을 추가
            for (int j = 0; j < 2; j++) {
                int uniquePanelId = -1;
                try {
                    uniquePanelId = panelDataObjects.get(i * 2 + j).getInt(MNPanel.PANEL_UNIQUE_ID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MNPanelType panelType = MNPanelType.valueOfUniqueId(uniquePanelId);

                // 패널 id에 맞게 패널 레이아웃 생성
                int index = i * 2 + j;
                panelLayouts[index] = MNPanelLayoutFactory.newPanelLayoutInstance(panelType, index, getContext());
                panelLineLayouts[i].addView(panelLayouts[index]);

                // 로딩 애니메이션이 onCreate시에는 제대로 생성이 안되기 때문에 뷰 로딩 이후에 리프레시
                final MNPanelLayout panelLayout = panelLayouts[index];

                // 눈에 보이는 패널들만 리프레시를 하게 구현
                if (panelMatrixType == MNPanelMatrixType.PANEL_MATRIX_2X3 ||
                        panelMatrixType == MNPanelMatrixType.PANEL_MATRIX_2X2 && i < 2) {
                    MNViewSizeMeasure.setViewSizeObserver(panelLayout, new MNViewSizeMeasure.OnGlobalLayoutObserver() {
                        @Override
                        public void onLayoutLoad() {
                            try {
                                panelLayout.refreshPanel();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

        if (panelDataObjects != null) {
            try {
                if (panelMatrixType == MNPanelMatrixType.PANEL_MATRIX_2X3) {
                    for (JSONObject panelObject : panelDataObjects) {
                        int uniquePanelId = panelObject.getInt(MNPanel.PANEL_UNIQUE_ID);
                        MNPanelType panelType = MNPanelType.valueOfUniqueId(uniquePanelId);

                        // 플러리
                        Map<String, String> params = new HashMap<>();
                        params.put(MNFlurry.PANEL_USAGE, panelType.toString());
                        FlurryAgent.logEvent(MNFlurry.ON_LAUNCH, params);
                    }
                } else {
                    for (int i = 0; i < 4; i++) {
                        JSONObject panelObject = panelDataObjects.get(i);
                        int uniquePanelId = panelObject.getInt(MNPanel.PANEL_UNIQUE_ID);
                        MNPanelType panelType = MNPanelType.valueOfUniqueId(uniquePanelId);

                        // 플러리
                        Map<String, String> params = new HashMap<>();
                        params.put(MNFlurry.PANEL_USAGE, panelType.toString());
                        FlurryAgent.logEvent(MNFlurry.ON_LAUNCH, params);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void applyTheme() {
        // 2X2 사용 중 2X3로 변경하였을 때 리프레시를 한번씩 해주자
        MNPanelMatrixType currentPanelMatrixType = MNPanelMatrix.getCurrentPanelMatrixType(getContext());
        if (previousPanelMatrixType != currentPanelMatrixType &&
                previousPanelMatrixType == MNPanelMatrixType.PANEL_MATRIX_2X2) {
            try {
                panelLayouts[4].refreshPanel();
                panelLayouts[5].refreshPanel();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        previousPanelMatrixType = currentPanelMatrixType;
        
        for (MNPanelLayout panelLayout : panelLayouts) {
            panelLayout.applyTheme();
        }
    }
    public void onActivityPause() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            panelLayout.onActivityPause();
        }
    }
    public void onActivityResume() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            panelLayout.onActivityResume();
        }
    }

    // 방향과 무관
    public void refreshAllPanels() {
        int i = 0;
        MNPanelMatrixType panelMatrixType = MNPanelMatrix.getCurrentPanelMatrixType(getContext());
        for (MNPanelLayout panelLayout : panelLayouts) {
            // 2X3일때는 모든 패널 리프레시, 2X2일 때는 0123 패널만 리프레시
            if (panelMatrixType == MNPanelMatrixType.PANEL_MATRIX_2X3 ||
                    (panelMatrixType == MNPanelMatrixType.PANEL_MATRIX_2X2 && i < 4)) {
                try {
                    panelLayout.refreshPanel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }

    // 패널 디테일 프래그먼트의 셀렉트 페이저에서 패널을 변경해서 메인으로 나올 때의 콜백 메서드
    public void replacePanel(Intent data) {
        JSONObject panelDataObject;
        try {
            panelDataObject = new JSONObject(data.getStringExtra(MNPanel.PANEL_DATA_OBJECT));
            if (panelDataObject != null) {
                int index = panelDataObject.getInt(MNPanel.PANEL_WINDOW_INDEX);
                int uniqueId = panelDataObject.getInt(MNPanel.PANEL_UNIQUE_ID);
                if (index >= 0 && index < panelLayouts.length) {
                    // 기존의 위치에 새 패널을 대입
                    panelLayouts[index] = MNPanelLayoutFactory.newPanelLayoutInstance(
                            MNPanelType.valueOfUniqueId(uniqueId), index, getContext());
                    panelLayouts[index].refreshPanel();

                    // 기존 위치에 새 패널을 대입
                    if (MNPanelMatrix.getCurrentPanelMatrixType(getContext()) ==
                            MNPanelMatrixType.PANEL_MATRIX_2X2) {
                        // 2X2는 크게 영향이 없으므로 기존 코드 사용(배열 변경 X)
                        panelLineLayouts[index / 2].removeViewAt(index % 2);
                        panelLineLayouts[index / 2].addView(panelLayouts[index], index % 2);
                    } else {
                        // 방향을 알아내어 그에 따라 배열을 변경
                        switch (getResources().getConfiguration().orientation) {
                            // 2X3
                            case Configuration.ORIENTATION_PORTRAIT:
                                // 회전 중 죽을 가능성이 있기에 null 체크 삽입
                                if (panelLineLayouts[index / 2].getChildAt(index % 2) != null) {
                                    panelLineLayouts[index / 2].removeViewAt(index % 2);
                                    panelLineLayouts[index / 2].addView(panelLayouts[index], index % 2);
                                }
                                break;

                            // 3X2
                            case Configuration.ORIENTATION_LANDSCAPE:
                                if (panelLineLayouts[index / 3].getChildAt(index % 3) != null) {
                                    panelLineLayouts[index / 3].removeViewAt(index % 3);
                                    panelLineLayouts[index / 3].addView(panelLayouts[index], index % 3);
                                }
                                break;
                        }
                    }
                } else {
                    throw new AssertionError("index must be > 0 and <= panelLayouts.length");
                }
                // 플러리 - 패널 디테일 액티비티에서 패널 변경
                Map<String, String> params = new HashMap<>();
                params.put(MNFlurry.CHANGE_PANEL_FROM, "Panel Detail Activity");
                FlurryAgent.logEvent(MNFlurry.PANEL, params);
            } else {
                throw new AssertionError("panelDataObject must not be null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refreshPanel(Intent data) {
        // data에서 index 추출, 회전과 무관
        JSONObject panelDataObject;
        try {
            panelDataObject = new JSONObject(data.getStringExtra(MNPanel.PANEL_DATA_OBJECT));
            if (panelDataObject != null) {
                int index = panelDataObject.getInt(MNPanel.PANEL_WINDOW_INDEX);
                if (index >= 0 && index < panelLayouts.length) {
                    // 새 패널데이터 삽입 및 패널 갱신
                    panelLayouts[index].setPanelDataObject(panelDataObject);
                    panelLayouts[index].refreshPanel();
                } else {
                    throw new AssertionError("index must be > 0 and <= panelLayouts.length");
                }
            } else {
                throw new AssertionError("panelDataObject must not be null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 방향에 따라 배열, 높이 변경
     */
    public void adjustPanelLayoutMatrixAtOrientation(final int orientation) {
        if (MNPanelMatrix.getCurrentPanelMatrixType(getContext()) ==
                MNPanelMatrixType.PANEL_MATRIX_2X3) {
            switch (orientation) {
                case Configuration.ORIENTATION_PORTRAIT:        // 2X3이 되게 새로 정렬
                    // 1 2
                    // 3 4
                    // 5 6
                    // 가로모드에서 세로모드로 전환할 때에는 3X2 -> 2X3
                    for (LinearLayout panelLineLayout : panelLineLayouts) {
                        panelLineLayout.removeAllViews();       // 모든 뷰를 먼저 삭제
                    }
                    panelLineLayouts[0].setWeightSum(2);        // 웨이트 정렬
                    panelLineLayouts[1].setWeightSum(2);
                    panelLineLayouts[2].setVisibility(View.VISIBLE);
                    for (int i = 0; i < panelLayouts.length; i++) {
                        panelLineLayouts[i / 2].addView(panelLayouts[i], i % 2);
                    }
                    break;

                case Configuration.ORIENTATION_LANDSCAPE:       // 3X2가 되게 새로 정렬
                    // 1 2 3
                    // 4 5 6
                    for (LinearLayout panelLineLayout : panelLineLayouts) {
                        panelLineLayout.removeAllViews();       // 모든 뷰를 먼저 삭제
                    }
                    panelLineLayouts[0].setWeightSum(3);        // 웨이트 정렬
                    panelLineLayouts[1].setWeightSum(3);
                    panelLineLayouts[2].setVisibility(View.GONE);
                    for (int i = 0; i < panelLayouts.length; i++) {
                        panelLineLayouts[i / 3].addView(panelLayouts[i], i % 3);
                    }
                    break;
            }
        } else if (MNPanelMatrix.getCurrentPanelMatrixType(getContext()) ==
                MNPanelMatrixType.PANEL_MATRIX_2X2) {
            // 1 2
            // 3 4
            if (panelLineLayouts.length == 3) {
                for (LinearLayout panelLineLayout : panelLineLayouts) {
                    panelLineLayout.removeAllViews();           // 모든 뷰를 먼저 삭제
                }
                panelLineLayouts[0].setWeightSum(2);            // 웨이트 정렬
                panelLineLayouts[1].setWeightSum(2);
                panelLineLayouts[2].setVisibility(View.GONE);
                for (int i = 0; i < panelLayouts.length; i++) {
                    panelLineLayouts[i / 2].addView(panelLayouts[i], i % 2);
                }
            }
        }
    }

    /**
     * 세팅에서 메인으로 나올 때 새 패널 데이터 리스트와 기존 리스트의 uniqueId를 비교해 교체되었으면 UI를 갱신
     */
    public void checkPanelHadReplacedAtSetting() {
        // 새 uniqueId 리스트를 구하기
        List<JSONObject> panelDataObjects = MNPanel.getPanelDataList(getContext());

        // 기존 uniqueId 리스트를 구하기
        for (int i = 0; i < panelLayouts.length; i++) {
            MNPanelLayout panelLayout = panelLayouts[i];
            MNPanelType previousPanelType = panelLayout.getPanelType();
            try {
                JSONObject newPanelDataObject = panelDataObjects.get(i);
                int uniqueId = newPanelDataObject.getInt(MNPanel.PANEL_UNIQUE_ID);
                MNPanelType newPanelType = MNPanelType.valueOfUniqueId(uniqueId);

                // 일치하지 않다면 교체
                if (previousPanelType != newPanelType) {
                    // 기존 replacePanel 메서드를 활용해 해결
                    Intent panelDataIntent = new Intent();
                    panelDataIntent.putExtra(MNPanel.PANEL_DATA_OBJECT, newPanelDataObject.toString());
                    replacePanel(panelDataIntent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 날씨 패널 LocationModule 관련
     */
    public boolean isThereAnyWeatherPanelsUsingCurrentLocation() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            MNPanelType panelType = panelLayout.getPanelType();
            if (panelType == MNPanelType.WEATHER) {
                if (((MNWeatherPanelLayout) panelLayout).isUsingCurrentLocation()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void refreshWeatherPanelsIfExistAndUseCurrentLocation() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            MNPanelType panelType = panelLayout.getPanelType();
            if (panelType == MNPanelType.WEATHER &&
                    ((MNWeatherPanelLayout) panelLayout).isUsingCurrentLocation()) {
                try {
                    panelLayout.refreshPanel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void changeAndRefreshWeatherPanelsNotToUseCurrentLocation() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            MNPanelType panelType = panelLayout.getPanelType();
            if (panelType == MNPanelType.WEATHER && panelLayout.getPanelDataObject() != null) {
                try {
                    panelLayout.getPanelDataObject().put(
                            MNWeatherPanelLayout.WEATHER_DATA_IS_USING_CURRENT_LOCATION, false);
                    panelLayout.refreshPanel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 캘린더 권한 관련
     */
    public boolean isThereAnyCalendarPanel() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            MNPanelType panelType = panelLayout.getPanelType();
            if (panelType == MNPanelType.CALENDAR) {
                return true;
            }
        }
        return false;
    }

    public void refreshCalendarPanels() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            MNPanelType panelType = panelLayout.getPanelType();
            if (panelType == MNPanelType.CALENDAR) {
                try {
                    panelLayout.refreshPanel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 외부 저장소 관련
     */
    public boolean isThereAnyPanelUsingPhoto() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            MNPanelType panelType = panelLayout.getPanelType();
            if (panelType == MNPanelType.FLICKR || panelType == MNPanelType.PHOTO_FRAME) {
                return true;
            }
        }
        return false;
    }

    public void refreshPhotoFramePanels() {
        for (MNPanelLayout panelLayout : panelLayouts) {
            MNPanelType panelType = panelLayout.getPanelType();
            if (panelType == MNPanelType.PHOTO_FRAME) {
                try {
                    panelLayout.refreshPanel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.yooiistudios.morningkit.panel.flickr.detail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yooiistudios.morningkit.R;
import com.yooiistudios.morningkit.common.bitmap.MNBitmapProcessor;
import com.yooiistudios.morningkit.common.bitmap.MNBitmapUtils;
import com.yooiistudios.morningkit.common.file.ExternalStorageManager;
import com.yooiistudios.morningkit.common.log.MNLog;
import com.yooiistudios.morningkit.panel.core.MNPanel;
import com.yooiistudios.morningkit.panel.core.detail.MNPanelDetailFragment;

import org.json.JSONException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import static com.yooiistudios.morningkit.panel.flickr.MNFlickrPanelLayout.FLICKR_DATA_GRAYSCALE;
import static com.yooiistudios.morningkit.panel.flickr.MNFlickrPanelLayout.FLICKR_DATA_KEYWORD;
import static com.yooiistudios.morningkit.panel.flickr.MNFlickrPanelLayout.FLICKR_DATA_PHOTO_ID;
import static com.yooiistudios.morningkit.panel.flickr.MNFlickrPanelLayout.FLICKR_PREFS;
import static com.yooiistudios.morningkit.panel.flickr.MNFlickrPanelLayout.FLICKR_PREFS_KEYWORD;

/**
 * Created by StevenKim in MorningKit from Yooii Studios Co., LTD. on 2014. 2. 21.
 *
 * MNFlickrDetailFragment
 */
public class MNFlickrDetailFragment extends MNPanelDetailFragment implements TextView.OnEditorActionListener {

    private static final String TAG = "MNFlickrDetailFragment";

    @InjectView(R.id.panel_detail_flickr_imageview) ImageView imageView;
    @InjectView(R.id.panel_detail_flickr_edit_text) EditText keywordEditText;
    @InjectView(R.id.panel_detail_flickr_grayscale_textview) TextView grayScaleTextView;
    @Optional @InjectView(R.id.panel_detail_flickr_grayscale_image_button) ImageButton grayscaleImageButton; // < V14
//    Switch grayscaleSwitch; // >= V14

    private boolean isGrayScale;
    private MNFlickrBitmapSaveAsyncTask bitmapSaveAsyncTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.panel_flickr_detail_fragment, container, false);
        if (rootView != null && savedInstanceState == null) {
            ButterKnife.inject(this, rootView);

            // 버전 체크해 스위치 얻어내기
            isGrayScale = false;
            if (getPanelDataObject().has(FLICKR_DATA_GRAYSCALE)) {
                try {
                    isGrayScale = getPanelDataObject().getBoolean(FLICKR_DATA_GRAYSCALE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                grayscaleSwitch = (Switch) rootView.findViewById(R.id.flickr_detail_grayscale_switch);
//                grayscaleSwitch.setChecked(isGrayScale);
//            } else {
//                grayscaleCheckbox.setChecked(isGrayScale);
//            }
            if (isGrayScale) {
                grayscaleImageButton.setImageResource(R.drawable.icon_panel_detail_checkbox_on);
            } else {
                grayscaleImageButton.setImageResource(R.drawable.icon_panel_detail_checkbox);
            }
            grayscaleImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isGrayScale = !isGrayScale;
                    if (isGrayScale) {
                        grayscaleImageButton.setImageResource(R.drawable.icon_panel_detail_checkbox_on);
                    } else {
                        grayscaleImageButton.setImageResource(R.drawable.icon_panel_detail_checkbox);
                    }
                }
            });

            // 비트맵 로컬에서 읽어오기
            try {
                String bitmapName = "flickr_" + getPanelDataObject().getInt(MNPanel.PANEL_WINDOW_INDEX);
                String directory = ExternalStorageManager.APP_DIRECTORY_HIDDEN + "/flickr";
                Bitmap bitmap = MNBitmapProcessor.loadBitmapFromDirectory(getActivity(),
                        "flickr_" + getPanelDataObject().getInt(MNPanel.PANEL_WINDOW_INDEX),
                        ExternalStorageManager.APP_DIRECTORY_HIDDEN + "/flickr");

                if (bitmap != null) {
                    // 읽은 뒤에는 바로 파일 삭제 - 패널 교체를 대비
                    ExternalStorageManager.deleteFileFromExternalDirectory(getActivity(),
                            bitmapName + ".jpg", directory);

                    // 비트맵 세팅
                    imageView.setImageDrawable(new BitmapDrawable(
                            getActivity().getApplicationContext().getResources(), bitmap));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    setImgViewOnClickListener();
                } else {
                    imageView.setVisibility(View.GONE); // 사진이 없을 때는 공간을 비움
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 키워드 텍스트
            String keywordString = null;
            if (getPanelDataObject().has(FLICKR_DATA_KEYWORD)) {
                try {
                    keywordString = getPanelDataObject().getString(FLICKR_DATA_KEYWORD);
                } catch (JSONException e) {
                    e.printStackTrace();
                    keywordString = "Morning";
                }
            } else {
                SharedPreferences prefs = getActivity().getSharedPreferences(FLICKR_PREFS, Context.MODE_PRIVATE);
                keywordString = prefs.getString(FLICKR_PREFS_KEYWORD, "Morning");
            }
            keywordEditText.setText(keywordString);
            keywordEditText.setSelection(keywordString.length());
            keywordEditText.setOnEditorActionListener(this);
            keywordEditText.setPrivateImeOptions("defaultInputmode=english;");
//            keywordEditText.requestFocus();

            // 그레이스케일 텍스트뷰
            grayScaleTextView.setText(R.string.flickr_use_gray_scale);

        }
        return rootView;
    }

    @Override
    protected void archivePanelData() throws JSONException {
        // grayscale
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//            getPanelDataObject().put(FLICKR_DATA_GRAYSCALE, grayscaleSwitch.isChecked());
//        } else {
//            getPanelDataObject().put(FLICKR_DATA_GRAYSCALE, grayscaleCheckbox.isChecked());
//        }
        getPanelDataObject().put(FLICKR_DATA_GRAYSCALE, isGrayScale);

        // 키워드 길이가 0 이상일 경우에만 적용
        if (keywordEditText.getText().length() > 0) {
            getPanelDataObject().put(FLICKR_DATA_KEYWORD, keywordEditText.getText().toString());

            // SharedPreferences에 키워드 아카이빙
            SharedPreferences prefs = getActivity().getSharedPreferences(FLICKR_PREFS, Context.MODE_PRIVATE);
            prefs.edit().putString(FLICKR_PREFS_KEYWORD, keywordEditText.getText().toString()).apply();
        }
    }

    private void setImgViewOnClickListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPanelDataObject().has(FLICKR_DATA_PHOTO_ID)) {
                    // AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(getResources().getString(R.string.flickr_save_to_library_guide))
                            .setPositiveButton(getResources().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                saveImageToLibrary(getPanelDataObject().getString(FLICKR_DATA_PHOTO_ID));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                            .setNegativeButton(getResources().getString(R.string.cancel), null);

                    // 중앙정렬
                    AlertDialog dialog = builder.show();
                    TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
                    if (messageText != null) {
                        messageText.setGravity(Gravity.CENTER);
                    }
                    dialog.show();
                }
            }
        });
    }

    private void saveImageToLibrary(String imageId) {
        if (bitmapSaveAsyncTask != null) {
            bitmapSaveAsyncTask.cancel(true);
            bitmapSaveAsyncTask = null;
        }
        bitmapSaveAsyncTask = new MNFlickrBitmapSaveAsyncTask(imageId, isGrayScale, getActivity(),
                getActivity().getApplicationContext());
        bitmapSaveAsyncTask.execute();
//        new MNFlickrPhotoSavingThread(getActivity().getApplicationContext, imageId, isGrayScale).start();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if ((actionId == EditorInfo.IME_ACTION_DONE) ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

			/*
			// 1. 변경 전 -> 엔터키 입력하면 키보드 내려가게
			InputMethodManager inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(edit_query.getWindowToken(), 0);   //mPwd는 EditText의 변수 - 내리기
//			inputManager.showSoftInput(edit_query, 0); //올리기 단, mPwd에 Focus 가야 됨. ( mPwd.requestFocus(); )
			*/

            // 2. 변경 후 -> confirm 과 동일한 효과
            onActionBarDoneClicked();

            // Flurry Insertion
//            Map<String, String> flickrParams = new HashMap<String, String>();
//            flickrParams.put("Keyword", edit_query.getText().toString());
//            FlurryAgent.logEvent("Widget - Flickr - Keyword", flickrParams);

        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MNBitmapUtils.recycleImageView(imageView)) {
            MNLog.i(TAG, "recycleImageView");
        }
    }

    @OnClick(R.id.panel_detail_flickr_removeAllButton)
    public void onRemoveAllButtonClicked() {
        keywordEditText.setText("");

        // 전체 삭제하며 키보드 보여줌
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(keywordEditText, InputMethodManager.SHOW_IMPLICIT);      // 보여줄때
//        mgr.hideSoftInputFromWindow(search_key.getWindowToken(), 0);        // 숨길때
    }
}

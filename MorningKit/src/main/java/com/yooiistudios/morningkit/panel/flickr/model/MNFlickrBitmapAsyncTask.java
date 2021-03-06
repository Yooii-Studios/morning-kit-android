package com.yooiistudios.morningkit.panel.flickr.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.yooiistudios.morningkit.R;
import com.yooiistudios.morningkit.common.bitmap.MNBitmapProcessor;

/**
 * Created by StevenKim in MorningKit from Yooii Studios Co., LTD. on 2014. 2. 18.
 *
 * MNFlickrBitmapAsyncTask
 *  원본 플리커 비트맵을 다양하게 처리
 */
public class MNFlickrBitmapAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = "MNFlickrBitmapAsyncTask";
    private Bitmap originalBitmap;
    private int width;
    private int height;
    boolean isGrayScale;
    private OnFlickrBitmapAsyncTaskListener flickrBitmapAsyncTaskListener;
    private Context context;

    public interface OnFlickrBitmapAsyncTaskListener {
        public void onBitmapProcessingLoad(Bitmap polishedBitmap);
    }

    public MNFlickrBitmapAsyncTask(Bitmap bitmap, int width, int height, boolean isGrayScale,
                                   OnFlickrBitmapAsyncTaskListener flickrBitmapAsyncTaskListener,
                                   Context context) {
        this.originalBitmap = bitmap;
        this.width = width;
        this.height = height;
        this.isGrayScale = isGrayScale;
        this.flickrBitmapAsyncTaskListener = flickrBitmapAsyncTaskListener;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        if (width == 0 || height == 0) {
            // 뷰가 로딩되지 않았거나 문제가 있을 경우 null 반환
            return null;
        } else {
            // 크롭, 라운딩, 그레이스케일 등등 처리하기
            Bitmap croppedBitmap = MNBitmapProcessor.getCroppedBitmap(originalBitmap, width, height);
            Bitmap polishedBitmap;
            try {
                polishedBitmap = MNBitmapProcessor.getRoundedCornerBitmap(croppedBitmap, width, height, isGrayScale,
                        (int) context.getResources().getDimension(R.dimen.panel_round_radius));
            } catch (OutOfMemoryError error) {
                polishedBitmap = null;
            }
            if (croppedBitmap != null) {
                croppedBitmap.recycle();
            }
            return polishedBitmap;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        flickrBitmapAsyncTaskListener.onBitmapProcessingLoad(bitmap);
    }
}

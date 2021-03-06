package com.yooiistudios.morningkit.panel.exchangerates.model;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yooiistudios.morningkit.R;
import com.yooiistudios.morningkit.common.bitmap.MNBitmapUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FlagBitmapFactory {

	static Map<String, Bitmap> flagMap = new HashMap<String, Bitmap>();
	static Map<String, Bitmap> grayscaledFlagMap = new HashMap<String, Bitmap>();

	static public Bitmap getFlagBitamp(Context _context, String _countryCode) {
		String fileName = "flag_"+_countryCode.toLowerCase();
		int id = _context.getResources().getIdentifier(fileName,  "raw", _context.getPackageName());

		if( flagMap.get(_countryCode) == null ) {
			Bitmap bitmap = null;

			try {
				InputStream io = _context.getResources().openRawResource(id);
				byte raw[];
				raw = new byte[io.available()];
				io.read(raw);
				bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length);
			} catch (IOException ignored) {} catch( NotFoundException ignored) {}

            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.flag_blank);
            }
			flagMap.put(_countryCode, bitmap);
		}
		return flagMap.get(_countryCode);
	}

    // RecyclingImageView를 사용하기 위해서 맵을 사용하지 않게 변경
	static public Bitmap getGrayscaledFlagBitmap(Context _context, String _countryCode)	{
		String fileName = "flag_"+_countryCode.toLowerCase()+"_bw";
		int id = _context.getResources().getIdentifier(fileName,  "raw", _context.getPackageName());

//		if( grayscaledFlagMap.get(_countryCode) == null ) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = MNBitmapUtils.getDefaultOptions();

			try {
				InputStream io = _context.getResources().openRawResource(id);
				byte raw[];
				raw = new byte[io.available()];
				io.read(raw);
				bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length, options);
			} catch (IOException ignored) {} catch( NotFoundException ignored) {}
			
			if( bitmap == null ) {
				bitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.flag_blank,
                        options);
            }

        return bitmap;
//			grayscaledFlagMap.put(_countryCode, bitmap);
//		}
//		return grayscaledFlagMap.get(_countryCode);
	}
}

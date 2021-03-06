package com.yooiistudios.morningkit.panel.newsfeed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yooiistudios.morningkit.R;
import com.yooiistudios.morningkit.common.dp.DipToPixel;
import com.yooiistudios.morningkit.setting.theme.themedetail.MNSettingColors;
import com.yooiistudios.morningkit.setting.theme.themedetail.MNTheme;
import com.yooiistudios.morningkit.setting.theme.themedetail.MNThemeType;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;

/**
 * Created by Dongheyon Jeong on in morning-kit from Yooii Studios Co., LTD. on 2014. 7. 3.
 */
public class MNNewsFeedAdapter extends BaseAdapter {
    private Context mContext;
    private RssFeed mFeed;

    public MNNewsFeedAdapter(Context context, RssFeed feed) {
        mContext = context;
        mFeed = feed;
    }

    @Override
    public int getCount() {
        return mFeed.getRssItems().size();
    }

    @Override
    public Object getItem(int i) {
        return mFeed.getRssItems().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.panel_news_feed_detail_list_item,
                    viewGroup, false);
        }
        RssItem item = mFeed.getRssItems().get(i);

        MNThemeType currentThemeType = MNTheme.getCurrentThemeType(mContext
                .getApplicationContext());
        int textColor = MNSettingColors.getSubFontColor(currentThemeType);

        TextView titleView = (TextView)view.findViewById(R.id.title);
        titleView.setText(item.getTitle());
        titleView.setTextColor(textColor);

        if (i == mFeed.getRssItems().size()-1) {
            view.findViewById(R.id.divider).setVisibility(View.GONE);
        }

        TextView contentView = (TextView)view.findViewById(R.id.content);
        contentView.setTextColor(textColor);
        // 퍼포먼스 개선을 위해 우선 200글자만 읽어서 보여줌(어차피 2줄 밖에 안보임)
        RelativeLayout.LayoutParams titleLp = (RelativeLayout.LayoutParams)
                titleView.getLayoutParams();
        String desc = item.getDescription();
        if (desc != null) {
            contentView.setText(item.getDescription());
            contentView.setVisibility(View.VISIBLE);
            view.setMinimumHeight(0);
            titleLp.addRule(RelativeLayout.CENTER_VERTICAL, 0);
        }
        else {
            contentView.setVisibility(View.GONE);
            view.setMinimumHeight(DipToPixel.dpToPixel(mContext,
                    mContext.getResources().getDimension(
                            R.dimen.panel_news_feed_detail_list_item_min_height)));
            titleLp.addRule(RelativeLayout.CENTER_VERTICAL);
        }

        return view;
    }
}

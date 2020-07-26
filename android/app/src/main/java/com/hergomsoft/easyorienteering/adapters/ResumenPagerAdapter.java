package com.hergomsoft.easyorienteering.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.hergomsoft.easyorienteering.data.model.pages.ResumenPages;

public class ResumenPagerAdapter extends PagerAdapter {

    private Context mContext;

    public ResumenPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ResumenPages modelObject = ResumenPages.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return ResumenPages.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ResumenPages customPagerEnum = ResumenPages.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }

}
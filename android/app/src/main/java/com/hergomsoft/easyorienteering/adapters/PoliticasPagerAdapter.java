package com.hergomsoft.easyorienteering.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.hergomsoft.easyorienteering.data.model.pages.PoliticasPages;

public class PoliticasPagerAdapter extends PagerAdapter {

    private Context context;

    public PoliticasPagerAdapter(Context context) { this.context = context; }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        PoliticasPages modelObject = PoliticasPages.values()[position];
        LayoutInflater inflater = LayoutInflater.from(context);
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
        return PoliticasPages.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        PoliticasPages customPagerEnum = PoliticasPages.values()[position];
        return context.getString(customPagerEnum.getTitleResId());
    }

}
package com.hergomsoft.easyorienteering.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.Vector;

public class MisCarrerasPagerAdapter extends PagerAdapter {

    private Context context;
    private Vector<View> pages;
    private String[] titulos;

    public MisCarrerasPagerAdapter(Context context, Vector<View> pages, String[] titulos) {
        this.context = context;
        this.pages = pages;
        this.titulos = titulos;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View page = pages.get(position);
        container.addView(page);
        return page;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titulos[position];
    }

}
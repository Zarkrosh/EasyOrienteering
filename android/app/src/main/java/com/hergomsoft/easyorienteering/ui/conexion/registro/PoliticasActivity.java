package com.hergomsoft.easyorienteering.ui.conexion.registro;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.hergomsoft.easyorienteering.R;
import com.hergomsoft.easyorienteering.adapters.PoliticasPagerAdapter;
import com.hergomsoft.easyorienteering.util.BackableActivity;

public class PoliticasActivity extends BackableActivity {

    private ViewPager pager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_politicas);

        pager = findViewById(R.id.politicasPager);
        tabs = findViewById(R.id.politicasTabs);

        pager.setAdapter(new PoliticasPagerAdapter(this));
        tabs.setupWithViewPager(pager);
    }

}

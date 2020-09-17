package com.hergomsoft.easyorienteering.components;

import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hergomsoft.easyorienteering.R;

public class BaseActivity extends AppCompatActivity {

    private DialogoCarga dialogoCarga;

    @Override
    public void setContentView(int layoutResID) {
        /*
        ConstraintLayout constraintLayout = getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        dialogoCarga = constraintLayout.findViewById(R.id.base_dialogo_carga);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(constraintLayout);
        */
    }
}

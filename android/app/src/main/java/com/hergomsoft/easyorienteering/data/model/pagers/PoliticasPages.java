package com.hergomsoft.easyorienteering.data.model.pagers;

import com.hergomsoft.easyorienteering.R;

public enum PoliticasPages {
    POLITICA(R.string.politicas_politica_corto, R.layout.politica_datos),
    CONDICIONES(R.string.politicas_condiciones_corto, R.layout.condiciones_uso);

    private int mTitleResId;
    private int mLayoutResId;

    PoliticasPages(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}

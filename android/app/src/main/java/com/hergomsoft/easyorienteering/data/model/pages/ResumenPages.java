package com.hergomsoft.easyorienteering.data.model.pages;

import com.hergomsoft.easyorienteering.R;

public enum ResumenPages {
    EJEMPLO1(R.string.resumen_primera_titulo, R.layout.resumen_primera),
    EJEMPLO2(R.string.resumen_segunda_titulo, R.layout.resumen_segunda);

    private int mTitleResId;
    private int mLayoutResId;

    ResumenPages(int titleResId, int layoutResId) {
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

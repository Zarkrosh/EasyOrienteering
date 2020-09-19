package com.hergomsoft.easyorienteering.data.model.pages;

import com.hergomsoft.easyorienteering.R;

public enum ResumenPages {
    PAGINA1(R.string.resumen_que_es_titulo, R.layout.resumen_que_es),
    PAGINA2(R.string.resumen_como_funciona_titulo, R.layout.resumen_como_funciona),
    PAGINA3(R.string.resumen_resultados_titulo, R.layout.resumen_resultados),
    PAGINA4(R.string.resumen_final_titulo, R.layout.resumen_final);

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

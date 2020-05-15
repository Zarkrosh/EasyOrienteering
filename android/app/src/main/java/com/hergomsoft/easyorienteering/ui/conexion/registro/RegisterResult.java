package com.hergomsoft.easyorienteering.ui.conexion.registro;

import androidx.annotation.Nullable;

/**
 * Registration result : success (user details) or error message.
 */
class RegisterResult {
    @Nullable
    private Boolean success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable Boolean success) {
        this.success = success;
    }

    @Nullable
    Boolean getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}

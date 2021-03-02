package com.example.androidcouchbase;

import androidx.annotation.NonNull;

public enum TipoTelefono {
    MOVIL,
    CASA,
    TRABAJO,
    OTRO;

    @NonNull
    @Override
    public String toString() {
        return this.name();
    }
}

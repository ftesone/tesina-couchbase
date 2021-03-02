package com.example.androidcouchbase;

import androidx.annotation.NonNull;

public class Telefono {
    public String id;
    public String numero;
    public TipoTelefono tipo;

    @NonNull
    @Override
    public String toString() {
        return tipo.toString() + ": "+numero;
    }
}

package com.example.androidcouchbase;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Contacto {
    public String id;
    public String apellido;
    public String nombre;
    public Date fechaNacimiento;
    public String apodo;
    public String empresa;
    public List<Telefono> telefonos = new ArrayList<>();

    @NonNull
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(apellido+", "+nombre);

        if (apodo != null) {
            s.append(" '"+apodo+"'");
        }

        if (fechaNacimiento != null) {
            s.append(" ("+(new SimpleDateFormat("dd/MM/yyyy")).format(fechaNacimiento)+")");
        }

        if (empresa != null) {
            s.append(" @"+empresa);
        }

        for (Telefono telefono : telefonos) {
            s.append(" +"+telefono.numero+" {"+telefono.tipo.name()+"}");
        }

        return s.toString();
    }
}

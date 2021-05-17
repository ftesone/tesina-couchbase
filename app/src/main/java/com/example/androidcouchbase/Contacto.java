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
    public Direccion direccion;
    public List<Telefono> telefonos = new ArrayList<>();
    public List<String> emails = new ArrayList<>();

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

        if (direccion != null) {
            s.append("DIR: "+direccion.calle + " "+ direccion.nro);

            if (direccion.piso != null || direccion.depto != null) {
                s.append(" ");

                if (direccion.piso != null) {
                    s.append(direccion.piso+"º");
                }

                if (direccion.depto != null) {
                    s.append(direccion.depto);
                }
            }
        }

        for (Telefono telefono : telefonos) {
            s.append("["+telefono.tipo.name()+": "+telefono.numero+"]");
        }

        for (String email : emails) {
            s.append("<"+email+">");
        }

        return s.toString();
    }
}

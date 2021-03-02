package com.example.androidcouchbase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;

import java.util.Calendar;
import java.util.Date;

public class CreateActivity extends AppCompatActivity {
    private static String[] apellidos = {"Sánchez", "Ginóbilli", "Montecchia", "Oberto", "Victoriano", "Fernández", "Sconochini", "Scola", "Gutiérrez", "Nocioni", "Palladino", "Wolkowyski", "Magnano"};
    private static String[] nombres = {"Juan", "Emanuel", "Alejandro", "Fabricio", "Lucas", "Gabriel", "Hugo", "Luis", "Leonardo", "Andrés", "Leandro", "Rubén", "Ignacio"};
    private static String[] apodos = {"Pepe", "Manu", "Chapu", "Leo", "Colo", "Luifa", "Puma"};
    private static String[] empresas = {"Pied Piper", "Aviato", "Raviga", "Nucleus", "Endframe", "SeeFood"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        String apellido = apellidos[random(0, apellidos.length-1)];
        String nombre = nombres[random(0, nombres.length-1)];

        String apodo = null;
        if (0 == random(0,3)) {
            apodo = apodos[random(0, apodos.length-1)];
        }

        Date fechaNacimiento = null;
        if (0 == random(0,3)) {
            fechaNacimiento = getRandomDate();
        }

        String empresa = null;
        if (0 == random(0,3)) {
            empresa = empresas[random(0, empresas.length-1)];
        }

        Contacto contacto = new Contacto();
        contacto.apellido = apellido;
        contacto.nombre = nombre;

        if (apodo != null) {
            contacto.apodo = apodo;
        }

        if (fechaNacimiento != null) {
            contacto.fechaNacimiento = fechaNacimiento;
        }

        if (empresa != null) {
            contacto.empresa = empresa;
        }

        for (int i=0, j=random(0,4) ; i<=j ; i++) {
            Telefono telefono = new Telefono();
            telefono.numero = String.valueOf(random(1000,9999));
            telefono.tipo = getRandomTipoTelefono();
            contacto.telefonos.add(telefono);
        }

        CouchbaseLite.init(getApplicationContext());
        try {
            ContactoDao.getInstance(new Database(String.valueOf(R.string.database), new DatabaseConfiguration()))
                .insert(contacto)
            ;

            TextView textView = (TextView) findViewById(R.id.contacto);
            textView.setText(contacto.toString());

            ArrayAdapter<Telefono> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacto.telefonos);
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(adapter);
        } catch (CouchbaseLiteException e) {
            Log.e("DB_ERROR", e.getMessage());
        }
    }

    private int random(int start, int end)
    {
        return start + (int) Math.round(Math.random() * (end-start));
    }

    private Date getRandomDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, random(1950,2000));
        calendar.set(Calendar.DAY_OF_YEAR, random(1,365));

        return calendar.getTime();
    }

    private TipoTelefono getRandomTipoTelefono()
    {
        TipoTelefono[] values = TipoTelefono.values();

        return values[random(0, values.length-1)];
    }
}

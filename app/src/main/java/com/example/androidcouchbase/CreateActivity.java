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
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;

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

        CouchbaseLite.init(getApplicationContext());

        DatabaseConfiguration config = new DatabaseConfiguration();
        Database database;
        try {
            database = new Database(String.valueOf(R.string.database), config);

            Contacto contacto = new Contacto();
            MutableDocument contactoDocument = new MutableDocument();

            contactoDocument.setString("apellido", contacto.apellido = apellido);
            contactoDocument.setString("nombre", contacto.nombre = nombre);

            if (apodo != null) {
                contactoDocument.setString("apodo", contacto.apodo = apodo);
            }

            if (fechaNacimiento != null) {
                contactoDocument.setDate("fechaNacimiento", contacto.fechaNacimiento = fechaNacimiento);
            }

            if (empresa != null) {
                contactoDocument.setString("empresa", contacto.empresa = empresa);
            }

            MutableArray telefonosArray = new MutableArray();
            for (int i=0, j=random(0,4) ; i<=j ; i++) {
                Telefono telefono = new Telefono();
                MutableDictionary telefonoDictionary = new MutableDictionary();

                telefonoDictionary.setString("numero", telefono.numero = String.valueOf(random(1000,9999)));
                telefonoDictionary.setString("tipo", (telefono.tipo = getRandomTipoTelefono()).toString());

                contacto.telefonos.add(telefono);
                telefonosArray.addDictionary(telefonoDictionary);
            }

            if (telefonosArray.count() > 0) {
                contactoDocument.setArray("telefonos", telefonosArray);
            }

            database.save(contactoDocument);

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

package com.example.androidcouchbase;

import android.content.Intent;
import android.os.Bundle;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String[] terminosBusqueda = {"L", "no", "3", "6", "9", "e"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateActivity.class));
            }
        });

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateContactsList(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateContactsList();
    }

    private void updateContactsList()
    {
        String busqueda = null;
        if (0 == ((int) Math.round(Math.random()))) {
            busqueda = terminosBusqueda[(int) Math.round(Math.random() * (terminosBusqueda.length-1))];
        }

        CouchbaseLite.init(getApplicationContext());
        try {
            ContactoDao contactoDao = ContactoDao.getInstance(new Database(String.valueOf(R.string.database), new DatabaseConfiguration()));

            List<Contacto> contactos = busqueda == null ? contactoDao.obtenerTodos() : contactoDao.obtenerPorBusqueda(busqueda);

            TextView textView = (TextView) findViewById(R.id.busqueda);
            textView.setText(busqueda != null ? "Término de búsqueda: "+busqueda : null);

            ArrayAdapter<Contacto> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactos);

            ListView listView = (ListView) findViewById(R.id.list);

            listView.setAdapter(adapter);
        } catch (CouchbaseLiteException e) {
            Log.e("DB_ERROR", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

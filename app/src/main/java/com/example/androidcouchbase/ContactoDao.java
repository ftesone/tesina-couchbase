package com.example.androidcouchbase;

import com.couchbase.lite.Array;
import com.couchbase.lite.ArrayExpression;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Function;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Ordering;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.Result;
import com.couchbase.lite.SelectResult;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContactoDao {
    private static ContactoDao instance = null;
    private static Database database = null;

    private ContactoDao() {}

    public static ContactoDao getInstance(@NotNull Database database) {
        if (instance == null) {
            ContactoDao.database = database;
            instance = new ContactoDao();
        }

        return instance;
    }

    public void insert(@NotNull Contacto contacto) throws CouchbaseLiteException
    {
        MutableDocument contactoDocument = new MutableDocument();

        contactoDocument.setString("apellido", contacto.apellido);
        contactoDocument.setString("nombre", contacto.nombre);

        if (contacto.apodo != null) {
            contactoDocument.setString("apodo", contacto.apodo);
        }

        if (contacto.fechaNacimiento != null) {
            contactoDocument.setDate("fechaNacimiento", contacto.fechaNacimiento);
        }

        if (contacto.empresa != null) {
            contactoDocument.setString("empresa", contacto.empresa);
        }

        if (contacto.telefonos.size() > 0) {
            MutableArray telefonosArray = new MutableArray();

            for (Telefono telefono : contacto.telefonos) {
                MutableDictionary telefonoDictionary = new MutableDictionary();

                telefonoDictionary.setString("numero", telefono.numero);
                telefonoDictionary.setString("tipo", telefono.tipo.toString());

                telefonosArray.addDictionary(telefonoDictionary);
            }

            contactoDocument.setArray("telefonos", telefonosArray);
        }

        database.save(contactoDocument);
    }

    public List<Contacto> obtenerTodos() throws CouchbaseLiteException
    {
        Query query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.database(database))
            .orderBy(Ordering.property("apellido").ascending(), Ordering.property("nombre").ascending())
        ;

        return hydrateResults(query.execute().allResults());
    }

    public List<Contacto> obtenerPorBusqueda(String termino) throws CouchbaseLiteException
    {
        Expression busquedaLike = Expression.string("%"+termino.toLowerCase()+"%");
        Query query = QueryBuilder
            .select(SelectResult.all())
            .from(DataSource.database(database))
            .where(Function.lower(Expression.property("apellido")).like(busquedaLike)
                .or(Function.lower(Expression.property("nombre")).like(busquedaLike))
                .or(Function.lower(Expression.property("fechaNacimiento")).like(busquedaLike))
                .or(Function.lower(Expression.property("apodo")).like(busquedaLike))
                .or(Function.lower(Expression.property("empresa")).like(busquedaLike))
                .or(ArrayExpression.any(ArrayExpression.variable("telefono"))
                    .in(Expression.property("telefonos"))
                    .satisfies(ArrayExpression.variable("telefono.numero").like(Expression.string("%"+termino.toLowerCase()+"%")))
                )
            )
            .orderBy(Ordering.property("apellido").ascending(), Ordering.property("nombre").ascending())
        ;

        return hydrateResults(query.execute().allResults());
    }

    private List<Contacto> hydrateResults(List<Result> resultados)
    {
        List<Contacto> contactos = new ArrayList<>();

        for (Result resultado : resultados) {
            Contacto contacto = new Contacto();
            contacto.apellido = resultado.getDictionary(0).getString("apellido");
            contacto.nombre = resultado.getDictionary(0).getString("nombre");
            contacto.fechaNacimiento = resultado.getDictionary(0).getDate("fechaNacimiento");
            contacto.apodo = resultado.getDictionary(0).getString("apodo");
            contacto.empresa = resultado.getDictionary(0).getString("empresa");

            Array telefonosArray = resultado.getDictionary(0).getArray("telefonos");
            for (int i=0, j=telefonosArray.count() ; i<j ; i++) {
                Dictionary telefonoDictionary = telefonosArray.getDictionary(i);
                Telefono telefono = new Telefono();
                telefono.numero = telefonoDictionary.getString("numero");
                telefono.tipo = TipoTelefono.valueOf(telefonoDictionary.getString("tipo"));
                contacto.telefonos.add(telefono);
            }

            contactos.add(contacto);
        }

        return contactos;
    }
}

package com.example.androidcouchbase;

import com.couchbase.lite.Array;
import com.couchbase.lite.ArrayExpression;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.DataSource;
import com.couchbase.lite.Database;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
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

        if (contacto.direccion != null) {
            MutableDictionary direccionDictionary = new MutableDictionary();

            direccionDictionary.setString("calle", contacto.direccion.calle);
            direccionDictionary.setString("nro", contacto.direccion.nro);

            if (contacto.direccion.piso != null) {
                direccionDictionary.setString("piso", contacto.direccion.piso);
            }

            if (contacto.direccion.depto != null) {
                direccionDictionary.setString("depto", contacto.direccion.depto);
            }

            contactoDocument.setValue("direccion", direccionDictionary);
        }

        if (contacto.telefonos.size() > 0) {
            MutableArray telefonosArray = new MutableArray();

            for (Telefono telefono : contacto.telefonos) {
                MutableDictionary telefonoDictionary = new MutableDictionary();

                telefonoDictionary.setString("numero", telefono.numero);
                telefonoDictionary.setString("tipo", telefono.tipo.name());

                telefonosArray.addDictionary(telefonoDictionary);
            }

            contactoDocument.setArray("telefonos", telefonosArray);
        }

        if (contacto.emails.size() > 0) {
            MutableArray emailsArray = new MutableArray();

            for (String email : contacto.emails) {
                emailsArray.addString(email);
            }

            contactoDocument.setArray("emails", emailsArray);
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
                .or(Function.lower(Expression.property("direccion.calle")).like(busquedaLike))
                .or(Function.lower(Expression.property("direccion.nro")).like(busquedaLike))
                .or(Function.lower(Expression.property("direccion.piso")).like(busquedaLike))
                .or(Function.lower(Expression.property("direccion.depto")).like(busquedaLike))
                .or(ArrayExpression.any(ArrayExpression.variable("telefono"))
                    .in(Expression.property("telefonos"))
                    .satisfies(ArrayExpression.variable("telefono.numero").like(busquedaLike))
                )
                .or(ArrayExpression.any(ArrayExpression.variable("email"))
                    .in(Expression.property("emails"))
                    .satisfies(ArrayExpression.variable("email").like(busquedaLike))
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

            Dictionary direccionDictionary = resultado.getDictionary(0).getDictionary("direccion");
            if (direccionDictionary != null) {
                contacto.direccion = new Direccion();
                contacto.direccion.calle = direccionDictionary.getString("calle");
                contacto.direccion.nro = direccionDictionary.getString("nro");
                contacto.direccion.piso = direccionDictionary.getString("piso");
                contacto.direccion.depto = direccionDictionary.getString("depto");
            }

            Array telefonosArray = resultado.getDictionary(0).getArray("telefonos");
            for (int i=0, j=telefonosArray.count() ; i<j ; i++) {
                Dictionary telefonoDictionary = telefonosArray.getDictionary(i);
                Telefono telefono = new Telefono();
                telefono.numero = telefonoDictionary.getString("numero");
                telefono.tipo = TipoTelefono.valueOf(telefonoDictionary.getString("tipo"));
                contacto.telefonos.add(telefono);
            }

            Array emailsArray = resultado.getDictionary(0).getArray("emails");
            for (int i=0, j=emailsArray.count() ; i<j ; i++) {
                contacto.emails.add(emailsArray.getString(i));
            }

            contactos.add(contacto);
        }

        return contactos;
    }
}

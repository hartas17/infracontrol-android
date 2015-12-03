package info.androidhive.materialdesign.activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Hartas on 20/10/2015.
 */
public class Database_Infra extends SQLiteOpenHelper {
    //public static String DB_PATH="/data/data/com.android4dev.navigationview/databases/";
    public static String DB_NAME="db_infracontrol";
    private final Context context;
    //Sentencia SQL para crear la tabla de Usuarios
    String sqlCreate1 = "CREATE TABLE Infracontrol (infracontrol TEXT PRIMARY KEY, nombre TEXT)";
    String sqlCreate2 = "CREATE TABLE Control (control INTEGER PRIMARY KEY AUTOINCREMENT,color_icono INTEGER, nombre TEXT,infracontrol TEXT )";
    String sqlCreate3 = "CREATE TABLE Boton (boton INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT,control INTEGER,color INTEGER,sentencia TEXT, posicion INTEGER )";
    String sqlCreate4 = "CREATE TABLE Seleccion (seleccion INTEGER PRIMARY KEY , infracontrol TEXT,control INTEGER )";
    public Database_Infra( CursorFactory factory, int version, Context context) {
        super(context, DB_NAME, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate1);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);
        db.execSQL(sqlCreate4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS db_infracontrol");
        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate1);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);
        db.execSQL(sqlCreate4);
    }
}

package info.androidhive.materialdesign.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import info.androidhive.materialdesign.R;

public class Editar_control extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private SQLiteDatabase db;
    private int id;
    private EditText Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_control);
        mToolbar = (Toolbar) findViewById(R.id.toolbar7);
        setSupportActionBar(mToolbar);
        Bundle bundle=getIntent().getExtras();
        setTitle("Editar control");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        id=bundle.getInt("control");
        Spinner spinner_animales = (Spinner) findViewById(R.id.New_color_control);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.Colores , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_animales.setAdapter(spinner_adapter);

        findViewById(R.id.aceptar_control).setOnClickListener(this);

        Name=(EditText)findViewById(R.id.new_name_control);

        Database_Infra usdbh =
                new Database_Infra( null, 1,this);
        db = usdbh.getWritableDatabase();
        llenar_elementos();
    }

    public void llenar_elementos(){
        Cursor c = db.rawQuery("SELECT nombre FROM Control WHERE control = "+id+"", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                Name.setText(c.getString(0));
            } while (c.moveToNext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onClick(View view) {
        Spinner color=(Spinner)findViewById(R.id.New_color_control);
        String name= Name.getText().toString().trim();
        int col=0;
        System.out.println(color.getSelectedItem());
        if(color.getSelectedItem().equals("Rojo")){
            col=1;
        }
        if(color.getSelectedItem().equals("Azul")){
            col=3;
        }
        if(color.getSelectedItem().equals("Azul bajo")){
            col=2;
        }
        if(color.getSelectedItem().equals("Naranja")){
            col = 4;
        }
        if (color.getSelectedItem().equals("Purpura")) {
            col = 5;
        }
        db.execSQL("UPDATE Control SET color_icono=" + col + ",nombre='" + name + "' WHERE control = " + id + "");
        finish();
        onBackPressed();

    }
}

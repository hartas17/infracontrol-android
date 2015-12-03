package info.androidhive.materialdesign.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import info.androidhive.materialdesign.R;


public class Enviar_to_infra extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    public CheckBox dhcp;
    public Context context;
    private ProgressDialog pd;
    WifiManager wifiManager;
    public String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_to_infra);
        mToolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(mToolbar);
        setTitle("Configuración de red");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.btEnviar).setOnClickListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dhcp = (CheckBox) findViewById(R.id.btDhcp);
        final LinearLayout cont_ip=(LinearLayout)findViewById(R.id.DHCP_contenedor);
        cont_ip.setVisibility(LinearLayout.INVISIBLE);
        dhcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox)view).isChecked();

                if (isChecked) {
                    cont_ip.setVisibility(LinearLayout.INVISIBLE);
                }
                else {
                    cont_ip.setVisibility(LinearLayout.VISIBLE);

                }
            }
        });;
        context=this;
        asing();
    }

    public void asing(){
        AsyncTask<Void, Void, Void> task2 = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(Enviar_to_infra.this);
                pd.setTitle("Cargando...");
                pd.setMessage("Porfavor espere.");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
                obtenet_mac();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                try {

                    //Do something...


                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (pd!=null) {
                    pd.dismiss();
                }

                cargarSSID();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                // TODO Auto-generated method stub
                //super.onProgressUpdate(values);
            }
        };
        task2.execute((Void[]) null);

    }

    private void obtenet_mac() {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.4.1/mac", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                System.out.println(new String(response));
                String macs = new String(response);
                mac = macs.replace(":", "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void cargarSSID() {
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.4.1:555/scan", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                System.out.println(new String(response));

                // called when response HTTP status is "200 OK"
                try {
                    JSONObject jsonArray1 = new JSONObject(new String(response));

                    JSONArray jsonArray = jsonArray1.getJSONArray("results");
                    LinkedList comidas = new LinkedList();
                    String s[] = new String[1];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String var1 = jsonObject.getString("ssid");
                        System.out.println(var1);
                        s[0] = s[0] + ";" + var1;
                        comidas.add(var1);
                    }
                    cargartoSpinner(s,comidas);
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }
    @Override
    protected void onDestroy(){

        super.onDestroy();
    }
    @Override
    protected void onPause(){

        super.onPause();
    }
    @Override
    protected void onStop(){

        super.onStop();
    }
    public void cargartoSpinner(String s[],LinkedList comida){
        Spinner prueba=(Spinner)findViewById(R.id.SSid_infra);
        String com[]=s[0].split(";");
        ArrayAdapter spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, comida);
//Añadimos el layout para el menú y se lo damos al spinner
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prueba.setAdapter(spinner_adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //noinspection SimplifiableIfStatement


        //return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    ///////////////////AGREGADO
    public void onClick(View v) {
        System.out.println(v.getId());

        switch (v.getId()) {

            case R.id.btEnviar:
                final boolean temp=dhcp.isChecked();
                v.setEnabled(false);
                AsyncTask<Void, Void, Void> task2 = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        pd = new ProgressDialog(Enviar_to_infra.this);
                        pd.setTitle("Cargando...");
                        pd.setMessage("Porfavor espere.");
                        pd.setCancelable(false);
                        pd.setIndeterminate(true);
                        pd.show();
                    }

                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {

                            //Do something...
                            Thread.sleep(3000);

                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (pd!=null) {
                            pd.dismiss();
                            findViewById(R.id.btEnviar).setEnabled(true);
                        }
                        Send(temp);
                        startsearch();

                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        // TODO Auto-generated method stub
                        //super.onProgressUpdate(values);
                    }
                };
                task2.execute((Void[]) null);
                break;

        }

    }
    ///////// ENVIAR METODO POST
    private void Send(final boolean dhcp) {
        try{
            new Thread(){
                httpHandler handler = new httpHandler(dhcp,Enviar_to_infra.this);
                String txt = handler.post("http://192.168.4.1:555/configure");

            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        //System.out.println(txt);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        try{
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        wifiManager.setWifiEnabled(true);

    }
    public void startsearch(){//ABRE LAYOUT PARA BUSCAR LAS IPS DE LOS INFRACONTROLS
        final Intent intent = new Intent(this, Conf_Infra.class);
        // ActivityTransitionLauncher.with(getActivity()).from(v).launch(intent);
        intent.putExtra("mac",mac);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);


    }

}

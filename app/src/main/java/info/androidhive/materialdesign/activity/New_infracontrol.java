package info.androidhive.materialdesign.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.androidhive.materialdesign.R;

/**
 *
 *
 * @author Hartas17
 *
 */
public class New_infracontrol extends AppCompatActivity implements View.OnClickListener {
    WifiManager wifi;
    List<ScanResult> wifiList;
    ArrayList<HashMap<String,String>> arrayList;
    SimpleAdapter adapter;
    Spinner ListaWifi = null;
    String SSID="";
    int Infras=0;
    boolean FinishScann=false;
    Intent act;
    private SQLiteDatabase db;
    private ProgressDialog pd;
    private Context context;
    WifiManager wifiManager;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_infra_instrucciones);
        mToolbar = (Toolbar) findViewById(R.id.toolbar3);
        context = this;
        setSupportActionBar(mToolbar);
        setTitle("Configurar Blastbot");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.btsiguiente).setOnClickListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        arrayList = new ArrayList<HashMap<String,String>>();
        wifiList = new ArrayList();
        /*La clase WifiManager nos permite realizar diversas operaciones de
        conectivididad wifi, para ello necesitamos llamar al servicio*/
        wifi=  (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //Comprueba si el wifi esta encencido, en caso negativo lo activa.
        if(!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "activando wifi...", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        //Registramos nuestro receptor, para así poder recibir eventos del sistema.
        WifiReceiver receiver = new WifiReceiver();
        Scan();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_infracontrol, menu);
        return true;
    }

    @Override
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

    @Override
    protected void onStop(){

        super.onStop();
    }
    @Override
    public void onClick(View v) {

        System.out.println(v.getId());

        switch (v.getId()){

            case R.id.btConectar:
                Connect();
                break;
            case R.id.btsiguiente://Busqueda de los infracontrol... la primera pantalla(SIGUIENTE)
                v.setEnabled(false);
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        pd = new ProgressDialog(New_infracontrol.this);
                        pd.setTitle("Buscando Blastbot...");
                        pd.setMessage("Porfavor espere.");
                        pd.setCancelable(false);
                        pd.setIndeterminate(true);
                        pd.show();
                    }

                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {
                            Scan();
                            //Do something...
                            Thread.sleep(2000);
                            Scan();
                            Thread.sleep(2000);
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
                            findViewById(R.id.btsiguiente).setEnabled(true);
                        }
                        delivery();
                    }

                };
                task.execute((Void[])null);
                break;
            case R.id.click:
                if (android.os.Build.VERSION.SDK_INT > 9)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                final Intent intent = new Intent(New_infracontrol.this, Sear_mdns.class);
                            // ActivityTransitionLauncher.with(getActivity()).from(v).launch(intent);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                break;

        }

    }



    private void Define(){

        if(Infras==1){
            Connect_SSID();
        }
        if(Infras==0){
            //Scan();
            //Toast.makeText(New_infracontrol.this, "Asegurese de almenos tener un Infracontrol en modo configuración.", Toast.LENGTH_SHORT).show();
        }
        if(Infras>1){
            searchManual();
        }
    }

    private void searchManual() {
        setContentView(R.layout.activity_nuevo_infracontrol);
        findViewById(R.id.btConectar).setOnClickListener(this);
        ListaWifi = (Spinner) findViewById(R.id.lvWifi);
        String[] from = new String[]{"SSID", "BSSID"};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        adapter = new SimpleAdapter(this, arrayList, android.R.layout.two_line_list_item, from, to);
        ListaWifi.setAdapter(adapter);
    }


    ////////// Scan for wifi
    private void Scan(){
        Infras=0;
        arrayList.clear();
        wifi.startScan();
        //Toast.makeText(getApplicationContext(), "Scaneando...", Toast.LENGTH_LONG).show();
        HashMap<String,String> item;
        for (int i = 0; i < wifiList.size();i++ ) {
            item =  new HashMap<String, String>();
            //           System.out.println(wifiList.get(i).SSID.substring(0,12));
            System.out.println("Blastbot");
            if(wifiList.get(i).SSID.length()>=11){
                if(wifiList.get(i).SSID.substring(0,8).equals("Blastbot")){
                    item.put("SSID", wifiList.get(i).SSID);
                    item.put("BSSID", wifiList.get(i).BSSID);
                    SSID=wifiList.get(i).SSID;
                    Infras++;
                    System.out.println(Infras+" "+SSID);
                }
            }

            arrayList.add(item);
        }
        //adapter.notifyDataSetChanged();
        Define();
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            wifiList = wifi.getScanResults();
        }

    }

    ////////// Connect to a spesifyc wifi
    public void Connect() {
        System.out.println(ListaWifi.getSelectedItem().toString());
        String SSIDs[]=ListaWifi.getSelectedItem().toString().split(",");
        if(SSIDs[0].charAt(1)=='S'){
            SSID=SSIDs[0];
        }
        else{
            SSID=SSIDs[1];
        }
        SSID=SSID.split("=")[1];
        SSID=SSID.substring(0,SSID.length()-1);
        System.out.println(SSID+" Este SSID");
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + SSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        System.out.println(wifiManager.getConnectionInfo().getSSID());
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
        Verify();
    }

    public void Connect_SSID(){

        System.out.println(SSID);

        System.out.println(SSID + " Este SSID");
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + SSID + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        System.out.println(wifiManager.getConnectionInfo().getSSID());
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }

        }
        Verify();
    }

    private void Verify(){
        final boolean[] si = {false};
        try {
            Thread.sleep(6000);
            si[0]=true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(!si[0]){
        }

    }
    public void delivery(){
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        System.out.println(wifiManager.getConnectionInfo().getSSID());
        System.out.println("\"" + SSID + "\"");
        if(!wifiManager.getConnectionInfo().getSSID().equals("\""+SSID+"\"")){
            Toast.makeText(New_infracontrol.this, "No se encontró ningún Blastbot, asegúrese de haber seguido las instrucciones correctamente", Toast.LENGTH_SHORT).show();
        }
        else{
            Config();
        }


    }

    public void Config(){

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final Intent intent = new Intent(this, Enviar_to_infra.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    ////////// Send POSTs elements

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

}

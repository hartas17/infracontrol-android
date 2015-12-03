package info.androidhive.materialdesign.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import cz.msebera.android.httpclient.Header;
import fr.ganfra.materialspinner.MaterialSpinner;
import info.androidhive.materialdesign.R;

public class Sear_mdns extends AppCompatActivity implements View.OnClickListener  {

    ////////////UDP
    static final int SERVER_PORT = 1555;

    public static String MESSAGE_STRING;

    public DatagramSocket socket;
    public static final String TAG="UDP";
    ////////////
    private String sentencia_leer;
    private Toolbar mToolbar;
    private ProgressDialog pd;
    MaterialSpinner ListaWifi = null;
    String mac="FFFFFFFFFFFF";
    Vector<String> macs=new Vector<String>();
    private SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_infra);
        TypefaceProvider.registerDefaultIconSets();

        mToolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(mToolbar);
        setTitle("Configuración de dispositivo");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.btBuscarI).setOnClickListener(this);
        findViewById(R.id.btGuardarI).setOnClickListener(this);
        findViewById(R.id.identify).setOnClickListener(this);

        Database_Infra usdbh =
                new Database_Infra( null, 1,this);
        db = usdbh.getWritableDatabase();
        ListaWifi=(MaterialSpinner)findViewById(R.id.Infras);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.identify:
                Identificar("");
                break;
            case R.id.btBuscarI://Boton buscar Infracontrol de la ultima pestaña
                v.setEnabled(false);
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        pd = new ProgressDialog(Sear_mdns.this);
                        pd.setTitle("Buscando Blastbot...");
                        pd.setMessage("Porfavor espere.");
                        pd.setCancelable(false);
                        pd.setIndeterminate(true);
                        pd.show();
                        new Thread(new Runnable() {
                            public void run() {
                                send("");
                                //Aquí ejecutamos nuestras tareas costosas
                            }
                        }).start();
                    }

                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {
                            new Thread(new Runnable() {
                                public void run() {
                                    recive();
                                    //Aquí ejecutamos nuestras tareas costosas
                                }
                            }).start();
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
                            findViewById(R.id.btBuscarI).setEnabled(true);
                        }
                        actualizar_spinner();
                    }

                };
                task.execute((Void[]) null);
                break;

            case R.id.btGuardarI:
                saveInfra();
                break;
        }
    }

    private void Identificar(String sent) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //Open a random port to send the package
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String mac_to_identy=ListaWifi.getSelectedItem().toString();
            System.out.println(mac_to_identy);
            byte[] data2 = mac_to_identy.getBytes();
            byte[] sente=sent.getBytes();
            byte[] sendData=new byte[data2.length+3+sente.length];
            byte ver=1;
            byte tipo=0;

            if(sent.equals("")) {
                byte comport=1;
                sendData[0] = ver;
                int cont = 0;
                for (int i = 0; i < data2.length; i++) {
                    sendData[i + 1] = data2[i];
                    cont++;
                }

                sendData[cont+1] = tipo;

                sendData[cont+2] = comport;
            }else{
                byte comport=2;
                sendData[0] = ver;
                int cont = 0;
                for (int i = 0; i < data2.length; i++) {
                    sendData[i + 1] = data2[i];
                    cont++;
                }

                sendData[cont+1] = tipo;

                sendData[cont+2] = comport;

                for (int j = 0; j < sente.length; j++) {
                    sendData[cont +3+ j] = sente[j];

                }

            }


            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), SERVER_PORT);
            socket.send(sendPacket);

            System.out.println(getClass().getName() + "Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
    }

    private void saveInfra() {//METODO PARA REGRESAR LOS ELEMETNOS DEL INFRACONTROL PARA GUARDAR EN BASE DE DATOS

        TextView se=(TextView)findViewById(R.id.Put_name);

        ListaWifi=(MaterialSpinner)findViewById(R.id.Infras);
        final String s3=ListaWifi.getSelectedItem().toString();

        final String s1=se.getText()+"".trim();
        final Intent intent = new Intent(this, MainActivity.class);
        if(!s1.equals("")&&!s3.equals("")){
            try{
            db.execSQL("INSERT INTO Infracontrol (infracontrol,nombre) VALUES ('" + s3 + "','" + s1+ "') ");
                startActivity(intent);
                finish();
            }
            catch (Exception e){
                new AlertDialog.Builder(this)
                        .setTitle("Duplicado Blastbot")
                        .setMessage("Ya se encuentra ese Blastbot en la aplicacion, ¿desea sobre escribir?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.execSQL("UPDATE Infracontrol SET nombre='"+s1+"' WHERE infracontrol='"+s3+"' ");
                                startActivity(intent);
                                finish();
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        else{
            new AlertDialog.Builder(this)
                    .setTitle("Elementos vacios")
                    .setMessage("Asegurese de llenar todos los elementos, recuerde no dejar campos vacios.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // continue with delete
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy(){
        try{
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
    @Override
    protected void onPause(){
        try{
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }
    @Override
    protected void onStop(){
        try{
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }
    ////////////////UDP
    public void send(String sent){
        sendBroadcast(sent);
    }
    public void recive(){
        reciveBroadcast();
    }

    public void reciveBroadcast(){
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(SERVER_PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {

                Log.i(TAG, "Ready to receive broadcast packets!");

                //Receive a packet

                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                String ver=recvBuf[0]+"";
                String pet=recvBuf[13]+"";
                String comport=recvBuf[14]+"";
                //Packet received
                Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(ver+"    "+pet+"     "+comport);
                String data = new String(packet.getData()).trim();
                Log.i(TAG, "Packet received; data: " + data);
                //Log.i(TAG, "Packet received; data: " + data1);

                if(ver.equals("1")&&pet.equals("1")&&comport.equals("0")){
                    // System.out.println(data.split(" ")[1]);
                    String temp=data.substring(0);
                    System.out.println(temp);
                    sentencia_leer=temp;
                    if(!macs.contains(temp)){
                        macs.add(temp);
                    }


                }
                else {

                }

                //Learn.setText(data.toString());
                /* Send the packet data back to the UI thread
                Intent localIntent = new Intent(BROADCAST_ACTION)
                        // Puts the data into the Intent
                        .putExtra(EXTENDED_DATA_STATUS, data);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);*/
            }
        } catch (IOException ex) {
            Log.i(TAG, "Oops" + ex.getMessage());

        }
    }
    public void actualizar_spinner(){
        String []name=new String[macs.size()];
        for(int i=0;i<macs.size();i++){
            name[i]=macs.elementAt(i);
            System.out.println(macs.elementAt(i));
        }
        ListaWifi=(MaterialSpinner)findViewById(R.id.Infras);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, name);
        ListaWifi.setAdapter(adapter);

    }
    public void sendBroadcast(String sent) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //Open a random port to send the package
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);


            byte[] data2 = mac.getBytes();
            byte[] sente=sent.getBytes();
            byte[] sendData=new byte[data2.length+3+sente.length];
            byte ver=1;
            byte tipo=0;

            if(sent.equals("")) {
                byte comport=0;
                sendData[0] = ver;
                int cont = 0;
                for (int i = 0; i < data2.length; i++) {
                    sendData[i + 1] = data2[i];
                    cont++;
                }

                sendData[cont+1] = tipo;

                sendData[cont+2] = comport;
            }else{
                byte comport=2;
                sendData[0] = ver;
                int cont = 0;
                for (int i = 0; i < data2.length; i++) {
                    sendData[i + 1] = data2[i];
                    cont++;
                }

                sendData[cont+1] = tipo;

                sendData[cont+2] = comport;

                for (int j = 0; j < sente.length; j++) {
                    sendData[cont +3+ j] = sente[j];

                }

            }


            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), SERVER_PORT);
            socket.send(sendPacket);

            System.out.println(getClass().getName() + "Broadcast packet sent to: " + getBroadcastAddress().getHostAddress());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
    ////////////////
}

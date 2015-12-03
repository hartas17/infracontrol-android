package info.androidhive.materialdesign.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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

import com.dexafree.materialList.card.Card;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.Provider;

import info.androidhive.materialdesign.R;
/**
 * Created by Hartas on 29/08/15.
 */
public class Add_Boton extends AppCompatActivity implements View.OnClickListener {



    ////////////UDP
    static final int SERVER_PORT = 1555;

    public DatagramSocket socket;
    public static final String TAG="UDP";
    ////////////

    private String sentencia_leer;
    private Toolbar mToolbar;
    private SQLiteDatabase db;
    int control;
    String mac;
    private ProgressDialog pd;
    private int posicion=0;
    private TextView sentencia,nombre;
    Spinner spinner_animales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__boton);
        mToolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Agregar boton");

        Database_Infra usdbh =
                new Database_Infra( null, 1,this);
        db = usdbh.getWritableDatabase();

        Bundle bundle=getIntent().getExtras();
        control= bundle.getInt("control");
        mac=bundle.getString("mac");
        posicion=bundle.getInt("tamaño");

        findViewById(R.id.Scann_code_newButton).setOnClickListener(this);
        findViewById(R.id.Add_newButton).setOnClickListener(this);
        sentencia=(TextView)findViewById(R.id.new_code);
        nombre=(TextView)findViewById(R.id.new_name);

        spinner_animales= (Spinner) findViewById(R.id.Color_new_buton);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.Colores , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_animales.setAdapter(spinner_adapter);
        int temp=(int)Math.floor(Math.random() * (1 - 6) + 6);
        System.out.println();
        spinner_animales.setSelection(temp - 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add__boton, menu);
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

        switch (view.getId()){
            case R.id.Add_newButton:
                Agregar();
                break;
            case R.id.Scann_code_newButton:
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        pd = new ProgressDialog(Add_Boton.this);
                        pd.setTitle("Buscando Infracontrol...");
                        pd.setMessage("Please wait.");
                       // pd.setContentView(R.layout.custom_dialog);
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
                            Thread.sleep(1000);
                            new Thread(new Runnable() {
                                public void run() {
                                    recive();
                                    //Aquí ejecutamos nuestras tareas costosas
                                }
                            }).start();
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {

                        if (pd!=null) {
                            pd.dismiss();
                        }
                        sentencia.setText(sentencia_leer);
                    }
                };
                task.execute((Void[]) null);

                break;
        }
    }

    private void Agregar() {
        String nombr=nombre.getText().toString().trim();
        String cod=sentencia.getText().toString();
        if(nombr.equals("")){
            new AlertDialog.Builder(this)
                    .setTitle("Faltan datos")
                    .setMessage("Compruebe que tenga un nombre y un codigo, recuerde no dejar campos vacios")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{
            if(!cod.equals("")){
                System.out.println(spinner_animales.getSelectedItemPosition());
                int temp=0;
                if(spinner_animales.getSelectedItem().equals("Rojo")){
                    temp=1;
                }
                if(spinner_animales.getSelectedItem().equals("Azul")){
                    temp=3;
                }
                if(spinner_animales.getSelectedItem().equals("Azul bajo")){
                    temp=2;
                }
                if(spinner_animales.getSelectedItem().equals("Naranja")){
                    temp=4;
                }
                if(spinner_animales.getSelectedItem().equals("Purpura")){
                    temp=5;
                }
                /////
                db.execSQL("INSERT INTO Boton (boton,nombre,control,color,sentencia,posicion) VALUES (" + null + ",'" + nombr + "'," + control + "," + temp + ",'" + cod + "',"+(posicion+1)+") ");
                finish();
            }
            else{
                new AlertDialog.Builder(this)
                        .setTitle("Faltan datos")
                        .setMessage("Compruebe que tenga un nombre y un codigo, recuerde no dejar campos vacios")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
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

                if(ver.equals("1")&&pet.equals("1")&&comport.equals("3")){
                    // System.out.println(data.split(" ")[1]);
                    String temp=data.substring(14);
                    System.out.println(temp);
                    sentencia_leer=temp;

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
                byte comport=3;
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

    @Override
    protected void onResume(){
        try{
            this.onRestart();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onResume();
    }
    @Override
    protected void onPause(){
        try{
            socket.close();

        }catch (Exception e){

        }
        super.onPause();
    }
    @Override
    protected void onDestroy(){
        try{
            socket.close();

        }catch (Exception e){

        }
        super.onDestroy();
    }
}

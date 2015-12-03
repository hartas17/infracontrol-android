package info.androidhive.materialdesign.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import info.androidhive.materialdesign.R;

public class Editar extends AppCompatActivity implements View.OnClickListener{
    static final int SERVER_PORT = 1555;

    public static String MESSAGE_STRING;

    public DatagramSocket socket;
    public static final String TAG="UDP";

    private Toolbar mToolbar;
    public String mac="";
    public int id=0;
    private SQLiteDatabase db;
    private ProgressDialog pd;
    EditText Code,Name;
    String sentencia_leida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getIntent().getExtras();

        mac=bundle.getString("mac");
        id=bundle.getInt("boton");

        setContentView(R.layout.activity_editar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar7);
        setSupportActionBar(mToolbar);
        setTitle("Editar boton");
        findViewById(R.id.Scann_code).setOnClickListener(this);
        findViewById(R.id.Enviar_edit).setOnClickListener(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Code=(EditText)findViewById(R.id.new_code);
        Name=(EditText)findViewById(R.id.new_name);


        Spinner spinner_animales = (Spinner) findViewById(R.id.spinner_color);
        ArrayAdapter spinner_adapter = ArrayAdapter.createFromResource( this, R.array.Colores , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_animales.setAdapter(spinner_adapter);


        Database_Infra usdbh =
                new Database_Infra( null, 1,this);
        db = usdbh.getWritableDatabase();
        llenar_elementos();
    }

    public void llenar_elementos(){
        Cursor c = db.rawQuery("SELECT nombre,sentencia FROM Boton WHERE boton = "+id+"", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                 Name.setText(c.getString(0));
                 Code.setText(c.getString(1));
            } while (c.moveToNext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar, menu);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Enviar_edit:
                Enviar();
                break;
            case R.id.Scann_code:

                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        pd = new ProgressDialog(Editar.this);
                        pd.setTitle("Buscando Infracontrol...");
                        pd.setMessage("Please wait.");
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
                        Code.setText("");
                        Code.setText(sentencia_leida);
                    }

                };
                task.execute((Void[]) null);
                break;
        }
    }
    private void Enviar(){
        Spinner color=(Spinner)findViewById(R.id.spinner_color);
        String name= Name.getText().toString().trim();
        String code= Code.getText().toString();
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
            col=4;
        }
        if(color.getSelectedItem().equals("Purpura")){
            col=5;
        }
        db.execSQL("UPDATE Boton SET nombre='" + name + "',color=" + col + ",sentencia='" + code + "' WHERE boton = " + id + "");
        finish();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
                    System.out.println(temp+"si llego del esp");
                    sentencia_leida=temp;
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
    @Override
    protected void onPause(){

        super.onPause();
    }
    @Override
    protected void onStop(){
        if(socket!=null){
            socket.close();
        }

        super.onStop();
    }
    @Override
    protected void onDestroy(){
        if(socket!=null){
            socket.close();
        }
        super.onDestroy();
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
}

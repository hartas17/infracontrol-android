package info.androidhive.materialdesign.activity;

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
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import info.androidhive.materialdesign.R;

public class Conf_Infra extends AppCompatActivity implements View.OnClickListener {

    private SQLiteDatabase db;

    private Toolbar mToolbar;
    private ProgressDialog pd;
    static final int SERVER_PORT = 1555;

    public DatagramSocket socket;
    public static final String TAG="UDP";
    public static String MESSAGE_STRING;

    public String mac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf__infra);
        mToolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(mToolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("Configurar Blasbot");
        Bundle bundle=getIntent().getExtras();
        mac=bundle.getString("mac");
        findViewById(R.id.bt_add_infra).setOnClickListener(this);
        MESSAGE_STRING="FFFFFFFFFFFF";
        Database_Infra usdbh =
                new Database_Infra( null, 1,this);
        db = usdbh.getWritableDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conf__infra, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
    public void onClick(View view) {
        view.setEnabled(false);
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(Conf_Infra.this);
                pd.setTitle("Buscando Blastbot...");
                pd.setMessage("Porfavor espere.");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... arg0) {
                try {

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
                    findViewById(R.id.bt_add_infra).setEnabled(true);
                }
                addDB_infra();
            }

        };
        task.execute((Void[]) null);
    }

    private void addDB_infra() {
        final TextView nom=(TextView)findViewById(R.id.editText_infra);
        if(!nom.equals("")){
            try{
            db.execSQL("INSERT INTO Infracontrol (infracontrol,nombre) VALUES ('" + mac + "','" + (nom.getText().toString().trim() + "") + "') ");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            catch (Exception e){
                new AlertDialog.Builder(this)
                        .setTitle("Duplicado Blastbot")
                        .setMessage("Ya se encuentra ese Blastbot en la aplicacion, ¿desea sobre escribir?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.execSQL("UPDATE Infracontrol SET nombre='"+ (nom.getText().toString().trim() + "")+"' WHERE infracontrol='"+mac+"' ");
                                Intent intent = new Intent(Conf_Infra.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                Intent intent = new Intent(Conf_Infra.this, MainActivity.class);
                                startActivity(intent);
                                finish();
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

                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    public void send(){
        sendBroadcast(MESSAGE_STRING);
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
                System.out.println(data);
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
    public void sendBroadcast(String messageStr) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            //Open a random port to send the package
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            byte[] data2 = messageStr.getBytes();
            byte[] sendData=new byte[data2.length+3];
            byte ver=1;
            byte tipo=0;
            byte comport=3;
            while(true){
                sendData[0]=ver;
                int cont=0;
                for(int i=0;i<data2.length;i++){
                    sendData[i+1]=data2[i];
                    cont++;
                }
                sendData[cont+1]=tipo;
                sendData[cont+2]=comport;
                break;
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
}

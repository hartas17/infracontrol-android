package info.androidhive.materialdesign.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ExitActivityTransition;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.melnykov.fab.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import cz.msebera.android.httpclient.Header;
import info.androidhive.materialdesign.R;

import static info.androidhive.materialdesign.R.drawable.color_selector;

public class ButtonsActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    //private ExitActivityTransition exitTransition;


    ////////////UDP
    static final int SERVER_PORT = 1555;

    public static String MESSAGE_STRING;

    public DatagramSocket socket;
    public static final String TAG="UDP";
    ////////////
    Hashtable<Integer, Buttons> content;
    Hashtable<Integer,String[]> elemetos_boton;
    private Vector<String> Nombre=new Vector<String> ();
    private Vector<Integer> Color=new Vector<Integer> ();
    private Vector<Integer> id_control=new Vector<Integer> ();
    private Vector<String> sentencia=new Vector<String> ();
    private Vector<Integer> id_boton=new Vector<Integer> ();
    private String sentencia_leer;
    private int Buttons = 0;
    private SQLiteDatabase db;
    public boolean editar=false;
    public boolean mover=false;
    public int pos_boton=0;
    int control;
    String mac;
    private ProgressDialog pd;
   RelativeLayout mainLayout;
    Context context;
    TextView texto_nobuttons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_buttons);
        setContentView(R.layout.activity_buttons);
        setTitle("Buttons");
        texto_nobuttons=(TextView)findViewById(R.id.label_texto);
        Database_Infra usdbh =
                new Database_Infra( null, 1,this);
        db = usdbh.getWritableDatabase();
        //exitTransition= ActivityTransition.with(getIntent()).to(findViewById(R.id.container_body_button)).start(savedInstanceState);
        mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=this;
        Bundle bundle=getIntent().getExtras();
        control= Integer.parseInt(bundle.getString("control"));
        mac=bundle.getString("mac");
        ObservableScrollView  scroll=(ObservableScrollView )findViewById(R.id.scroll);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.attachToScrollView(scroll, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                Log.d("ListViewFragment", "onScrollDown()");
            }

            @Override
            public void onScrollUp() {
                Log.d("ListViewFragment", "onScrollUp()");
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "FAB CLICKED", Toast.LENGTH_LONG).show();

                mover=false;
                action_add_button();
            }
        });

        try{
            generateView();
        }catch (Exception e){
            e.printStackTrace();

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buttons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_edit:
                if(mover==true){
                    mover=false;
                }else{
                editar=!editar;
                }
                generateView();
                return true;

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


    private void action_add_button() {
        Intent intent = new Intent(ButtonsActivity.this, Add_Boton.class);
        // ActivityTransitionLauncher.with(getActivity()).from(v).launch(intent);
        intent.putExtra("control", control);
        intent.putExtra("mac", mac);
        intent.putExtra("tamaño",content.size());
        System.out.println(content.size());
        startActivity(intent);
        ButtonsActivity.this.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

    }



    ///////////GENERACION BOTONES
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void generateView() {
        content=new Hashtable<Integer, Buttons>();
        elemetos_boton=new Hashtable<Integer,String[]>();
        Buttons = 0;
        Nombre=new Vector<String>();
        Color=new Vector<Integer>();
        sentencia=new Vector<String>();
        id_boton=new Vector<Integer>();


        Display display = getWindowManager().getDefaultDisplay();
        final int width = display.getWidth() / 6;

        Cursor c = db.rawQuery("SELECT boton,nombre,color,sentencia,posicion FROM Boton WHERE control=" + control + " ORDER BY posicion", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                id_boton.add(c.getInt(0));
                Nombre.add(c.getString(1));
                if(!editar){
                    if(!mover){
                     Color.add(c.getInt(2));
                    }
                    else{
                        Color.add(c.getInt(2));
                    }
                }
                else{
                    Color.add(c.getInt(2));
                }
                sentencia.add(c.getString(3));
                Buttons++;
            } while (c.moveToNext());
        }

        if(Buttons>0){
            try{

                texto_nobuttons.setVisibility(TextView.INVISIBLE);}catch (Exception e){
                e.printStackTrace();
            }
            for (int i = 0; i < Buttons; i++) {
                final Buttons Viewn = new Buttons(this);
                final int pos=i;
                Viewn.setOnClickListener(this);
                Viewn.setOnLongClickListener(new View.OnLongClickListener() {

                    public boolean onLongClick( View arg1) {
                        // TODO Auto-generated method stub

                        return true;
                    }
                });
                content.put(i + 1, Viewn);
                elemetos_boton.put(i+1,new String[]{control+"",Nombre.elementAt(i),sentencia.elementAt(i),id_boton.elementAt(i)+""});
            }
            mainLayout= (RelativeLayout) findViewById(R.id.container_body_button);


            ViewTreeObserver vto = mainLayout.getViewTreeObserver();


            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    final int oneUnitWidth = mainLayout.getMeasuredWidth() / 3;
                    System.out.println("Unit Width: " + oneUnitWidth);

                    for (int i = 0; i < Buttons; i++) {
                        if(!mover) {
                            if(!editar){
                                content.get(i + 1).setText(Nombre.elementAt(i));
                                content.get(i + 1).setIniciales(Nombre.elementAt(i).charAt(0) + "", oneUnitWidth / 2);
                                content.get(i + 1).setBackgroud(Color.elementAt(i));
                                content.get(i + 1).setId(i + 1);
                            }
                            else{
                                content.get(i + 1).setIcon(oneUnitWidth / 2);
                                content.get(i + 1).setText2(Nombre.elementAt(i),oneUnitWidth/2);
                                content.get(i + 1).setBackgroud(Color.elementAt(i));
                                content.get(i + 1).setId(i + 1);
                            }

                        }else{
                            content.get(i + 1).setText("");
                            content.get(i + 1).setIniciales(Nombre.elementAt(i).charAt(0) + "", oneUnitWidth / 2);
                            if(i+1==pos_boton){
                            content.get(i + 1).setBackgroud(8);}
                            else{
                                content.get(i + 1).setBackgroud(7);
                            }

                            content.get(i + 1).setId(i + 1);
                        }
                    }
                    //
                    Buttons StartLine = content.get(1);
                    Buttons LastOne = content.get(1);
                    Buttons MiddleOne = content.get(1);
                    Buttons FinishLine = content.get(1);
                    RelativeLayout.LayoutParams otherParams;
                    for (int i = 0; i < Buttons; i++) {
                        if (i > 2) {
                            switch (i % 3) {
                                case 1:
                                    otherParams = new RelativeLayout.LayoutParams(
                                            oneUnitWidth, oneUnitWidth * 10 / 12);
                                    otherParams.addRule(RelativeLayout.BELOW, MiddleOne.getId());
                                    otherParams.addRule(RelativeLayout.RIGHT_OF, LastOne.getId());
                                    otherParams.setMargins(1, 1, 0, 0);
                                    content.get(i + 1).setLayoutParams(otherParams);
                                    LastOne = content.get(i + 1);
                                    MiddleOne = content.get(i + 1);
                                    break;
                                case 2:
                                    otherParams = new RelativeLayout.LayoutParams(
                                            oneUnitWidth, oneUnitWidth * 10 / 12);
                                    otherParams.addRule(RelativeLayout.BELOW, FinishLine.getId());
                                    otherParams.addRule(RelativeLayout.RIGHT_OF, LastOne.getId());
                                    otherParams.setMargins(1, 1, 1, 0);
                                    content.get(i + 1).setLayoutParams(otherParams);
                                    LastOne = content.get(i + 1);
                                    FinishLine = content.get(i + 1);
                                    break;
                                case 0:

                                    //otherParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                    otherParams = new RelativeLayout.LayoutParams(
                                            oneUnitWidth, oneUnitWidth * 10 / 12);
                                    otherParams.addRule(RelativeLayout.BELOW, StartLine.getId());
                                    otherParams.setMargins(1, 1, 0, 0);
                                    content.get(i + 1).setLayoutParams(otherParams);
                                    LastOne = content.get(i + 1);
                                    StartLine = content.get(i + 1);
                                    break;
                            }

                        } else {
                            if (i == 0) {
                                otherParams = new RelativeLayout.LayoutParams(
                                        oneUnitWidth, oneUnitWidth * 10 / 12);
                                otherParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                otherParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                otherParams.setMargins(1, 1, 0, 0);
                                content.get(1).setLayoutParams(otherParams);

                            }
                            if (i == 1) {
                                otherParams = new RelativeLayout.LayoutParams(
                                        oneUnitWidth, oneUnitWidth * 10 / 12);
                                otherParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                otherParams.addRule(RelativeLayout.RIGHT_OF, LastOne.getId());
                                otherParams.setMargins(1, 1, 0, 0);
                                content.get(2).setLayoutParams(otherParams);
                                MiddleOne = content.get(i + 1);
                                LastOne = content.get(i + 1);
                            }
                            if (i == 2) {
                                otherParams = new RelativeLayout.LayoutParams(
                                        oneUnitWidth, oneUnitWidth * 10 / 12);
                                otherParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                otherParams.addRule(RelativeLayout.RIGHT_OF, LastOne.getId());
                                otherParams.setMargins(1, 1, 1, 0);
                                content.get(3).setLayoutParams(otherParams);
                                LastOne = content.get(i + 1);
                                FinishLine = content.get(i + 1);
                            }
                        }
                        /***************************************************************/
                        /**
                         * Delete tree observer
                         */


                    }
                    ViewTreeObserver obs = mainLayout.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                }


            });

            for (int i = 0; i < Buttons; i++) {
                View s=content.get(i+1);
                s.setClickable(true);
                mainLayout.addView(s);
            }
        }
        else{
            texto_nobuttons.setVisibility(TextView.VISIBLE);
        }
    }


    public void actualizar(){
        mainLayout.removeAllViews();
        generateView();
        this.onRestart();

    }
    public void editar_boton(int i){
        final Intent intent = new Intent(this, Editar.class);
        // ActivityTransitionLauncher.with(getActivity()).from(v).launch(intent);
        intent.putExtra("boton",Integer.parseInt(elemetos_boton.get(i+1)[3]));
        intent.putExtra("mac", mac);
        startActivity(intent);
        this.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }
    @Override
    protected void onResume(){
        try{
            this.onRestart();
            generateView();
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

    @Override
    public void onClick(View v) {
        if(!editar){
            if(!mover) {
                System.out.println(v.getId() + "aqui se hace");
                //element[]={mac,nombre_boton,sentencia}
                final String elemn[] = elemetos_boton.get(v.getId());
                System.out.println(elemn[0] + "   " + elemn[1] + "    " + elemn[2]);
                //v.getResources().getColor(R.color.red);
                v.getSolidColor();
                v.setClickable(true);
                //////////DB
                ////////////
                new Thread(new Runnable() {
                    public void run() {
                        send(elemn[2]);
                        //Aquí ejecutamos nuestras tareas costosas
                    }
                }).start();
                new Thread(new Runnable() {
                    public void run() {
                        recive();
                        //Aquí ejecutamos nuestras tareas costosas
                    }
                }).start();
                //v.setBackgroundColor(getResources().getColor(R.color.feedback));
            }
            else{
                if(v.getId()<pos_boton){
                    mover_derecha(v.getId());
                }
                else{
                    if(v.getId()>pos_boton){
                        mover_izquierda(v.getId());
                    }
                    else{

                        mainLayout.removeAllViews();
                        mover=false;
                        generateView();

                    }
                }
            }
        }
        else{
            final int pos=v.getId()-1;
            String[] opc = new String[]{"Editar", "Eliminar","Mover", "Cancelar"};

            Toast.makeText(context,
                    "pos: " + pos, Toast.LENGTH_SHORT).show();

            AlertDialog opciones = new AlertDialog.Builder(
                    context)
                    .setTitle("Opciones")
                    .setItems(opc,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int selected) {
                                    if (selected == 0) {
                                        editar=!editar;
                                        mover=false;
                                        editar_boton(pos);

                                        //acciones para editar
                                    } else if (selected == 1) {

                                        new AlertDialog.Builder(context)
                                                .setTitle("Eliminar boton")
                                                .setMessage("¿Está seguro que desea eliminar el boton?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        db.execSQL("DELETE FROM Boton WHERE nombre = '" + Nombre.elementAt(pos) + "'");
                                                        editar=!editar;
                                                        mover=false;
                                                        actualizar();
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

                                        //
                                    } else if (selected == 3) {
                                        editar=!editar;

                                        generateView();
                                        //acciones para eliminar
                                    } else if (selected == 2) {
                                        mainLayout.removeAllViews();
                                        editar=!editar;
                                        mover=true;
                                        pos_boton=pos+1;
                                        generateView();
                                        //acciones para eliminar
                                    }
                                }
                            }).create();
            opciones.show();

        }
    }

    private void mover_izquierda(int pos_final) {

        int id_boton=0;

        Cursor d = db.rawQuery("SELECT boton FROM Boton WHERE posicion="+pos_boton+"", null);

        //Nos aseguramos de que existe al menos un registro
        if (d.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                id_boton=d.getInt(0);
            } while (d.moveToNext());
        }
        d.close();

        Cursor c = db.rawQuery("SELECT boton,posicion FROM Boton WHERE (posicion>" + pos_boton + " AND posicion<="+pos_final+")", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                db.execSQL("UPDATE Boton SET posicion='"+(c.getInt(1)-1)+"' WHERE boton="+c.getInt(0)+" ");
            } while (c.moveToNext());
        }
        db.execSQL("UPDATE Boton SET posicion='"+(pos_final)+"' WHERE boton="+id_boton+" ");
        mover=false;
        editar=false;
        mainLayout.removeAllViews();
        generateView();

    }

    private void mover_derecha(int pos_final) {

        System.out.println(pos_final+"   "+pos_boton);
        int id_boton=0;
        Cursor d = db.rawQuery("SELECT boton FROM Boton WHERE posicion="+pos_boton+"", null);

        //Nos aseguramos de que existe al menos un registro
        if (d.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                id_boton=d.getInt(0);
            } while (d.moveToNext());
        }
        d.close();


        Cursor c = db.rawQuery("SELECT boton,posicion FROM Boton WHERE (posicion>=" + pos_final + " AND posicion<"+pos_boton+")", null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                db.execSQL("UPDATE Boton SET posicion='"+(c.getInt(1)+1)+"' WHERE boton="+c.getInt(0)+" ");
            } while (c.moveToNext());
        }
        db.execSQL("UPDATE Boton SET posicion='"+(pos_final)+"' WHERE boton="+id_boton+" ");
        mover=false;
        editar=false;
        mainLayout.removeAllViews();
        generateView();
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

                Log.i(TAG,"Ready to receive broadcast packets!");

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
}


package info.androidhive.materialdesign.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.balysv.materialripple.MaterialRippleLayout;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import info.androidhive.materialdesign.R;


/**
 * Created by Hartas on 5/10/15.
 */

public class ControlFragment extends ListFragment {
    private SQLiteDatabase db;
    public String Control_Selected="";
    public Hashtable<Integer,String[]> Control=new Hashtable<Integer,String[]>();
    CodeLearnAdapter chapterListAdapter;
    public Hashtable<String,String> infras=new Hashtable<String,String>();
    MainActivity s;
    public boolean editable=false;
    public ControlFragment(){

    }

    /////LLENADO DE CONTROLES
    public void llenar_controles(){
        try{
            Cursor c = db.rawQuery("SELECT control,color_icono,nombre,infracontrol FROM Control ", null);
            int cont=0;
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    Control.put(cont,new String[]{c.getInt(0)+"",c.getInt(1) + "", c.getString(2), c.getString(3)});
                    cont++;
                } while (c.moveToNext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            Cursor c = db.rawQuery("SELECT infracontrol,nombre FROM Infracontrol", null);

            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    //list.add(c.getString(0));
                    infras.put(c.getString(0),c.getString(1));
                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public void addDB(SQLiteDatabase db,MainActivity m) {
        this.db=db;
        llenar_controles();
        s=m;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        if(infras.size()>0){
            if(Control.size()>0){
                rootView= inflater.inflate(R.layout.fragment_control, container, false);
            }
            else{
                rootView= inflater.inflate(R.layout.fragment_nocontrol, container, false);
            }
        }
        else{
            rootView = inflater.inflate(R.layout.fragment_noinfra, container, false);
        }


        chapterListAdapter = new CodeLearnAdapter();
        setListAdapter(chapterListAdapter);

        ////////////////
        final ListView list = (ListView) rootView.findViewById(android.R.id.list);
        //rootView.findViewById(R.id.ripple).setOnItemClickListener(this);
        //registerForContextMenu(list);


        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub

                String[] opc = new String[]{"Editar", "Eliminar", "Cancelar"};

                Toast.makeText(getContext(),
                        "pos: " + pos, Toast.LENGTH_SHORT).show();

                AlertDialog opciones = new AlertDialog.Builder(
                        getContext())
                        .setTitle("Opciones")
                        .setItems(opc,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int selected) {
                                        if (selected == 0) {
                                            int control = Integer.parseInt(Control.get(pos)[0]);
                                            editar(control);
                                            //acciones para editar
                                        } else if (selected == 1) {
                                            new AlertDialog.Builder(getContext())
                                                    .setTitle("Eliminar control")
                                                    .setMessage("¿Está seguro que desea eliminar el control?")
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            int control = Integer.parseInt(Control.get(pos)[0]);
                                                            db.execSQL("DELETE FROM Control WHERE control = " + control + "");
                                                            db.execSQL("DELETE FROM Boton WHERE control = " + control + "");
                                                            s.displayView(0);
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


                                        } else if (selected == 2) {
                                            //acciones para eliminar
                                        }
                                    }
                                }).create();
                opciones.show();

                return true;
            }
        });

        list.setAdapter(getListAdapter());
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        fab.attachToListView(list, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                Log.d("ListViewFragment", "onScrollDown()");
            }

            @Override
            public void onScrollUp() {
                Log.d("ListViewFragment", "onScrollUp()");
            }
        }, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("ListViewFragment", "onScrollStateChanged()");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("ListViewFragment", "onScroll()");
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "FAB CLICKED", Toast.LENGTH_LONG).show();
                System.out.println("Si llego");
                agregarControl();
            }
        });
        ////////////////
        return rootView;
    }

    private void editar(final int control) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
        final Intent intent = new Intent(getActivity(), Editar_control.class);
        // ActivityTransitionLauncher.with(getActivity()).from(v).launch(intent);
        intent.putExtra("control",control);
        editable=true;
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);}
        }, 200);
    }

    public void agregarControl(){
        Vector<String> Nombre_infra=new Vector<String>();
        Vector<String> Infracontrol=new Vector<String>();
        int cont=0;
        try{
            Cursor c = db.rawQuery("SELECT infracontrol,nombre FROM Infracontrol", null);

            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    //list.add(c.getString(0));
                    Infracontrol.add(c.getString(0));
                    Nombre_infra.add(c.getString(1));
                    cont++;
                } while (c.moveToNext());
            }
        }catch (Exception e){
        }
        System.out.println(cont);
        String [] nombres=new String[cont];
        for(int i=0;i<cont;i++){
            nombres[i]=Nombre_infra.elementAt(i);
            System.out.println(Nombre_infra.elementAt(i));
        }
        new_Dialog_Control(nombres, Infracontrol);
    }
    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {

        final Intent intent2 = new Intent(getActivity(), ButtonsActivity.class);
       // ActivityTransitionLauncher.with(getActivity()).from(v).launch(intent);
        intent2.putExtra("control",Control.get(position)[0]);
        intent2.putExtra("mac",Control.get(position)[3]);
        startActivity(intent2);
        getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        onStop();



        // TODO implement some logic
    }

@Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public class CodeLearnAdapter extends BaseAdapter implements ListAdapter{

        List<codeLearnChapter> codeLearnChapterList = getDataForListView();
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return codeLearnChapterList.size();
        }

        @Override
        public codeLearnChapter getItem(int arg0) {
            // TODO Auto-generated method stub

            return codeLearnChapterList.get(arg0);
        }
        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }
        public List<codeLearnChapter> getDataForListView()
        {

            List<codeLearnChapter> codeLearnChaptersList = new ArrayList<codeLearnChapter>();
            int x=Control.size();

            for(int i=0;i<x;i++)
            {
                int temp=Integer.parseInt(Control.get(i)[1]);
                codeLearnChapter chapter = new codeLearnChapter();
                chapter.chapterName = Control.get(i)[2];
                chapter.chapterDescription = infras.get(Control.get(i)[3]);
                String iniciales[]=Control.get(i)[2].split(" ");
                if(iniciales.length>=2){
                    try{
                        chapter.Initial=(iniciales[0].charAt(0)+""+iniciales[1].charAt(0)+"").toUpperCase();
                    }catch (Exception e){
                        try{
                        chapter.Initial=(iniciales[0].charAt(0)+""+iniciales[1].charAt(0));}
                        catch (Exception f){
                            try{
                            chapter.Initial=(iniciales[0].charAt(0)+"");}catch (Exception r){}
                            f.printStackTrace();
                        }
                    }

                }
                else{
                    try{
                        chapter.Initial=(Control.get(i)[2].charAt(0)+""+Control.get(i)[2].charAt(1)).toUpperCase();
                    }catch (Exception e){
                        try{chapter.Initial=Control.get(i)[2].charAt(0)+""+Control.get(i)[2].charAt(1);}
                        catch (Exception f){

                        }
                    }

                }


                codeLearnChaptersList.add(chapter);
                switch (temp){
                    case 1:
                        chapter.image=R.drawable.img_red;
                        break;
                    case 2:
                        chapter.image=R.drawable.img;
                        break;
                    case 3:
                        chapter.image=R.drawable.img_yellow;
                        break;
                    case 4:
                        chapter.image=R.drawable.img_orange;
                        break;
                    case 5:
                        chapter.image=R.drawable.img_green;
                        break;
                }
            }

            return codeLearnChaptersList;

        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            if(arg1==null)
            {
                //LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LayoutInflater inflater = LayoutInflater.from(arg2.getContext());
                arg1 = (MaterialRippleLayout.on(inflater.inflate(R.layout.listitem_control, arg2, false)).rippleOverlay(true)
                        .rippleAlpha(0.2f)
                        .rippleColor(0xFF585858)
                        .rippleHover(true)

                        .create());
            }

            TextView chapterName = (TextView)arg1.findViewById(R.id.textView1);
            TextView chapterDesc = (TextView)arg1.findViewById(R.id.textView2);
            TextView Initial=(TextView)arg1.findViewById(R.id.Iniciales);
            ImageView icon=(ImageView)arg1.findViewById(R.id.imageView1);
            codeLearnChapter chapter = codeLearnChapterList.get(arg0);

            chapterName.setText(chapter.chapterName);
            Initial.setText(chapter.Initial);
            icon.setImageResource(chapter.image);
            chapterDesc.setText(chapter.chapterDescription);
            return arg1;
        }

    }
    public class codeLearnChapter {

        int image;
        String chapterName;
        String chapterDescription;
        String Initial;
    }

    //////////////////DIALOG CONTROL
    public void new_Dialog_Control(final String []nombres,final Vector<String> Infracont){


        LayoutInflater li = LayoutInflater.from(getActivity());

        View promptsView = li.inflate(R.layout.dialog_control, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setView(promptsView);

        // set dialog message

        alertDialogBuilder.setTitle("Agregar control");
        //alertDialogBuilder.setMessage("Seleccione para guardar");
        //alertDialogBuilder.setIcon(R.drawable.profile);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final Spinner mSpinner= (Spinner) promptsView
                .findViewById(R.id.dialog_spinner);
        final Button mButtonok = (Button) promptsView
                .findViewById(R.id.dialogOk);
        final Button mButtonNo=(Button)promptsView
                .findViewById(R.id.dialogNo);
        final TextView mNombre=(TextView)promptsView
                .findViewById(R.id.Nombre_Control);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, nombres);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(dataAdapter);
        mButtonok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Toast.makeText(getActivity(),
                            "OnClickListener : " +
                                    "\nSpinner 1 : " + String.valueOf(mSpinner.getSelectedItem()),
                            Toast.LENGTH_SHORT).show();
                    if(mNombre.getText().equals("")){
                        Toast.makeText(getActivity(),
                                "Escribe un Nombre",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        String s=Infracont.elementAt(mSpinner.getSelectedItemPosition());
                        int temp=(int)Math.floor(Math.random() * (1 - 6) + 6);
                        System.out.println(s+" asdasdsad "+mNombre.getText().toString());
                        if(s.equals("")||mNombre.getText().toString().equals("")){
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Elementos vacios")
                                    .setMessage("Asegurese de llenar todos los elementos, recuerde no dejar campos vacios.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                        else{
                            System.out.println("ENTRO AQUI");
                            db.execSQL("INSERT INTO Control (control,color_icono,nombre,infracontrol) VALUES (" + null + "," + temp + ",'" + mNombre.getText().toString().trim() + "','" + s + "') ");
                            borrar();
                            alertDialog.cancel();

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    alertDialog.cancel();
                }

            }

        });
        mButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.cancel();
            }

        });
        // reference UI elements from my_dialog_layout in similar fashion

        mSpinner.setOnItemSelectedListener(new Dialog_Control());

        // show it
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

    }
    public void borrar(){
        s.displayView(0);
        
    }
    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        if(editable){
            s.displayView(0);
            editable=false;
        }
        //
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
class Dialog_Control implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        // Do nothing.
    }
}

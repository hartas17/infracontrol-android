package info.androidhive.materialdesign.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import info.androidhive.materialdesign.R;

/**
 * Created by Ravi on 29/07/15.
 */
public class AddInfraFragment extends ListFragment {


    private SQLiteDatabase db;
    public String Control_Selected="";
    public Hashtable<Integer,String[]> Infracontrol=new Hashtable<Integer,String[]>();
    CodeLearnAdapter chapterListAdapter;
    MainActivity s;
    public AddInfraFragment() {
        // Required empty public constructor
    }

    /////LLENADO DE CONTROLES
    public void llenar_controles(){
        try{
            Cursor c = db.rawQuery("SELECT infracontrol,nombre FROM Infracontrol ", null);
            int cont=0;
            //Nos aseguramos de que existe al menos un registro
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    Infracontrol.put(cont,new String[]{c.getString(0),c.getString(1)});
                    cont++;
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
        System.out.println(Infracontrol.size());
        if(Infracontrol.size()>0){
            rootView = inflater.inflate(R.layout.fragment_control, container, false);
        }
        else{
            rootView = inflater.inflate(R.layout.fragment_noinfra, container, false);
        }


        chapterListAdapter = new CodeLearnAdapter();
        setListAdapter(chapterListAdapter);

        ////////////////
        ListView list = (ListView) rootView.findViewById(android.R.id.list);
        //registerForContextMenu(list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub

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
                final Intent intent = new Intent(getActivity(), New_infracontrol.class);
                // ActivityTransitionLauncher.with(getActivity()).from(v).launch(intent);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                //Toast.makeText(getApplicationContext(), "FAB CLICKED", Toast.LENGTH_LONG).show();
                System.out.println("Si llego");
                agregarInfracontrol();
            }
        });
        ////////////////
        return rootView;
    }

    private void agregarInfracontrol() {

    }

    private void editar(int control) {
        final Intent intent = new Intent(getActivity(), Editar_infracontrol.class);
        // ActivityTransitionLauncher.with(getActivity()).from(v).launch(intent);
        //intent.putExtra("control", control);
        startActivity(intent);
        //getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }


    @Override
    public void onListItemClick(ListView l, View v, final int pos, long id) {
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
                                    //int control=Integer.parseInt(Infracontrol.get(pos)[0]);
                                    //editar(control);
                                    //acciones para editar
                                    editar(pos);
                                } else if (selected == 1) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Eliminar Blastbot")
                                            .setMessage("¿Está seguro que desea eliminar el Blastbot?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String infracontrol=Infracontrol.get(pos)[0];
                                                    db.execSQL("DELETE FROM Infracontrol WHERE infracontrol = '"+infracontrol+"'");
                                                    Cursor c = db.rawQuery("SELECT control FROM Control WHERE infracontrol='" + infracontrol + "'", null);
                                                    s.displayView(1);
                                                    //Nos aseguramos de que existe al menos un registro
                                                    if (c.moveToFirst()) {
                                                        //Recorremos el cursor hasta que no haya más registros
                                                        do {
                                                            db.execSQL("DELETE FROM Boton WHERE control = "+c.getInt(0)+"");

                                                        } while (c.moveToNext());
                                                    }
                                                    db.execSQL("DELETE FROM Control WHERE infracontrol = '"+infracontrol+"'");
                                                    //db.execSQL("DELETE FROM Boton WHERE control = "+infracontrol+"");
                                                    s.displayView(2);
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
    public class CodeLearnAdapter extends BaseAdapter implements ListAdapter {

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
            int x=Infracontrol.size();


            for(int i=0;i<x;i++)
            {
                int temp=2;
                codeLearnChapter chapter = new codeLearnChapter();
                chapter.chapterName = Infracontrol.get(i)[1];
                chapter.chapterDescription = Infracontrol.get(i)[0];
                String iniciales[]=Infracontrol.get(i)[1].split(" ");
                if(iniciales.length>=2){
                    try{
                        chapter.Initial=(iniciales[0].charAt(0)+""+iniciales[1].charAt(0)+"").toUpperCase();
                    }catch (Exception e){
                        chapter.Initial=(iniciales[0].charAt(0)+""+iniciales[1].charAt(0));
                    }
                }
                else{
                    try{
                    chapter.Initial=(Infracontrol.get(i)[1].charAt(0)+""+Infracontrol.get(i)[1].charAt(1)).toUpperCase();}
                    catch (Exception e){
                        try{
                            chapter.Initial=(Infracontrol.get(i)[1].charAt(0)+""+Infracontrol.get(i)[1].charAt(1));

                        }catch (Exception f){
                            f.printStackTrace();
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
                        chapter.image=R.drawable.img_orange;
                        break;
                    case 4:
                        chapter.image=R.drawable.img_yellow;
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
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                arg1 = (MaterialRippleLayout.on(inflater.inflate(R.layout.listitem, arg2, false)).rippleOverlay(true)
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

    public void borrar(){
        s.displayView(2);
    }
    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        //s.displayView(2);
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
class Dialog_Control2 implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
        Toast.makeText(parent.getContext(), "Clicked : " +
                parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        // Do nothing.
    }

}

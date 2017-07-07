package com.javifocus2009gmail.ubication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jbenitez on 13/10/16.
 */

public class MyListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Ubication> ubicaciones;
    private String permission;
//    private static final String URL_DELETE_UBICATION = "http://mismaquinas.esy.es/delete_ubication_2.php";
    private static final String URL_DELETE_UBICATION = "http://mismaquinas.esy.es/delete_ubication.php";
    private AlertDialog.Builder dialog;
    private int i = 0;


    public MyListAdapter(Activity activity, ArrayList<Ubication> sitios, String c) {
        this.activity = activity;
        this.ubicaciones = sitios;
        this.permission = c;
    }


    @Override
    public int getCount() {
        return ubicaciones.size();
    }

    @Override
    public Object getItem(int position) {
        return ubicaciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View converView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        // Probar a no hacerlo final...
        final View view = inflater.inflate(R.layout.list_item_ubication, null, true);

        final Ubication ubication = ubicaciones.get(position);

        TextView tvUbicacion = (TextView) view.findViewById(R.id.tvUbication);
        tvUbicacion.setText(ubication.getUbication());

        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvDate.setText(ubication.getDate());

        String nUser = ubication.getUser().toUpperCase();
        TextView tvUser = (TextView) view.findViewById(R.id.tvUser);
        tvUser.setText(nUser);

        // Implementación por si se quiere eliminar una ubicación errónea
//        if(this.permission.equals("permission")) {
//            Button btnDel = (Button) view.findViewById(R.id.btnDelete);
//            btnDel.setVisibility(view.getVisibility());
//            btnDel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog = new AlertDialog.Builder(activity);
//                    dialog.setMessage("Seguro que quieres eliminar esta ubicación")
//                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    new WebService().execute("2", ubication.getId());
//                                    ubicaciones.remove(position);
//                                    notifyDataSetChanged();
//                                }
//                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            Toast.makeText(activity,
//                                    "No se ha podido eliminar la ubicación", Toast.LENGTH_LONG);
//                        }
//                    });
//
//                    dialog.show();
//                }
//            });
//        }

        // Implementación para mostrar ubicación geolocalizada
        Button bt = (Button) view.findViewById(R.id.btnUbication);
        //Terminar de implementar, para que aparezca botón dinámicamente si tiene latitud y longitud
//            ViewGroup linearLayout = (ViewGroup) activity.findViewById(R.id.activity_main);
//            Button bt = new Button(activity);
//            bt.setText("Ubicación");
//            bt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
//            linearLayout.addView(bt);

        if (ubication.getLat() != null && ubication.getLng() != null
                && ubication.getLat() != "" && ubication.getLng() != "") {
            bt.setVisibility(view.VISIBLE);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    System.out.println("Machine  : -- "+ubication.getNameMachine());
                    Intent intent = new Intent(activity, ShowUbicationMapActivity.class);
                    intent.putExtra("NAME_MACHINE", ubication.getNameMachine());
                    intent.putExtra("MACHINE", ubication.getMachine());
                    intent.putExtra("USER", ubication.getUser());
                    intent.putExtra("LATITUD", ubication.getLat());
                    intent.putExtra("LONGITUD", ubication.getLng());
                    activity.startActivityForResult(intent, 2);
                    activity.overridePendingTransition(R.anim.fade_in_2, R.anim.fade_out_2);
                }
            });
        }else{
            bt.setVisibility(view.GONE);
            bt.setVisibility(view.INVISIBLE);
        }

        if(position == 0){
            view.setBackgroundColor(Color.BLACK);
            tvUbicacion.setTextColor(Color.RED);
            tvDate.setTextColor(Color.WHITE);
        }

        return view;
    }

    class WebService extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {

            URL url;
            String resp = null;

            Toast.makeText(activity, params[0], Toast.LENGTH_SHORT).show();
            if(params[0] == "1"){

            }else if(params[0] == "2")
            try {
                url = new URL(URL_DELETE_UBICATION);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("ubication", params[1]);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();

                int response = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if(response == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null){
                        result.append(line);
                    }

                    JSONObject responseJSON = new JSONObject(result.toString());

                    String resultJSON = responseJSON.getString("estado");

                    if(resultJSON == "1"){
                        resp = "Ubicación eliminada";
                    }else{
                        resp = "No se ha podido eliminar la ubicación";
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return resp;
        }
    }
}

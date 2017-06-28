package com.javifocus2009gmail.ubication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class CreateUbicationMapActivity extends AppCompatActivity
        implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

    private GetWebService threadConec;
    private static final String URL_ROOT = "http://mismaquinas.esy.es";
//    private static final String URL_ROOT = "http://192.168.100.2";
    private static final String URL_SET_UBICATION = URL_ROOT + "/set_ubication_machine_2.php";
//    private static final String URL_SET_UBICATION = URL_ROOT + "/set_ubication_machine.php";
    private AlertDialog.Builder dialog;

    private GoogleMap mMap;
    double lat, lon;
    private TextView mTapTextView;
    private Button btnSave;
    private Button btnCancel;

    private static final LatLng GRUAS_SEBASTIAN = new LatLng(38.36000676549384, -4.8410747945308685);
    private static LatLng yourUbication;
    private double latitude = 38.36000676549384;
    private double longitude = -4.8410747945308685;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_create_ubication);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapCreate);
        btnSave = (Button) findViewById(R.id.btnCreate);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("¿Estás seguro que esta es la ubicación?");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateUbicationMapActivity.this, SecondActivity.class);
                i.putExtra("MACHINE", String.valueOf(getIntent().getExtras().getInt("MACHINE")));
                setResult(RESULT_OK, i);
                finish();
            }
        });
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Líneas necesarias para obtener la localización del usuario
//        GPSTracker gps = new GPSTracker(this);
//        if(gps.getLatitude() != 0.0 && gps.getLongitude() != 0.0){
//            latitude = gps.getLatitude();
//            longitude = gps.getLongitude();
//        }

        yourUbication = new LatLng(latitude, longitude);
        mMap = googleMap;
        mMap.setMaxZoomPreference(18.0f);
        mMap.setMinZoomPreference(10.0f);
        setMarkMap(yourUbication);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
    }

    private void setMarkMap(LatLng position) {
        latitude = position.latitude;
        longitude = position.longitude;
        mMap.addMarker(new MarkerOptions().position(position)
                .title("Estás aquí"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(yourUbication));
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onMapClick(LatLng point) {
        mMap.clear();
        setMarkMap(point);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    private void showAlertDialog(final String message) {
        dialog = new AlertDialog.Builder(CreateUbicationMapActivity.this);
        dialog.setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        threadConec = new GetWebService();
                        Ubication ub = new Ubication();
                        ub.setUbication(getIntent().getExtras().getString("UBICATION"));
                        ub.setMachine(getIntent().getExtras().getInt("MACHINE"));
                        ub.setUser(getIntent().getExtras().getString("USER"));
                        ub.setLat(String.valueOf(latitude));
                        ub.setLng(String.valueOf(longitude));
                        threadConec.execute(URL_SET_UBICATION, ub.getUbication(), String.valueOf(ub.getMachine()), ub.getUser(), ub.getLat(), ub.getLng());
                        Intent i = new Intent(CreateUbicationMapActivity.this, SecondActivity.class);
                        i.putExtra("MACHINE", String.valueOf(ub.getMachine()));
                        setResult(RESULT_OK, i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        dialog.show();
    }

    class GetWebService extends AsyncTask<String, Integer, String> {
        ProgressDialog pDialog = new ProgressDialog(CreateUbicationMapActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateUbicationMapActivity.this);
            pDialog.setMessage("Espere por favor...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String string = params[0];
            String resp = null;
            URL url = null;

            // POST - Insercción de una nueva ubicación para un máquina
                try {

                    url = new URL(string);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.connect();

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("ubication", params[1]);
                    jsonParam.put("idMachine", params[2]);
                    jsonParam.put("idUser", params[3]);
                    jsonParam.put("latitud", params[4]);
                    jsonParam.put("longitud", params[5]);

                    System.out.println("jsonParam: -- "+jsonParam);

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();

                    int response = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    System.out.println("Ubicacion create: -- : " +response);
//                    System.out.println("Ubicacion create: -- "+ub.getUbication());
//                    System.out.println("Ubicacion create: -- "+ub.getMachine());
//                    System.out.println("Ubicacion create: -- "+ub.getUser());
//                    System.out.println("Ubicacion create: -- "+ub.getLat());
//                    System.out.println("Ubicacion create: -- "+ub.getLng());

                    if (response == HttpURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }

                        JSONObject responseJSON = new JSONObject(result.toString());

                        String resultJSON = responseJSON.getString("estado");

                        if (resultJSON == "1") {
                            resp = "Ubicación añadida correctamente";
                        } else {
                            resp = "No se ha podido añadir la ubicación";
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String c = "";

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

        }
    }
}

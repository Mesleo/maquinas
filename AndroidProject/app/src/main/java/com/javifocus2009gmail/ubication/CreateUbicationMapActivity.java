package com.javifocus2009gmail.ubication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = CreateUbicationMapActivity.class.getSimpleName();

    private GetWebService threadConec;
    private static final String URL_ROOT = "http://mismaquinas.esy.es";
//    private static final String URL_ROOT = "http://192.168.100.2";
    private static final String URL_SET_UBICATION = URL_ROOT + "/set_ubication_machine_2.php";
//    private static final String URL_SET_UBICATION = URL_ROOT + "/set_ubication_machine.php";

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GoogleApiClient mGoogleApiClient;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private Button btnSave;
    private Button btnCancel;
    private AlertDialog.Builder dialog;

    private static final LatLng GRUAS_SEBASTIAN = new LatLng(38.36000676549384, -4.8410747945308685);
    private static LatLng yourUbication;
    private double latitude = 38.36000676549384;
    private double longitude = -4.8410747945308685;
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_create_ubication);
        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
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
        mGoogleApiClient.connect();
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

        mMap = googleMap;
        mMap.setMaxZoomPreference(18.0f);
        mMap.setMinZoomPreference(6.0f);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        setMarkMap(yourUbication);
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            yourUbication = new LatLng(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourUbication, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void setMarkMap(LatLng position) {
        latitude = position.latitude;
        longitude = position.longitude;
        yourUbication = new LatLng(latitude, longitude);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapCreate);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
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

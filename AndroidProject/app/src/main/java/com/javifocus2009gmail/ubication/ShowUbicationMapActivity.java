package com.javifocus2009gmail.ubication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowUbicationMapActivity extends AppCompatActivity
        implements GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

    private static final String URL_ROOT = "http://mismaquinas.esy.es";
//    private static final String URL_ROOT = "http://192.168.100.2";
//    private static final String URL_GET_MACHINE = URL_ROOT + "/get_machine_2.php";
    private static final String URL_GET_MACHINE = URL_ROOT + "/get_machine.php";
    private AlertDialog.Builder dialog;

    private GoogleMap mMap;
    private Button btnBack;
    private Ubication ub;
    private String mach = "";

    private static final LatLng GRUAS_SEBASTIAN = new LatLng(38.36000676549384, -4.8410747945308685);
    private static LatLng yourUbication;
    private double latitude = 38.36000676549384;
    private double longitude = -4.8410747945308685;

    private CameraPosition cameraUbicationMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_ubication);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        btnBack = (Button) findViewById(R.id.btnBack);
        ub = new Ubication();
        ub.setUbication("");
        ub.setMachine(getIntent().getExtras().getInt("MACHINE"));
        ub.setUser(getIntent().getExtras().getString("USER"));
        ub.setLat(getIntent().getExtras().getString("LATITUD"));
        ub.setLng(getIntent().getExtras().getString("LONGITUD"));
        latitude = Double.parseDouble(ub.getLat());
        longitude = Double.parseDouble(ub.getLng());
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mach = getIntent().getExtras().getString("NAME_MACHINE");
        }else{
            Toast.makeText(ShowUbicationMapActivity.this, "Error de conexión, comprueba si tienes acceso" +
                    " a Internet", Toast.LENGTH_LONG).show();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowUbicationMapActivity.this, SecondActivity.class);
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

        mMap = googleMap;
        // Líneas necesarias para obtener la localización del usuario
//        GPSTracker gps = new GPSTracker(this);
//        if(gps.getLatitude() != 0.0 && gps.getLongitude() != 0.0){
//            latitude = gps.getLatitude();
//            longitude = gps.getLongitude();
//        }

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);

        yourUbication = new LatLng(latitude, longitude);
        cameraUbicationMachine = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
                .zoom(15.5f)
                .bearing(0)
                .tilt(25)
                .build();

        setMarkMap(yourUbication);

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourUbication, 16));
    }

    private void setMarkMap(LatLng position) {
        latitude = position.latitude;
        longitude = position.longitude;
        mMap.addMarker(new MarkerOptions().position(position)
                .title(mach));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(yourUbication));
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }
}

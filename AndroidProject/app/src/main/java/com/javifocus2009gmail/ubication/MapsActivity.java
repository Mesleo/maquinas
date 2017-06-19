package com.javifocus2009gmail.ubication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity
        implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView mTapTextView;
    private Button btnSave;

    private static final LatLng GRUAS_SEBASTIAN = new LatLng(38.36000676549384, -4.8410747945308685);
    private static LatLng yourUbication = new LatLng(38.38043008500165, -4.849629700183868);

    private Marker mGruasSebastian;
    private Marker mYourUbication;

    LocationManager locationManager;
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mTapTextView = (TextView) findViewById(R.id.tap_text);
        btnSave = (Button) findViewById(R.id.btnSave);
//        mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);

        setUpMapIfNeeded();
//
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Ubicación guardada");
                Toast.makeText(MapsActivity.this, "Ubicación guardada", Toast.LENGTH_LONG);
            }
        });
    }

    private void setUpMap() {
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        mTapTextView.setText("" + point);
        mMap.clear();

        yourUbication = new LatLng(point.latitude, point.longitude);
        mYourUbication = mMap.addMarker(new MarkerOptions()
                .position(yourUbication)
                .title("Aquí está la máquina"));
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        mTapTextView.setText("" + point);
    }

    private void setUpMapIfNeeded() {
        // Hacer una comprobación nula para confirmar que ya no hemos instanciado el mapa.
        if (mMap == null) {
            // Intenta obtener el mapa desde el SupportMapFragment.
            mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
//            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(MapsActivity.this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            // Comprueba si hemos tenido éxito en la obtención del mapa.
            if (mMap != null) {

                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub

                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    }
                });
                setUpMap();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(yourUbication)
                .title("Tu ubicación"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(yourUbication));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(yourUbication, 17);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}

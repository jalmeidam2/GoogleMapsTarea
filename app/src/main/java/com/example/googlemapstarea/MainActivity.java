package com.example.googlemapstarea;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {
    private GoogleMap mMap;
    CheckBox checkBox;
    SeekBar rojo1, verde1, azul1;
    Button btDibujar, btborrar, btguardar;

    Polygon poligono = null;
    List<LatLng> ListlatLng = new ArrayList<>();
    List<Marker> Listmarker = new ArrayList<>();
    int amarillo = 0, negro = 0, azul = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ASIGNAMOS VARIABLES
        checkBox = findViewById(R.id.check_box);
        rojo1 = findViewById(R.id.seek_red);
        verde1 = findViewById(R.id.seek_green);
        azul1 = findViewById(R.id.seek_blue);
        btDibujar = findViewById(R.id.bt_draw);
        btborrar = findViewById(R.id.bt_clear);
        btguardar = findViewById(R.id.bt_cerrar);
        //INICIALIZAR SUPPORTMAP
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    if (poligono == null)
                        return;
                    poligono.setFillColor(Color.rgb(amarillo, negro, azul));
                } else {
                    poligono.setFillColor(Color.TRANSPARENT);
                }
            }
        });
        btDibujar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poligono != null)
                    poligono.remove();
                PolygonOptions polygonOptions = new PolygonOptions().addAll(ListlatLng).clickable(true);
                poligono = mMap.addPolygon(polygonOptions);
                poligono.setStrokeColor(Color.rgb(amarillo, negro, azul));
                if (checkBox.isChecked()) {
                    poligono.setFillColor(Color.rgb(amarillo, negro, azul));
                }
            }
        });
        btborrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (poligono != null) {
                    poligono.remove();
                    for (Marker marker : Listmarker)
                        marker.remove();
                    ListlatLng.clear();
                    Listmarker.clear();
                    checkBox.setChecked(false);
                    rojo1.setProgress(0);
                    verde1.setProgress(0);
                    azul1.setProgress(0);
                    /*LinearLayout my_layout = findViewById(R.id.Ocultar);
                    my_layout.setVisibility(View.GONE);*/
                    //mMap.clear();
                }
            }
        });
        rojo1.setOnSeekBarChangeListener(this);
        verde1.setOnSeekBarChangeListener(this);
        azul1.setOnSeekBarChangeListener(this);
        btguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout my_layout = findViewById(R.id.Ocultar);
                my_layout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Marcadores (Marker) sobre el Mapa
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                //mMap.clear();
                mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,5),5000,null);
                //Move Camera
               CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(5).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //Movimiento sobre Mapa: Cámaras
                Projection proj = mMap.getProjection();
                Point coord = proj.toScreenLocation(latLng);
                Toast.makeText(MainActivity.this, "Click\n" +
                                "Lat: " + latLng.latitude + "\n" +
                                "Lng: " + latLng.longitude + "\n" +
                                "X: " + coord.x + " - Y: " + coord.y,
                        Toast.LENGTH_SHORT).show();
                //poligono
                MarkerOptions markerOptions1 = new MarkerOptions().position(latLng);
                Marker marker = mMap.addMarker(markerOptions);
                ListlatLng.add(latLng);
                Listmarker.add(marker);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opciones_map, menu);
        return true;
    }

    // LinearLayout l = findViewById(R.id.Ocultar);
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.Puntos:
                LinearLayout my_layout = findViewById(R.id.Ocultar);
                my_layout.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seek_red:
                amarillo = i;
                break;
            case R.id.seek_green:
                negro = i;
                break;
            case R.id.seek_blue:
                azul = i;
                break;
        }
        if (poligono != null) {
            poligono.setStrokeColor(Color.rgb(amarillo, negro, azul));
            if (checkBox.isChecked()) {
                poligono.setFillColor(Color.rgb(amarillo, negro, azul));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
package cl.rescuecar.www.rescuecarclient;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends ConexionMysqlHelper implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    Location location;
    double lat = 0.0;
    double lng = 0.0;
    Marker marcador, m1, m2, m3, m4, m5;
    android.app.AlertDialog alert = null;
    String ciudad, direc, id_mob,json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    ImageView gps, internet, setting;
    Button btAlerta, btCancelar;
    EditText Direccion;
    int prop=0,servicioGPS=0, servicioInt=0;
    String rut, div, nom, ape, dir;
    String grut, gdiv, gnombre, gapellido,gtelefono, gemail, gveh;
    TextView vehiculo;
    varGlob varglob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //CapturaIdDispositivo
        id_mob = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        new CargarDatos().execute("http://www.webinfo.cl/soshelp/?id_mob="+id_mob);
        //Termino carga de ID dispositivo

        //Creacion de boton y estado
        gps = (ImageView) findViewById(R.id.imGps);
        internet= (ImageView) findViewById(R.id.imInt);
        setting= (ImageView) findViewById(R.id.imSet);
        Direccion = (EditText) findViewById(R.id.etDireccion);
        btAlerta = (Button) findViewById(R.id.btAlerta);
        btCancelar = (Button) findViewById(R.id.btCancelar);
        vehiculo = (TextView) findViewById(R.id.tvvehiculo);
        btAlerta.setEnabled(false);
        btAlerta.setBackgroundColor(Color.LTGRAY);
        Direccion.setEnabled(false);

        //Escucha servicios
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
          escuchaServicios();
        //fin escucha servicios

        //recuperadatos
        recuperarDatos();
        //fin recupera datos

        setting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), detalleInfo.class);
                s.putExtra("rut",grut);
                s.putExtra("div",gdiv);
                s.putExtra("nom",gnombre);
                s.putExtra("ape",gapellido);
                s.putExtra("tel",gtelefono);
                s.putExtra("ema",gemail);
                startActivity(s);
            }
        });


        btAlerta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(Direccion.getText()!=null && Direccion.getText().toString() != ""){

                /****** Ingreso segunda pagina ******/
                Intent a = new Intent(getApplicationContext(), SelEmergencia.class);
                    a.putExtra("rut",grut);
                    a.putExtra("div",gdiv);
                    a.putExtra("nom",gnombre);
                    a.putExtra("ape",gapellido);
                    a.putExtra("tel",gtelefono);
                    a.putExtra("ema",gemail);
                    a.putExtra("dir",dir);
                    a.putExtra("lat",lat);
                    a.putExtra("lng",lng);
                startActivity(a);
            }else{
                    Toast.makeText(getApplicationContext(),"Debe especificar una dirección",Toast.LENGTH_SHORT).show();

                }
            }
        });
        agregaGruas();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();
        // Controles UI
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            }
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void recuperarDatos() {

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            grut = (String) b.get("rut");
            gdiv = (String) b.get("div");
            gnombre = (String) b.get("nom");
            gapellido = (String) b.get("ape");
            gtelefono = (String) b.get("tel");
            gemail = (String) b.get("ema");
            gveh = (String) b.get("veh");
            vehiculo.setText(gveh);
        }
    }

    public void agregarMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 15);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .visible(false)
                .title("Mi Ubicación"));
        mMap.animateCamera(miUbicacion);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregarMarcador(lat, lng);
            if (lat!=0.0 && lng!=0.0){
                try{
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation(lat,lng,5);
                    if (!list.isEmpty()){
                        android.location.Address direccion = list.get(0);
                        ciudad = direccion.getLocality();
                        direc = direccion.getAddressLine(0);
                        dir = direc+","+ciudad;
                        ciudad=ciudad.replaceAll(" ", "%20");
                        Direccion.setText(dir);

                    }
                }catch (IOException e){


                }
            }

        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,0,locListener);
    }

    private void AlertNoGps() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();


        new CargarDatos().execute("http://www.webinfo.cl/soshelp/?id_mob="+id_mob);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, locListener);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500, 0, locListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locListener);
    }

    public void agregaGruas() {new BackgroundTask().execute();}
    class BackgroundTask extends AsyncTask<Void,Void,String>
    {
        String json_url;
        @Override
        protected void onPreExecute() {
            json_url = "http://www.webinfo.cl/soshelp/cons_driv.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(JSON_STRING+"\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            JSON_STRING = result;
            presentar();

        }
    }
    public void presentar() {
        if (JSON_STRING != null) {
            json_string = JSON_STRING;
            try {
                jsonObject = new JSONObject(json_string);
                jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                String mob, ciu;
                Double lati, longi;
                while (count<6) {
                    JSONObject JO = jsonArray.getJSONObject(count);
                    mob = JO.getString("id_mob");
                    lati = Double.parseDouble(JO.getString("lat_driv"));
                    longi = Double.parseDouble(JO.getString("lng_driv"));
                    switch(count){
                        case 0:
                            if (lati!=0.0 && longi!=0.0){
                                if (m1 != null) m1.remove();
                                LatLng ub1 = new LatLng(lati, longi);
                                m1 = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.mk_grua))
                                        .position(ub1)
                                        .title(mob));
                            }
                            break;
                        case 1:
                            if (lati!=0.0 && longi!=0.0){
                                if (m2 != null) m2.remove();
                                LatLng ub2 = new LatLng(lati, longi);
                                m2 = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.mk_grua))
                                        .position(ub2)
                                        .title(mob));
                            }
                            break;
                        case 2:
                            if (lati!=0.0 && longi!=0.0){
                                if (m3 != null) m3.remove();
                                LatLng ub3 = new LatLng(lati, longi);
                                m3 = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.mk_grua))
                                        .position(ub3)
                                        .title(mob));
                            }
                            break;
                        case 3:
                            if (lati!=0.0 && longi!=0.0){
                                LatLng ub4 = new LatLng(lati, longi);
                                if (m4 != null) m4.remove();
                                m4 = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.mk_grua))
                                        .position(ub4)
                                        .title(mob));
                            }
                            break;
                        case 4:
                            if (lati!=0.0 && longi!=0.0){
                                if (m5 != null) m5.remove();
                                LatLng ub5 = new LatLng(lati, longi);
                                m5 = mMap.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.mk_grua))
                                        .position(ub5)
                                        .title(mob));
                            }
                            break;
                    }
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        agregaGruas();
                    }
                });
            }
        },5000);
    }

    public void escuchaServicios(){

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gps.setImageResource(R.drawable.gps_no);
            servicioGPS=0;
            Toast.makeText(getApplicationContext(),"¡¡ Tu teléfono no tiene señal GPS!!", Toast.LENGTH_SHORT).show();
            AlertNoGps();
        }else{
            gps.setImageResource(R.drawable.gps_si);
            servicioGPS=1;
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null){
            internet.setImageResource(R.drawable.int_si);
            servicioInt=1;

        }else{
            internet.setImageResource(R.drawable.int_no);
            Toast.makeText(getApplicationContext(),"¡¡ Tu teléfono no esta conectado a internet!!", Toast.LENGTH_SHORT).show();
            servicioInt=0;
        }

        if (servicioInt==1 && servicioGPS==1){
            btAlerta.setEnabled(true);
            btAlerta.setBackgroundColor(Color.parseColor("#062B51"));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        escuchaServicios();
                    }
                });
            }
        },50000);

    }


}







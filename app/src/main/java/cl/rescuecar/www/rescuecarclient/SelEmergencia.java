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
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelEmergencia extends ConexionMysqlHelper  {
    LocationManager locationManager;
    int prop=0,servicioGPS=0, servicioInt=0;
    android.app.AlertDialog alert = null;
    LinearLayout menuGrua, menuTransporte, menuEmergencia, menuCombustible, menuNeumatico, menu2Grua, menuMecanico ;
    ImageView gps, internet, grua, neumatico, mecanico, policia, ambulancia, bomberos, moto, auto, camioneta, camion, transporte, combustible;
    ImageView gmoto,gauto,gcamioneta,gcamion, gVehiculo;
    String rut, div, nom, ape, dir, fallaGrua, fallaMec, fallaNeu, fallaCom, fallaEmer, fallaTra;
    EditText direccion, descMecanico, descNeumatico, descCombustible, descEmergencia, descTransporte, descPolicia, descAmbulancia, descBombero, descGmoto, descGauto, descGcamioneta, descGcamion;
    int actGrua=0, actMec=0, actNeu=0, actCom=0, actPol=0, actBom=0, actAmb=0, actTra=0, actGmoto=0, actGauto=0, actGcamioneta=0, actGcamion=0, actEmer=0;
    TextView descGrua;
    Button btAlerta, btCancelar;
    double lat = 0.0;
    double lng = 0.0;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String grut, gdiv, gnombre, gapellido, gtelefono, gemail;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_emergencia);

        direccion = (EditText) findViewById(R.id.etDireccion);
        direccion.setEnabled(false);
        descGrua = (TextView) findViewById(R.id.tvDescGrua);
        descMecanico = (EditText) findViewById(R.id.etMecanico);
        descNeumatico = (EditText) findViewById(R.id.etNeumatico);
        descCombustible = (EditText) findViewById(R.id.etCombustible);
        descEmergencia = (EditText) findViewById(R.id.etEmergencia);
        descTransporte = (EditText) findViewById(R.id.etTransporte);
        internet= (ImageView) findViewById(R.id.imInt);

        escuchaServicios();
        recuperarDatos();

        //Comienzo de linearLayout
        menuGrua = (LinearLayout) findViewById(R.id.lgrua);
        menu2Grua = (LinearLayout) findViewById(R.id.llGrua);
        menuTransporte = (LinearLayout) findViewById(R.id.lTransporte);
        menuEmergencia = (LinearLayout) findViewById(R.id.lEmergencia);
        menuCombustible = (LinearLayout) findViewById(R.id.lCombustible);
        menuNeumatico = (LinearLayout) findViewById(R.id.lNeumatico);
        menuMecanico = (LinearLayout) findViewById(R.id.lMecanico);
        //fin LinearLayout

        //Comienzo de botones
        grua = (ImageView) findViewById(R.id.ic_grua);
        neumatico = (ImageView) findViewById(R.id.ic_neu);
        mecanico = (ImageView) findViewById(R.id.ic_mec);
        policia = (ImageView) findViewById(R.id.ic_pol);
        ambulancia = (ImageView) findViewById(R.id.ic_amb);
        bomberos = (ImageView) findViewById(R.id.ic_bom);
        combustible = (ImageView) findViewById(R.id.ic_com);
        transporte = (ImageView) findViewById(R.id.ic_tran);
        gmoto = (ImageView) findViewById(R.id.ic_gmoto);
        gauto = (ImageView) findViewById(R.id.ic_gauto);
        gcamioneta = (ImageView) findViewById(R.id.ic_gcamioneta);
        gcamion = (ImageView) findViewById(R.id.ic_gcamion);
        btAlerta = (Button) findViewById(R.id.btAlerta);
        btCancelar = (Button) findViewById(R.id.btCancelar);
        btAlerta.setEnabled(false);
        btAlerta.setBackgroundColor(Color.LTGRAY);

        //fin botones
        //Listener de los botones

        grua.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actGrua==0){
                    grua.setImageResource(R.drawable.grup);
                    actGrua=1;
                    Util.expand(menuGrua, 500);
                    Util.expand(menu2Grua, 500);
                    bloquearDemasServicios("grua");
                    desactivarBoton();
                    habilitaMenuGrua();
                    descGrua.setText("Solicitud de grua para..");
                }else{
                    grua.setImageResource(R.mipmap.ic_gru);
                    actGrua=0;
                    Util.collapse(menuGrua, 500);
                    Util.collapse(menu2Grua, 500);
                    deshabilitarMenuGrua();
                    descGrua.setText("");
                }
            }
        });

        mecanico.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actMec==0){
                    mecanico.setImageResource(R.drawable.mecp);
                    actMec=1;
                    bloquearDemasServicios("mecanico");
                    Util.expand(menuMecanico, 500);
                    descMecanico.requestFocus();
                    activarBoton();
                }else{
                    mecanico.setImageResource(R.mipmap.ic_mec);
                    actMec=0;
                    Util.collapse(menuMecanico, 500);
                    desactivarBoton();
                }
            }
        });

        neumatico.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actNeu==0){
                    neumatico.setImageResource(R.drawable.neup);
                    bloquearDemasServicios("neumatico");
                    actNeu=1;
                    Util.expand(menuNeumatico, 500);
                    activarBoton();
                }else{
                    neumatico.setImageResource(R.mipmap.ic_neu);
                    actNeu=0;
                    Util.collapse(menuNeumatico, 500);
                    desactivarBoton();
                }
            }
        });


        combustible.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actCom==0){
                    combustible.setImageResource(R.drawable.comp);
                    actCom=1;
                    bloquearDemasServicios("combustible");
                    Util.expand(menuCombustible, 500);
                    activarBoton();
                }else{
                    combustible.setImageResource(R.mipmap.ic_com);
                    actCom=0;
                    Util.collapse(menuCombustible, 500);
                    desactivarBoton();
                }
            }
        });

        policia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actPol==0){
                    policia.setImageResource(R.drawable.polp); actPol=1;
                    activarMenuEmer();

                }else{
                    policia.setImageResource(R.mipmap.ic_pol); actPol=0;
                    desactivarMenuEmer();
                }
            }
        });

        bomberos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actBom==0){
                    bomberos.setImageResource(R.drawable.bomp); actBom=1;
                    activarMenuEmer();
                }else{
                    bomberos.setImageResource(R.mipmap.ic_bom); actBom=0;
                    desactivarMenuEmer();
                    desactivarBoton();
                }
            }
        });

        ambulancia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actAmb==0){
                    ambulancia.setImageResource(R.drawable.ambp); actAmb=1;
                    activarMenuEmer();
                }else{
                    ambulancia.setImageResource(R.mipmap.ic_amb); actAmb=0;
                    desactivarMenuEmer();
                }
            }
        });

        transporte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actTra==0){
                    transporte.setImageResource(R.drawable.tranp); actTra=1;
                    Util.expand(menuTransporte, 500);
                    activarBoton();
                }else{
                    transporte.setImageResource(R.mipmap.ic_tra); actTra=0;
                    Util.collapse(menuTransporte, 500);
                    desactivarBoton();
                }
            }
        });

        gmoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actGmoto==0){
                    gmoto.setImageResource(R.drawable.gmotop);
                    actGmoto=1;
                    actGauto=0;actGcamioneta=0;actGcamion=0;
                    gauto.setImageResource(R.mipmap.ic_gauto);
                    gcamioneta.setImageResource(R.mipmap.ic_gcamioneta);
                    gcamion.setImageResource(R.mipmap.ic_camion);
                    descGrua.setText("Solicitud de grua para motocicleta");
                    activarBoton();
                }else{
                    gmoto.setImageResource(R.mipmap.ic_gmoto);
                    actGmoto=0;
                    descGrua.setText("Solicitud de grua para..");
                    desactivarBoton();
                }
            }
        });

        gauto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actGauto==0){
                    gauto.setImageResource(R.drawable.gautop);
                    actGauto=1;
                    actGmoto=0;actGcamioneta=0;actGcamion=0;
                    gmoto.setImageResource(R.mipmap.ic_gmoto);
                    gcamioneta.setImageResource(R.mipmap.ic_gcamioneta);
                    gcamion.setImageResource(R.mipmap.ic_camion);
                    descGrua.setText("Solicitud de grua para vehículo menor ");
                    activarBoton();
                }else{
                    gauto.setImageResource(R.mipmap.ic_gauto);
                    actGauto=0;
                    descGrua.setText("Solicitud de grua para..");
                    desactivarBoton();
                }
            }
        });

        gcamioneta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actGcamioneta==0){
                    gcamioneta.setImageResource(R.drawable.gcamionetap);
                    actGcamioneta=1;
                    actGmoto=0;actGauto=0;actGcamion=0;
                    gmoto.setImageResource(R.mipmap.ic_gmoto);
                    gauto.setImageResource(R.mipmap.ic_gauto);
                    gcamion.setImageResource(R.mipmap.ic_camion);
                    descGrua.setText("Solicitud de grua para vehículo mediano");
                    activarBoton();
                }else{
                    gcamioneta.setImageResource(R.mipmap.ic_gcamioneta);
                    actGcamioneta=0;
                    descGrua.setText("Solicitud de grua para..");
                    desactivarBoton();
                }
            }
        });

        gcamion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (actGcamion==0){
                    gcamion.setImageResource(R.drawable.gcamionp);
                    actGcamion=1;
                    actGmoto=0;actGauto=0;actGcamioneta=0;
                    gmoto.setImageResource(R.mipmap.ic_gmoto);
                    gauto.setImageResource(R.mipmap.ic_gauto);
                    gcamioneta.setImageResource(R.mipmap.ic_gcamioneta);
                    descGrua.setText("Solicitud de grua para vehículo mayor");
                    activarBoton();
                }else{
                    gcamion.setImageResource(R.mipmap.ic_camion);
                    actGcamion=0;
                    descGrua.setText("Solicitud de grua para..");
                    desactivarBoton();
                }
            }
        });
        //fin listener botones

        btAlerta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (actGmoto==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud de grua para","motocicleta","gm",R.mipmap.ic_gmoto)); }
                if (actGauto==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud de grua para","vehículo menor","ga",R.mipmap.ic_gauto)); }
                if (actGcamioneta==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud de grua para","vehículo mediano","gc",R.mipmap.ic_gcamioneta)); }
                if (actGcamion==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud de grua para","vehículo mayor","go",R.mipmap.ic_camion)); }
                if (actMec==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud ayuda mecánica",descMecanico.getText().toString(),"me",R.mipmap.ic_mec)); }
                if (actNeu==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud ayuda neumatico",descNeumatico.getText().toString(),"ne",R.mipmap.ic_neu)); }
                if (actCom==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud ayuda de combustible",descCombustible.getText().toString(),"co",R.mipmap.ic_com)); }
                if (actTra==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud ayuda de transporte",descTransporte.getText().toString(),"tr",R.mipmap.ic_tra)); }
                if (actPol==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud ayuda de emergencia",descEmergencia.getText().toString(),"po",R.mipmap.ic_pol)); }
                if (actAmb==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud ayuda de emergencia",descEmergencia.getText().toString(),"am",R.mipmap.ic_amb)); }
                if (actBom==1){ ConfirmaAlerta.servicios.add(new Servicios("Solicitud ayuda de emergencia",descEmergencia.getText().toString(),"bo",R.mipmap.ic_bom)); }

                Intent m = new Intent(getApplicationContext(), ConfirmaAlerta.class);
                m.putExtra("rut",grut);
                m.putExtra("div",gdiv);
                m.putExtra("nom",gnombre);
                m.putExtra("ape",gapellido);
                m.putExtra("tel",gtelefono);
                m.putExtra("ema",gemail);
                m.putExtra("lat", lat);
                m.putExtra("lng", lng);
                m.putExtra("dir", dir);
                startActivity(m);
                }
        });


}

    @Override
    protected void onResume() {
        super.onResume();

        //Elimina datos del array
        ConfirmaAlerta.servicios.clear();
        ConfirmaAlerta.services.clear();
        //Datos eliminados

        recuperarDatos();
    }

    private void recuperarDatos() {
        //Recuperar datos
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            grut = (String) b.get("rut");
            gdiv = (String) b.get("div");
            gnombre = (String) b.get("nom");
            gapellido = (String) b.get("ape");
            gtelefono = (String) b.get("tel");
            gemail = (String) b.get("ema");
            dir = (String) b.get("dir");
            lat = (Double) b.get("lat");
            lng = (Double) b.get("lng");
            direccion.setText(dir);
            //fin recuperar datos
        }

        eliminarAlertas();
    }

    private void eliminarAlertas(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new ConexionMysqlHelper.CargarDatos().execute("http://www.webinfo.cl/soshelp/del_alerta.php?rut="+grut);
                    }
                });
            }
        },0);

    }

    public void escuchaServicios(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null){
            internet.setImageResource(R.drawable.int_si);
            servicioInt=1;
        }else{
            internet.setImageResource(R.drawable.int_no);
            Toast.makeText(getApplicationContext(),"¡¡ Tu teléfono no esta conectado a internet!!", Toast.LENGTH_SHORT).show();
            servicioInt=0;
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

    private void bloquearDemasServicios(String valor){

        switch (valor){
            case "grua":
                //mecanico.setEnabled(false);neumatico.setEnabled(false);combustible.setEnabled(false);
                mecanico.setImageResource(R.mipmap.ic_mec);
                neumatico.setImageResource(R.mipmap.ic_neu);
                combustible.setImageResource(R.mipmap.ic_com);
                actMec=0; actNeu=0; actCom=0;
                Util.collapse(menuCombustible, 500);
                Util.collapse(menuMecanico, 500);
                Util.collapse(menuNeumatico, 500);
            break;
            case "mecanico":
                grua.setImageResource(R.mipmap.ic_gru);
                neumatico.setImageResource(R.mipmap.ic_neu);
                combustible.setImageResource(R.mipmap.ic_com);
                //grua.setEnabled(false);neumatico.setEnabled(false);combustible.setEnabled(false);
                actGrua=0; actNeu=0; actCom=0;
                deshabilitarMenuGrua();
                Util.collapse(menuGrua, 500);
                Util.collapse(menu2Grua, 500);
                Util.collapse(menuCombustible, 500);
                Util.collapse(menuNeumatico, 500);
                descGrua.setText("");
                break;
            case "neumatico":
                //grua.setEnabled(false);mecanico.setEnabled(false);combustible.setEnabled(false);
                grua.setImageResource(R.mipmap.ic_gru);
                mecanico.setImageResource(R.mipmap.ic_mec);
                combustible.setImageResource(R.mipmap.ic_com);
                actGrua=0; actMec=0; actCom=0;
                deshabilitarMenuGrua();
                descGrua.setText("");
                Util.collapse(menuGrua, 500);
                Util.collapse(menu2Grua, 500);
                Util.collapse(menuCombustible, 500);
                Util.collapse(menuMecanico, 500);
                break;
            case "combustible":
                //grua.setEnabled(false);mecanico.setEnabled(false);neumatico.setEnabled(false);
                grua.setImageResource(R.mipmap.ic_gru);
                mecanico.setImageResource(R.mipmap.ic_mec);
                neumatico.setImageResource(R.mipmap.ic_neu);
                actGrua=0; actMec=0; actNeu=0;
                deshabilitarMenuGrua();
                descGrua.setText("");
                Util.collapse(menuGrua, 500);
                Util.collapse(menu2Grua, 500);
                Util.collapse(menuMecanico, 500);
                Util.collapse(menuNeumatico, 500);
                break;
        }
    }

    private void habilitarDemasServicios(){
        grua.setEnabled(true);mecanico.setEnabled(true);neumatico.setEnabled(true);combustible.setEnabled(true);
    }

    private void deshabilitarMenuGrua(){
        gmoto.setImageResource(R.mipmap.ic_gmoto); actGmoto=0; gmoto.setEnabled(false);
        gauto.setImageResource(R.mipmap.ic_gauto); actGauto=0; gauto.setEnabled(false);
        gcamioneta.setImageResource(R.mipmap.ic_gcamioneta); actGcamioneta=0; gcamioneta.setEnabled(false);
        gcamion.setImageResource(R.mipmap.ic_camion); actGcamion=0; gcamion.setEnabled(false);
        if (actMec==0 && actNeu==0 && actCom==0 && actPol==0 && actBom==0 && actAmb==0 && actTra==0 && actGmoto==0 && actGauto==0 && actGcamioneta==0 && actGcamion==0 && actEmer==0){
            btAlerta.setEnabled(false);
            btAlerta.setBackgroundColor(Color.LTGRAY);

        }

    }

    private void habilitaMenuGrua(){
        actGmoto=0; gmoto.setEnabled(true);
        actGauto=0; gauto.setEnabled(true);
        actGcamioneta=0; gcamioneta.setEnabled(true);
        actGcamion=0; gcamion.setEnabled(true);

    }

    private void activarMenuEmer(){
        if (actEmer==0){
            actEmer=1;
            Util.expand(menuEmergencia, 500);
            descEmergencia.requestFocus();
            activarBoton();
        }
    }

    private void desactivarMenuEmer(){

        if (actPol==0 && actBom==0 && actAmb==0){
            actEmer=0;
            Util.collapse(menuEmergencia, 500);
            desactivarBoton();
        }


    }

    private void activarBoton(){
        btAlerta.setEnabled(true);
        btAlerta.setBackgroundColor(Color.parseColor("#062B51"));


    }

    private void desactivarBoton(){
        if (actMec==0 && actNeu==0 && actCom==0 && actPol==0 && actBom==0 && actAmb==0 && actTra==0 && actGmoto==0 && actGauto==0 && actGcamioneta==0 && actGcamion==0 && actEmer==0){
            btAlerta.setEnabled(false);
            btAlerta.setBackgroundColor(Color.LTGRAY);

        }

    }

}
    /*


    ImageView gps, internet, ic_grua, ic_neumatico, ic_mecanico, ic_policia, ic_ambulancia, ic_bomberos, ic_moto, ic_auto, ic_camioneta, ic_camion, ic_transporte, ic_combustible;
    Button btAlerta;
    EditText Direccion;
    int pres=0, prop=0,servicioGPS=0, servicioInt=0;

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


        /*

        ic_neumatico.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("AN"); }});
        ic_mecanico.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("AM"); }});
        ic_policia.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("CA"); }});
        ic_ambulancia.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("AM"); }});
        ic_bomberos.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("BO"); }});
        ic_moto.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("GM"); }});
        ic_auto.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("GA"); }});
        ic_camioneta.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("GC"); }});
        ic_camion.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("GV"); }});
        ic_transporte.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("AT"); }});
        ic_combustible.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {registrar("AC"); }});
        btAlerta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Presionado",Toast.LENGTH_SHORT).show();
                presentarIconos();
            }
        });
        ic_grua.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {
            if(pres==0){
                ic_grua.setImageResource(R.mipmap.ic_grua2);
                pres = 1;
                menu_principal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
                menu_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80));
                menu_secundario.setVisibility(View.VISIBLE);
                menu_secundario.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                menu_grua.setVisibility(View.VISIBLE);
                menu_grua.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 70));
                ic_neumatico.setEnabled(false);
                ic_combustible.setEnabled(false);
                ic_transporte.setEnabled(false);
                ic_mecanico.setEnabled(false);
                ic_policia.setEnabled(false);
                ic_ambulancia.setEnabled(false);
                ic_bomberos.setEnabled(false);

            }else if (pres ==1){
                ic_grua.setImageResource(R.mipmap.ic_grua);
                menu_principal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
                menu_grua.setVisibility(View.INVISIBLE);
                menu_grua.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                pres = 0;
                ic_neumatico.setEnabled(true);
                ic_transporte.setEnabled(true);
                ic_combustible.setEnabled(true);
                ic_mecanico.setEnabled(true);
                ic_policia.setEnabled(true);
                ic_ambulancia.setEnabled(true);
                ic_bomberos.setEnabled(true);
            }
        }});

 */

    /*
    gps = (ImageView) findViewById(R.id.imGps);
    internet= (ImageView) findViewById(R.id.img_internet);
    Direccion = (EditText) findViewById(R.id.etDireccion);
    btAlerta = (Button) findViewById(R.id.btAlerta);
    btAlerta.setEnabled(false);

    //Escucha servicios
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    //escuchaServicios();
    //fin escucha servicios

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
                        Direccion.setText(ciudad+", "+direc);
                        btAlerta.setEnabled(true);
                        btAlerta.setBackgroundColor(Color.RED);
                    }
                }catch (IOException e){
                    Toast.makeText(getApplicationContext(),"¡¡ Revisa tu conexion a internet !!", Toast.LENGTH_SHORT).show();

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
        agregaGruas();
        new ConexionMysqlHelper.CargarDatos().execute("http://www.webinfo.cl/soshelp/?id_mob="+id_mob);
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

    public void agregaGruas() {new MapsActivity.BackgroundTask().execute();}
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


    public void presentarIconos() {

        if(prop==0){

            menu_principal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
            menu_info.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80));
            menu_secundario.setVisibility(View.VISIBLE);
            menu_secundario.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            menu_grua.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));

            btAlerta.setText("CANCELAR");
            prop=1;
        } else if (prop==1) {

            ic_grua.setImageResource(R.mipmap.ic_grua);
            menu_principal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 70));
            menu_secundario.setVisibility(View.INVISIBLE);

            btAlerta.setText("ALERTA");
            prop=0;
        }
    }

    public void registrar(String tipo){
        Intent m = new Intent(getApplicationContext(), EntreMaps.class);
        m.putExtra("latitud", lat);
        m.putExtra("longitud", lng);
        m.putExtra("ciudad", ciudad);
        m.putExtra("direccion", direc);
        m.putExtra("id_mob", id_mob);
        m.putExtra("tipo", tipo);
        startActivity(m);
    }

    public void escuchaServicios(){

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gps.setImageResource(R.drawable.gps_no);
            servicioGPS=0;
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
        },10000);

    }

     */



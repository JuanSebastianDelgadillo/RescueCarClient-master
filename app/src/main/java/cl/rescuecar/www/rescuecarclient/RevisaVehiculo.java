package cl.rescuecar.www.rescuecarclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RevisaVehiculo extends ConexionMysqlHelper {
    String id_mob, id_mob_ext, rut_user, prueba="1";
    String JSON_STRING;
    JSONObject jsonObject;
    JSONArray jsonArray;
    int servicioInt=0, intentos=0;
    varGlob varglob;
    String grut, gdiv, gnombre, gapellido, gtelefono, gemail;
    String vehiculos;
    LinearLayout llvehiculo1, llvehiculo2, llvehiculo3,llvehiculo4, llvehiculo5, llvehiculo6, llsiveh, llnoveh;
    EditText marca1, marca2, marca3, modelo1, modelo2, modelo3, patente1, patente2, patente3;
    EditText mar1, mar2, mar3, mod1, mod2, mod3, pat1, pat2, pat3;
    TextView nombre;
    Button guardar,guardar2;
    ImageView sig1, sig2, sig3, plus;
    String m1, m2, m3, d1, d2, d3, p1, p2, p3;
    String mr1, mr2, mr3, dr1, dr2, dr3, pr1, pr2, pr3;
    int vehSI=0, vehNO=0;
    int cantVeh=1;
    List<String> vehicle = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revisa_vehiculo);
        //CapturaIdDispositivo
        id_mob = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        //Termino carga de ID dispositivo
        //inicio SharedPreference
        Context context = this;
        SharedPreferences sharPrefs = getSharedPreferences("DatosCliente", context.MODE_PRIVATE);
        //fin SharedPreference
        escuchaServicios();
        nombre = (TextView) findViewById(R.id.tvNombre);
        llvehiculo1 = (LinearLayout) findViewById(R.id.llvehiculo1);
        llvehiculo1.setVisibility(View.INVISIBLE);
        llvehiculo2 = (LinearLayout) findViewById(R.id.llvehiculo2);
        llvehiculo2.setVisibility(View.INVISIBLE);
        llvehiculo3 = (LinearLayout) findViewById(R.id.llvehiculo3);
        llvehiculo3.setVisibility(View.INVISIBLE);
        llvehiculo4 = (LinearLayout) findViewById(R.id.llvehiculo4);
        llvehiculo5 = (LinearLayout) findViewById(R.id.llvehiculo5);
        llvehiculo5.setVisibility(View.INVISIBLE);
        llvehiculo6 = (LinearLayout) findViewById(R.id.llvehiculo6);
        llvehiculo6.setVisibility(View.INVISIBLE);
        llsiveh = (LinearLayout) findViewById(R.id.llsiveh);
        llsiveh.setVisibility(View.INVISIBLE);
        llnoveh = (LinearLayout) findViewById(R.id.llnoveh);
        llnoveh.setVisibility(View.INVISIBLE);
        marca1 = (EditText) findViewById(R.id.etMarca1);
        marca2 = (EditText) findViewById(R.id.etMarca2);
        marca3 = (EditText) findViewById(R.id.etMarca3);
        modelo1 = (EditText) findViewById(R.id.etModelo1);
        modelo2 = (EditText) findViewById(R.id.etModelo2);
        modelo3 = (EditText) findViewById(R.id.etModelo3);
        patente1 = (EditText) findViewById(R.id.etPatente1);
        patente2 = (EditText) findViewById(R.id.etPatente2);
        patente3 = (EditText) findViewById(R.id.etPatente3);
        mar1 = (EditText) findViewById(R.id.etMar1);
        mar2 = (EditText) findViewById(R.id.etMar2);
        mar3 = (EditText) findViewById(R.id.etMar3);
        mod1 = (EditText) findViewById(R.id.etMod1);
        mod2 = (EditText) findViewById(R.id.etMod2);
        mod3 = (EditText) findViewById(R.id.etMod3);
        pat1 = (EditText) findViewById(R.id.etPat1);
        pat2 = (EditText) findViewById(R.id.etPat2);
        pat3 = (EditText) findViewById(R.id.etPat3);
        guardar = (Button) findViewById(R.id.btGuardar);
        guardar2 = (Button) findViewById(R.id.btGuadar2);
        sig1 = (ImageView) findViewById(R.id.sig1);
        sig2 = (ImageView) findViewById(R.id.sig2);
        sig3 = (ImageView) findViewById(R.id.sig3);
        plus = (ImageView) findViewById(R.id.imPlus);

        guardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Guardado", Toast.LENGTH_SHORT).show();

                m1 = mar1.getText().toString();
                d1 = mod1.getText().toString();
                p1 = pat1.getText().toString();

                if (m1.length()>0 && d1.length()>0 && p1.length()>0){

                    vehicle.add(m1+":"+d1+":"+p1);

                }

                m2 = mar2.getText().toString();
                d2 = mod2.getText().toString();
                p2 = pat2.getText().toString();

                if (m2.length()>0&& d2.length()>0 && p2.length()>0){

                    vehicle.add(m2+":"+d2+":"+p2);
                }

                m3 = mar3.getText().toString();
                d3 = mod3.getText().toString();
                p3 = pat3.getText().toString();

                if (m3.length()>0 && d3.length()>0 && p3.length()>0){

                    vehicle.add(m3+":"+d3+":"+p3);
                }

                if (servicioInt==1){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0; i<vehicle.size();i++){
                                        new ConexionMysqlHelper.CargarDatos().execute("http://www.webinfo.cl/soshelp/save_veh.php?rut="+grut+"&veh="+vehicle.get(i));
                                        Toast.makeText(getApplicationContext(),"Registrado correctamente",Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                        }
                    },0);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent m = new Intent(getApplicationContext(), RevisaRegistro.class);
                                    startActivity(m);

                                }
                            });
                        }
                    },1000);


                }else{

                    Toast.makeText(getApplicationContext(),"Revisa tu conexión a internet y vuelve a intentarlo",Toast.LENGTH_SHORT).show();



                }


            }
        });
        guardar2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cantVeh==2){

                    mr2 = marca2.getText().toString();
                    dr2 = modelo2.getText().toString();
                    pr2 = patente2.getText().toString();

                    if (mr2.length()>0 && dr2.length()>0 && pr2.length()>0){

                        vehicle.add(mr2+":"+dr2+":"+pr2);
                    }
                }

                if (cantVeh==3){

                    mr3 = marca3.getText().toString();
                    dr3 = modelo3.getText().toString();
                    pr3 = patente3.getText().toString();

                    if (mr3.length()>0 && dr3.length()>0 && pr3.length()>0){

                        vehicle.add(mr3+":"+dr3+":"+pr3);
                    }
                }
                Toast.makeText(getApplicationContext(), "Cant Veh"+cantVeh, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Vehiculos"+vehicle, Toast.LENGTH_SHORT).show();

                if (servicioInt==1){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i=0; i<vehicle.size();i++){
                                        Toast.makeText(getApplicationContext(), "Vehiculos"+vehicle.get(i), Toast.LENGTH_SHORT).show();
                                        new ConexionMysqlHelper.CargarDatos().execute("http://www.webinfo.cl/soshelp/save_veh.php?rut="+grut+"&veh="+vehicle.get(i));
                                        Toast.makeText(getApplicationContext(),"Registrado correctamente",Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                        }
                    },0);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent m = new Intent(getApplicationContext(), RevisaRegistro.class);
                                    startActivity(m);

                                }
                            });
                        }
                    },1000);


                }else{

                    Toast.makeText(getApplicationContext(),"Revisa tu conexión a internet y vuelve a intentarlo",Toast.LENGTH_SHORT).show();

                }


            }
        });

        sig1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent m = new Intent(getApplicationContext(), MapsActivity.class);
                m.putExtra("rut",grut);
                m.putExtra("div",gdiv);
                m.putExtra("nom",gnombre);
                m.putExtra("ape",gapellido);
                m.putExtra("tel",gtelefono);
                m.putExtra("ema",gemail);
                m.putExtra("veh",marca1.getText()+" "+modelo1.getText()+" "+patente1.getText());
                startActivity(m);
            }
        });

        sig2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent m = new Intent(getApplicationContext(), MapsActivity.class);
                m.putExtra("rut",grut);
                m.putExtra("div",gdiv);
                m.putExtra("nom",gnombre);
                m.putExtra("ape",gapellido);
                m.putExtra("tel",gtelefono);
                m.putExtra("ema",gemail);
                m.putExtra("veh",marca2.getText()+" "+modelo2.getText()+" "+patente2.getText());
                startActivity(m);
            }
        });

        sig3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent m = new Intent(getApplicationContext(), MapsActivity.class);
                m.putExtra("rut",grut);
                m.putExtra("div",gdiv);
                m.putExtra("nom",gnombre);
                m.putExtra("ape",gapellido);
                m.putExtra("tel",gtelefono);
                m.putExtra("ema",gemail);
                m.putExtra("veh",marca3.getText()+" "+modelo3.getText()+" "+patente3.getText());
                startActivity(m);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (vehNO==1){
                    switch(cantVeh){
                        case 2:
                            llvehiculo5.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            llvehiculo6.setVisibility(View.VISIBLE);
                            break;

                    }
                    cantVeh++;


                }else if (vehSI==1){

                    switch(cantVeh){
                        case 2:
                            llvehiculo2.setVisibility(View.VISIBLE);
                            marca2.setEnabled(true);
                            modelo2.setEnabled(true);
                            patente2.setEnabled(true);
                            sig2.setVisibility(View.INVISIBLE);

                            break;
                        case 3:
                            llvehiculo3.setVisibility(View.VISIBLE);
                            marca3.setEnabled(true);
                            modelo3.setEnabled(true);
                            patente3.setEnabled(true);
                            sig3.setVisibility(View.INVISIBLE);
                            break;

                    }




                }

            }
        });
        obtenerdatos();
        String nom = sharPrefs.getString("gnombre","No hay datos");
        String apell = sharPrefs.getString("gapellido","No hay datos");

        nombre.setText(nom + " "+apell);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void obtenerdatos() {

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            grut = (String) b.get("rut");
            gdiv = (String) b.get("div");
            gnombre = (String) b.get("nom");
            gapellido = (String) b.get("ape");
            gtelefono = (String) b.get("tel");
            gemail = (String) b.get("ema");

        }
        ConsultarDatos();
    }

    public void escuchaServicios(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null){
            servicioInt=1;
        }else{
            servicioInt=0;
        }
    }

    public void ConsultarDatos() {

        new RevisaVehiculo.BackgroundTask().execute();

    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {

            json_url = "http://www.webinfo.cl/soshelp/cons_vehiculo.php?rut="+grut;

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
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
            JSON_STRING = null;
            JSON_STRING = result;
            presentarDatos();

        }
    }

    public void presentarDatos() {
        if (JSON_STRING != null) {

            try {
                jsonObject = new JSONObject(JSON_STRING);
                jsonArray = jsonObject.getJSONArray("server_response");


                JSONObject JO = jsonArray.getJSONObject(0);
                vehiculos = JO.getString("vehiculo");


                String[]  vehiculo = vehiculos.split("&");

                for (int i=0;i<vehiculo.length;i++){

                    switch (i){

                        case 0:

                            String[]  veh1 = vehiculo[i].split(",");

                            if (veh1[0].equals("no")){
                                vehNO=1; cantVeh++;
                                Toast.makeText(getApplicationContext(),"¡¡ No tiene vehículos registrados !!",Toast.LENGTH_SHORT).show();
                                llsiveh.setVisibility(View.GONE);
                                llnoveh.setVisibility(View.VISIBLE);
                            }else{
                                vehSI=1; cantVeh=2;
                                llsiveh.setVisibility(View.VISIBLE);
                                llnoveh.setVisibility(View.GONE);
                            llvehiculo1.setVisibility(View.VISIBLE);
                            marca1.setText(veh1[0]);
                            modelo1.setText(veh1[1]);
                            patente1.setText(veh1[2]);
                            }
                            break;
                        case 1:
                            cantVeh=3;
                            String[]  veh2 = vehiculo[i].split(",");
                            llvehiculo2.setVisibility(View.VISIBLE);
                            marca2.setText(veh2[0]);
                            modelo2.setText(veh2[1]);
                            patente2.setText(veh2[2]);


                            break;
                        case 2:
                            cantVeh=3;
                            String[]  veh3 = vehiculo[i].split(",");
                            llvehiculo3.setVisibility(View.VISIBLE);
                            marca3.setText(veh3[0]);
                            modelo3.setText(veh3[1]);
                            patente3.setText(veh3[2]);

                            break;

                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

package cl.rescuecar.www.rescuecarclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class EntreMaps extends ConexionMysqlHelper {
    Double lat = 0.0, lng = 0.0;
    Double latC = 0.0, lngC = 0.0;
    String ciudad, direccion, id_mob, mob_chofer, tipo_alert, textAlert;
    TextView dir, tipo, nombreChofer, tServicio, tvDescripcion;
    EditText etDescripcion;
    Button cancelar, aceptar, aceptar1;
    LinearLayout menu_progress;
    ImageView internet;
    JSONObject jsonObject;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entre_maps);

        dir = (TextView) findViewById(R.id.tvDireccion);
        menu_progress = (LinearLayout) findViewById(R.id.menu_progress);
        cancelar = (Button) findViewById(R.id.btCancelar);
        aceptar = (Button) findViewById(R.id.btAceptar);
        aceptar1 = (Button) findViewById(R.id.bt_Aceptar1);
        etDescripcion = (EditText)findViewById(R.id.etDescripcion);
        tipo = (TextView) findViewById(R.id.tvTipo);
        tServicio = (TextView) findViewById(R.id.tvTServicio);
        nombreChofer = (TextView) findViewById(R.id.tvNombre);
        etDescripcion = (EditText) findViewById(R.id.etDescripcion);
        tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
        presentarDatos();
        agregaChofer();
        aceptar.setEnabled(false);



        cancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /******eliminacion de alerta ******/
                new CargarDatos().execute("http://www.webinfo.cl/soshelp/?id_mob="+id_mob);
                Intent m = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(m);
            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /****** Ingreso segunda pagina ******/
                new CargarDatos().execute("http://www.webinfo.cl/soshelp/?id_mob=" + id_mob);
                Intent s = new Intent(getApplicationContext(), Maps2Activity.class);
                s.putExtra("latGrua", latC);
                s.putExtra("longGrua", lngC);
                s.putExtra("latAuto", lat);
                s.putExtra("longAuto", lng);
                s.putExtra("mob_chofer", mob_chofer);
                s.putExtra("id_mob", id_mob);
                startActivity(s);
            }
        });

        aceptar1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    textAlert = String.valueOf(etDescripcion.getText());
                    textAlert = textAlert.replaceAll(" ","%20" );

                etDescripcion.setEnabled(false);
                    aceptar1.setEnabled(false);
                    aceptar1.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.deshabilitado)));
                    menu_progress.setVisibility(View.VISIBLE);
                    aceptar.setText("BUSCANDO SERVICIO.....");
                /****** Agrego en base de datos en otro hilo******/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new CargarDatos().execute("http://www.webinfo.cl/soshelp/ins_alert.php?id_mob=" + id_mob + "&lat=" + lat + "&lng=" + lng+ "&tipo=" +tipo_alert+"&texAlert="+textAlert);

                            }
                        });
                    }
                },0);

            }
        });
    }
    private void presentarDatos() {
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            lat = (Double) b.get("latitud");
            lng = (Double) b.get("longitud");
            ciudad = (String) b.get("ciudad");
            direccion = (String) b.get("direccion");
            id_mob = (String) b.get("id_mob");
            tipo_alert = (String) b.get("tipo");
            dir.setText(ciudad + ", " + direccion);
            presentarTipo(tipo_alert);
        }
        aceptar1.setEnabled(true);
        aceptar1.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.aceptar)));
    }

    private void presentarTipo(String tipo_alert) {
        switch(tipo_alert){
            case "GM": tipo.setText("Solicitud de grúa de moto");  tvDescripcion.setText("Ingrese modelo y marca de motocicleta : "); break;
            case "GA": tipo.setText("Solicitud de grúa de vehículo menor"); tvDescripcion.setText("Ingrese modelo y marca del vehículo : ");break;
            case "GC": tipo.setText("Solicitud de grúa de vehiculo medio"); tvDescripcion.setText("Ingrese modelo y marca del vehículo : ");break;
            case "GV": tipo.setText("Solicitud de grúa de vehículo Mayor"); tvDescripcion.setText("Ingrese modelo y marca del vehículo mayor : ");break;
            case "CA": tipo.setText("Solicitud de Carabineros"); tvDescripcion.setText("Ingrese situacion y hechos concretos : ");break;
            case "BO": tipo.setText("Solicitud de Bomberos"); tvDescripcion.setText("Ingrese situacion y hechos concretos : ");break;
            case "AB": tipo.setText("Solicitud de Ambulancia"); tvDescripcion.setText("Ingrese situacion y hechos concretos : ");break;
            case "AN": tipo.setText("Solicitud de asistencia de neumático"); tvDescripcion.setText("Indique medidas y cantidad de hoyos del neumatico : ");break;
            case "AC": tipo.setText("Solicitud de asistencia de combustible"); tvDescripcion.setText("Indique el octanaje y cantidad a requerir : ");break;
            case "AM": tipo.setText("Solicitud de asistencia mecánica"); tvDescripcion.setText("Indique sintomas de las fallas : ");break;
            case "AT": tipo.setText("Solicitud de asistencia de transporte"); tvDescripcion.setText("Indique cantidad de personas a transportar : ");break;

        }
    }

    public void agregaChofer() {
        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "http://www.webinfo.cl/soshelp/cons_chofer.php?id_mob=" + id_mob;
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
            JSON_STRING = result;
            presentar();

        }
    }

    public void presentar() {
        if (JSON_STRING != null) {
            json_string = JSON_STRING;
            if (json_string.length() < 23) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    agregaChofer();
                                }
                            });
                        }
                    }, 5000);

            } else {
                    try {
                        jsonObject = new JSONObject(json_string);
                        jsonArray = jsonObject.getJSONArray("server_response");
                        int count = 0;
                        String tip, nombreCh;

                        JSONObject cho = jsonArray.getJSONObject(count);
                        mob_chofer = cho.getString("id_mob_driv");
                        nombreCh = cho.getString("nom_driv");
                        tip = cho.getString("tip_driv");
                        latC = Double.parseDouble(cho.getString("lat_driv"));
                        lngC = Double.parseDouble(cho.getString("lng_driv"));
                        nombreChofer.setText(nombreCh);
                        tServicio.setText(tip);
                        aceptar.setBackgroundColor((ContextCompat.getColor(getApplicationContext(), R.color.aceptar)));
                        aceptar.setEnabled(true);
                        aceptar.setText("ACEPTAR SERVICIO");
                        menu_progress.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

    }
}

//Toast.makeText(getApplicationContext(),"""",Toast.LENGTH_SHORT).show();
//Toast.makeText(getApplicationContext(),"Presionado",Toast.LENGTH_SHORT).show();
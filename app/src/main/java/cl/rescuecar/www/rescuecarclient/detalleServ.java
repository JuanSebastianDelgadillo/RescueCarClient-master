package cl.rescuecar.www.rescuecarclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class detalleServ extends ConexionMysqlHelper {
    ImageView internet;
    int servicioInt, cantidadServ;
    String grut, gdiv, gnombre, gapellido, gtelefono, gemail;
    String rut, time, dist, tip;
    EditText nombre, solicitud, tipo;
    JSONObject jsonObject;
    JSONArray jsonArray;
    String rut_serv, dig_serv, nom_serv, ape_serv, ema_serv, tel_serv, serv_serv;
    String[] services;
    EditText nombre_serv, patente_serv, telefono_serv;
    TextView serv1,serv2, serv3, serv4, serv5,serv6, serv7, serv8, serv9, serv10, tiempo_serv, distancia_serv;
    ImageView star1, star2, star3, star4, star5;
    String detalle, calif, vehiculo, servicios_serv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_serv);

        internet = (ImageView) findViewById(R.id.imInt);
        nombre_serv = (EditText) findViewById(R.id.etNombre);
        patente_serv= (EditText) findViewById(R.id.etPatente);
        telefono_serv = (EditText) findViewById(R.id.etTelefono);
        tiempo_serv = (TextView) findViewById(R.id.tvTiempo);
        distancia_serv = (TextView) findViewById(R.id.tvDistancia);
        serv1 = (TextView) findViewById(R.id.tvServicio1);
        serv2 = (TextView) findViewById(R.id.tvServicio2);
        serv3 = (TextView) findViewById(R.id.tvServicio3);
        serv4 = (TextView) findViewById(R.id.tvServicio4);
        serv5 = (TextView) findViewById(R.id.tvServicio5);
        serv6 = (TextView) findViewById(R.id.tvServicio6);
        serv7 = (TextView) findViewById(R.id.tvServicio7);
        serv8 = (TextView) findViewById(R.id.tvServicio8);
        serv9 = (TextView) findViewById(R.id.tvServicio9);
        serv10 = (TextView) findViewById(R.id.tvServicio10);
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);

        escuchaServicios();
        obtenerDatos();
    }

    public void escuchaServicios() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null) {
            internet.setImageResource(R.drawable.int_si);
            servicioInt = 1;
        } else {
            internet.setImageResource(R.drawable.int_no);
            Toast.makeText(getApplicationContext(), "¡¡ Tu teléfono no esta conectado a internet!!", Toast.LENGTH_SHORT).show();
            servicioInt = 0;
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
        }, 50000);

    }

    private void obtenerDatos() {

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {


            tip = (String) b.get("tipo");
            rut = (String) b.get("rut");
            time = (String) b.get("time");
            dist = (String) b.get("dist");
        }

        varGlob varglob = (varGlob) getApplicationContext();

        grut = varglob.getRut();
        gdiv = varglob.getDiv();
        gnombre = varglob.getNombre();
        gapellido = varglob.getApellido();
        gtelefono = varglob.getTelefono();
        gemail= varglob.getEmail();
        BuscarAlerta();
    }

    public void BuscarAlerta() {

        new BackgroundTask().execute();

    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {

            Toast.makeText(getApplicationContext(), "Buscando chofer de servicio", Toast.LENGTH_SHORT).show();

            json_url = "http://www.webinfo.cl/soshelp/cons_chofer_serv.php?rut="+rut;

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
                detalle = JO.getString("detalle");
                servicios_serv = JO.getString("servicios");
                calif = JO.getString("calificaciones");
                vehiculo = JO.getString("vehiculo");

                if (detalle.length() > 2 && calif.length() > 2 && vehiculo.length() > 2) {

                   String[]  infoP = detalle.split(",");
                        nombre_serv.setText(infoP[1]+" "+infoP[2]);
                        telefono_serv.setText("+569"+infoP[3]);
                        distancia_serv.setText(dist);
                        tiempo_serv.setText(time);

                    String[] infoV = vehiculo.split(",");
                        patente_serv.setText(infoV[2]);

                    String[] infoS = servicios_serv.split(",");

                    for (int i=0;i<infoS.length;i++){

                        switch(i){
                            case 0: serv1.setText(tipoAlerta(infoS[0])); break;
                            case 1: serv2.setText(tipoAlerta(infoS[1])); break;
                            case 2: serv3.setText(tipoAlerta(infoS[2])); break;
                            case 3: serv4.setText(tipoAlerta(infoS[3])); break;
                            case 4: serv5.setText(tipoAlerta(infoS[4])); break;
                            case 5: serv6.setText(tipoAlerta(infoS[5])); break;
                            case 6: serv7.setText(tipoAlerta(infoS[6])); break;
                            case 7: serv8.setText(tipoAlerta(infoS[7])); break;
                            case 8: serv9.setText(tipoAlerta(infoS[8])); break;
                            case 9: serv10.setText(tipoAlerta(infoS[9])); break;

                        }

                    }


                } else {

                    Toast.makeText(this, "No se ha encontrado al conductor de servicio", Toast.LENGTH_SHORT).show();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String tipoAlerta(String tip_alerta){
        String texto = null;

        switch (tip_alerta){
            case "gm":
                texto = "solicitud de grúa para motocicleta";
                break;
            case "ga":
                texto = "solicitud de grúa para vehículo";
                break;
            case "gc":
                texto = "solicitud de grúa para camioneta";
                break;
            case "go":
                texto = "solicitud de grúa para vehículo mayor";
                break;
            case "po":
                texto = "solicitud de carabineros";
                break;
            case "am":
                texto = "solicitud de ambulancia";
                break;
            case "bo":
                texto = "solicitud de bomberos";
                break;
            case "me":
                texto = "solicitud de mecánico en ruta";
                break;
            case "ne":
                texto = "solicitud de asistencia de neumático";
                break;
            case "tr":
                texto = "solicitud de servicio de transporte";
                break;
            case "co":
                texto = "solicitud de servicio de combustible";
                break;

        }

        return texto;
    }
}

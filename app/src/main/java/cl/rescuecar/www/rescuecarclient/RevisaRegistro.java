package cl.rescuecar.www.rescuecarclient;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RevisaRegistro extends ConexionMysqlHelper {
    String id_mob, id_mob_ext, rut_user, prueba="1";
    String JSON_STRING;
    JSONObject jsonObject;
    JSONArray jsonArray;
    int servicioInt=0, intentos=0;
    varGlob varglob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revisa_registro);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //CapturaIdDispositivo
        id_mob = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        //Termino carga de ID dispositivo

        escuchaServicios();

    }

    public void escuchaServicios(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null){
            servicioInt=1;
            ConsultarDatos();

        }else {
            intentos++;

            if (intentos <= 3) {
                Toast.makeText(getApplicationContext(), "¡¡ Tu teléfono no esta conectado a internet!!", Toast.LENGTH_SHORT).show();
                servicioInt = 0;
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
                }, 10000);

            }else{
                finish();
            }
        }

    }

    public void ConsultarDatos(){ new RevisaRegistro.BackgroundTask().execute(); }
    class BackgroundTask extends AsyncTask<Void,Void,String> {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "http://www.webinfo.cl/soshelp/cons_idMob.php?id_mob="+id_mob;
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
            JSONArray jo = null;
            presentar();
        }
    }

    public void presentar() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (JSON_STRING != null) {
                            json_string = JSON_STRING;
                            try {
                                jsonObject = new JSONObject(json_string);
                                jsonArray = jsonObject.getJSONArray("server_response");
                                String rut, div, nom, ape, tel, ema;
                                int count = 0;

                                JSONObject JO = jsonArray.getJSONObject(count);
                                rut = JO.getString("rut_user");
                                div = JO.getString("dig_user");
                                nom = JO.getString("nom_user");
                                ape = JO.getString("ape_user");
                                tel = JO.getString("tel_user");
                                ema = JO.getString("ema_user");

                                if (rut.equals("no")) {
                                    Toast.makeText(getApplicationContext(), "No se encuentra registrado!!", Toast.LENGTH_SHORT).show();
                                    Intent m = new Intent(getApplicationContext(), FormRegister.class);
                                    startActivity(m);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Bienvenido " + nom + " " + ape, Toast.LENGTH_SHORT).show();
                                    Intent m = new Intent(getApplicationContext(), MapsActivity.class);
                                    m.putExtra("rut",rut);
                                    m.putExtra("div",div);
                                    m.putExtra("nom",nom);
                                    m.putExtra("ape",ape);
                                    m.putExtra("tel",tel);
                                    m.putExtra("ema",ema);
                                    startActivity(m);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                            Toast.makeText(getApplicationContext(), "No esta registrado¡¡¡¡¡", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        },0);

    }

    public String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Respuesta", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }


}



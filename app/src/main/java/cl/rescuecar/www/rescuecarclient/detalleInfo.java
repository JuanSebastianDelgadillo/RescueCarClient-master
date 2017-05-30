package cl.rescuecar.www.rescuecarclient;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class detalleInfo extends ConexionMysqlHelper{

    ImageView perfil;
    ImageView internet;
    int servicioInt, cantidadServ;
    String grut, gdiv, gnombre, gapellido, gtelefono, gemail;
    String rut, time, dist, tip;
    EditText nombre, solicitud, tipo;
    JSONObject jsonObject;
    JSONArray jsonArray;
    TextView rut_user, dig_user, nom_user, ape_user, ema_user, tel_user, patente_serv;
    String[] services;
    EditText etNombre, etApellido, etTelefono, etEmail;
    int servp=0,servv=0, serve=0, servh=0, servM=0;
    ImageView star1, star2, star3, star4, star5, leftArrow;
    String calif, vehiculo, id_mob;
    LinearLayout llinfop, llinfov, llinfoh, llinfoe, lltp, lltv, llte, llth, llMain;
    ImageView imp, imv, ime, imh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_info);

        rut_user = (TextView) findViewById(R.id.tvRut);
        nom_user =(TextView) findViewById(R.id.tvNombre);
        ema_user = (TextView) findViewById(R.id.tvEmail);
        tel_user = (TextView) findViewById(R.id.tvTelefono);
        patente_serv= (TextView) findViewById(R.id.tvPat);


        perfil = (ImageView) findViewById(R.id.improfile);
        internet = (ImageView) findViewById(R.id.imInt);
        leftArrow = (ImageView) findViewById(R.id.imLeftArrow);
        star1 = (ImageView) findViewById(R.id.imStar1);
        star2 = (ImageView) findViewById(R.id.imStar2);
        star3 = (ImageView) findViewById(R.id.imStar3);
        star4 = (ImageView) findViewById(R.id.imStar4);
        star5 = (ImageView) findViewById(R.id.imStar5);
        imp = (ImageView) findViewById(R.id.imp);
        imv = (ImageView) findViewById(R.id.imv);
        ime = (ImageView) findViewById(R.id.ime);
        imh = (ImageView) findViewById(R.id.imh);
        imp.setEnabled(false); imv.setEnabled(false); ime.setEnabled(false); imh.setEnabled(false);
        star5 = (ImageView) findViewById(R.id.imStar5);
        llMain = (LinearLayout) findViewById(R.id.llprincipal);
        lltp = (LinearLayout) findViewById(R.id.lltp);
        lltv = (LinearLayout) findViewById(R.id.lltv);
        llte = (LinearLayout) findViewById(R.id.llte);
        llth = (LinearLayout) findViewById(R.id.llth);
        llinfop = (LinearLayout) findViewById(R.id.llinfoP);
        llinfov = (LinearLayout) findViewById(R.id.llinfoV);
        llinfoe = (LinearLayout) findViewById(R.id.llinfoE);
        llinfoh = (LinearLayout) findViewById(R.id.llinfoH);
        etNombre = (EditText) findViewById(R.id.etName);
        etApellido = (EditText) findViewById(R.id.etApellido);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        id_mob = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        escuchaServicios();
        obtenerDatos();

        leftArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), MapsActivity.class);
                s.putExtra("rut",rut);
                startActivity(s);
            }
        });

        imp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gnombre = etNombre.getText().toString();
                gapellido = etApellido.getText().toString();
                gemail = etEmail.getText().toString();
                gtelefono = etTelefono.getText().toString();
                etNombre.setText(gnombre) ;
                etApellido.setText(gapellido);
                etTelefono.setText(gtelefono);
                etEmail.setText(gemail);
                nom_user.setText(gnombre+" "+gapellido);
                tel_user.setText("+569"+gtelefono);
                ema_user.setText(gemail);
                varGlob varglob = (varGlob) getApplicationContext();
                varglob.setNombre(gnombre);
                varglob.setApellido(gapellido);
                varglob.setTelefono(gtelefono);
                varglob.setEmail(gemail);
                llinfop.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)); servp=0;
                ocultarMain();
                imp.setImageResource(R.mipmap.abajosi);imp.setEnabled(false);

                /****** Agrego en base de datos en otro hilo******/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new ConexionMysqlHelper.CargarDatos().execute("http://www.webinfo.cl/soshelp/update_client.php?id_mob="+id_mob+"&rut="+grut+"&div="+gdiv+"&nom="+gnombre+"&ape="+gapellido+"&ema="+gemail+"&tel="+gtelefono);
                                Toast.makeText(getApplicationContext(),"Actualizado correctamente",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                },0);

            }
        });

        lltp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ocultarMain();
                if(servp==0){
                    imp.setImageResource(R.mipmap.save); imp.setEnabled(true);
                    llinfop.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT )); servp=1;
                    etNombre.setText(gnombre) ;
                    etApellido.setText(gapellido);
                    etTelefono.setText(gtelefono);
                    etEmail.setText(gemail);
                }else{

                    llinfop.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)); servp=0;
                    imp.setImageResource(R.mipmap.abajosi);imp.setEnabled(false);
                }

            }
        });

        lltv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ocultarMain();
                if(servv==0){
                    imv.setImageResource(R.mipmap.save); imv.setEnabled(true);
                    llinfov.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400)); servv=1;
                }else{
                    imv.setImageResource(R.mipmap.abajosi); imv.setEnabled(false);
                    llinfov.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)); servv=0;
                }
            }
        });

        llte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ocultarMain();
                if(serve==0){
                    ime.setImageResource(R.mipmap.save);ime.setEnabled(true);
                    llinfoe.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400)); serve=1;
                }else{
                    ime.setImageResource(R.mipmap.abajosi); ime.setEnabled(false);
                    llinfoe.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)); serve=0;
                }

            }
        });

        llth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ocultarMain();
                if(servh==0){
                    imh.setImageResource(R.mipmap.save); imh.setEnabled(true);
                    llinfoh.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400)); servh=1;
                }else{
                    imh.setImageResource(R.mipmap.abajosi); imh.setEnabled(false);
                    llinfoh.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)); servh=0;
                }

            }
        });

    }

    public void ocultarMain() {
        if (servM==0){
            llMain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0));
            servM=1;
        }else{
            llMain.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            servM=0;
        }


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
            rut = (String) b.get("rut");
        }

        varGlob varglob = (varGlob) getApplicationContext();

        grut = varglob.getRut();
        gdiv = varglob.getDiv();
        rut_user.setText(grut+"-"+gdiv) ;
        gnombre = varglob.getNombre();
        gapellido = varglob.getApellido();
        nom_user.setText(gnombre+" "+gapellido);
        gtelefono = varglob.getTelefono();
        tel_user.setText("+569"+gtelefono);
        gemail = varglob.getEmail();
        ema_user.setText(gemail);

        BuscarAlerta();
    }

    public void BuscarAlerta() {

        new BackgroundTask().execute();

    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {

            json_url = "http://www.webinfo.cl/soshelp/cons_chofer_client.php?rut="+grut;

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
                calif = JO.getString("calificaciones");
                vehiculo = JO.getString("vehiculo");

                String[]  infoC = calif.split(",");
                String[]  infoV = vehiculo.split(",");

                if (infoC.length >= 1 && infoV.length >= 1) {

                    patente_serv.setText(infoV[0]+" "+infoV[1]+" PAT: "+infoV[2]);

                    int valoracion = Integer.parseInt(infoC[0]);

                    switch (valoracion){

                        case 1:
                            star1.setImageResource(R.drawable.starup);
                            break;
                        case 2:
                            star1.setImageResource(R.drawable.starup);
                            star2.setImageResource(R.drawable.starup);
                            break;
                        case 3:
                            star1.setImageResource(R.drawable.starup);
                            star2.setImageResource(R.drawable.starup);
                            star3.setImageResource(R.drawable.starup);
                            break;
                        case 4:
                            star1.setImageResource(R.drawable.starup);
                            star2.setImageResource(R.drawable.starup);
                            star3.setImageResource(R.drawable.starup);
                            star4.setImageResource(R.drawable.starup);
                            break;
                        case 5:
                            star1.setImageResource(R.drawable.starup);
                            star2.setImageResource(R.drawable.starup);
                            star3.setImageResource(R.drawable.starup);
                            star4.setImageResource(R.drawable.starup);
                            star5.setImageResource(R.drawable.starup);
                            break;
                    }



                } else {

                    Toast.makeText(this, "No se ha encontrado al conductor de servicio", Toast.LENGTH_SHORT).show();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

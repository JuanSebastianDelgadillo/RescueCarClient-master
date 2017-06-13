package cl.rescuecar.www.rescuecarclient;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.List;

public class ConfirmaAlerta extends ConexionMysqlHelper{

    int actMe=0, actNe=0, actCo=0, actPo=0, actBo=0, actAm=0, actTr=0, actGm=0, actGa=0, actGc=0, actGo=0;
    ImageView internet, iconoLV;
    EditText direccion, falla;
    String id_mob, tipo, dir;
    int servicioInt;
    double lat = 0.0;
    double lng = 0.0;
    ProgressBar cirProgress;
    JSONObject jsonObject;
    JSONArray jsonArray;
    LinearLayout llgm, llga, llgc, llgo, llpo, llam, llbo, llme, llne, lltr, llco;
    String[] servAct;
    ProgressBar pbgm, pbga, pbgc, pbgo, pbpo, pbam, pbbo, pbme, pbne, pbtr, pbco;
    CheckBox cbgm, cbga, cbgc, cbgo, cbpo, cbam, cbbo, cbme, cbne, cbtr, cbco;
    String rutgm, rutga, rutgc, rutgo, rutpo, rutam, rutbo, rutme, rutne, ruttr, rutco;
    String timegm, timega, timegc, timego, timepo, timeam, timebo, timeme, timene, timetr, timeco;
    String distgm, distga, distgc, distgo, distpo, distam, distbo, distme, distne, disttr, distco;
    String tips;
    String grut, gdiv, gnombre, gapellido, gtelefono, gemail;
    int contando=0, contador=0;



    public static List<Servicios> servicios = new ArrayList<Servicios>();
    public static List<ServiciosFin> services = new ArrayList<ServiciosFin>();
    public static List<String> tipos = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_alerta);


        //CapturaIdDispositivo
        id_mob = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //Variables
        direccion = (EditText) findViewById(R.id.etDireccion);
        direccion.setEnabled(false);
        internet = (ImageView) findViewById(R.id.imInt);
        llgm = (LinearLayout) findViewById(R.id.llgm);
        llga = (LinearLayout) findViewById(R.id.llga);
        llgc = (LinearLayout) findViewById(R.id.llgc);
        llgo = (LinearLayout) findViewById(R.id.llgo);
        llpo = (LinearLayout) findViewById(R.id.llpo);
        llam = (LinearLayout) findViewById(R.id.llam);
        llbo = (LinearLayout) findViewById(R.id.llbo);
        llme = (LinearLayout) findViewById(R.id.llme);
        llne = (LinearLayout) findViewById(R.id.llne);
        lltr = (LinearLayout) findViewById(R.id.lltr);
        llco = (LinearLayout) findViewById(R.id.llco);

        //progress bar
        pbgm = (ProgressBar) findViewById(R.id.pbGm);
        pbga = (ProgressBar) findViewById(R.id.pbGa);
        pbgc = (ProgressBar) findViewById(R.id.pbGc);
        pbgo = (ProgressBar) findViewById(R.id.pbGo);
        pbpo = (ProgressBar) findViewById(R.id.pbPo);
        pbam = (ProgressBar) findViewById(R.id.pbAm);
        pbbo = (ProgressBar) findViewById(R.id.pbBo);
        pbme = (ProgressBar) findViewById(R.id.pbMe);
        pbne = (ProgressBar) findViewById(R.id.pbNe);
        pbtr = (ProgressBar) findViewById(R.id.pbTr);
        pbco = (ProgressBar) findViewById(R.id.pbCo);

        //ChekBox

        cbgm = (CheckBox) findViewById(R.id.cbGm);
        cbga = (CheckBox) findViewById(R.id.cbGa);
        cbgc = (CheckBox) findViewById(R.id.cbGc);
        cbgo = (CheckBox) findViewById(R.id.cbGo);
        cbpo = (CheckBox) findViewById(R.id.cbPo);
        cbam = (CheckBox) findViewById(R.id.cbAm);
        cbbo = (CheckBox) findViewById(R.id.cbBo);
        cbme = (CheckBox) findViewById(R.id.cbMe);
        cbne = (CheckBox) findViewById(R.id.cbNe);
        cbtr = (CheckBox) findViewById(R.id.cbTr);
        cbco = (CheckBox) findViewById(R.id.cbCo);

        Button btFinalizar = (Button) findViewById(R.id.btFinalizar);
        btFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        escuchaServicios();
        obtenerDatos();
        llenarListView();

    }

    @Override
    protected void onPause() {
        super.onPause();

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
    private void obtenerDatos() {

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {

            grut = (String) b.get("rut");
            gdiv = (String) b.get("div");
            gnombre = (String) b.get("nom");
            gapellido = (String) b.get("ape");
            gtelefono = (String) b.get("tel");
            gemail = (String) b.get("ema");
            servAct = (String[]) b.get("servAct");
            lat = (Double) b.get("lat");
            lng = (Double) b.get("lng");
            dir = (String) b.get("dir");
            direccion.setText(dir);
        }


    }

    private void llenarListView(){

        for (final Servicios serv : servicios)
        {
          
            /****** Agrego en base de datos en otro hilo******/

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new ConexionMysqlHelper.CargarDatos().execute("http://www.webinfo.cl/soshelp/ins_alert.php?rut=" + grut + "&lat=" + lat + "&lng=" + lng+ "&tipo=" +serv.getTipo()+"&texAlert="+serv.getSubTit());

                        }
                    });
                }
            },0);


            switch (serv.getTipo()){
                case "gm": llgm.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            tipos.add("gm"); actGm=1;
                    break;
                case "ga": llga.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            tipos.add("ga"); actGa=1;

                    break;
                case "gc": llgc.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            tipos.add("gc"); actGc=1;

                    break;
                case "go": llgo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            tipos.add("go"); actGo=1;

                    break;
                case "po": llpo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            tipos.add("po"); actPo=1;

                    break;
                case "am": llam.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            tipos.add("am"); actAm=1;

                    break;
                case "bo": llbo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            actBo=1; tipos.add("bo");

                    break;
                case "me": llme.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            actMe=1; tipos.add("me");

                    break;
                case "ne": llne.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            actNe=1; tipos.add("ne");

                    break;
                case "tr": lltr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            actTr=1; tipos.add("tr");

                    break;
                case "co": llco.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
                            actCo=1; tipos.add("co");

                    break;
            }

        }

        buscarChofer();

    }

private void buscarChofer(){

    for(int i=0;i<tipos.size();i++){

        tips = tipos.get(contando);

        agregaChofer();
        break;
    }
}


    public void agregaChofer() {
        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "http://www.webinfo.cl/soshelp/cons_chofer.php?rut="+grut+"&tipo="+tips;
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
                }, 500);

            } else {
                try {
                    jsonObject = new JSONObject(json_string);
                    jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    String rut_chofer, time_chofer, dist_chofer;

                    Log.i("Toy buscando", tips);

                    JSONObject cho = jsonArray.getJSONObject(count);

                    rut_chofer = cho.getString("rut_driv");
                    time_chofer = cho.getString("time_driv");
                    dist_chofer = cho.getString("dist_driv");

                    Log.i("Chofer", rut_chofer);


                    if (rut_chofer.equals("no")){


                    }else{

                        switch (tips){


                            case "gm":  pbgm.setVisibility(View.INVISIBLE);  cbgm.setChecked(true); rutgm=rut_chofer; timegm=time_chofer; distgm=dist_chofer;
                                llgm.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "gm");
                                    m.putExtra("rutg", rutgm);
                                    m.putExtra("timeg", timegm);
									m.putExtra("distg", distgm);
                                    startActivity(m); }});
                                break;
                            case "ga":  pbga.setVisibility(View.INVISIBLE);  cbga.setChecked(true); rutga=rut_chofer; timega=time_chofer; distga=dist_chofer;
                                llga.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "ga");
                                    m.putExtra("rutg", rutga);
                                    m.putExtra("timeg", timega);
									m.putExtra("distg", distga);
                                    startActivity(m); }});
                                break;
                            case "gc":  pbgc.setVisibility(View.INVISIBLE);  cbgc.setChecked(true); rutgc=rut_chofer; timegc=time_chofer; distgc=dist_chofer;
                                llgc.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "gc");
                                    m.putExtra("rutg", rutgc);
                                    m.putExtra("timeg", timegc);
									m.putExtra("distg", distgc);
                                    startActivity(m); }});
                                break;
                            case "go":  pbgo.setVisibility(View.INVISIBLE);  cbgo.setChecked(true); rutgo=rut_chofer; timego=time_chofer; distgo=dist_chofer;
                                llgo.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "go");
                                    m.putExtra("rutg", rutgo);
                                    m.putExtra("timeg", timego);
									m.putExtra("distg", distgo);
                                    startActivity(m); }});
                                break;
                            case "po":  pbpo.setVisibility(View.INVISIBLE);  cbpo.setChecked(true); rutpo=rut_chofer; timepo=time_chofer; distpo=dist_chofer;
                                llpo.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "po");
                                    m.putExtra("rutg", rutpo);
                                    m.putExtra("timeg", timepo);
									m.putExtra("distg", distpo);
                                    startActivity(m); }});
                                break;
                            case "am":  pbam.setVisibility(View.INVISIBLE);  cbam.setChecked(true); rutam=rut_chofer; timeam=time_chofer; distam=dist_chofer;
                                llam.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "am");
                                    m.putExtra("rutg", rutam);
                                    m.putExtra("timeg", timeam);
									m.putExtra("distg", distam);
                                    startActivity(m); }});
                                break;
                            case "bo":  pbbo.setVisibility(View.INVISIBLE);  cbbo.setChecked(true); rutbo=rut_chofer; timebo=time_chofer; distbo=dist_chofer;
                                llbo.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "bo");
                                    m.putExtra("rutg", rutbo);
                                    m.putExtra("timeg", timebo);
									m.putExtra("distg", distbo);
                                    startActivity(m); }});
                                break;
                            case "me":  pbme.setVisibility(View.INVISIBLE);  cbme.setChecked(true); rutme=rut_chofer; timeme=time_chofer; distme=dist_chofer;
                                llme.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "me");
                                    m.putExtra("rutg", rutme);
                                    m.putExtra("timeg", timeme);
									m.putExtra("distg", distme);
                                    startActivity(m); }});
                                break;
                            case "ne":  pbne.setVisibility(View.INVISIBLE);  cbne.setChecked(true); rutne=rut_chofer; timene=time_chofer; distne=dist_chofer;
                                llne.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "ne");
                                    m.putExtra("rutg", rutne);
                                    m.putExtra("timeg", timene);
									m.putExtra("distg", distne);
                                    startActivity(m); }});
                                break;
                            case "tr":  pbtr.setVisibility(View.INVISIBLE);  cbtr.setChecked(true); ruttr=rut_chofer; timetr=time_chofer; disttr=dist_chofer;
                                lltr.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "tr");
                                    m.putExtra("rutg", ruttr);
                                    m.putExtra("timeg", timetr);
									m.putExtra("distg", disttr);
                                    startActivity(m); }});
                                break;
                            case "co":  pbco.setVisibility(View.INVISIBLE);  cbco.setChecked(true); rutco=rut_chofer; timeco=time_chofer; distco=dist_chofer;
                                llco.setOnClickListener(new View.OnClickListener() {@Override
                                public void onClick(View v) {
                                    Intent m = new Intent(getApplicationContext(), detalleServ.class);
                                    m.putExtra("rut",grut);
                                    m.putExtra("div",gdiv);
                                    m.putExtra("nom",gnombre);
                                    m.putExtra("ape",gapellido);
                                    m.putExtra("tel",gtelefono);
                                    m.putExtra("ema",gemail);
                                    m.putExtra("tipo", "co");
                                    m.putExtra("rutg", rutco);
                                    m.putExtra("timeg", timeco);
									m.putExtra("distg", distco);
                                    startActivity(m); }});
                                break;

                        }
                        Log.i("Elimine:", tips);
                        tipos.remove(contando);
                        contando=0;
                        contador=0;
                    }

                    contando++;


                    if (contando == tipos.size())
                    {
                        contando=0;
                        contador++;
                        Log.i("Reseteo", "El numero "+contando);
                        Log.i("Contador general", "Cantidad: "+contador);

                    }

                    if (contador<30){
                        if (tipos.size()!=0) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            buscarChofer();

                                        }
                                    });
                                }
                            }, 5000);

                            Log.i("Cantidad en ArrayList:",String.valueOf(tipos.size()));
                        }
                    }else{


                        Toast.makeText(this, "Eltiempo de busqueda, ha superado el tiempo maximo, porfavor vuelva a intentarlo !!", Toast.LENGTH_SHORT).show();
                        eliminarAlertas();
                        Intent m = new Intent(getApplicationContext(), SelEmergencia.class);
                        startActivity(m);
                        
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

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
}


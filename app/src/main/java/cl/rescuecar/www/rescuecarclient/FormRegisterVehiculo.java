package cl.rescuecar.www.rescuecarclient;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FormRegisterVehiculo extends ConexionMysqlHelper {

    EditText marc, mod, pat;
    String gnombre, gapellido, grut, gdiv, gtelefono, gemail, gserv, gmarc, gmod, gpat;
    Button registrar;
    String id_mob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehiculo);


        marc = (EditText) findViewById(R.id.etMarca);
        mod = (EditText) findViewById(R.id.etModelo);
        pat = (EditText) findViewById(R.id.etPatente);
        registrar = (Button) findViewById(R.id.btRegistrar);
        //CapturaIdDispositivo
        id_mob = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        obtenerDatos();

        registrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gmarc = marc.getText().toString();
                gmod = mod.getText().toString();
                gpat = pat.getText().toString();


                Intent m = new Intent(getApplicationContext(), MapsActivity.class);
                m.putExtra("rut",grut);
                m.putExtra("div",gdiv);
                m.putExtra("nom",gnombre);
                m.putExtra("ape",gapellido);
                m.putExtra("tel",gtelefono);
                m.putExtra("ema",gemail);
                m.putExtra("serv",gserv);
                m.putExtra("veh",gmarc +" "+gmod+" "+gpat);
                startActivity(m);

            }
        });

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
            gserv = (String) b.get("serv");

        }
    }

}

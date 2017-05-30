package cl.rescuecar.www.rescuecarclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FormRegister extends ConexionMysqlHelper{

    EditText nombre, apellido, rut, div, telefono, cod, email;
    String gnombre, gapellido, grut, gdiv, gtelefono, gcod, gemail;
    Button registrar;
    String id_mob;
    varGlob varglob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_access);

        nombre = (EditText) findViewById(R.id.etNombre);
        apellido = (EditText) findViewById(R.id.etApellido);
        rut = (EditText) findViewById(R.id.etRut);
        div = (EditText) findViewById(R.id.etDiv);
        email = (EditText) findViewById(R.id.etEmail);
        cod = (EditText) findViewById(R.id.etCod);
        telefono = (EditText) findViewById(R.id.etTelefono);
        registrar = (Button) findViewById(R.id.btRegistrar);
        //CapturaIdDispositivo
        id_mob = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        registrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                grut = rut.getText().toString();
                gdiv = div.getText().toString();
                gnombre = nombre.getText().toString();
                gapellido = apellido.getText().toString();
                gemail = email.getText().toString();
                gtelefono = telefono.getText().toString();

                varglob = (varGlob) getApplicationContext();
                varglob.setRut(grut);
                varglob.setDiv(gdiv);
                varglob.setNombre(gnombre);
                varglob.setApellido(gapellido);
                varglob.setTelefono(gtelefono);
                varglob.setEmail(gemail);


                /****** Agrego en base de datos en otro hilo******/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new ConexionMysqlHelper.CargarDatos().execute("http://www.webinfo.cl/soshelp/save_client.php?id_mob="+id_mob+"&rut="+grut+"&div="+gdiv+"&nom="+gnombre+"&ape="+gapellido+"&ema="+gemail+"&tel="+gtelefono);
                                Toast.makeText(getApplicationContext(),"Registrado correctamente",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                },0);

                Toast.makeText(getApplicationContext(),"Bienvenido "+gnombre+" "+gapellido, Toast.LENGTH_SHORT).show();
                Intent m = new Intent(getApplicationContext(), MapsActivity.class);
                m.putExtra("rut",grut);
                startActivity(m);


            }
        });

    }

}

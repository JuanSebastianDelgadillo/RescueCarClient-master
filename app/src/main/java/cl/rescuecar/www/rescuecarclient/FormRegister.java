package cl.rescuecar.www.rescuecarclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_access);
        //inicio SharedPreference
        Context context = this;
        final SharedPreferences sharPrefs = getSharedPreferences("DatosCliente", context.MODE_PRIVATE);
        //fin SharedPreference


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

                if (validaForm()==true){

                    SharedPreferences.Editor editor = sharPrefs.edit();
                    editor.putString("grut", grut);
                    editor.putString("gdiv", gdiv);
                    editor.putString("gnombre", gnombre);
                    editor.putString("gapellido", gapellido);
                    editor.putString("gemail", gemail);
                    editor.putString("gtelefono", gtelefono);
                    editor.commit();

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

                Toast.makeText(getApplicationContext(),"Bienvenido "+gnombre+" "+gapellido, Toast.LENGTH_SHORT).show();Intent m = new Intent(getApplicationContext(), RevisaVehiculo.class);
                m.putExtra("rut",grut);
                m.putExtra("div",gdiv);
                m.putExtra("nom",gnombre);
                m.putExtra("ape",gapellido);
                m.putExtra("tel",gtelefono);
                m.putExtra("ema",gemail);
                startActivity(m);
                }
            }
        });

    }

    private Boolean validaForm(){

        int ruti=0, dvi=0, nomi=0, api=0, emi=0, teli=0;

        if (rut.length()!=0){
            grut = rut.getText().toString();
            ruti=1;
        }else{
            rut.setHint("¡ Debe completar este campo !");
            rut.setHintTextColor(Color.RED);
        }

        if (div.length()!=0){
            gdiv = div.getText().toString();
            dvi=1;
        }else{
            div.setHint("¡ !");
            div.setHintTextColor(Color.RED);
        }
        if (nombre.length()!=0){
            gnombre = nombre.getText().toString();
            nomi=1;
        }else{
            nombre.setHint("¡ Debe completar este campo !");
            nombre.setHintTextColor(Color.RED);
        }
        if (apellido.length()!=0){
            gapellido = apellido.getText().toString();
            api=1;
        }else{
            apellido.setHint("¡ Debe completar este campo !");
            apellido.setHintTextColor(Color.RED);
        }

        if (email.length()!=0){
            gemail = email.getText().toString();
            emi=1;
        }else{
            email.setHint("¡ Debe completar este campo !");
            email.setHintTextColor(Color.RED);
        }
        if (telefono.length()!=0){
            gtelefono= telefono.getText().toString();
            teli=1;
        }else{
            telefono.setHint("¡ Debe completar este campo !");
            telefono.setHintTextColor(Color.RED);
        }

        if (ruti==1 && dvi==1 && nomi==1 && api==1 && emi==1 && teli==1) {

            return true;

        }else{

            return false;

        }

    }

}

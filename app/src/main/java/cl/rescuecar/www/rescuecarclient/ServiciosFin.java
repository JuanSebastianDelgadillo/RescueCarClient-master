package cl.rescuecar.www.rescuecarclient;

/**
 * Created by juansebastian on 4/20/17.
 */

public class ServiciosFin {
    private String Tit, SubTit, Tipo;
    private int Icono;

    public ServiciosFin(String tit, String subTit, String tipo, int icono) {
        Tit = tit;
        SubTit = subTit;
        Tipo = tipo;
        Icono = icono;
    }

    public String getTit() {
        return Tit;
    }

    public void setTit(String tit) {
        Tit = tit;
    }

    public String getSubTit() {
        return SubTit;
    }

    public void setSubTit(String subTit) {
        SubTit = subTit;
    }

    public String getTipo() { return Tipo; }

    public void setTipo(String tipo) { Tipo = tipo; }

    public int getIcono() {
        return Icono;
    }

    public void setIcono(int icono) {
        Icono = icono;
    }


}

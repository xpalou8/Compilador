/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.t_symbols;

/**
 *
 * @author Usuario
 */
public class Tipo {

    private TipoSubyacente tsb;
    private int ocupacion;
//    private int rangoI;
//    private int rangoF;  

    public Tipo(TipoSubyacente t, int n) {
        this.tsb = t;
        ocupacion = n;
    }

    public TipoSubyacente getTsb() {
        return tsb;
    }

    public void setTsb(TipoSubyacente tsb) {
        this.tsb = tsb;
    }

    public int getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(int ocupacion) {
        this.ocupacion = ocupacion;
    }

//    public int getRangoI() {
//        return rangoI;
//    }
//
//    public void setRangoI(int rangoI) {
//        this.rangoI = rangoI;
//    }
//
//    public int getRangoF() {
//        return rangoF;
//    }
//
//    public void setRangoF(int rangoF) {
//        this.rangoF = rangoF;
//    }
    
    
    

}





class tSubprog extends Tipo {

    public tSubprog(TipoSubyacente tsb, int ocupacion) {
        super(tsb, ocupacion);
    }
}

class targ extends Tipo {

    public targ(TipoSubyacente tsb, int ocupacion) {
        super(tsb, ocupacion);
    }
}

class literal extends Tipo{
    public literal(TipoSubyacente tsb, int ocupacion) {
        super(tsb, ocupacion);
    }
}

//ttabla, tproc, tfunc, targ, 


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.t_symbols;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Usuario
 */
public class t_symbols {

    private int ambitoActual;
    private int ambitoActualNoPrimario;
    private ArrayList<Integer> ta;
    private HashMap<Integer, Te_elemento> te;
    private HashMap<String, Td_elemento> td;
    private int errores;

    public t_symbols() {
        ambitoActual = 0;
        ta = new ArrayList();
        te = new HashMap();
        td = new HashMap();
        errores = 0;
        ta.add(ambitoActual, 0);
        ambitoActualNoPrimario = 0;
    }

    public boolean vaciar() {

        while (td.size() != 0) {
            td.remove(0);

        }
        while (ta.size() != 0) {
            ta.remove(0);
        }
        ambitoActual = 0;
        ta.add(ambitoActual, 0);
        return true;
    }

    public boolean entrarbloqueNoprimario() {
        ambitoActualNoPrimario += 10;

        return true;
    }

    public boolean entrarbloque() {
        ambitoActual = ambitoActual + 1;
        ambitoActualNoPrimario = ambitoActual;
        ta.add(-1);
        ta.set(ambitoActual, ta.get(ambitoActual - 1));
        return true;
    }
    
    public String getIdCamp(int id) {
        if(id == 0 || id == -1) {
            return "";
        }
        return te.get(id).getIdCamp();
    }

    public Descripcion consultar(String id) {

        if (td.containsKey(id)) {
            if ((this.ambitoActualNoPrimario % 10) == this.ambitoActual) {
                if (((td.get(id).getAmbito() % 10) == ambitoActual) && ((td.get(id).getAmbito() <= ambitoActualNoPrimario))) {
                    return td.get(id).getD();
                } else if (td.get(id).getAmbito() == 0 && ((id.equals("Int")
                        || (id.equals("Bool") || (id.equals("Char") || (id.equals("True")
                        || (id.equals("False")))))))) {
                    return td.get(id).getD();
                } else if (td.get(id).getAmbito() == 0
                        && ((td.get(id).getD().td instanceof DFunc) || (td.get(id).getD().td instanceof DProc))) {
                    return td.get(id).getD();
                }
            } else {
                if (td.get(id).getAmbito() == ambitoActual) {
                    return td.get(id).getD();
                } else if (td.get(id).getAmbito() == 0 && ((id.equals("Int")
                        || (id.equals("Bool") || (id.equals("Char") || (id.equals("True")
                        || (id.equals("False")))))))) {
                    return td.get(id).getD();

                } else if (td.get(id).getAmbito() == 0
                        && ((td.get(id).getD().td instanceof DFunc) || (td.get(id).getD().td instanceof DProc))) {
                    return td.get(id).getD();
                }
            }

        }
        return null;
    }

    public boolean salirBloque() {
        int lini = 0;
        int lfin = 0;
        lini = ta.get(ambitoActual);
        ambitoActual = ambitoActual - 1;
        ambitoActualNoPrimario = ambitoActual;
        lfin = ta.get(ambitoActual);
        while (lini > lfin) {
            if (te.get(lini).getAmbito() != -1) {
                String id = te.get(lini).getId();
                td.get(id).setAmbito(te.get(lini).getAmbito());
                td.get(id).setD(te.get(lini).getD());
                td.get(id).setFirst(te.get(lini).getNext());

            }
            lini = lini - 1;
        }
        ArrayList<String> ids = new ArrayList();
        for (String key : td.keySet()) {
            if (td.get(key).getAmbito() == (this.ambitoActual + 1)) {
                ids.add(key);
            }
        }
        for (int i = 0; i < ids.size(); i++) {
            td.remove(ids.get(i));
        }

        return true;

    }

    public boolean salirBloqueNoPrimario() {
        boolean fin = false;
        ArrayList<String> ids = new ArrayList();
        for (String key : td.keySet()) {
            if (td.get(key).getAmbito() == this.ambitoActualNoPrimario) {
                ids.add(key);

            }
        }
        for (int i = 0; i < ids.size(); i++) {
            td.remove(ids.get(i));
        }
        this.ambitoActualNoPrimario -= 10;

        return true;

    }

    public boolean ponerIndice(String id, Descripcion d) {
        Descripcion da = td.get(id).getD();
        if ((da.td instanceof DTipus)) {
            DTipus ti = (DTipus) da.td;
            if (!(ti.getTipo() instanceof ttabla)) {
                errores++;
                return false;

            }
        } else {
            errores++;
            return false;
        }

        int idxe = td.get(id).getFirst();
        int idxep = 0;
        while (idxe != 0) {
            idxep = idxe;
            idxe = te.get(idxe).getNext();
        }
        idxe = ta.get(ambitoActual);
        idxe = idxe + 1;
        ta.set(ambitoActual, idxe);
        te.put(idxe, new Te_elemento());
        te.get(idxe).setIdCamp(null);
        te.get(idxe).setAmbito(-1);
        te.get(idxe).setD(d);
        te.get(idxe).setNext(0);
        if (idxep == 0) {
            td.get(id).setFirst(idxe);

        } else {
            te.get(idxep).setNext(idxe);
        }

        return true;

    }

    public int first(String id) {
        Descripcion d = td.get(id).getD();
        if (d.td instanceof DTipus) {
            DTipus tipo = (DTipus) d.td;
            if (!(tipo.getTipo() instanceof ttabla)) {
                return -1;

            }
        } else if (!(d.td instanceof DFunc) && !(d.td instanceof DProc)) {
            return -1;
        }

        return td.get(id).getFirst();
    }

    public int next(int idx) {
        if (te.get(idx).getNext() == 0) {
            return -1;

        }
        return te.get(idx).getNext();
    }

    public boolean last(int idx) {
        return te.get(idx).getNext() == 0;
    }

    public Descripcion consulta(int idx) {
        if(idx == 0){
            return null;
        }
        return te.get(idx).getD();
    }

    public Descripcion consultar2(String id, String id2) {
        if (td.containsKey(id)) {
            return td.get(id).getD();
        } else {
            int idxe = this.first(id2);
            Descripcion d = this.consulta(idxe);
            return d;
        }
    }

    public boolean poner(String id, Descripcion d) {
        if ((this.ambitoActualNoPrimario % 10) == this.ambitoActual) {
            if (td.get(id) != null) {
                if (td.get(id).getAmbito() == ambitoActualNoPrimario || td.get(id).getAmbito() == ambitoActual) {
                    System.out.println("ERROR: símbolo definida anteriormente");
                    errores++;
                    return false;
                }

            } else {
                Td_elemento tde = new Td_elemento(d, ambitoActualNoPrimario);
                td.put(id, tde);
            }
            td.get(id).setAmbito(ambitoActualNoPrimario);
            td.get(id).setD(d);
        } else {
            if (td.get(id) != null) {
                if (td.get(id).getAmbito() == ambitoActual) {
                    System.out.println("ERROR: símbolo definida anteriormente");
                    errores++;
                    return false;
                } else {
                    int idxe = ta.get(ambitoActual);
                    idxe = idxe + 1;
                    while ((te.containsKey(idxe))) {
                        idxe = idxe + 1;
                    }
                    ta.set(ambitoActual, idxe);
                    te.put(idxe, new Te_elemento(id, td.get(id).getAmbito(), td.get(id).getD()));
                }

            } else {
                Td_elemento tde = new Td_elemento(d, ambitoActual);
                td.put(id, tde);
            }
            td.get(id).setAmbito(ambitoActual);
            td.get(id).setD(d);
        }

        return true;
    }

    public boolean ponerAmbitoNoPrimario(String id, Descripcion d) {
        if (td.get(id) != null) {
            if (td.get(id).getAmbito() == this.ambitoActualNoPrimario) {
                System.out.println("ERROR: símbolo definida anteriormente");
                errores++;
                return false;
            } else if (td.get(id).getAmbito() == this.ambitoActual) {
                System.out.println("ERROR: símbolo definida anteriormente");
                errores++;
                return false;
            }

        } else {
            Td_elemento tde = new Td_elemento(d, this.ambitoActualNoPrimario);
            td.put(id, tde);
        }
        td.get(id).setAmbito(this.ambitoActualNoPrimario);
        td.get(id).setD(d);
        return true;
    }

    //Subprogramas
    public boolean ponerParam(String idpr, String idparam, Descripcion d) {
        Descripcion daux;
        daux = td.get(idpr).getD();
        if (!(daux.td instanceof DProc) && !(daux.td instanceof DFunc)) {
            return false;
        }
        int idxe = td.get(idpr).getFirst();
        int idxep = 0;

        while ((idxe != 0) && (te.get(idxe).getId() != idparam)) {
            idxep = idxe;
            idxe = te.get(idxe).getNext();
        }

        if (idxe != 0) {
            return false;
        }

        idxe = ta.get(ambitoActual);
        idxe++;
//        ta.remove(ambitoActual);
        ta.set(ambitoActual, idxe);
        te.put(idxe, new Te_elemento());
        te.get(idxe).setIdCamp(idparam);
        te.get(idxe).setAmbito(-1);
        te.get(idxe).setD(d);
        te.get(idxe).setNext(0);
        if (idxep == 0) {
            td.get(idpr).setFirst(idxe);

        } else {
            te.get(idxep).setNext(idxe);

        }

        return true;

    }

    public void actualiza(String id, Descripcion dt) {
        Td_elemento el = td.get(id);
        el.setD(dt);
        td.replace(id, el);

    }
    

    public String imprimirTablaSymbols() {
        String resultado = "***********************Tabla de símbolos***********************\n";
        resultado += "Ámbito actual: " + ambitoActual + "\n";
        resultado += "Tabla de expansión:\n";
        for (Integer key : te.keySet()) {
            resultado += "ámbito: " + te.get(key).getAmbito() + " id_simbolo: " + te.get(key).getIdCamp() + "\n";
        }

        resultado += "Tabla de descripción:\n";
        for (String key : td.keySet()) {
            resultado += "ámbito: " + td.get(key).getAmbito() + " id_simbolo: " + key + "\n";
        }
        return resultado;
    }

    public boolean isIn() {
        if (this.ambitoActualNoPrimario == this.ambitoActual + 10) {
            return true;

        } else {
            return false;
        }
    }

}

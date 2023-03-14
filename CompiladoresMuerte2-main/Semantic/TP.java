/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

import java.util.HashMap;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class TP {

    private int np;
    private HashMap<Integer, Procedimiento> procs;

    public TP() {
        np = 0;
        procs = new HashMap();

    }

    public int nou_proc() {
        np++;
        Procedimiento proc = new Procedimiento();
        procs.put(np, proc);

        return np;
    }

    public Procedimiento consulta(int np) {
        return procs.get(np);
    }

    public HashMap<Integer, Procedimiento> getProcs() {
        return procs;
    }

    public void setProcs(HashMap<Integer, Procedimiento> procs) {
        this.procs = procs;
    }
    
    public String imprimirTP(){
        String s = "*****************TABLA DE PROCEDIMIENTOS***********************\n";
        for(Integer key : procs.keySet()){
            s += "Subprograma "+key+" :"+ procs.get(key).imprimirProcedimiento()+"\n";
        }
        return s;
    }


    
}


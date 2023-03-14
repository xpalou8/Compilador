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
public class TV {
    
    private int nv;
    private int nvt;
    private TP tp;
//    private String id;
    private HashMap<Integer, Variable> ids;

    public HashMap<Integer, Variable> getIds() {
        return ids;
    }
    
    public TV(TP tp){
        nv = 0;
        ids = new HashMap();
        this.tp = tp;
    }
    
    public int nova_var(int np, boolean f, boolean t, String name, int ocup, String tipo){
        nv++;
        int rtnValue = 0;
        Variable v;
        if(t) {
            nvt++;
            v = new Variable(np, f, t, tipo, nvt);
            rtnValue = nvt;
        } else {
            v = new Variable(np, f, t, name, ocup, tipo);
            rtnValue = nv;
        }
        
        ids.put(nv, v);
        if(np != 0) {
            tp.consulta(np).setNumVar(tp.consulta(np).getNumVar()+1);
        }
        return rtnValue;
    }
    
    public int nova_var(int np, boolean f, boolean t, String name, int ocup){
        nv++;
        int rtnValue = 0;
        Variable v;
        if(t) {
            nvt++;
            v = new Variable(np, f, t, "", nvt);
            rtnValue = nvt;
        } else {
            v = new Variable(np, f, t, name, ocup, "");
            rtnValue = nvt;
        }
        
        ids.put(nv, v);
        if(np != 0) {
            tp.consulta(np).setNumVar(tp.consulta(np).getNumVar()+1);
        }
        return rtnValue;
    }
    
    public Variable consulta(int n){
        return ids.get(n);
    }
    
    public int getNvVar(String s, int proc){
        Variable aux;
        for(Integer key : ids.keySet()){
            aux=ids.get(key);
            if (!aux.isTemporal() && aux.getVarName().equals(s) && aux.getSubprog()==proc) {
                return key;
                
            }
        }
        return -1;
    }
    public int getNV() {
        return nv;
    }
    
    public String imprimirTV(){
        String s="****************TABLA DE VARIABLES****************\n";
        for(Integer key: ids.keySet()){
            s +="Variable "+key+": "+ ids.get(key).imprimirVariable()+"\n";
        }
        return s;
    }
    
}

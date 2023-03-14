/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class Variable{
    private int subprog;
    private boolean isParametro;
    private boolean temporal;
    private String varName;
    private String tipo;
    private int ocup;
    private int nvt;
    
    public boolean isTemporal() {
        return temporal;
    }

    public void setTemporal(boolean isTemporal) {
        this.temporal = isTemporal;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }
   
    public Variable(int proc, boolean f, boolean t, String tipo, int n){
        isParametro = f;
        subprog = proc;
        temporal = t;
        ocup = 1;
        this.nvt = n;
        this.tipo = tipo;
        this.varName = "t" + this.nvt;
    }
    
    public Variable(int proc, boolean f, boolean t, String varName, int ocup, String tipo){
        this.varName = varName;
        isParametro = f;
        subprog = proc;
        temporal = t;
        this.ocup = ocup;
        this.tipo = tipo;
        this.nvt = 0;
    }

    public int getNvt() {
        return nvt;
    }

    public int getOcup() {
        return ocup;
    }

    public void setOcup(int ocup) {
        this.ocup = ocup;
    }

    public int getSubprog() {
        return subprog;
    }

    public void setSubprog(int subprog) {
        this.subprog = subprog;
    }

    public boolean isIsParametro() {
        return isParametro;
    }

    public void setIsParametro(boolean isParametro) {
        this.isParametro = isParametro;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String imprimirVariable(){
        String s = this.varName + ",\t subprograma: " + this.subprog+",\t isParametro: "+ this.isParametro + ",\t isTemporal: " + this.temporal + "\t ocupacion: " + this.ocup;
        return s;
    }
    
    
    
}

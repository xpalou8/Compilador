/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class Procedimiento {

    private C3DirEtiqueta ei;
    private int profundidad;
    private int numVar;
    private int numParam;
    private boolean isProcedure;
    
    public String imprimirProcedimiento(){
        String s ="";
        s += "etiqueta inicial: "+ this.ei + ",\tnúmero de variables: "+ numVar+",\t número de parámetros: "+ numParam+ ",\t isProcedure: " + isProcedure;
        return s;
    }

    public void setIsProcedure(boolean isProcedure) {
        this.isProcedure = isProcedure;
    }

    public boolean isIsProcedure() {
        return isProcedure;
    }

    public Procedimiento() {

    }

    public C3DirEtiqueta getEi() {
        return ei;
    }

    public void setEi(C3DirEtiqueta ei) {
        this.ei = ei;
    }

    public int getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(int profundidad) {
        this.profundidad = profundidad;
    }

    public int getNumVar() {
        return numVar;
    }

    public void setNumVar(int numVar) {
        this.numVar = numVar;
    }

    public int getNumParam() {
        return numParam;
    }

    public void setNumParam(int numParam) {
        this.numParam = numParam;
    }

}


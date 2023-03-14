/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class C3DirVariable extends C3DirOp{
    private String variable;
    private int nv;
    private boolean isParam;
    
    public C3DirVariable(String v, int n) {
        this.variable = v;
        nv = n;
        isParam = false;
    }

    @Override
    public String toString() {
        if(!isParam){
            return variable+"_"+nv;
        }else{
            return "param_"+variable+"_"+nv;
        }
        
    }

    public boolean isIsParam() {
        return isParam;
    }

    public void setIsParam(boolean isParam) {
        this.isParam = isParam;
    }

    public int getNv() {
        return nv;
    }

    public void setNv(int nv) {
        this.nv = nv;
    }
    
    public String getVariable() {
        return variable;
    }
    
}

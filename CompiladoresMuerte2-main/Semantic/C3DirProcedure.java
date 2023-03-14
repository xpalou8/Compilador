/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class C3DirProcedure extends C3DirOp{
    private int indice;
    private String id;
    private boolean isProcedure;
    
    public C3DirProcedure(int i, String id, boolean isProcedure) {
        this.indice = i;
        this.id= id;
        this.isProcedure = isProcedure;
    }

    @Override
    public String toString() {
        return "" + id;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public boolean getisProcedure() {
        return isProcedure;
    }
    
    
    
    
    
}

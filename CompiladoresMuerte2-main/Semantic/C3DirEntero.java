/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class C3DirEntero extends C3DirOp{
    private int entero;
    
    public C3DirEntero(int e) {
        this.entero = e;
    }

    @Override
    public String toString() {
        return "" + entero;
    }
    
    public void setEntero(int entero) {
        this.entero = entero;
    }
    
    public int getEntero() {
        return this.entero;
    }
}

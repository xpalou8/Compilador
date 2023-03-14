/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class C3DirEtiqueta extends C3DirOp{
    private int nEtiqueta;
    
    public C3DirEtiqueta(int n) {
        this.nEtiqueta = n;
    }

    @Override
    public String toString() {
        return "e" + nEtiqueta;
    }
    
    
}

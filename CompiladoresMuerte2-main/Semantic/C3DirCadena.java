/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class C3DirCadena extends C3DirOp{
    private String cadena;
    private int nCadena;
    
    public C3DirCadena(String c, int n) {
        this.cadena = c;
        this.nCadena = n;
    }
    
    @Override
    public String toString() {
        return this.cadena;
    }
    
    public int getNCadena() {
        return nCadena;
    }
}

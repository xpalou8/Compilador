/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class C3DirTVariable extends C3DirOp{
    private int nVariable;
    
    
    public C3DirTVariable(int n) {
        this.nVariable = n;
    }

    public int getnVariable() {
        return nVariable;
    }

    @Override
    public String toString() {
        return "t" + nVariable;
    }
    
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class C3DirAccesoArray extends C3DirOp{
    private C3DirOp indice;
    private C3DirOp variable;
    
    public C3DirAccesoArray(C3DirOp v, C3DirOp i) {
        this.variable = v;
        this.indice = i;
    }

    public C3DirOp getIndice() {
        return indice;
    }

    public C3DirOp getVariable() {
        return variable;
    }
    
    @Override
    public String toString() {
        return variable.toString() + "[" + indice.toString() + "]";
    }
    
}

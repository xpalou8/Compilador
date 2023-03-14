/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompiladoresMuerte2.Semantic;

import java.util.ArrayList;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 */
public class C3Dir {

    public class Codi {

        C3DirOps operacio = null;
        C3DirOp operand1 = null;
        C3DirOp operand2 = null;
        C3DirOp desti = null;

        public Codi(C3DirOps operacio, C3DirOp operand1, C3DirOp operand2, C3DirOp desti) {
            this.operacio = operacio;
            this.operand1 = operand1;
            this.operand2 = operand2;
            this.desti = desti;
        }

        public C3DirOps getOperacio() {
            return operacio;
        }

        public C3DirOp getOp1() {
            return operand1;
        }

        public C3DirOp getOp2() {
            return operand2;
        }

        public C3DirOp getDesti() {
            return desti;
        }

        public boolean isOperand(C3DirOp op) {
            return (this.getOp1() == op) || (this.getOp2() == op);
        }

        public boolean isDesti(C3DirOp op) {
            return this.getDesti() == op;
        }

        public String show_3dircode() {
            String resultado = "";
            resultado += operacio
                    + " " + operand1.toString();

            if (operand1 instanceof C3DirVacio) {
                resultado += " "
                        + operand2.toString();
            } else {
                resultado += ", "
                        + operand2.toString();
            }

            if (operand2 instanceof C3DirVacio) {
                resultado += " "
                        + desti.toString() + "\n";;
            } else {
                resultado += ", " + desti.toString() + "\n";;
            }
            return resultado;
        }
    }

    private ArrayList<Codi> Codi3direcciones = new ArrayList<>();
    private int en = 0;

    //metodo que genera un codigo en 3 direcciones
    public void genera(C3DirOps codi, C3DirOp operand1, C3DirOp operand2, C3DirOp desti) {
        Codi3direcciones.add(new Codi(codi, operand1, operand2, desti));
    }

    //metodo que genera una nueva etiqueta
    public C3DirEtiqueta genera_etiqueta() {
        en = en + 1;
        return new C3DirEtiqueta(en);
    }

    //metodo que muestra el codigo de 3 direcciones
    public String show_3dircode() {
        String resultado = "";
        for (int i = 0; i < Codi3direcciones.size(); i++) {
            resultado += Codi3direcciones.get(i).operacio
                    + " " + Codi3direcciones.get(i).operand1.toString();

            if (Codi3direcciones.get(i).operand1 instanceof C3DirVacio) {
                resultado += " "
                        + Codi3direcciones.get(i).operand2.toString();
            } else {
                resultado += ", "
                        + Codi3direcciones.get(i).operand2.toString();
            }

            if (Codi3direcciones.get(i).operand2 instanceof C3DirVacio) {
                resultado += " "
                        + Codi3direcciones.get(i).desti.toString() + "\n";
            } else {
                if((Codi3direcciones.get(i).operacio == C3DirOps.IFEQ) || 
                        (Codi3direcciones.get(i).operacio == C3DirOps.IFLT)||
                        (Codi3direcciones.get(i).operacio == C3DirOps.IFGT)){
                    resultado += ", GOTO " + Codi3direcciones.get(i).desti.toString() + "\n";
                }else{
                    resultado += ", " + Codi3direcciones.get(i).desti.toString() + "\n";
                }
                
            }
        }
        return resultado;
    }

    //metodo que devuelve el codigo
    public Codi getCodi(int i) {
        return this.Codi3direcciones.get(i);
    }

    public ArrayList<Codi> getCodi() {
        return this.Codi3direcciones;
    }

    //metodo que devuelve la longitud del codigo
    public int getLength() {
        return this.Codi3direcciones.size();
    }

}

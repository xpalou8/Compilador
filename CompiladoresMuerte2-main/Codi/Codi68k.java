/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CompiladoresMuerte2.Codi;

import CompiladoresMuerte2.Semantic.C3Dir;
import CompiladoresMuerte2.Semantic.C3Dir.Codi;
import CompiladoresMuerte2.Semantic.Generacion_C3Dir;
import CompiladoresMuerte2.Semantic.TP;
import CompiladoresMuerte2.Semantic.C3DirOp;
import CompiladoresMuerte2.Semantic.C3DirVariable;
import CompiladoresMuerte2.Semantic.C3DirCadena;
import CompiladoresMuerte2.Semantic.C3DirEntero;
import CompiladoresMuerte2.Semantic.C3DirTVariable;
import CompiladoresMuerte2.Semantic.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author XAMAP
 */
public class Codi68k {

    private String resultado = "";
    private C3Dir codi;
    private TP tp;
    private TV tv;
    private boolean inSubprog;
    ArrayList<C3DirOp> parametrosRecoger = new ArrayList();
    private int nInputs;

    public Codi68k(C3Dir c, Generacion_C3Dir g3dir) {
        this.codi = c;
        this.tp = g3dir.return_tp();
        this.tv = g3dir.return_tv();
        inSubprog = false;
    }

    int t, t2, sCounter;
    ArrayList<String> s = new ArrayList<>();
    //hashmap para saber el string que carga una variable y mostrarlo por pantalla
    HashMap<Integer, String> strings = new HashMap<>();

    //ArrayList para printear los programas
    ArrayList<String> Codigo = new ArrayList<>();

    //pila de subprogramas
    Stack<String> param_prog = new Stack<>();

    //Método que genera el código ensamblador a partir del código 3 direcciones
    public void genera() {
        for (int i = 0; i < codi.getLength(); i++) {
            Codi c = codi.getCodi(i);
            switch (c.getOperacio()) {
                case COPY:
                    String op1;
                    String desti;
                    resultado += ";" + c.show_3dircode() + "\n";
                    if (c.getOp1() instanceof C3DirCadena) {
                        s.add(c.getOp1().toString());
                        op1 = "#MESSAGE" + ((C3DirCadena) c.getOp1()).getNCadena();
                    } else if (c.getOp1() instanceof C3DirTVariable || c.getOp1() instanceof C3DirVariable) {
                        op1 = "(" + c.getOp1().toString() + ")";
                    } else if (c.getOp1() instanceof C3DirAccesoArray) {
                        resultado += "\tLEA " + ((C3DirAccesoArray) c.getOp1()).getVariable() + ",A1\n\tADDA.L "
                                + ((C3DirAccesoArray) c.getOp1()).getIndice() + ",A1\n";
                        op1 = "(A1)";
                    } else {
                        op1 = "#" + c.getOp1().toString();
                    }

                    if (c.getDesti() instanceof C3DirAccesoArray) {
                        resultado += "\tLEA " + ((C3DirAccesoArray) c.getDesti()).getVariable() + ",A1\n\tADDA.L "
                                + ((C3DirAccesoArray) c.getDesti()).getIndice() + ",A1\n";
                        desti = "(A1)";
                    } else {
                        desti = c.getDesti().toString();
                    }
                    resultado += "\tMOVE.L " + op1 + "," + desti + "\n";
                    break;
                case NEG:
                    resultado += ";" + c.show_3dircode() + "\n";
                    resultado += "\tMOVE.L #" + c.getOp1().toString() + ",D0\n";
                    resultado += "\tNEG.L D0\n";
                    resultado += "\tMOVE.L D0," + c.getDesti().toString() + "\n";
                    break;
                case ADD:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tADD.L D0,D1\n";
                    resultado += "\tMOVE.L D1,(" + c.getDesti().toString() + ")\n";
                    break;
                case SUB:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tSUB.L D0,D1\n";
                    resultado += "\tMOVE.L D1,(" + c.getDesti().toString() + ")\n";
                    break;
                case PROD:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tMULS D0,D1\n";
                    resultado += "\tMOVE.L D1,(" + c.getDesti().toString() + ")\n";
                    break;
                case DIV:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tDIVS D0,D1\n";
                    resultado += "\tMOVE.L D1,(" + c.getDesti().toString() + ")\n";
                    break;
                case AND:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tAND.L D0,D1\n";
                    resultado += "\tMOVE.L D1,(" + c.getDesti().toString() + ")\n";
                    break;
                case OR:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tOR.L D0,D1\n";
                    resultado += "\tMOVE.L D1,(" + c.getDesti().toString() + ")\n";
                    break;
                case IFLT:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tCMP.L D1,D0\n";
                    resultado += "\tBLT ." + c.getDesti() + "\n";
                    break;
                case IFGT:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tCMP.L D1,D0\n";
                    resultado += "\tBGT ." + c.getDesti() + "\n";
                    break;
                case IFEQ:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tCMP.L D1,D0\n";
                    resultado += "\tBEQ ." + c.getDesti() + "\n";
                    break;
                case IFNE:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tCMP.L D1,D0\n";
                    resultado += "\tBNE ." + c.getDesti() + "\n";
                    break;
                case IFGE:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tCMP.L D1,D0\n";
                    resultado += "\tBGE ." + c.getDesti() + "\n";
                    break;
                case IFLE:
                    resultado += ";" + c.show_3dircode() + "\n";
                    prepareOperators(c);
                    resultado += "\tCMP.L D1,D0\n";
                    resultado += "\tBLE ." + c.getDesti() + "\n";
                    break;
                case GOTO:
                    resultado += ";" + c.show_3dircode() + "\n";
                    resultado += "\tBRA ." + c.getDesti() + "\n";
                    break;

                case SKIP:
                    resultado += ";" + c.show_3dircode() + "\n";
//                    Integer[] nps = (Integer[]) tp.getProcs().keySet().toArray();
//                    for (int j = 0; j < nps.length; j++) {
//                        Procedimiento p = tp.consulta(nps[j]);
//                    }
                    //mirar si la estiqueta destino pertenece a un subprograma para crearlo sin punto y hacerlo subrutina
                    resultado += "." + c.getDesti() + "\n";
                    //mirar si la estiqueta destino pertenece a un subprograma para crearlo sin punto y hacerlo subrutina
                    break;
                case OUT:
                    resultado += ";" + c.show_3dircode() + "\n";
                    if (c.getDesti() instanceof C3DirVariable) {
                        if (tv.consulta(((C3DirVariable) c.getDesti()).getNv()).getTipo() == "String") {
                            resultado += "\tMOVE.L " + c.getDesti().toString() + ",A2\n";
                            resultado += "\tLEA (A2),A1\n";
                            resultado += "\tMOVE.B #13,D0\n";
                        } else {
                            resultado += "\tMOVE.L (" + c.getDesti().toString() + "),D1\n";
                            resultado += "\tMOVE.B #3,D0\n";
                        }
                    } else if (c.getDesti() instanceof C3DirTVariable) {
                        resultado += "\tMOVE.L (" + c.getDesti().toString() + "),D1\n";
                        resultado += "\tMOVE.B #3,D0\n";
                    } else if (c.getDesti() instanceof C3DirCadena) {
                        s.add(c.getDesti().toString());
                        strings.put(s.size() - 1, c.getDesti().toString());
                        resultado += "\tLEA MESSAGE" + (s.size() - 1) + ",A1\n";
//                        resultado += "\tMOVE.L #" + (strings.get(s.size() - 1).length() - 2) + ",D1\n";
                        resultado += "\tMOVE.B #13,D0\n";
                    } else if (c.getDesti() instanceof C3DirAccesoArray) {
                        resultado += "\tLEA " + ((C3DirAccesoArray) c.getDesti()).getVariable() + ",A1\n\tADDA.L "
                                + ((C3DirAccesoArray) c.getDesti()).getIndice() + ",A1\n";
                        resultado += "\tMOVE.L (A1),D1\n";
                        resultado += "\tMOVE.B #3,D0\n";
                    } else { // entero
                        resultado += "\tMOVE.L #" + c.getDesti().toString() + ", D1\n";
                        resultado += "\tMOVE.B #3,D0\n";
                    }
                    resultado += "\tTRAP #15\n";
                    break;
                case ININT:
                    resultado += ";" + c.show_3dircode() + "\n";
                    resultado += "\tMOVE.B #4,D0\n";
                    resultado += "\tTRAP #15\n";
                    resultado += "\tMOVE.L D1,(" + c.getDesti().toString() + ")\n";
                    break;
                case INSTRING:
                    resultado += ";" + c.show_3dircode() + "\n";
                    resultado += "\tMOVE.B #2,D0\n";
                    resultado += "\tTRAP #15\n";
                    resultado += "\tMOVE.L A1,INPUT" + nInputs + "\n";
                    resultado += "\tMOVE.L INPUT" + nInputs + ",(" + c.getDesti().toString() + ")\n";
                    nInputs++;
                    break;
                case PARAM_S:
                    resultado += ";" + c.show_3dircode() + "\n";
                    if (c.getDesti() instanceof C3DirTVariable || c.getDesti() instanceof C3DirVariable) {
                        resultado += "\tMOVE.L " + c.getDesti().toString() + "," + "-(A7)\n";
                    } else {
                        resultado += "\tMOVE.L #" + c.getDesti().toString() + "," + "-(A7)\n";
                    }
                    this.parametrosRecoger.add(c.getDesti());
                    break;
                case CALL:
                    resultado += ";" + c.show_3dircode() + "\n";
                    int id = ((C3DirProcedure) c.getOp1()).getIndice();
                    int numPar = tp.consulta(id).getNumParam();

                    //
                    ArrayList<Variable> parametros = new ArrayList();
                    int encontrados = 0;

                    for (int l = 1; encontrados != numPar; l++) {
                        if (tv.consulta(l) != null) {
                            if (tv.consulta(l).isIsParametro() && tv.consulta(l).getSubprog() == id) {
                                parametros.add(tv.consulta(l));
                                encontrados++;
                            }
                        }
                    }
                    //
                    if (!((C3DirProcedure) c.getOp1()).getisProcedure()) {
                        resultado += "\tMOVE.L " + "#0" + "," + "-(A7)\n";
                    }
                    resultado += "\tJSR " + c.getOp1() + "\n";
                    if (!((C3DirProcedure) c.getOp1()).getisProcedure()) {
                        if (c.getDesti() instanceof C3DirAccesoArray) {
                            resultado += "\tLEA " + ((C3DirAccesoArray) c.getDesti()).getVariable() + ",A1\n\tADDA.L "
                                    + ((C3DirAccesoArray) c.getDesti()).getIndice() + ",A1\n";
                            resultado += "\tMOVE.L (A7)+" + ",(A1)\n";
                        } else {
                            resultado += "\tMOVE.L (A7)+" + "," + c.getDesti().toString() + "\n";
                        }
                    }
                    for (int j = parametrosRecoger.size() - 1; j >= 0; j--) {
                        if (!((C3DirProcedure) c.getOp1()).getisProcedure()) {
                            resultado += "\tMOVE.L #-1" + "," + "(A7)+\n";
                        } else {
                            if (!(parametrosRecoger.get(j) instanceof C3DirEntero || parametrosRecoger.get(j) instanceof C3DirBooleano)) {
                                resultado += "\tMOVE.L (A7)+," + parametrosRecoger.get(j).toString() + "\n";
                            } else {
                                resultado += "\tMOVE.L (A7)+,D0\n";
                            }
                        }
                    }
                    this.parametrosRecoger.clear();

                    this.inSubprog = true;

                    break;
                case PMB:
                    resultado += ";" + c.show_3dircode() + "\n";
                    resultado += c.getDesti() + "\n";
                    int id2 = ((C3DirProcedure) c.getDesti()).getIndice();
                    int numPar2 = tp.consulta(id2).getNumParam();
                    int m = 0;
                    if (!((C3DirProcedure) c.getDesti()).getisProcedure()) {
                        m = 8;
                    } else {
                        m = 4;
                    }
                    ArrayList<Variable> parametros2 = new ArrayList();
                    int encontrados2 = 0;

                    for (int l = 1; encontrados2 != numPar2; l++) {
                        if (tv.consulta(l) != null) {
                            if (tv.consulta(l).isIsParametro() && tv.consulta(l).getSubprog() == id2) {
                                parametros2.add(tv.consulta(l));
                                encontrados2++;
                            }
                        }
                    }
                    for (int j = 0; j < numPar2; j++) {
                        resultado += "\tMOVE.L " + m + "(A7)" + "," + "param_" + parametros2.get(j).getVarName() + "_" + (tv.getNvVar(parametros2.get(j).getVarName(), parametros2.get(j).getSubprog())) + "\n";
                        m = m + 4;
                    }

                    break;
                case RTN:
                    resultado += ";" + c.show_3dircode() + "\n";
                    if (!(c.getOp1() instanceof C3DirVacio)) {
                        if (c.getOp1() instanceof C3DirVariable || c.getOp1() instanceof C3DirTVariable) {
                            resultado += "\tMOVE.L " + c.getOp1().toString() + "," + "4(A7)\n";
                        } else {
                            resultado += "\tMOVE.L #" + c.getOp1().toString() + "," + "4(A7)\n";
                        }

                    } else {
                        int id3 = ((C3DirProcedure) c.getDesti()).getIndice();
                        int numPar3 = tp.consulta(id3).getNumParam();
                        int m2 = 4;
                        ArrayList<Variable> parametros3 = new ArrayList();
                        int encontrados3 = 0;

                        for (int l = 1; encontrados3 != numPar3; l++) {
                            if (tv.consulta(l) != null) {
                                if (tv.consulta(l).isIsParametro() && tv.consulta(l).getSubprog() == id3) {
                                    parametros3.add(tv.consulta(l));
                                    encontrados3++;
                                }
                            }
                        }
                        for (int j = 0; j < numPar3; j++) {
                            resultado += "\tMOVE.L " + "param_" + parametros3.get(j).getVarName() + "_" + (tv.getNvVar(parametros3.get(j).getVarName(), parametros3.get(j).getSubprog())) + "," + m2 + "(A7)" + "\n";
                            m2 = m2 + 4;
                        }
                    }
                    this.inSubprog = false;

                    resultado += "\tRTS\n";
                    if (finalSubprograma(c.getDesti(), i)) {
                        Codigo.add(resultado);
                        resultado = "";
                    }
                    break;
                default:
            }
        }
    }

    private void prepareOperators(Codi c) {

        if (c.getOp1() instanceof C3DirVariable || c.getOp1() instanceof C3DirTVariable) {
            resultado += "\tMOVE.L (" + c.getOp1().toString() + "),D0\n";
        } else if (c.getOp1() instanceof C3DirAccesoArray) {
            resultado += "\tLEA " + ((C3DirAccesoArray) c.getOp1()).getVariable() + ",A1\n\tADDA.L "
                    + ((C3DirAccesoArray) c.getOp1()).getIndice() + ",A1\n";
            resultado += "\tMOVE.L (A1),D0\n";
        } else {
            resultado += "\tMOVE.L #" + c.getOp1().toString() + ",D0\n";
        }

        if (c.getOp2() instanceof C3DirVariable || c.getOp2() instanceof C3DirTVariable) {
            resultado += "\tMOVE.L (" + c.getOp2().toString() + "),D1\n";
        } else if (c.getOp2() instanceof C3DirAccesoArray) {
            resultado += "\tLEA " + ((C3DirAccesoArray) c.getOp2()).getVariable() + ",A1\n\tADDA.L "
                    + ((C3DirAccesoArray) c.getOp2()).getIndice() + ",A1\n";
            resultado += "\tMOVE.L (A1),D0\n";
        } else {
            resultado += "\tMOVE.L #" + c.getOp2().toString() + ",D1\n";
        }
    }
//       

//    private void crearBloque(C3DirOp op1){
//        int BP = op1.distancia;
//        int ambito = op1.ambito;
//        if (op1 instanceof C3DirVariable) {
//            resultado += "\tMOVE.L " + (t2 == 0 ? "" : t2 * 4) + "(A7)" + ",D1\n";
//        }
//    }
//    
//    private void storeVariable(C3DirOp op1){
//        int BP = op1.distancia;
//        int ambito = op1.ambito;
//        if (op1 instanceof C3DirVariable) {
//            resultado += "\tMOVE.L " + (t2 == 0 ? "" : t2 * 4) + "(A7)" + ",D1\n";
//        }
//    }
    public boolean finalSubprograma(C3DirOp c, int i) {
        for (int j = i + 1; j < codi.getLength(); j++) {
            if ((codi.getCodi(j).getOperacio() == C3DirOps.RTN) && (((C3DirProcedure) codi.getCodi(j).getDesti()).getIndice() == ((C3DirProcedure) c).getIndice())) {
                return false;
            }
        }
        return true;
    }

    public String getResultado() {
        String ret = "";
        ret += "\t ORG    $1000\n";
        ret += "START:\n";
        ret += resultado;
        ret += "\tSIMHALT\n";

        //Recorrido de los subprogramas
        for (int i = Codigo.size() - 1; i >= 0; i--) {
            ret += Codigo.get(i);
        }

        //Strings utilizados
        for (int i = 0; i < s.size(); i++) {
            ret += "MESSAGE" + i + "\tDC.L\t" + "'" + s.get(i).replace("\"", "") + (getStringLen(s.get(i).replace("\"", "")) % 4 == 0 ? " '" : "'") + "\n";
        }

        for (int i = 0; i < nInputs; i++) {
            ret += "INPUT" + i + "\tDC.L\t1\n";
        }

        //Declaración de variables
        for (int i = 1; i <= tv.getNV(); i++) {
            if (tv.consulta(i) != null) {
                if (tv.consulta(i).isTemporal()) {
                    ret += "t" + tv.consulta(i).getNvt() + "\tDS.L\t1\n";
                } else if (tv.consulta(i).isIsParametro()) {
                    ret += "param_" + tv.consulta(i).getVarName() + "_" + i + "\tDS.L\t" + tv.consulta(i).getOcup() + "\n";
                } else {
                    ret += tv.consulta(i).getVarName() + "_" + i + "\tDS.L\t" + tv.consulta(i).getOcup() + "\n";
                }
            }
        }
        //final
        ret += "\tEND    START";

        return ret;
    }

    public int getStringLen(String s) {
        String result = new String(s.getBytes(), StandardCharsets.US_ASCII);
        return result.length();
    }
}

/*++
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CompiladoresMuerte2.Semantic;

import CompiladoresMuerte2.Sintactic.Nodo;
import CompiladoresMuerte2.Sintactic.Tipo;
import CompiladoresMuerte2.t_symbols.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard
 * Medina Martorell
 *
 */
public class Generacion_C3Dir_Opt {

    private C3Dir codi;
    private TV tv;
    private C3DirVacio vacio = new C3DirVacio();
    private Generacion_C3Dir gc3dir;

    public Generacion_C3Dir getGc3dir() {
        return gc3dir;
    }

    C3DirEntero v0 = new C3DirEntero(0);
    C3DirEntero v1 = new C3DirEntero(1);

    //la generacion del codigo de 3 direcciones se hace mediante un 
    //arbol sintactico
    public Generacion_C3Dir_Opt(Generacion_C3Dir c) {
        this.gc3dir = c;
        this.codi = this.gc3dir.getCodi();
        this.tv = this.gc3dir.return_tv();
    }

    public void optimitzar() {

        brancaments_adjacents();
        brancaments_sobre_brancaments();
        operacions_constants();
        eliminar_inaccesible();
        simplificar_ifs();
        eliminar_etiquetes();
        llevar_gotos();
        rentar_TV();
    }

    public void eliminar_inaccesible() {
        for (int i = 0; i < this.codi.getCodi().size() - 1; i++) {
            C3Dir.Codi cAct = this.codi.getCodi(i);
            C3Dir.Codi cSig = this.codi.getCodi(i + 1);

            if (cAct.operacio == C3DirOps.RTN && cSig.operacio == C3DirOps.RTN) {
                this.codi.getCodi().remove(i);
            }

            if (cAct.operacio == C3DirOps.GOTO) {
                int j = i + 1;
                while (!(cSig.operacio == C3DirOps.SKIP && cSig.desti == cAct.desti) && j < this.codi.getCodi().size() - 2) {
                    if (cSig.operacio == C3DirOps.SKIP) {
                        boolean trobat = false;
                        for (int k = 0; k < this.codi.getCodi().size() - 1; k++) {
                            C3Dir.Codi cAux = this.codi.getCodi(k);
                            if (cAux.desti == cAct.desti && cAux.operacio != C3DirOps.SKIP) {
                                trobat = true;
                                break;
                            }
                        }

                        if (!trobat) {
                            this.codi.getCodi().remove(cSig);
                        } else {
                            break;
                        }

                    } else {
                        this.codi.getCodi().remove(cSig);
                    }
                    j++;
                    cSig = this.codi.getCodi(j);
                }
            }
        }
    }

    public void brancaments_adjacents() {
        for (int i = 0; i < this.codi.getCodi().size() - 1; i++) {
            C3Dir.Codi cAct = this.codi.getCodi(i);
            C3Dir.Codi cSig = this.codi.getCodi(i + 1);

            if (isConditional(cAct.operacio) && cSig.operacio == C3DirOps.GOTO) {
                C3DirOp etiqueta = cSig.desti;
                C3Dir.Codi cAux = null;

                cAct.operacio = getCondContrari(cAct.operacio);
                cAct.desti = cSig.desti;
                this.codi.getCodi().remove(i + 1);
            }
        }
    }

    public boolean isConditional(C3DirOps op) {
        return (op == C3DirOps.IFEQ || op == C3DirOps.IFNE
                || op == C3DirOps.IFGT || op == C3DirOps.IFLT
                || op == C3DirOps.IFGE || op == C3DirOps.IFLE);
    }

    public C3DirOps getCondContrari(C3DirOps op) {
        C3DirOps opResult = null;
        switch (op) {
            case IFEQ:
                opResult = C3DirOps.IFNE;
                break;
            case IFNE:
                opResult = C3DirOps.IFEQ;
                break;
            case IFGT:
                opResult = C3DirOps.IFLE;
                break;
            case IFLE:
                opResult = C3DirOps.IFGT;
                break;
            case IFLT:
                opResult = C3DirOps.IFGE;
                break;
            case IFGE:
                opResult = C3DirOps.IFLT;
                break;
        }
        return opResult;
    }

    public void brancaments_sobre_brancaments() {
        for (int i = 0; i < this.codi.getCodi().size(); i++) {
            C3Dir.Codi cAct = this.codi.getCodi(i);
            if (isConditional(cAct.operacio) || cAct.operacio == C3DirOps.GOTO) {
                C3DirOp etiquetaDesti = cAct.desti;
                for (int j = 0; j < this.codi.getCodi().size() - 1; j++) {
                    C3Dir.Codi cAct2 = this.codi.getCodi(j);
                    C3Dir.Codi cSig2 = this.codi.getCodi(j + 1);
                    if (cAct2.operacio == C3DirOps.SKIP && cAct2.desti == etiquetaDesti && cSig2.operacio == C3DirOps.GOTO) {
                        substituirEtiquetes(cAct.desti, cSig2.desti);
                    }
                }
            }
        }
    }

    public void substituirEtiquetes(C3DirOp e1, C3DirOp e2) {
        for (int i = 0; i < this.codi.getCodi().size(); i++) {
            if ((isConditional(this.codi.getCodi(i).operacio)
                    || this.codi.getCodi(i).operacio == C3DirOps.GOTO)
                    && this.codi.getCodi(i).desti == e1) {
                this.codi.getCodi(i).desti = e2;
            }
        }
    }

    //Tractament d'operacions constants
    public void operacions_constants() {
        for (int i = 0; i < this.codi.getCodi().size(); i++) {
            C3Dir.Codi cAct = this.codi.getCodi(i);
            switch (cAct.operacio) {
                case IFEQ:
                    if (cAct.operand1 instanceof C3DirEntero && cAct.operand2 instanceof C3DirEntero) {
                        if (((C3DirEntero) cAct.operand1).getEntero() == ((C3DirEntero) cAct.operand2).getEntero()) {
                            cAct.operacio = C3DirOps.GOTO;
                            cAct.operand1 = vacio;
                            cAct.operand2 = vacio;
                        } else {
                            this.codi.getCodi().remove(i);
                        }
                    }
                    break;
                case IFGT:
                    if (cAct.operand1 instanceof C3DirEntero && cAct.operand2 instanceof C3DirEntero) {
                        if (((C3DirEntero) cAct.operand1).getEntero() > ((C3DirEntero) cAct.operand2).getEntero()) {
                            cAct.operacio = C3DirOps.GOTO;
                            cAct.operand1 = vacio;
                            cAct.operand2 = vacio;
                        } else {
                            this.codi.getCodi().remove(i);
                        }
                    }
                    break;
                case IFLT:
                    if (cAct.operand1 instanceof C3DirEntero && cAct.operand2 instanceof C3DirEntero) {
                        if (((C3DirEntero) cAct.operand1).getEntero() < ((C3DirEntero) cAct.operand2).getEntero()) {
                            cAct.operacio = C3DirOps.GOTO;
                            cAct.operand1 = vacio;
                            cAct.operand2 = vacio;
                        } else {
                            this.codi.getCodi().remove(i);
                        }
                    }
                    break;
                case ADD:
                    if (cAct.operand1 instanceof C3DirEntero && cAct.operand2 instanceof C3DirEntero) {
                        cAct.operacio = C3DirOps.COPY;
                        cAct.operand1 = new C3DirEntero(((C3DirEntero) cAct.operand1).getEntero() + ((C3DirEntero) cAct.operand2).getEntero());
                        cAct.operand2 = vacio;
                    }
                    break;
                case SUB:
                    if (cAct.operand1 instanceof C3DirEntero && cAct.operand2 instanceof C3DirEntero) {
                        cAct.operacio = C3DirOps.COPY;
                        cAct.operand1 = new C3DirEntero(((C3DirEntero) cAct.operand1).getEntero() - ((C3DirEntero) cAct.operand2).getEntero());
                        cAct.operand2 = vacio;
                    }
                    break;
                case PROD:
                    if (cAct.operand1 instanceof C3DirEntero && cAct.operand2 instanceof C3DirEntero) {
                        cAct.operacio = C3DirOps.COPY;
                        cAct.operand1 = new C3DirEntero(((C3DirEntero) cAct.operand1).getEntero() * ((C3DirEntero) cAct.operand2).getEntero());
                        cAct.operand2 = vacio;
                    }
                    break;
                case DIV:
                    if (cAct.operand1 instanceof C3DirEntero && cAct.operand2 instanceof C3DirEntero) {
                        cAct.operacio = C3DirOps.COPY;
                        cAct.operand1 = new C3DirEntero(((C3DirEntero) cAct.operand1).getEntero() * ((C3DirEntero) cAct.operand2).getEntero());
                        cAct.operand2 = vacio;
                    }
                    break;
            }
        }
    }

    public void simplificar_ifs() {
        for (int i = 0; i < this.codi.getCodi().size(); i++) {
            C3Dir.Codi cAct = this.codi.getCodi(i);
            if (isConditional(cAct.operacio) && !inLogicOp(cAct, i) && inIf(cAct, i)) {
                for (int j = 0; j < 6; j++) {
                    this.codi.getCodi().remove(i + 1);
                }
                cAct.desti = this.codi.getCodi(i + 1).desti;
                this.codi.getCodi().remove(i + 1);
            }
        }
    }

    public boolean inLogicOp(C3Dir.Codi cAct, int i) {
        if (i + 14 < this.codi.getLength() && i - 1 > 0) {
            return (this.codi.getCodi(i + 14).operacio == C3DirOps.AND
                    || this.codi.getCodi(i + 7).operacio == C3DirOps.AND
                    || this.codi.getCodi(i + 14).operacio == C3DirOps.OR
                    || this.codi.getCodi(i + 7).operacio == C3DirOps.OR
                    || this.codi.getCodi(i - 1).operacio == C3DirOps.AND
                    || this.codi.getCodi(i - 1).operacio == C3DirOps.OR);
        }
        return false;
    }

    public boolean inIf(C3Dir.Codi cAct, int i) {
        if (i + 7 < this.codi.getLength()) {
            return isConditional(codi.getCodi(i + 7).operacio);
        }
        return false;
    }

    public void eliminar_etiquetes() {
        for (int i = 0; i < this.codi.getCodi().size(); i++) {
            if (this.codi.getCodi(i).operacio == C3DirOps.SKIP) {
                C3DirEtiqueta e = (C3DirEtiqueta) this.codi.getCodi(i).desti;
                boolean trobat = false;
                for (int j = 0; j < this.codi.getCodi().size(); j++) {
                    if (this.codi.getCodi(j).operacio != C3DirOps.SKIP && this.codi.getCodi(j).desti == e) {
                        trobat = true;
                        break;
                    }
                }
                if (!trobat) {
                    this.codi.getCodi().remove(i);
                } else {
                    if ((i + 1 < this.codi.getCodi().size()) && this.codi.getCodi(i + 1).operacio == C3DirOps.SKIP) {
                        C3DirEtiqueta e2 = (C3DirEtiqueta) this.codi.getCodi(i + 1).desti;
                        for (int j = 0; j < this.codi.getCodi().size(); j++) {
                            if ((isConditional(this.codi.getCodi(j).operacio) || this.codi.getCodi(j).operacio == C3DirOps.GOTO) && this.codi.getCodi(j).desti == e2) {
                                this.codi.getCodi(j).desti = e;
                            }
                        }
                        this.codi.getCodi().remove(i + 1);
                    }
                }

            }

        }
    }

    public void llevar_gotos() {
        for (int i = 0; i < this.codi.getCodi().size(); i++) {
            C3Dir.Codi cAct = this.codi.getCodi(i);
            if (isConditional(cAct.operacio) || cAct.operacio == C3DirOps.GOTO) {
                if (i + 1 < this.codi.getCodi().size() && this.codi.getCodi(i + 1).operacio == C3DirOps.SKIP && this.codi.getCodi(i + 1).desti == cAct.desti) {
                    this.codi.getCodi().remove(i);
                }
            }
        }
    }

    public void rentar_TV() {
        ArrayList<Integer> keys = new ArrayList();
        for (Integer k : tv.getIds().keySet()) {
            Variable v = tv.consulta(k);
            if (v.isTemporal()) {
                if (notInCode(v)) {
                    keys.add(k);
                }
            }
        }
        
        for (int i = 0; i < keys.size(); i++) {
            tv.getIds().remove(keys.get(i));
        }
        
        this.gc3dir.setTv(this.tv);
    }

    public boolean notInCode(Variable v) {
        for (int i = 0; i < this.codi.getCodi().size(); i++) {
            if (hasVariable(this.codi.getCodi(i), v.getNvt())) {
                return false;
            }
        }
        return true;
    }

    public boolean hasVariable(C3Dir.Codi c, int k) {
        if (c.desti instanceof C3DirTVariable) {
            if (((C3DirTVariable) c.desti).getnVariable() == k) {
                return true;
            }
        }

        if (c.operand1 instanceof C3DirTVariable) {
            if (((C3DirTVariable) c.operand1).getnVariable() == k) {
                return true;
            }
        }

        if (c.operand2 instanceof C3DirTVariable) {
            if (((C3DirTVariable) c.operand2).getnVariable() == k) {
                return true;
            }
        }
        return false;
    }

    public C3Dir getCodi() {
        return codi;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CompiladoresMuerte2.Semantic;

import CompiladoresMuerte2.Sintactic.Nodo;
import CompiladoresMuerte2.Sintactic.Tipo;
import CompiladoresMuerte2.t_symbols.*;
import java.util.Stack;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard
 * Medina Martorell
 *
 */
public class Generacion_C3Dir {

    private C3Dir codi;
    private TV tv;
    private TP tp;
    private t_symbols ts;
    private int nv = 0;
    private int np = 0;
    private int p = 0;
    private int nCadenas = 0;
    private Stack<Integer> profundidad;
    C3DirVacio vacio = new C3DirVacio();
    C3DirEntero v0 = new C3DirEntero(0);
    C3DirEntero v1 = new C3DirEntero(1);
    String procedureActual = "";
    boolean isProcedure = false;

    //la generacion del codigo de 3 direcciones se hace mediante un 
    //arbol sintactico
    public Generacion_C3Dir(AnalisisSemantico as) {
        this.codi = new C3Dir();
        this.tp = new TP();
        this.tv = new TV(tp);
        this.ts = as.ts;
        this.profundidad = new Stack<>();
    }

    public TV getTv() {
        return tv;
    }

    public void setTv(TV tv) {
        this.tv = tv;
    }

    public TP getTp() {
        return tp;
    }

    public void setTp(TP tp) {
        this.tp = tp;
    }

    //metodo que genera el codigo de 3 direcciones
    public void genera(Nodo nodo) {
        Nodo hijo;
        for (int i = 0; i < nodo.nSons(); i++) {
            hijo = nodo.getHijo(i);
            switch (hijo.tipoNodo()) {
                case MAIN:
                    genera_sentencias(hijo, 0, hijo);
                    break;
                case FUNCION:
                    isProcedure = false;
                    genera_subprograma(hijo);
                    break;
                case PROCEDURE:
                    isProcedure = true;
                    genera_subprograma(hijo);
                    isProcedure = false;
                    break;

            }
        }
    }

    //metodo que genera el codigo de las sentencias
    public void genera_sentencias(Nodo nodo, int proc, Nodo n) {
        Nodo hijo;
        for (int i = 0; i < nodo.nSons(); i++) {
            hijo = nodo.getHijo(i);
            switch (hijo.tipoNodo()) {
                case DECLARACION:
                case DECL_CONST:
                    genera_declaracion(hijo, proc, n);
                    break;

                case LLAMADA_SUB:
                    C3DirProcedure paux = genera_Llamada(hijo, proc, n);
                    codi.genera(C3DirOps.CALL, paux, vacio, vacio);

                    break;

                case SALIDA:
                    genera_Print(hijo, proc, n);
                    break;

                case RETURN:
                    genera_return(n, hijo, proc);
                    break;

                case LISTAASIGNACION:
                case ASIGNACION:
                    genera_lista_asignacion(hijo, proc, n);
                    break;

                case IF:
                    genera_condicional(hijo, proc, n);
                    break;

                case WHILE:
                    genera_while(hijo, proc, n);
                    break;

                case FOR:
                    genera_for(hijo, proc, n);
                    break;

                case SWITCH:
                    genera_switch(hijo, proc, n);
                    break;
            }
        }
    }

    //metodo que genera el codigo de las declaraciones
    public void genera_declaracion(Nodo nodo, int proc, Nodo prog) {
        Nodo hijo;
        String tipo = "";
        for (int i = 0; i < nodo.nSons(); i++) {
            hijo = nodo.getHijo(i);
            switch (hijo.tipoNodo()) {
                case VARIABLE:
                    for (int j = 0; j < hijo.nSons(); j++) {
                        Nodo id = hijo.getHijo(j);
                        tv.nova_var(proc, false, false, id.valor(), 1, tipo);
                    }
                    break;
                case ARRAY:
                    int ocup = 1;
                    for (int j = 0; j < hijo.nSons(); j++) {
                        Nodo dim = hijo.getHijo(j);
                        ocup = ocup * Integer.parseInt(dim.valor());
                    }
                    tv.nova_var(proc, false, false, hijo.valor(), ocup, tipo);
                    break;
                case TIPO:
                    tipo = hijo.valor();
                    break;

                case ASIGNACION:
                    tv.nova_var(proc, false, false, hijo.getHijo(0).valor(), 1, tipo);
                    genera_asignacion(hijo, proc, prog);
                    break;
            }

        }
    }

    //metodo que genera el codigo de la declaracion de un subprograma
    public void genera_subprograma(Nodo nodo) {
        Nodo hijo;
        C3DirEtiqueta ei;
        procedureActual = nodo.valor();
        int np = 0;
        np = tp.nou_proc();
        int num_param = 0;
        for (int i = 0; i < nodo.nSons(); i++) {
            hijo = nodo.getHijo(i);
            switch (hijo.tipoNodo()) {
                case LISTAPARAMS:
                    for (int j = 0; j < hijo.nSons(); j++) {
                        tv.nova_var(np, true, false, hijo.getHijo(j).getHijo(1).valor(), 1, hijo.getHijo(j).getHijo(0).valor());
                        tp.consulta(np).setNumParam((tp.consulta(np).getNumParam()) + 1);
                    }

                    break;

                case SENTENCIAS:
                    ei = codi.genera_etiqueta();
                    C3DirProcedure p = new C3DirProcedure(np, nodo.valor(), isProcedure);
                    tp.consulta(np).setEi(ei);
//                    ts.edita(proc, "dproc - " + np + ei);
                    codi.genera(C3DirOps.SKIP, vacio, vacio, ei);
                    codi.genera(C3DirOps.PMB, vacio, vacio, p);
                    genera_sentencias(hijo, np, nodo);
                    if (codi.getCodi(codi.getLength() - 1).operacio != C3DirOps.RTN) {
                        codi.genera(C3DirOps.RTN, vacio, vacio, p);
                    }
                    break;
            }
        }
    }

    public void genera_lista_asignacion(Nodo nodo, int proc, Nodo n) {
        if (nodo.tipoNodo() == Tipo.ASIGNACION) {
            genera_asignacion(nodo, proc, n);
        } else {
            for (int i = 0; i < nodo.nSons(); i++) {
                genera_asignacion(nodo.getHijo(i), proc, n);
            }
        }

    }

    //metodo que genera el codigo de una asignacion
    public void genera_asignacion(Nodo nodo, int proc, Nodo n) {
        Nodo id = nodo.getHijo(0);
        Nodo v = nodo.getHijo(1);
        int t, t1;
        Descripcion consulta;
        C3DirOp destino = null, op = null;

        switch (id.tipoNodo()) {
            case ID:
//                tv.nova_var(np, true, true, "");
                destino = genera_uso_identificador(id, proc, n);
                break;
            case ACCESOARRAY:
                destino = genera_acceso_array(id, proc, n);
                break;
        }
        switch (v.tipoNodo()) {
            case LLAMADA_SUB:
                op = genera_Llamada(v, proc, n);
                codi.genera(C3DirOps.CALL, op, vacio, destino);
                break;
            case ID:
                if (v.valor().startsWith("-")) {
                    t = tv.nova_var(proc, false, true, "", 1);
                    C3DirTVariable vtemporal = new C3DirTVariable(t);
                    consulta = proc == 0 ? ts.consultar(v.valor().replace("-", "")) : ts.consultar2(v.valor().replace("-", ""), procedureActual);
                    if (consulta.getTd() instanceof DConst) { //se mete el valor de la constante
                        DConst c2 = (DConst) consulta.getTd();
                        if (c2.getTipo().equals("int")) {
                            C3DirEntero negado = new C3DirEntero((int) c2.getValor());
                            codi.genera(C3DirOps.NEG, negado, vacio, vtemporal);
                            codi.genera(C3DirOps.COPY, vtemporal, vacio, destino);
                        }
                    } else { //se mete el identificador de la variable
                        C3DirVariable negado = new C3DirVariable(v.valor().replace("-", ""), tv.getNvVar(v.valor().replace("-", ""), proc));
                        codi.genera(C3DirOps.NEG, negado, vacio, vtemporal);
                        codi.genera(C3DirOps.COPY, vtemporal, vacio, destino);
                    }
                } else {
                    consulta = proc == 0 ? ts.consultar(v.valor()) : ts.consultar2(v.valor(), procedureActual);
                    t = tv.nova_var(proc, false, true, "", 1);
                    C3DirTVariable vtemporal = new C3DirTVariable(t);
                    if (consulta.getTd() instanceof DConst) {//se mete el valor de la constante
                        DConst c2 = (DConst) consulta.getTd();
                        if (c2.getTipo().equals("string")) {
                            nCadenas++;
                            op = new C3DirCadena(c2.getValor().toString(), nCadenas);
                            codi.genera(C3DirOps.COPY, op, vacio, vtemporal);
                            codi.genera(C3DirOps.COPY, vtemporal, vacio, destino);
                        } else if (c2.getTipo().equals("int")) {
                            op = new C3DirEntero((int) c2.getValor());
                            codi.genera(C3DirOps.COPY, op, vacio, vtemporal);
                            codi.genera(C3DirOps.COPY, vtemporal, vacio, destino);
                        } else {
                            op = new C3DirBooleano((boolean) c2.getValor());
                            codi.genera(C3DirOps.COPY, op, vacio, vtemporal);
                            codi.genera(C3DirOps.COPY, vtemporal, vacio, destino);
                        }
                    } else { //se mete el identificador de la variable
                        op = genera_uso_identificador(v, proc, n);
                        codi.genera(C3DirOps.COPY, op, vacio, vtemporal);
                        codi.genera(C3DirOps.COPY, vtemporal, vacio, destino);
                    }
                }
                break;
            case ACCESOARRAY:
                if (v.valor().startsWith("-")) {
                    t = tv.nova_var(proc, false, true, "", 1);
                    C3DirTVariable vtemporal = new C3DirTVariable(t);
                    consulta = proc == 0 ? ts.consultar(v.valor()) : ts.consultar2(v.valor(), procedureActual);
                    C3DirAccesoArray negado = genera_acceso_array(v, proc, n);
                    codi.genera(C3DirOps.NEG, negado, vacio, vtemporal);
                    codi.genera(C3DirOps.COPY, vtemporal, vacio, destino);
                } else {
                    consulta = proc == 0 ? ts.consultar(v.valor()) : ts.consultar2(v.valor(), procedureActual);
                    t = tv.nova_var(proc, false, true, "", 1);
                    C3DirTVariable vtemporal = new C3DirTVariable(t);
                    op = genera_acceso_array(v, proc, n);
                    codi.genera(C3DirOps.COPY, op, vacio, vtemporal);
                    codi.genera(C3DirOps.COPY, vtemporal, vacio, destino);
                }
                break;

            case STRING:
                op = new C3DirCadena(v.valor(), nCadenas);
                nCadenas++;
                codi.genera(C3DirOps.COPY, op, vacio, destino);
                break;

            case INT:
                op = new C3DirEntero(Integer.parseInt(v.valor()));
                codi.genera(C3DirOps.COPY, op, vacio, destino);
                break;

            case BOOL:
                op = new C3DirBooleano(v.valor().equals("True"));
                codi.genera(C3DirOps.COPY, op, vacio, destino);
                break;

            case SUM:
            case RES:
            case MULT:
            case DIV:
                op = genera_Oparitmetico(v, proc, n);
                codi.genera(C3DirOps.COPY, op, vacio, destino);
                break;

            case OPREL:
                op = genera_Oprelacional(v, proc, n);
                codi.genera(C3DirOps.COPY, op, vacio, destino);
                break;

            case OPLOG:
                op = genera_Oplogica(v, proc, n);
                codi.genera(C3DirOps.COPY, op, vacio, destino);
                break;

            case READ:
                op = genera_Input(v, proc, n);
                codi.genera(C3DirOps.COPY, op, vacio, destino);
                break;
        }
    }

    //metodo que genera una operacion aritmetica y devuelve su variable asociada
    public C3DirTVariable genera_Oparitmetico(Nodo nodo, int proc, Nodo n) {
        Nodo op1 = nodo.getHijo(0);
        Nodo op2 = nodo.getHijo(1);
        C3DirOp operador1 = null, operador2 = null;
        int v, t;

        //se procesan primero los dos operadores, ya que se tiene que resolver
        //desde la raiz del arbol donde esta la operacion mas prioritaria para 
        //finalizar procesando la menos prioritaria
        switch (op1.tipoNodo()) {
            case ID:
                operador1 = genera_uso_identificador(op1, proc, n);
                break;
            case ACCESOARRAY:
                operador1 = genera_acceso_array(op1, proc, n);
                break;
            case SUM:
            case RES:
            case MULT:
            case DIV:
                //se debe recoger la variable asociada al operador aritmetico
                operador1 = genera_Oparitmetico(op1, proc, n);
                break;
            case STRING:
                nCadenas++;
                operador1 = new C3DirCadena(op1.valor(), nCadenas);
                break;
            default:
                operador1 = new C3DirEntero(Integer.parseInt(op1.valor()));
                break;
        }

        switch (op2.tipoNodo()) {
            case ID:
                operador2 = genera_uso_identificador(op2, proc, n);
                break;
            case ACCESOARRAY:
                operador2 = genera_acceso_array(op2, proc, n);
                break;
            case SUM:
            case RES:
            case MULT:
            case DIV:
                //se debe recoger la variable asociada al operador aritmetico
                operador2 = genera_Oparitmetico(op2, proc, n);
                break;
            case STRING:
                nCadenas++;
                operador2 = new C3DirCadena(op2.valor(), nCadenas);
                break;
            default:
                operador2 = new C3DirEntero(Integer.parseInt(op2.valor()));
                break;
        }

        //se genera la variable asociada a la operacion aritmetica
        t = tv.nova_var(proc, false, true, "", 1);
        C3DirTVariable vResult = new C3DirTVariable(t);
        switch (nodo.tipoNodo()) {
            case SUM:
                codi.genera(C3DirOps.ADD, operador1, operador2, vResult);
                break;

            case RES:
                codi.genera(C3DirOps.SUB, operador1, operador2, vResult);
                break;

            case MULT:
                codi.genera(C3DirOps.PROD, operador1, operador2, vResult);
                break;

            case DIV:
                codi.genera(C3DirOps.DIV, operador1, operador2, vResult);
                break;
        }
        return vResult;
    }

    //metodo que genera una operacion condicional y devuelve su variable asociada
    public C3DirTVariable genera_Oplogica(Nodo nodo, int proc, Nodo n) {
        Nodo op1 = nodo.getHijo(0);
        Nodo op2 = nodo.getHijo(1);
        C3DirOp operador1 = null, operador2 = null;
        int t = 0;

        switch (op1.tipoNodo()) {
            case OPLOG:
                operador1 = genera_Oplogica(op1, proc, n);
                break;
            case OPREL:
                operador1 = genera_Oprelacional(op1, proc, n);
                break;
            case BOOL:
                operador1 = new C3DirBooleano(op1.valor().equals("True"));
                break;
        }

        switch (op2.tipoNodo()) {
            case OPLOG:
                operador2 = genera_Oplogica(op2, proc, n);
                break;
            case OPREL:
                operador2 = genera_Oprelacional(op2, proc, n);
                break;
            case BOOL:
                operador2 = new C3DirBooleano(op2.valor().equals("True"));
                break;
        }
        t = tv.nova_var(proc, false, true, "", 1);
        C3DirTVariable vResult = new C3DirTVariable(t);
        switch (nodo.valor()) {
            case "and":
                codi.genera(C3DirOps.AND, operador1, operador2, vResult);
                break;
            case "or":
                codi.genera(C3DirOps.OR, operador1, operador2, vResult);
                break;
        }
        return vResult;
    }

    //metodo que genera un operador relacional y devuelve su variable asociada
    public C3DirTVariable genera_Oprelacional(Nodo nodo, int proc, Nodo n) {
        Nodo op1 = nodo.getHijo(0);
        Nodo op2 = nodo.getHijo(1);
        C3DirEtiqueta e1, e2, e3;
        C3DirOp operador1 = null, operador2 = null;

        C3DirTVariable vTemporal = null;
        int v, t = 0;

        switch (nodo.valor()) {

            case "<":
                switch (op1.tipoNodo()) {
                    case ID:
                        operador1 = genera_uso_identificador(op1, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador1 = genera_acceso_array(op1, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador1 = genera_Oparitmetico(op1, proc, n);
                        break;

                    default:
                        operador1 = new C3DirEntero(Integer.parseInt(op1.valor()));
                        break;
                }
                switch (op2.tipoNodo()) {
                    case ID:
                        operador2 = genera_uso_identificador(op2, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador2 = genera_acceso_array(op2, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador2 = genera_Oparitmetico(op2, proc, n);
                        break;

                    default:
                        operador2 = new C3DirEntero(Integer.parseInt(op2.valor()));
                        break;
                }
                //devolver la variable del opRelacional
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal = new C3DirTVariable(t);
                e1 = codi.genera_etiqueta();
                e2 = codi.genera_etiqueta();
                e3 = codi.genera_etiqueta();
                codi.genera(C3DirOps.IFLT, operador1, operador2, e1);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e2);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e1);
                codi.genera(C3DirOps.COPY, v1, vacio, vTemporal);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e3);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e2);
                codi.genera(C3DirOps.COPY, v0, vacio, vTemporal);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e3);
                break;

            case ">":
                switch (op1.tipoNodo()) {
                    case ID:
                        operador1 = genera_uso_identificador(op1, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador1 = genera_acceso_array(op1, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador1 = genera_Oparitmetico(op1, proc, n);
                        break;

                    default:
                        operador1 = new C3DirEntero(Integer.parseInt(op1.valor()));
                        break;
                }
                switch (op2.tipoNodo()) {
                    case ID:
                        operador2 = genera_uso_identificador(op2, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador2 = genera_acceso_array(op2, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador2 = genera_Oparitmetico(op2, proc, n);
                        break;

                    default:
                        operador2 = new C3DirEntero(Integer.parseInt(op2.valor()));
                        break;
                }
                //devolver la variable del opRelacional
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal = new C3DirTVariable(t);
                e1 = codi.genera_etiqueta();
                e2 = codi.genera_etiqueta();
                e3 = codi.genera_etiqueta();
                codi.genera(C3DirOps.IFGT, operador1, operador2, e1);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e2);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e1);
                codi.genera(C3DirOps.COPY, v1, vacio, vTemporal);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e3);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e2);
                codi.genera(C3DirOps.COPY, v0, vacio, vTemporal);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e3);
                break;

            case "==":
                switch (op1.tipoNodo()) {
                    case ID:
                        operador1 = genera_uso_identificador(op1, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador1 = genera_acceso_array(op1, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador1 = genera_Oparitmetico(op1, proc, n);
                        break;
                    case BOOL:
                        operador1 = new C3DirBooleano(op1.valor().equals("True"));
                        break;
                    case LLAMADA_SUB:
                        operador1 = genera_Llamada(op1, proc, n);
                        break;
                    default:
                        operador1 = new C3DirEntero(Integer.parseInt(op1.valor()));
                        break;
                }
                switch (op2.tipoNodo()) {
                    case ID:
                        operador2 = genera_uso_identificador(op2, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador2 = genera_acceso_array(op2, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador2 = genera_Oparitmetico(op2, proc, n);
                        break;
                    case BOOL:
                        operador2 = new C3DirBooleano(op2.valor().equals("True"));
                        break;
                    case LLAMADA_SUB:
                        operador2 = genera_Llamada(op2, proc, n);
                        break;
                    default:
                        operador2 = new C3DirEntero(Integer.parseInt(op2.valor()));
                        break;
                }
                //devolver la variable del opRelacional
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal = new C3DirTVariable(t);
                e1 = codi.genera_etiqueta();
                e2 = codi.genera_etiqueta();
                e3 = codi.genera_etiqueta();
                codi.genera(C3DirOps.IFEQ, operador1, operador2, e1);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e2);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e1);
                codi.genera(C3DirOps.COPY, v1, vacio, vTemporal);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e3);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e2);
                codi.genera(C3DirOps.COPY, v0, vacio, vTemporal);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e3);
                break;

            case "!=":
                switch (op1.tipoNodo()) {
                    case ID:
                        operador1 = genera_uso_identificador(op1, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador1 = genera_acceso_array(op1, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador1 = genera_Oparitmetico(op1, proc, n);
                        break;
                    case BOOL:
                        operador1 = new C3DirBooleano(op1.valor().equals("True"));
                        break;
                    default:
                        operador1 = new C3DirEntero(Integer.parseInt(op1.valor()));
                        break;
                }
                switch (op2.tipoNodo()) {
                    case ID:
                        operador2 = genera_uso_identificador(op2, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador2 = genera_acceso_array(op2, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador2 = genera_Oparitmetico(op2, proc, n);
                        break;

                    case BOOL:
                        operador2 = new C3DirBooleano(op2.valor().equals("True"));
                        break;
                    default:
                        operador2 = new C3DirEntero(Integer.parseInt(op2.valor()));
                        break;
                }
                //devolver la variable del opRelacional
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal = new C3DirTVariable(t);
                e1 = codi.genera_etiqueta();
                e2 = codi.genera_etiqueta();
                e3 = codi.genera_etiqueta();
                codi.genera(C3DirOps.IFNE, operador1, operador2, e1);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e2);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e1);
                codi.genera(C3DirOps.COPY, v1, vacio, vTemporal);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e3);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e2);
                codi.genera(C3DirOps.COPY, v0, vacio, vTemporal);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e3);
                break;

            case ">=":
                switch (op1.tipoNodo()) {
                    case ID:
                        operador1 = genera_uso_identificador(op1, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador1 = genera_acceso_array(op1, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador1 = genera_Oparitmetico(op1, proc, n);
                        break;
                    case BOOL:
                        operador1 = new C3DirBooleano(op1.valor().equals("True"));
                        break;
                    default:
                        operador1 = new C3DirEntero(Integer.parseInt(op1.valor()));
                        break;
                }
                switch (op2.tipoNodo()) {
                    case ID:
                        operador2 = genera_uso_identificador(op2, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador2 = genera_acceso_array(op2, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador2 = genera_Oparitmetico(op2, proc, n);
                        break;

                    case BOOL:
                        operador2 = new C3DirBooleano(op2.valor().equals("True"));
                        break;
                    default:
                        operador2 = new C3DirEntero(Integer.parseInt(op2.valor()));
                        break;
                }
                //devolver la variable del opRelacional
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal = new C3DirTVariable(t);
                e1 = codi.genera_etiqueta();
                e2 = codi.genera_etiqueta();
                e3 = codi.genera_etiqueta();
                codi.genera(C3DirOps.IFGE, operador1, operador2, e1);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e2);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e1);
                codi.genera(C3DirOps.COPY, v1, vacio, vTemporal);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e3);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e2);
                codi.genera(C3DirOps.COPY, v0, vacio, vTemporal);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e3);
                break;

            case "<=":
                switch (op1.tipoNodo()) {
                    case ID:
                        operador1 = genera_uso_identificador(op1, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador1 = genera_acceso_array(op1, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador1 = genera_Oparitmetico(op1, proc, n);
                        break;
                    case BOOL:
                        operador1 = new C3DirBooleano(op1.valor().equals("True"));
                        break;
                    default:
                        operador1 = new C3DirEntero(Integer.parseInt(op1.valor()));
                        break;
                }
                switch (op2.tipoNodo()) {
                    case ID:
                        operador2 = genera_uso_identificador(op2, proc, n);
                        break;
                    case ACCESOARRAY:
                        operador2 = genera_acceso_array(op2, proc, n);
                        break;
                    case SUM:
                    case RES:
                    case MULT:
                    case DIV:
                        operador2 = genera_Oparitmetico(op2, proc, n);
                        break;

                    case BOOL:
                        operador2 = new C3DirBooleano(op2.valor().equals("True"));
                        break;
                    default:
                        operador2 = new C3DirEntero(Integer.parseInt(op2.valor()));
                        break;
                }
                //devolver la variable del opRelacional
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal = new C3DirTVariable(t);
                e1 = codi.genera_etiqueta();
                e2 = codi.genera_etiqueta();
                e3 = codi.genera_etiqueta();
                codi.genera(C3DirOps.IFLE, operador1, operador2, e1);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e2);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e1);
                codi.genera(C3DirOps.COPY, v1, vacio, vTemporal);
                codi.genera(C3DirOps.GOTO, vacio, vacio, e3);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e2);
                codi.genera(C3DirOps.COPY, v0, vacio, vTemporal);
                codi.genera(C3DirOps.SKIP, vacio, vacio, e3);
                break;
        }
        return vTemporal;
    }

    //metodo que genera el codigo de un bucle
    public void genera_while(Nodo nodo, int proc, Nodo n) {
        C3DirOp ei, operador = null, efi, e2;
        int t;
        Nodo hijo, hijo1;
        hijo = nodo.getHijo(0);
        hijo1 = nodo.getHijo(1);

        ei = codi.genera_etiqueta();
        codi.genera(C3DirOps.SKIP, vacio, vacio, ei);
        switch (hijo.tipoNodo()) {
            case OPLOG:
                operador = genera_Oplogica(hijo, proc, n);
                break;
            case OPREL:
                operador = genera_Oprelacional(hijo, proc, n);
                break;
            case BOOL:
                operador = new C3DirBooleano(hijo.valor().equals("True"));
                break;
        }

        efi = codi.genera_etiqueta();
        e2 = codi.genera_etiqueta();
        codi.genera(C3DirOps.IFEQ, operador, v1, e2);
        codi.genera(C3DirOps.GOTO, vacio, vacio, efi);
        codi.genera(C3DirOps.SKIP, vacio, vacio, e2);
        genera_sentencias(hijo1, proc, nodo);
        codi.genera(C3DirOps.GOTO, vacio, vacio, ei);
        codi.genera(C3DirOps.SKIP, vacio, vacio, efi);
    }

    //metodo que genera el codigo de un bucle
    public void genera_for(Nodo nodo, int proc, Nodo prog) {
        C3DirOp ei, operador = null, efi, e2;
        int t;
        Nodo hijo, hijo1, decl = null, cond = null, asig = null;
        //for args
        hijo = nodo.getHijo(0);
        //for sentencias
        hijo1 = nodo.getHijo(1);

        //tratar args
        for (int i = 0; i < hijo.nSons(); i++) {
            Nodo n = hijo.getHijo(i);
            switch (n.tipoNodo()) {
                case DECLARACION:
                    decl = n;
                    break;
                case OPLOG:
                case OPREL:
                    cond = n;
                    break;
                case ASIGNACION:
                    asig = n;
                    break;
            }
        }
        if (decl != null) {
            tv.nova_var(proc, false, false, decl.getHijo(1).getHijo(0).valor(), 1, decl.getHijo(0).valor());
            genera_asignacion(decl.getHijo(1), proc, prog);
        }

        ei = codi.genera_etiqueta();
        codi.genera(C3DirOps.SKIP, vacio, vacio, ei);
        efi = codi.genera_etiqueta();

        if (cond != null) {
            switch (cond.tipoNodo()) {
                case OPLOG:
                    operador = genera_Oplogica(cond, proc, prog);
                    break;
                case OPREL:
                    operador = genera_Oprelacional(cond, proc, prog);
                    break;
                case BOOL:
                    operador = new C3DirBooleano(cond.valor().equals("True"));
                    break;
            }
            e2 = codi.genera_etiqueta();
            codi.genera(C3DirOps.IFEQ, operador, v1, e2);
            codi.genera(C3DirOps.GOTO, vacio, vacio, efi);
            codi.genera(C3DirOps.SKIP, vacio, vacio, e2);
        }
        genera_sentencias(hijo1, proc, nodo);

        if (asig != null) {
            genera_asignacion(asig, proc, prog);
        }

        codi.genera(C3DirOps.GOTO, vacio, vacio, ei);
        codi.genera(C3DirOps.SKIP, vacio, vacio, efi);
    }

    //metodo que genera el codigo de un condicional
    public void genera_condicional(Nodo nodo, int proc, Nodo prog) {
        Nodo hijo, hijo1, hijo2;
        C3DirOp operador = null, e1, e2, e3 = null;
        int t;
        hijo = nodo.getHijo(0);
        hijo1 = nodo.getHijo(1);
        switch (hijo.tipoNodo()) {
            case OPLOG:
                operador = genera_Oplogica(hijo, proc, prog);
                break;
            case OPREL:
                operador = genera_Oprelacional(hijo, proc, prog);
                break;
            case BOOL:
                operador = new C3DirBooleano(hijo.valor().equals("True"));
                break;
        }
        e1 = codi.genera_etiqueta();
        e2 = codi.genera_etiqueta();
        codi.genera(C3DirOps.IFEQ, operador, v1, e1);
        codi.genera(C3DirOps.GOTO, vacio, vacio, e2);
        codi.genera(C3DirOps.SKIP, vacio, vacio, e1);
        genera_sentencias(hijo1, proc, prog);
        if (nodo.nSons() > 2) {
            e3 = codi.genera_etiqueta();
            codi.genera(C3DirOps.GOTO, vacio, vacio, e3);
        }
        codi.genera(C3DirOps.SKIP, vacio, vacio, e2);

        if (nodo.nSons() > 2) {
            hijo2 = nodo.getHijo(2);
            genera_sentencias(hijo2, proc, prog);
            codi.genera(C3DirOps.SKIP, vacio, vacio, e3);
        }
    }

    public void genera_switch(Nodo nodo, int proc, Nodo prog) {
        C3DirVariable v = new C3DirVariable(nodo.valor(), tv.getNvVar(nodo.valor(), proc));
        C3DirOp aux = null;
        int t;
        C3DirTVariable vTemporal = null;
        C3DirEtiqueta efi = codi.genera_etiqueta();
        for (int i = 0; i < nodo.nSons(); i++) {
            C3DirEtiqueta ei, ei2, ei3, ei4, ei5;
            Nodo n = nodo.getHijo(i);
//            switch (n.getHijo(0).tipoNodo()) {
//                case INT:
//                    aux = new C3DirEntero(Integer.parseInt(n.valor()));
//                    break;
//                case STRING:
//                    nCadenas++;
//                    aux = new C3DirCadena(n.valor(), nCadenas);
//                    break;
//            }
            if (n.valor() != null) {
                aux = new C3DirEntero(Integer.parseInt(n.valor()));
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal = new C3DirTVariable(t);
                ei = codi.genera_etiqueta();
                ei2 = codi.genera_etiqueta();
                ei3 = codi.genera_etiqueta();
                codi.genera(C3DirOps.IFEQ, aux, v, ei);
                codi.genera(C3DirOps.GOTO, vacio, vacio, ei2);
                codi.genera(C3DirOps.SKIP, vacio, vacio, ei);
                codi.genera(C3DirOps.COPY, v1, vacio, vTemporal);
                codi.genera(C3DirOps.GOTO, vacio, vacio, ei3);
                codi.genera(C3DirOps.SKIP, vacio, vacio, ei2);
                codi.genera(C3DirOps.COPY, v0, vacio, vTemporal);
                codi.genera(C3DirOps.SKIP, vacio, vacio, ei3);

                ei4 = codi.genera_etiqueta();
                ei5 = codi.genera_etiqueta();
                codi.genera(C3DirOps.IFEQ, vTemporal, v1, ei4);
                codi.genera(C3DirOps.GOTO, vacio, vacio, ei5);
                codi.genera(C3DirOps.SKIP, vacio, vacio, ei4);
                if (n.nSons() > 0) {
                    genera_sentencias(n, proc, prog);
                }
                codi.genera(C3DirOps.GOTO, vacio, vacio, efi);
                codi.genera(C3DirOps.SKIP, vacio, vacio, ei5);

            } else {
                if (n.nSons() > 0) {
                    genera_sentencias(n, proc, prog);
                }
            }
        }
//        genera_sentencias(nodo.getSon(0), proc, null);
        codi.genera(C3DirOps.SKIP, vacio, vacio, efi);
    }

    public void genera_return(Nodo n, Nodo nodo, int proc) {
        C3DirProcedure np = new C3DirProcedure(proc, n.valor(), n.tipoNodo() == Tipo.PROCEDURE);
        C3DirOp operador;
        Nodo hijo = nodo.getHijo(0);
        if (n.tipoNodo() == Tipo.FUNCION) {
            switch (hijo.tipoNodo()) {
                case OPREL:
                    operador = genera_Oprelacional(hijo, proc, n);
                    break;
                case BOOL:
                    operador = new C3DirBooleano(hijo.valor().equals("True"));
                    break;
                case ID:
                    operador = genera_uso_identificador(hijo, proc, n);
                    break;
                case ACCESOARRAY:
                    operador = genera_acceso_array(hijo, proc, n);
                    break;
                case SUM:
                case RES:
                case MULT:
                case DIV:
                    operador = genera_Oparitmetico(hijo, proc, n);
                    break;
                case STRING:
                    nCadenas++;
                    operador = new C3DirCadena(hijo.valor(), nCadenas);
                    break;
                default:
                    operador = new C3DirEntero(Integer.parseInt(hijo.valor()));
                    break;
            }
            codi.genera(C3DirOps.RTN, operador, vacio, np);
        } else {
            codi.genera(C3DirOps.RTN, vacio, vacio, np);
        }

    }
    //metodo que genera el codigo de un print

    public void genera_Print(Nodo nodo, int proc, Nodo n) {
        Nodo hijo = nodo.getHijo(0);
        String tipo = null;
        C3DirOp operador = null;
        int t;
        switch (hijo.tipoNodo()) {
            case OPLOG:
                operador = genera_Oplogica(hijo, proc, n);
                break;
            case OPREL:
                operador = genera_Oprelacional(hijo, proc, n);
                break;
            case ID:
                operador = genera_uso_identificador(hijo, proc, n);
                break;
            case ACCESOARRAY:
                operador = genera_acceso_array(hijo, proc, n);
                break;
            case SUM:
            case RES:
            case MULT:
            case DIV:
                operador = genera_Oparitmetico(hijo, proc, n);
                break;
            case STRING:
                nCadenas++;
                operador = new C3DirCadena(hijo.valor(), nCadenas);
                break;
            case INT:
                operador = new C3DirEntero(Integer.parseInt(hijo.valor()));
                break;
            case BOOL:
                operador = new C3DirBooleano(hijo.valor().equals("True"));
                break;
        }
        codi.genera(C3DirOps.OUT, vacio, vacio, operador);
    }

    //metodo que general el codigo de un input
    public C3DirTVariable genera_Input(Nodo nodo, int proc, Nodo n) {
        int t = tv.nova_var(proc, false, true, "", 1);
        C3DirTVariable vTemporal = new C3DirTVariable(t);
        if (nodo.valor() == "Int") {
            codi.genera(C3DirOps.ININT, vacio, vacio, vTemporal);
        } else {
            codi.genera(C3DirOps.INSTRING, vacio, vacio, vTemporal);
        }

        return vTemporal;
    }

    //metodo que genera el codigo de una llamada
    public C3DirProcedure genera_Llamada(Nodo nodo, int proc, Nodo n) {
        Nodo hijo;
        C3DirOp parametro;
        Descripcion d;
        C3DirProcedure np;
        Stack<C3DirOp> param = new Stack<>();
        Stack<Integer> obj = new Stack<>();
        int act;
        int i;
        //si tiene parametros gestionamos el tipo y el numero 
        if (nodo.nSons() > 0) {
            //conseguimos el primer parametro
            act = ts.first(nodo.valor());
            for (i = 0; i < nodo.nSons() && act != -1; i++) {
                hijo = nodo.getHijo(i);
                switch (hijo.tipoNodo()) {
                    case ID:
                        parametro = genera_uso_identificador(hijo, proc, n);
                        param.push(parametro);
                        obj.push(act);
                        break;
                    case STRING:
                        nCadenas++;
                        parametro = new C3DirCadena(hijo.valor(), nCadenas);
                        param.push(parametro);
                        obj.push(act);
                        break;
                    case INT:
                        parametro = new C3DirEntero(Integer.parseInt(hijo.valor()));
                        param.push(parametro);
                        obj.push(act);
                        break;
                    case BOOL:
                        parametro = new C3DirBooleano(hijo.valor().equals("True"));
                        param.push(parametro);
                        obj.push(act);
                        break;
                    case ACCESOARRAY:
                        parametro = genera_acceso_array(hijo, proc, n);
                        param.push(parametro);
                        obj.push(act);
                        break;

                }
                act = ts.next(act);
            }
            //desempilamos los parametros y generamos la llamada
            while (!param.empty()) {
                parametro = param.pop();
                if (parametro instanceof C3DirAccesoArray) {
                    codi.genera(C3DirOps.PARAM_C, ((C3DirAccesoArray) parametro).getIndice(), vacio, ((C3DirAccesoArray) parametro).getVariable());
                } else {
                    codi.genera(C3DirOps.PARAM_S, vacio, vacio, parametro); //Is this really null, null, param?

                }

            }
            //generamos la llamada 

//            codi.genera("call", vacio, vacio, np); //Pillar el numero de procedimiento y crear un C3DirProcedure para llamarlo
        }
        d = proc == 0 ? ts.consultar(nodo.valor()) : ts.consultar2(nodo.valor(), procedureActual);

        if (ts.consultar(nodo.valor()).td instanceof DProc) {
            np = new C3DirProcedure(((DProc) d.td).getNp(), nodo.valor(), true);
        } else {
            np = new C3DirProcedure(((DFunc) d.td).getNp(), nodo.valor(), false);
        }

        return np;
    }

    public C3DirOp genera_uso_identificador(Nodo nodo, int proc, Nodo n) {
        C3DirVariable v = new C3DirVariable(nodo.valor(), tv.getNvVar(nodo.valor(), proc));
        if (tv.consulta(v.getNv()).isIsParametro()) {
            v.setIsParam(true);
        }

        if (nodo.valor().startsWith("-")) {
            int t = tv.nova_var(proc, false, true, "", 1);
            C3DirTVariable vTemp = new C3DirTVariable(t);
            codi.genera(C3DirOps.NEG, v, vacio, vTemp);
            return vTemp;
        }

        return v;
    }

    public C3DirAccesoArray genera_acceso_array(Nodo nodo, int proc, Nodo n) {
        Descripcion d = proc == 0 ? ts.consultar(nodo.valor()) : ts.consultar2(nodo.valor(), procedureActual);
        String tipo = ((ts_array) ((ttabla) ((DTipus) d.getTd()).getTipo()).getTsb()).getTipoElemental();
        C3DirEntero nbytes = new C3DirEntero(4);
        int dimensiones = nodo.nSons();
        int t, t2, t3, index;
        C3DirEntero indice1, indice2;
        Descripcion desc;
        C3DirTVariable vTemporal1, vTemporal2, vTemporal3, indice;
        switch (dimensiones) {
            case 1:
                indice1 = new C3DirEntero(Integer.parseInt(nodo.getHijo(0).valor()));
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal1 = new C3DirTVariable(t);
                codi.genera(C3DirOps.PROD, indice1, nbytes, vTemporal1);
                indice = vTemporal1;
                break;
            case 2:
                indice1 = new C3DirEntero(Integer.parseInt(nodo.getHijo(0).valor()));
                indice2 = new C3DirEntero(Integer.parseInt(nodo.getHijo(1).valor()));
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal1 = new C3DirTVariable(t);
                index = ts.first(nodo.valor());
                desc = ts.consulta(index);
                int dimension2 = ((DIndex) desc.getTd()).getSize();
                C3DirEntero d2 = new C3DirEntero(dimension2);
                codi.genera(C3DirOps.PROD, indice1, d2, vTemporal1);
                t2 = tv.nova_var(proc, false, true, "", 1);
                vTemporal2 = new C3DirTVariable(t2);
                codi.genera(C3DirOps.ADD, vTemporal1, indice2, vTemporal2);
                t3 = tv.nova_var(proc, false, true, "", 1);
                vTemporal3 = new C3DirTVariable(t3);
                codi.genera(C3DirOps.PROD, vTemporal2, nbytes, vTemporal3);
                indice = vTemporal3;
                break;
            default:
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal1 = new C3DirTVariable(t);
                indice1 = new C3DirEntero(Integer.parseInt(nodo.getHijo(0).valor()));
                index = ts.first(nodo.valor());
                desc = ts.consulta(index);
                t = tv.nova_var(proc, false, true, "", 1);
                vTemporal1 = new C3DirTVariable(t);
                codi.genera(C3DirOps.COPY, indice1, vacio, vTemporal1);
                for (int i = 1; i < dimensiones; i++) {
                    index = ts.next(index);
                    desc = ts.consulta(index);
                    int nextDimension = ((DIndex) desc.getTd()).getSize();
                    C3DirEntero nd = new C3DirEntero(nextDimension);
                    t2 = tv.nova_var(proc, false, true, "", 1);
                    vTemporal2 = new C3DirTVariable(t2);
                    codi.genera(C3DirOps.PROD, vTemporal1, nd, vTemporal2);
                    indice2 = new C3DirEntero(Integer.parseInt(nodo.getHijo(i).valor()));
                    t = tv.nova_var(proc, false, true, "", 1);
                    vTemporal1 = new C3DirTVariable(t);
                    codi.genera(C3DirOps.ADD, vTemporal2, indice2, vTemporal1);
                }
                t3 = tv.nova_var(proc, false, true, "", 1);
                vTemporal3 = new C3DirTVariable(t3);
                codi.genera(C3DirOps.PROD, vTemporal1, nbytes, vTemporal3);
                indice = vTemporal3;
                break;
        }
        C3DirVariable v = new C3DirVariable(nodo.valor(), tv.getNvVar(nodo.valor(), proc));

        if (nodo.valor().startsWith("-")) {
            int taux = tv.nova_var(proc, false, true, "", 1);
            C3DirTVariable vTemp = new C3DirTVariable(taux);
            codi.genera(C3DirOps.NEG, v, vacio, vTemp);
            return new C3DirAccesoArray(vTemp, indice);
        }

        return new C3DirAccesoArray(v, indice);
    }

    //string del 3 dir
    public String get_codi() {
        return codi.show_3dircode();
    }

    //return del codigo de 3 direciones
    public C3Dir getCodi() {
        return codi;
    }

    //return tp 
    public TP return_tp() {
        return tp;
    }

    //return tv 
    public TV return_tv() {
        return tv;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CompiladoresMuerte2.Semantic;

import CompiladoresMuerte2.t_symbols.ttabla;
import CompiladoresMuerte2.t_symbols.tInt;
import CompiladoresMuerte2.Sintactic.Nodo;
import CompiladoresMuerte2.Sintactic.*;
import CompiladoresMuerte2.Sintactic.Tipo;
import CompiladoresMuerte2.t_symbols.*;
import CompiladoresMuerte2.t_symbols.Descripcion;
import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;

/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard
 * Medina Martorell
 */
public class AnalisisSemantico {

    //Atributs globals
    public t_symbols ts;
    private int numErrors;

    private int nv, np;
    private int profundidad = -1;
    private ArrayList<String> programa;

    private Nodo arbol;
    private ArrayList<String> errores;
    private String inSubprogram;

    public AnalisisSemantico(Nodo main) {
        this.ts = new t_symbols();
        this.numErrors = 0;
        nv = 0;
        np = 0;
        this.programa = new ArrayList<>();
        errores = new ArrayList();
        Descripcion d = new Descripcion();
        d.setTd(new DTipus());
        ((DTipus) d.getTd()).setTipo(new tBool(new ts_boolean(), 1));
        ((ts_boolean) (((tBool) ((DTipus) d.getTd()).getTipo()).getTsb())).setLimitInf(0);
        ((ts_boolean) (((tBool) ((DTipus) d.getTd()).getTipo()).getTsb())).setLimitSup(1);
        ts.poner("Bool", d);
        inSubprogram = "";
        d = new Descripcion();
        d.setTd(new DConst());
        ((DConst) d.getTd()).setTipo("bool");
        ((DConst) d.getTd()).setValor(0);
        ts.poner("False", d);

        d = new Descripcion();
        d.setTd(new DConst());
        ((DConst) d.getTd()).setTipo("bool");
        ((DConst) d.getTd()).setValor(-1);
        ts.poner("True", d);

        d = new Descripcion();
        d.setTd(new DTipus());
        ((DTipus) d.getTd()).setTipo(new tInt(new ts_entero(), 4));
        ((ts_entero) (((tInt) ((DTipus) d.getTd()).getTipo()).getTsb())).setLimitInf(Integer.MIN_VALUE);
        ((ts_entero) (((tInt) ((DTipus) d.getTd()).getTipo()).getTsb())).setLimitSup(Integer.MAX_VALUE);
        ts.poner("Int", d);

        d = new Descripcion();
        d.setTd(new DTipus());
        ((DTipus) d.getTd()).setTipo(new tChar(new ts_Char(), 4));
        ((ts_Char) (((tChar) ((DTipus) d.getTd()).getTipo()).getTsb())).setLimitInf(0);
        ((ts_Char) (((tChar) ((DTipus) d.getTd()).getTipo()).getTsb())).setLimitSup(255);
        ts.poner("String", d);

    }

    public int getNumErrors() {
        return numErrors;
    }

    public t_symbols getTs() {
        return ts;
    }

    public ArrayList<String> getErrores() {
        return errores;
    }

    public void setErrores(ArrayList<String> errores) {
        this.errores = errores;
    }

    public boolean analisis_previo(Nodo nodo) {
        Nodo nodoMain = null;
        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.getSon(i).tipoNodo()) {
                case MAIN:
                    nodoMain = arbol.getSon(i);

                    break;
                case FUNCION:
                    gestionFuncion(arbol.getSon(i));
                    this.inSubprogram = "";

                    break;
                case PROCEDURE:
                    gestionProcedure(arbol.getSon(i));
                    this.inSubprogram = "";
                    break;

            }
        }

        if (nodoMain != null) {
            analisis_semantico(nodoMain);
        }

        return true;
    }

    //metodo que analiza la semantica de un arbol de sintaxis
    public boolean analisis_semantico(Nodo nodo) {

        profundidad += 1;
        programa.add(profundidad, "main");
        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.getSon(i).tipoNodo()) {
                case DECLARACION:
                    if (!gestionDeclaraciones(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case FUNCION:
                    this.report_error("No es posible crear subprogramas dentro de subprogramas.", nodo);
//                    if (!gestionFuncion(arbol.getSon(i))) {
//                        numErrors++;
//                    }
                    break;
                case PROCEDURE:
                    this.report_error("No es posible crear subprogramas dentro de subprogramas.", nodo);
//                    if (!gestionProcedure(arbol.getSon(i))) {
//                        numErrors++;
//                    }
                    break;
                case DECL_CONST:
                    if (!gestionDeclConstante(arbol.getSon(i))) {
                        numErrors++;
                    }

                    break;
                //Operacion
                case LISTAASIGNACION:
                    if (!gestionListaAsignacion(arbol.getSon(i), "")) {
                        numErrors++;
                    }

                    break;
                case IF:
                    if (!gestionIf(arbol.getSon(i), "")) {
                        numErrors++;
                    }

                    break;
                case FORARGS:
                    if (!gestionForArgs(arbol.getSon(i))) {
                        numErrors++;
                    }

                    break;
                case FOR:
                    if (!gestionFor(arbol.getSon(i), "")) {
                        numErrors++;
                    }

                    break;
                case WHILE:
                    if (!gestionWhile(arbol.getSon(i), "")) {
                        numErrors++;
                    }

                    break;
                case SWITCH:
                    if (!gestionSwitch(arbol.getSon(i), "")) {
                        numErrors++;
                    }

                    break;
                //Llamada_sub
                case LLAMADA_SUB:
                    if (!gestionLlamadaSub(arbol.getSon(i), false)) {
                        numErrors++;
                    }

                    break;
                //Salida_pantalla
                case SALIDA:
                    if (!gestionSalida(arbol.getSon(i))) {
                        numErrors++;
                    }

                    break;
                //RETURN
                case RETURN:
                    this.report_error("Sentencia return en main", arbol.getSon(i));

                    break;
            }
        }
        profundidad -= 1;
        return true;
    }

    //Gestión de sentencias
    public boolean gestionSentencias(Nodo nodo, String subp) {

        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.getSon(i).tipoNodo()) {
                //Decl
                case DECLARACION:
                    if (!gestionDeclaraciones(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case FUNCION:
                    this.report_error("No es posible crear subprogramas dentro de subprogramas.", nodo);
//                    if (!gestionFuncion(arbol.getSon(i))) {
//                        numErrors++;
//                    }
                    break;
                case PROCEDURE:
                    this.report_error("No es posible crear subprogramas dentro de subprogramas.", nodo);
//                    if (!gestionProcedure(arbol.getSon(i))) {
//                        numErrors++;
//                    }
                    break;
                case DECL_CONST:
                    if (!gestionDeclConstante(arbol.getSon(i))) {
                        numErrors++;
                    }

                    break;
                //Operacion
                case LISTAASIGNACION:
                    if (!gestionListaAsignacion(arbol.getSon(i), subp)) {
                        numErrors++;
                    }

                    break;
                case IF:
                    if (!gestionIf(arbol.getSon(i), subp)) {
                        numErrors++;
                    }

                    break;
                case FORARGS:
                    if (!gestionForArgs(arbol.getSon(i))) {
                        numErrors++;
                    }

                    break;
                case FOR:
                    if (!gestionFor(arbol.getSon(i), subp)) {
                        numErrors++;
                    }

                    break;
                case WHILE:
                    if (!gestionWhile(arbol.getSon(i), subp)) {
                        numErrors++;
                    }

                    break;
                case SWITCH:
                    if (!gestionSwitch(arbol.getSon(i), subp)) {
                        numErrors++;
                    }

                    break;
                //Llamada_sub
                case LLAMADA_SUB:
                    if (!gestionLlamadaSub(arbol.getSon(i), false)) {
                        numErrors++;
                    }

                    break;
                //Salida_pantalla
                case SALIDA:
                    if (!gestionSalida(arbol.getSon(i))) {
                        numErrors++;
                    }

                    break;
                //RETURN
                case RETURN:
                    if (!this.inSubprogram.equals("")) {
                        Descripcion d = ts.consultar(this.inSubprogram);
                        if (d.td instanceof DFunc) {
                            if (!gestionReturn(arbol.getSon(i), ((DFunc) d.getTd()).getTipoReturn())) {
                                return false;
                            }
                        } else if (d.td instanceof DProc) {
                            this.report_error("No es posible realizar la instrucción return \n en un procedure. Revisar " + this.inSubprogram + "()", arbol.getSon(i));
                            return false;
                        }

                    } else {
                        this.report_error("No es posible realizar la instrucción return\n en el main(). Revisar " + this.inSubprogram + "()", arbol.getSon(i));
                        return false;
                    }

                    break;
            }
        }
        return true;
    }
    //Gestión de declaraciones

    public boolean gestionDeclaraciones(Nodo nodo) {
        boolean declareVar = true;
        arbol = nodo;
        if (!gestionTipo(arbol.getSon(1))) {
            return false;
        }

        switch (nodo.getSon(0).tipoNodo()) {
            case VARIABLE:
                Descripcion d = ts.consultar(arbol.getSon(1).valor());
                DTipus t = (DTipus) d.td;
                if (!this.gestionDecListaIDs(arbol.getSon(0), t, arbol.getSon(1), Tipo.VARIABLE)) {
                    numErrors++;
                    return false;
                }
                break;
            case ARRAY:
                Descripcion dt = ts.consultar(arbol.getSon(1).valor());
                DTipus tt = (DTipus) dt.td;

                if (!gestionDeclTablas(arbol.getSon(0), arbol.getSon(1), tt)) {
                    numErrors++;
                    return false;
                }
//                if (!this.gestionDecListaIDs(arbol.getSon(1), tt, arbol.getSon(1), Tipo.ARRAY)) {
//                    //codigo 3 direcciones
//                    numErrors++;
//                }
                break;
            case ASIGNACION:
                if (!this.gestionDecAsignacion(arbol.getSon(0), arbol.getSon(1))) {
                    //codigo 3 direcciones
                    numErrors++;
                    return false;
                }
                break;
            case PROCEDURE:
                break;
            case FUNCION:
                break;
        }

        return true;
    }

    private boolean gestionDecListaIDs(Nodo son, DTipus t, Nodo o, Tipo tip) {
        Descripcion d1 = new Descripcion();
        switch (tip) {
            case VARIABLE:
                for (int i = son.nSons() - 1; i >= 0; i--) {
                    d1.setTd(new DVar());
                    nv++;
                    ((DVar) d1.getTd()).setNv(nv);
                    ((DVar) d1.getTd()).setTipo(o.valor());
                    if (!ts.poner(son.getSon(i).valor(), d1)) {
                        this.report_error("La variable " + son.getSon(i).valor()
                                + " ya ha sido definida previamente. ", son.getSon(i));

                        return false;
                    }

                }
                break;
            case ARRAY:
                break;
            case DECL_CONST:

                d1.setTd(new DConst());
                nv++;

                ((DConst) d1.getTd()).setTipo(o.valor());
                switch (o.valor()) {
                    case "Int":

                        ((DConst) d1.getTd()).setValor(son.getSon(0).valor());
                        if (!ts.poner(son.getSon(1).valor(), d1)) {
                            this.report_error("La variable " + son.getSon(1).valor() + " ya ha sido definida previamente.", son.getSon(1));
                            return false;
                        }
                        break;
                    case "String":
                        ((DConst) d1.getTd()).setValor(son.getSon(0).valor());
                        if (!ts.poner(son.getSon(1).valor(), d1)) {
                            this.report_error("La variable " + son.getSon(1).valor() + " ya ha sido definida previamente.", son.getSon(1));
                            return false;
                        }
                        break;
                    case "Bool":
                        ((DConst) d1.getTd()).setValor(son.getSon(0).valor());
                        if (!ts.poner(son.getSon(1).valor(), d1)) {
                            this.report_error("La variable " + son.getSon(1).valor() + " ya ha sido definida previamente.", son.getSon(1));
                            return false;
                        }
                        break;

                }

                break;

        }

        return true;
    }

    //Gestión de tipoNodo
    public boolean gestionTipo(Nodo nodo) {
        semanticResponse s = new semanticResponse();
        Descripcion d = ts.consultar(nodo.valor());
        if (!(d.td instanceof DTipus)) {
            this.report_error(nodo.valor() + " no es un tipo.", nodo);
            s.setValid(false);
            return false;
        }
        DTipus t = (DTipus) d.td;

        return true;
    }

    // Gestión de asignación
    public boolean gestionAsignacion(Nodo nodo, String subp) {
        int err = 0;
        Descripcion d = ts.consultar(nodo.getSon(1).valor());

        if (d == null) {
            if (!this.inSubprogram.equals("")) {
                int idxe = ts.first(this.inSubprogram);
                boolean trobat = false;
                while (idxe != 0 && idxe != -1) {
                    if (ts.getIdCamp(idxe).equals(nodo.getSon(1).valor())) {
                        trobat = true;
                        break;
                    }
                    idxe = ts.next(idxe);
                }

                if (!trobat) {
                    this.report_error("Variable/Array sin previa declaracion ", nodo.getSon(1));
                    return false;
                }
                d = ts.consulta(idxe);
            } else {
                this.report_error("Variable/Array sin previa declaracion ", nodo.getSon(1));
                return false;
            }

        } else if (d == null) {
            this.report_error("Variable/Array sin previa declaracion ", nodo.getSon(1));
            return false;
        }

        arbol = nodo;
        if (d.td instanceof DConst) {
            this.report_error("Una constante no puede ver su valor alterado ", nodo.getSon(1));
            return false;
        }
        if (d.getTd() instanceof DVar) {
            arbol.getSon(1).getInfoNodo().setTipo(((DVar) d.getTd()).getTipo());
        } else if (d.getTd() instanceof DArg) {
            arbol.getSon(1).getInfoNodo().setTipo(((DArg) d.getTd()).getTipo());
        } else if (d.getTd() instanceof DTipus) {

            if (((DTipus) d.getTd()).getTipo().getTsb() instanceof ts_array) {
                String tipo = ((ts_array) ((ttabla) ((DTipus) d.getTd()).getTipo()).getTsb()).getTipoElemental();
                if (tipo.equals("Int")) {
                    arbol.getSon(1).getInfoNodo().setTipo("Int");
                } else if (tipo.equals("Bool")) {
                    arbol.getSon(1).getInfoNodo().setTipo("Bool");
                }

            }

        }

        switch (arbol.getSon(0).tipoNodo()) {
            case READ: //Lista_Sentencias
                //GESTION LECTURA DE TECLADO PENDIENTE
                if (!gestionRead(arbol.getSon(0))) {
                    this.report_error("Error al ejecutar Read()", nodo);
                    err++;
                }
                break;
            case SUM: //Lista_Sentencias
            case RES:
            case MULT:
            case DIV:
                //HECHO
                if (!gestionOpArit(arbol.getSon(0))) {
//                    this.report_error("Incompatibilidad de tipos:  operando 1: " + arbol.getSon(1).getInfoNodo().getTipo() + ", operando 2: " + arbol.getSon(0).getInfoNodo().getTipo(), nodo);
                    err++;
                }
                break;
            case INT:
                //HECHO
                if (!gestionElemental(arbol.getSon(0), "Int")) {
//                    this.report_error("Incompatibilidad de tipos:  operando 1: " + arbol.getSon(1).getInfoNodo().getTipo() + ", operando 2: " + arbol.getSon(0).getInfoNodo().getTipo(), nodo);
                    err++;
                }
                break;
            case BOOL:
                //HEHCO
                if (!gestionElemental(arbol.getSon(0), "Bool")) {
//                    this.report_error("Incompatibilidad de tipos:  operando 1: " + arbol.getSon(1).getInfoNodo().getTipo() + ", operando 2: " + arbol.getSon(0).getInfoNodo().getTipo(), nodo);
                    err++;
                }
                break;
            case ID:
                //HECHO
                if (!gestionId(arbol.getSon(0), Tipo.FUNCION)) {
//                    this.report_error("Incompatibilidad de tipos:  operando 1: " + arbol.getSon(1).getInfoNodo().getTipo() + ", operando 2: " + arbol.getSon(0).getInfoNodo().getTipo(), nodo);

                    err++;
                }
                break;
            case ACCESOARRAY:
                //GESTION TABLAS MULTIDIMENSIONALES PENDIENTE
                if (!gestionAccesoArray(arbol.getSon(0))) {
                    err++;
                }
                break;

            case OPREL:
                //HECHO
                if (!gestionOpRel(arbol.getSon(0))) {
                    err++;
                }
                break;

            case OPLOG:
                if (!gestionCondiciones(arbol.getSon(0))) {
                    err++;
                }
                arbol = nodo;
                arbol.getSon(0).getInfoNodo().setTipo("Bool");
                break;

            case LLAMADA_SUB:
                //HECHO
                if (!this.gestionLlamadaSub(arbol.getSon(0), true)) {
                    err++;
                }
                break;
            case STRING:
                if (!gestionElemental(arbol.getSon(0), "String")) {
//                    this.report_error("Incompatibilidad de tipos:  operando 1: " + arbol.getSon(1).getInfoNodo().getTipo() + ", operando 2: " + arbol.getSon(0).getInfoNodo().getTipo(), nodo);
                    err++;
                }
                break;

        }

        arbol = nodo;
        if (err > 0) {
            return false;
        } else {
            if (!arbol.getSon(1).getInfoNodo().getTipo().equals("")) {
                if (!arbol.getSon(0).getInfoNodo().getTipo().equals(arbol.getSon(1).getInfoNodo().getTipo())) {
                    this.report_error("Incompatibilidad de tipos:  operando 1: " + arbol.getSon(1).getInfoNodo().getTipo() + ", operando 2: " + arbol.getSon(0).getInfoNodo().getTipo(), arbol);

                    return false;
                }

            } else {
                return false;
            }

        }

        return true;
    }

    //Gestión de booleana, char e int
    public boolean gestionElemental(Nodo nodo, String b) {
        arbol = nodo;

        arbol.getInfoNodo().setTipo(b);
        // nodo.getInfoNodo().setTsb(tsb);
        arbol.getInfoNodo().setMvp(Modo.CONST);
        arbol.getInfoNodo().setValid(true);

        return true;
    }

    //Gestión de acceso a array
    public boolean gestionAccesoArray(Nodo nodo) {
        arbol = nodo;
        Descripcion d = ts.consultar(nodo.valor());
        if (d == null) {
            this.report_error("Array no declarada, linea:  " + nodo.getLinea() + " columna: ", nodo);

            return false;
        }
        DTipus t = (DTipus) d.td;
        ttabla tab = (ttabla) t.getTipo();
        ts_array tsa = (ts_array) tab.getTsb();
        int idx = ts.first(nodo.valor());
        DIndex dind = (DIndex) ts.consulta(idx).td;

        for (int i = 0; i < nodo.nSons(); i++) {

            if (Integer.parseInt(nodo.getHijo(i).valor()) >= dind.getSize()) {
                this.report_error("Índice de array innaccesible.", nodo);

                return false;
            }
            idx = ts.next(idx);
            if (idx != -1) {
                dind = (DIndex) ts.consulta(idx).td;
            }

        }
        arbol.getInfoNodo().setTipo(tsa.getTipoElemental());

        return true;
    }

    //Gestión de división
    public boolean gestionOpArit(Nodo nodo) {
//        semanticResponse r = new semanticResponse();
        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.getSon(i).tipoNodo()) {
                case ID:
                    arbol.getSon(i).getInfoNodo().setValid(this.gestionId(arbol.getSon(i), Tipo.FUNCION));
                    break;
                case INT:
                    gestionElemental(arbol.getSon(i), "Int");
                    break;
                case SUM: //Lista_Sentencias
                case RES:
                case MULT:
                case DIV:
                    arbol.getSon(i).getInfoNodo().setValid(gestionOpArit(arbol.getSon(i)));
                    break;

            }
            arbol = nodo;
            if (!arbol.getSon(i).getInfoNodo().isValid()) {
                return false;
            }

        }
        for (int i = 1; i < nodo.nSons(); i++) {
            if (!arbol.getSon(i - 1).getInfoNodo().getTipo().equals(arbol.getSon(i).getInfoNodo().getTipo())) {
                arbol.getInfoNodo().setValid(false);
            } else {
                if (arbol.getSon(i - 1).getInfoNodo().getTipo().equals("Int")) {
                    arbol.getInfoNodo().setValid(true);
                } else {
                    arbol.getInfoNodo().setValid(false);
                }

            }
        }

        if (arbol.getInfoNodo().isValid()) {
            arbol.getInfoNodo().setTipo(arbol.getSon(0).getInfoNodo().getTipo());
        } else {
            this.report_error("Error en operacion aritmética ", arbol.getSon(0));
            return false;
        }
        return true;
    }

    //Gestión de operacion relacional
    public boolean gestionOpRel(Nodo nodo) {
        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.tipoNodo()) {
                case ID:
                    arbol.getSon(i).getInfoNodo().setValid(this.gestionId(arbol.getSon(i), Tipo.FUNCION));
                    break;
                case INT:
                    this.gestionElemental(arbol.getSon(i), "Int");
                    break;
                case BOOL:
                case STRING:
                    this.report_error("No se esta operando con tipos Int", arbol.getSon(i));
                    return false;

                case SUM:
                case RES:
                case MULT:
                case DIV:
                    arbol.getSon(i).getInfoNodo().setValid(this.gestionOpArit(arbol.getSon(i)));
                    break;
            }
            if (!arbol.getSon(i).getInfoNodo().isValid()) {
                return false;
            }
        }

        arbol = nodo;

        for (int i = 1; i < nodo.nSons(); i++) {
            if (!arbol.getSon(i - 1).getInfoNodo().getTipo().equals(arbol.getSon(i))) {
                arbol.getInfoNodo().setValid(false);
            } else {
                arbol.getInfoNodo().setValid(true);
            }
        }

        if (arbol.getInfoNodo().isValid()) {
            arbol.getInfoNodo().setTipo("Bool");
        } else {
            return false;
        }
        return true;

    }

    //Gestión de true o false
    public boolean gestionTF(Nodo nodo) {
        arbol = nodo;
        Descripcion d = ts.consultar(arbol.valor());
        if (d.getTd() instanceof DConst) {
            DConst dc = (DConst) d.getTd();
            if (!dc.getTipo().equals("Boolean")) {
                return false;
            }
        }
        return true;
    }

    //Gestión de declaración de arrays
    public boolean gestionDeclIdSubprog(Nodo nodo, String tipo) {
        np++;
        arbol = nodo;
        Descripcion d = new Descripcion();
        d.setTd(new DFunc());
        ((DFunc) d.getTd()).setNp(np);
        ((DFunc) d.getTd()).setTipoReturn(tipo);
        ts.poner(arbol.valor(), d);

        arbol.getInfoNodo().setTeparams(false);
        return true;
    }

    public boolean gestionDeclIdProc(Nodo nodo, String tipo) {
        np++;
        arbol = nodo;
        Descripcion d = new Descripcion();
        d.setTd(new DProc());
        ((DProc) d.getTd()).setNp(np);
        ts.poner(arbol.valor(), d);

        arbol.getInfoNodo().setTeparams(false);
        return true;
    }

    //Gestión de función
    public boolean gestionFuncion(Nodo nodo) {
        arbol = nodo;

        int id = 0;
        String tipo = "";
        String idsub = "";
        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.getSon(i).tipoNodo()) {
                case TIPO:
                    if (!gestionTipo(arbol.getSon(i))) {

                        numErrors++;
                    }
                    if (!arbol.getSon(i).valor().equals("String")) {
                        arbol.getInfoNodo().setTipo(arbol.getSon(i).valor());
                        arbol.getInfoNodo().setMvp(Modo.PROCF);
                        tipo = arbol.getSon(i).valor();
                    }else{
                        this.report_error("String no puede ser valor de retorno", arbol.getSon(i));
                        return false;
                    }

                    break;
                case LISTAPARAMS:

                    if (!gestionParams(arbol.getSon(i), arbol, id, true)) {
                        //codigo 3 direcciones
                        numErrors++;
                    }
                    break;
                case SENTENCIAS:
                    ts.entrarbloque();
                    if (!gestionSentencias(arbol.getSon(i), idsub)) {
                        //codigo 3 direcciones
                        numErrors++;
                    }
                    break;
                case ID:
                    if (!gestionDeclIdSubprog(arbol.getSon(i), tipo)) {
                        //codigo 3 direcciones
                        numErrors++;

                    }
                    arbol = nodo;
                    inSubprogram = arbol.getSon(i).valor();

                    idsub = arbol.getSon(i).valor();
                    arbol.setValor(arbol.getSon(i).valor());
                    id = i;
                    break;
            }
        }
        ts.salirBloque();
        return true;
    }

    //Gestión de procedure
    public boolean gestionProcedure(Nodo nodo) {
        arbol = nodo;
        int id = 0;
        String tipo = "";
        String idsub = "";
        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.getSon(i).tipoNodo()) {

                case LISTAPARAMS:

                    if (!gestionParams(arbol.getSon(i), arbol, id, false)) {
                        //codigo 3 direcciones
                        numErrors++;
                    }
                    break;
                case SENTENCIAS:
                    ts.entrarbloque();
                    if (!gestionSentencias(arbol.getSon(i), idsub)) {
                        //codigo 3 direcciones
                        numErrors++;
                    }
                    break;
                case ID:
                    if (!gestionDeclIdProc(arbol.getSon(i), tipo)) {
                        //codigo 3 direcciones
                        numErrors++;
                    }
                    arbol = nodo;
                    inSubprogram = arbol.getSon(i).valor();
                    arbol.setValor(arbol.getSon(i).valor());
                    idsub = arbol.getSon(i).valor();
                    id = i;
                    break;
            }
        }
        ts.salirBloque();
        return true;
    }

    //Gestión de constante
    public boolean gestionDeclConstante(Nodo nodo) {

        arbol = nodo;
        if (!gestionTipo(arbol.getSon(1))) {
            return false;
        }

        Descripcion d = ts.consultar(arbol.getSon(1).valor());
        DTipus t = (DTipus) d.td;
        this.gestionDecListaIDs(arbol.getSon(0), t, arbol.getSon(1), Tipo.DECL_CONST);

        return true;

    }

    //Gestión de parametros
    public boolean gestionParams(Nodo par, Nodo padre, int id, boolean isFunc) {

        arbol = padre;
        int tipo = 0;
        arbol.getSon(id).getInfoNodo().setTeparams(true);
        for (int i = par.nSons() - 1; i >= 0; i--) {
            arbol = par;
            for (int j = arbol.getSon(i).nSons() - 1; j >= 0; j--) {
                switch (arbol.getSon(i).getSon(j).tipoNodo()) {
                    case TIPO: //Lista_Sentencias
                        // gestionTipo(arbol.getSon(i));
                        tipo = j;
                        break;
                    case ID:
                        if (gestionTipo(arbol.getSon(i).getSon(tipo))) {
                            Descripcion d = new Descripcion();
                            d.setTd(new DArg());
                            ((DArg) d.getTd()).setTipo(arbol.getSon(i).getSon(tipo).valor());
                            String prog = padre.getSon(id).valor();
                            ts.ponerParam(prog, arbol.getSon(i).getSon(j).valor(), d);
                            d = ts.consultar(padre.getSon(id).valor());
                            if (isFunc) {
                                ((DFunc) d.td).setNumP(((DFunc) d.td).getNumP() + 1);
                                ts.actualiza(padre.valor(), d);
                            } else {
                                ((DProc) d.td).setNumP(((DProc) d.td).getNumP() + 1);
                                ts.actualiza(padre.getSon(id).valor(), d);
                            }

                        }
                        break;
                }
            }

        }
        return true;
    }

    //Gestión de lista asignación
    public boolean gestionListaAsignacion(Nodo nodo, String sub) {
        Nodo hijo;
        arbol = nodo;
        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            if (!gestionAsignacion(arbol.getSon(i), sub)) {
                numErrors++;
            }

        }
        return true;
    }

    //Gestión de if
    public boolean gestionIf(Nodo nodo, String sub) {
        arbol = nodo;
        ts.entrarbloqueNoprimario();
        for (int i = nodo.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.getSon(i).tipoNodo()) {
                case IFOPC: //Lista_Sentencias
                    if (!gestionSentencias(arbol.getSon(i), sub)) {
                        numErrors++;
                    }
                    break;
                case ELSE: //Lista_Sentencias
                    if (!gestionSentencias(arbol.getSon(i), sub)) {
                        numErrors++;
                    }
                    break;
                case BOOL:
                    if (!gestionTF(arbol.getSon(i))) {
                        numErrors++;
                    }

                    break;
                case OPREL:
                    if (!gestionCondicion(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case OPLOG:
                    if (!gestionCondiciones(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
            }
        }
        ts.salirBloqueNoPrimario();
        return true;
    }

    private boolean gestionForArgs(Nodo hijo) {
        arbol = hijo;
        for (int i = hijo.nSons() - 1; i >= 0; i--) {
            arbol = hijo;
            switch (arbol.getSon(i).tipoNodo()) {
                case DECLARACION:
                    if (!gestionDeclaraciones(arbol.getSon(i))) {
                        numErrors++;
                    }

                    break;
                case OPLOG:
                    if (!gestionCondiciones(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case OPREL:
                    if (!gestionCondicion(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case BOOL:

                    break;
                case ASIGNACION:
                    if (!this.gestionAsignacion(arbol.getSon(i), null)) {
                        numErrors++;
                    }
                    break;
            }
        }
        return true;

    }

    private boolean gestionFor(Nodo hijo, String sub) {
        arbol = hijo;
        ts.entrarbloqueNoprimario();
        for (int i = hijo.nSons() - 1; i >= 0; i--) {
            arbol = hijo;
            switch (arbol.getSon(i).tipoNodo()) {
                case FORARGS:
                    if (!gestionForArgs(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case SENTENCIAS:
                    if (!this.gestionSentencias(arbol.getSon(i), sub)) {
                        numErrors++;
                    }
                    break;
            }
        }

        ts.salirBloqueNoPrimario();
        return true;
    }

    private boolean gestionWhile(Nodo hijo, String sub) {
        arbol = hijo;

        ts.entrarbloqueNoprimario();

        for (int i = hijo.nSons() - 1; i >= 0; i--) {
            arbol = hijo;
            switch (arbol.getSon(i).tipoNodo()) {
                case OPLOG:
                    if (!gestionCondiciones(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case OPREL:
                    if (!gestionCondicion(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case BOOL:
                    if (!this.gestionCondicion(arbol.getSon(i))) {
                        numErrors++;
                    }
                    break;
                case SENTENCIAS:
                    if (!this.gestionSentencias(arbol.getSon(i), sub)) {
                        numErrors++;
                    }
                    break;

            }
        }
        ts.salirBloqueNoPrimario();
        return true;
    }

    private boolean gestionSwitch(Nodo hijo, String sub) {
        arbol = hijo;
        ts.entrarbloqueNoprimario();
        for (int i = hijo.nSons() - 1; i >= 0; i--) {
            arbol = hijo;
            switch (arbol.getSon(i).tipoNodo()) {
                case ID:
                    this.gestionId(arbol.getSon(i), Tipo.FUNCION);
                    break;
                case CASE:
                    if (!this.gestionCases(arbol.getSon(i), sub)) {
                        return false;
                    }
                    break;
            }

        }
        ts.salirBloqueNoPrimario();
        arbol = hijo;
        return true;
    }

    private boolean gestionCases(Nodo son, String sub) {
        arbol = son;
        for (int i = son.nSons() - 1; i >= 0; i--) {
            arbol = son;
            switch (arbol.getSon(i).tipoNodo()) {
                case SENTENCIAS:
                    if (!this.gestionSentencias(arbol.getSon(i), sub)) {
                        return false;
                    }
                    break;

            }
        }
        return true;
    }

    private boolean gestionLlamadaSub(Nodo hijo, boolean inAsig) {
        arbol = hijo;

        Descripcion d = ts.consultar(arbol.valor());

        if (d == null) {
            this.report_error("No existe el subprograma " + arbol.valor() + "()", arbol);
            return false;
        }

        if (d.getTd() instanceof DFunc) {
            if (gestionLlamadaFunc(arbol)) {
                arbol = hijo;
                arbol.getInfoNodo().setTipo(((DFunc) d.getTd()).getTipoReturn());

            } else {
                return false;
            }
        } else if (d.getTd() instanceof DProc) {
            if (!gestionLlamadaProc(arbol)) {
                return false;
            }
        } else {
            this.report_error("Subprograma " + hijo.valor() + "() desconocido. Linea " + hijo.getLinea() + " columna: " + hijo.getColumna(), hijo);

        }

        if (inAsig) {
            if (!(d.getTd() instanceof DFunc)) {
                this.report_error("Un procedure no puede ser asignado.", hijo);
                return false;
            } else {
                DFunc dfunc = (DFunc) d.td;
                arbol.getInfoNodo().setTipo(dfunc.getTipoReturn());
            }

        }

        return true;
    }

    private boolean gestionLlamadaFunc(Nodo hijo) {
        arbol = hijo;
        if (((DFunc) ts.consultar(hijo.valor()).td).getNumP() == hijo.nSons()) {
            int idx = ts.first(hijo.valor());
            Descripcion d = ts.consulta(ts.first(hijo.valor()));
            if (d != null) {

                for (int i = hijo.nSons() - 1; i >= 0; i--) {
                    arbol = hijo;
                    switch (arbol.getSon(i).tipoNodo()) {
                        case ID:
                            String id = arbol.getSon(i).valor();
                            if (!this.gestionId(arbol.getSon(i), Tipo.FUNCION)) {
                                return false;
                            }
                            arbol = hijo;
                            if (ts.consultar(id) != null) {
                                if (ts.consultar(id).td instanceof DVar) {
                                    DVar dv = (DVar) ts.consultar(id).td;
                                    if (!((DArg) d.td).getTipo().equals(dv.getTipo())) {
                                        this.report_error("El parámetro " + arbol.getSon(i).valor()
                                                + "debe ser de tipo " + ((DArg) d.td).getTipo() + ". ", arbol.getSon(i));
                                        return false;
                                    }
                                } else {
                                    DConst dv = (DConst) ts.consultar(id).td;
                                    if (!((DArg) d.td).getTipo().equals(dv.getTipo())) {
                                        this.report_error("El parámetro " + arbol.getSon(i).valor()
                                                + "debe ser de tipo " + ((DArg) d.td).getTipo() + ". ", arbol.getSon(i));
                                        return false;
                                    }
                                }
                            } else {
                                if (!((DArg) d.td).getTipo().equals(arbol.getSon(i).getInfoNodo().getTipo())) {
                                    this.report_error("El parámetro " + arbol.getSon(i).valor()
                                            + "debe ser de tipo " + ((DArg) d.td).getTipo() + ". ", arbol.getSon(i));

                                    return false;
                                }
                            }

                            break;

                        case ACCESOARRAY:

                            this.report_error("No es posible pasar por parametros arrays/accesos_array,\n" + "unicamente varaibles o elementales.", hijo);
                            return false;

                    }
                    idx = ts.next(idx);
                    if (idx == 0) {
                        d = ts.consulta(idx);
                    }
                }
            } else {
                return true; // no tiene parametros
            }
        } else {
            this.report_error("Número de parámetros en " + hijo.valor() + "() no coincide.", hijo);

            return false;
        }

        return true;
    }

    private boolean gestionLlamadaProc(Nodo hijo) {
        arbol = hijo;
        if (((DProc) ts.consultar(hijo.valor()).td).getNumP() == hijo.nSons()) {
            DProc dpr = (DProc) ts.consultar(hijo.valor()).td;
            int idx = ts.first(hijo.valor());
            Descripcion d = ts.consulta(ts.first(hijo.valor()));
            for (int i = hijo.nSons() - 1; i >= 0; i--) {
                arbol = hijo;
                switch (arbol.getSon(i).tipoNodo()) {
                    case ID:
                        String id = arbol.getSon(i).valor();
                        if (!this.gestionId(arbol.getSon(i), Tipo.FUNCION)) {
                            return false;
                        }
                        arbol = hijo;
                        if (ts.consultar(id) != null) {
                            if (ts.consultar(id).td instanceof DVar) {
                                DVar dv = (DVar) ts.consultar(id).td;
                                if (!((DArg) d.td).getTipo().equals(dv.getTipo())) {
                                    this.report_error("El parámetro " + arbol.getSon(i).valor()
                                            + "debe ser de tipo " + ((DArg) d.td).getTipo() + ".", arbol.getSon(i));
                                    return false;
                                }
                            } else {
                                DConst dv = (DConst) ts.consultar(id).td;
                                if (!((DArg) d.td).getTipo().equals(dv.getTipo())) {
                                    this.report_error("El parámetro " + arbol.getSon(i).valor()
                                            + "debe ser de tipo " + ((DArg) d.td).getTipo() + ".", arbol.getSon(i));

                                    return false;
                                }
                            }
                        } else {
                            if (!((DArg) d.td).getTipo().equals(arbol.getSon(i).getInfoNodo().getTipo())) {
                                this.report_error("El parámetro " + arbol.getSon(i).valor()
                                        + "debe ser de tipo " + ((DArg) d.td).getTipo() + ". ", arbol.getSon(i));

                                return false;
                            }
                        }

                        break;

                    case ACCESOARRAY:
                        this.report_error("No es posible pasar por parametros arrays/accesos_array, unicamente variables o elementales.", hijo);
                        return false;

                    case INT:
                        arbol.getSon(i).getInfoNodo().setTipo("Int");
                        if (!((DArg) d.td).getTipo().equals("Int")) {
                            this.report_error("El parámetro " + arbol.getSon(i).valor()
                                    + "debe ser de tipo " + ((DArg) d.td).getTipo() + ".", arbol.getSon(i));
                            return false;
                        }
                        break;
                    case BOOL:
                        arbol.getSon(i).getInfoNodo().setTipo("Bool");
                        if (!((DArg) d.td).getTipo().equals("Bool")) {
                            this.report_error("El parámetro " + arbol.getSon(i).valor()
                                    + "debe ser de tipo " + ((DArg) d.td).getTipo() + ".", arbol.getSon(i));
                            return false;
                        }
                        break;
                    case STRING:
                        this.report_error("Un string no puede pasarse por parámetro", hijo);
                        return false;

                }

                idx = ts.next(idx);
                if (idx == 0) {
                    d = ts.consulta(idx);
                }

            }

        } else {
            this.report_error("Número de parámetros en " + hijo.valor() + "() no coincide.", hijo);

            return false;
        }
        return true;
    }

    private boolean gestionSalida(Nodo hijo) {

        arbol = hijo;
        return true;
    }

    private boolean gestionReturn(Nodo hijo, String tipo) {

        arbol = hijo;
        switch (hijo.getHijo(0).tipoNodo()) {
            case ID:
                if (this.gestionId(hijo.getHijo(0), Tipo.FUNCION)) {
                    Descripcion d;
                    if (arbol.valor().startsWith("-")) {
                        arbol.valor().replace("-", "");
                        d = ts.consultar(arbol.valor());
                        if (d != null && d.td instanceof DVar) {

                            DVar td = (DVar) d.td;
                            if (!td.getTipo().equals(tipo) || tipo.equals("String")) {
                                this.report_error("Valor de retorno no valido en " + this.inSubprogram, arbol);

                                return false;
                            }
                        } else if (d != null && d.td instanceof DConst) {
                            this.report_error("No es posible devolver un valor constante. Revisar " + this.inSubprogram + "()", arbol);

                            return false;
                        } else if (d == null) {
                            if (!this.inSubprogram.equals("")) {
                                d = ts.consulta(ts.first(inSubprogram));
                                if (d != null && d.td instanceof DArg) {
                                    DArg td = (DArg) d.td;
                                    if (!td.getTipo().equals(tipo) || tipo.equals("String")) {
                                        this.report_error("Valor de retorno no valido en " + this.inSubprogram, arbol);
                                        return false;
                                    }
                                }

                            }
                        }

                    } else {
                        d = ts.consultar(arbol.valor());
                        if (d != null && d.td instanceof DVar) {

                            DVar td = (DVar) d.td;
                            if (!td.getTipo().equals(tipo) || tipo.equals("String")) {
                                this.report_error("Valor de retorno no valido en " + this.inSubprogram, arbol);

                                return false;
                            }

                        } else if (d != null && d.td instanceof DConst) {
                            this.report_error("No es posible devolver un valor constante.", arbol);

                            return false;

                        } else if (d == null) {
                            if (!this.inSubprogram.equals("")) {
                                d = ts.consulta(ts.first(inSubprogram));
                                if (d != null && d.td instanceof DArg) {
                                    DArg td = (DArg) d.td;
                                    if (!td.getTipo().equals(tipo) || tipo.equals("String")) {
                                        this.report_error("Valor de retorno no valido en " + this.inSubprogram, arbol);

                                        return false;
                                    }

                                }

                            }

                        }
                    }
                }

                break;
            case INT:
                if (!tipo.equals("Int")) {
                    this.report_error("Valor de retorno no valido en " + this.inSubprogram, arbol);
                    return false;
                }
                break;
            case BOOL:
                if (!tipo.equals("Bool")) {
                    this.report_error("Valor de retorno no valido en " + this.inSubprogram, arbol);
                    return false;
                }
                break;
            case STRING:
                this.report_error("Valor de retorno no valido en " + this.inSubprogram, arbol);
                return false;

        }
        arbol.getInfoNodo().setTipo(tipo);
        return true;
    }

    private boolean gestionId(Nodo son, Tipo FUNCION) {
        arbol = son;
        Descripcion d;
        if (arbol.valor().startsWith("-")) {
            arbol.valor().replace("-", "");
            d = ts.consultar(arbol.valor());
            if (d != null && d.td instanceof DVar) {

                DVar td = (DVar) d.td;
                arbol.getInfoNodo().setTipo(td.getTipo());
                arbol.getInfoNodo().setMvp(Modo.VAR);

            } else if (d != null && d.td instanceof DConst) {

                DConst td = (DConst) d.td;
                arbol.getInfoNodo().setTipo(td.getTipo());
                arbol.getInfoNodo().setMvp(Modo.CONST);

            } else if (d == null) {
                if (!this.inSubprogram.equals("")) {
                    d = ts.consulta(ts.first(inSubprogram));
                    int idx = ts.first(inSubprogram);
                    if (d != null && d.td instanceof DArg) {

                        boolean seguir = true;
                        while (idx != 0 && seguir) {
                            DArg td = (DArg) d.td;
                            if (ts.getIdCamp(idx).equals(arbol.valor())) {
                                arbol.getInfoNodo().setTipo(td.getTipo());
                                arbol.getInfoNodo().setMvp(Modo.PROCF);
                                seguir = false;
                            } else {
                                idx = ts.next(idx);
                            }
                        }
                        if (idx == 0) {
                            this.report_error("variable " + arbol.valor() + " no existe", arbol);
                            return false;
                        }
                    } else {
                        this.report_error("variable " + arbol.valor() + " no existe", arbol);

                        return false;
                    }

                } else {
                    this.report_error("variable " + arbol.valor() + " no existe", arbol);
                    return false;

                }
            }

        } else {
            d = ts.consultar(arbol.valor());
            if (d != null && d.td instanceof DVar) {

                DVar td = (DVar) d.td;

                arbol.getInfoNodo().setTipo(td.getTipo());
                arbol.getInfoNodo().setMvp(Modo.VAR);

            } else if (d != null && d.td instanceof DConst) {

                DConst td = (DConst) d.td;
                arbol.getInfoNodo().setTipo(td.getTipo());
                arbol.getInfoNodo().setMvp(Modo.CONST);

            } else if (d == null) {
                if (!this.inSubprogram.equals("")) {
                    d = ts.consulta(ts.first(inSubprogram));
                    int idx = ts.first(inSubprogram);
                    if (d != null && d.td instanceof DArg) {
                        boolean seguir = true;
                        while (idx != -1 && seguir) {
                            DArg td = (DArg) d.td;
                            if (ts.getIdCamp(idx).equals(arbol.valor())) {
                                arbol.getInfoNodo().setTipo(td.getTipo());
                                arbol.getInfoNodo().setMvp(Modo.PROCF);
                                seguir = false;
                            } else {
                                idx = ts.next(idx);
                                d = ts.consulta(idx);
                            }
                        }
                        if (idx == -1) {
                            this.report_error("variable " + arbol.valor() + " no existe", arbol);
                            return false;
                        }

                    } else {
                        this.report_error("variable " + arbol.valor() + " no existe", arbol);
                        return false;

                    }

                } else {
                    this.report_error("variable " + arbol.valor() + " no existe", arbol);
                    return false;
                }

            }
        }
        return true;
    }

    //    private void gestionListaIDArray(Nodo son) {
    //
    //    }
    private boolean gestionRead(Nodo son) {
        arbol = son;
        if (son.valor().equals("Int")) {
            arbol.getInfoNodo().setTipo("Int");

        } else if (son.valor().equals("String")) {
            arbol.getInfoNodo().setTipo("String");
        } else {
            return false;
        }
        return true;
    }

    private boolean gestionDecAsignacion(Nodo son, Nodo tipo) {
        arbol = son;
        for (int i = son.nSons() - 1; i >= 0; i--) {
            switch (arbol.getSon(i).tipoNodo()) {
                case ID:
                    if (i == son.nSons() - 1) {
                        Descripcion d = new Descripcion();
                        d.setTd(new DVar());
                        nv++;
                        ((DVar) d.getTd()).setNv(nv);
                        ((DVar) d.getTd()).setTipo(tipo.valor());
                        if (!ts.poner(arbol.getSon(i).valor(), d)) {
                            this.report_error("La variable " + son.getSon(i).valor() + " ya ha sido definida previamente", son.getSon(i));

                            return false;
                        }
                    }

                    if (!this.gestionAsignacion(arbol, null)) {
                        numErrors++;
                    }

                    break;

            }
        }

        return true;
    }

    private boolean gestionCondicion(Nodo son) {
        arbol = son;
        switch (son.tipoNodo()) {

            case OPREL:
                arbol = son;
                for (int i = son.nSons() - 1; i >= 0; i--) {
                    arbol = son;
                    switch (arbol.getSon(i).tipoNodo()) {
                        case SUM:
                        case RES:
                        case MULT:
                        case DIV:
                            if (!gestionOpArit(arbol.getSon(i))) {
                                numErrors++;
                            }
                            break;
                        case OPREL:
                            //HECHO
                            if (!gestionOpRel(arbol.getSon(i))) {
                                numErrors++;
                            }
                            break;
                        case ID:
                            //HECHO
                            if (!gestionId(arbol.getSon(i), Tipo.FUNCION)) {

                                numErrors++;
                            }
                            break;
                        case INT:
                            //HECHO
                            if (!gestionElemental(arbol.getSon(i), "Int")) {
                                numErrors++;
                            }
                            break;
                        case BOOL:
                            //HEHCO
                            if (!gestionElemental(arbol.getSon(i), "Bool")) {
                                numErrors++;
                            }
                            break;

                    }
                }

                break;
        }
        arbol = son;
        if (arbol.nSons() == 0) {
            if (arbol.tipoNodo() != Tipo.BOOL) {
                this.report_error("Incompatibilidad de tipos en la concición", son);
                return false;
            }
        } else if (!arbol.getSon(1).getInfoNodo().getTipo().equals("")) {
            if (!arbol.getSon(0).getInfoNodo().getTipo().equals(arbol.getSon(1).getInfoNodo().getTipo())) {
                this.report_error("Incompatibilidad de tipos en la concición", son);
                return false;
            }

        } else {
            return false;
        }

        return true;

    }

    private boolean gestionCondiciones(Nodo hijo) {
        arbol = hijo;
        for (int i = arbol.nSons() - 1; i >= 0; i--) {
            arbol = hijo;
            if (arbol.getSon(i).tipoNodo() == Tipo.OPLOG) {
                gestionCondiciones(arbol.getSon(i));
            } else if (!this.gestionCondicion(arbol.getSon(i))) {
                return false;
            }

        }
        return true;
    }

    public Nodo getArbol() {
        return arbol;
    }

    public void setArbol(Nodo arbol) {
        this.arbol = arbol;
    }

    public boolean gestionDeclTablas(Nodo nodo, Nodo tt, DTipus b) {

        arbol = nodo;
        arbol.setInfoArray(new infoNodoArr());
        int ocupacion = 0;
        Descripcion d = new Descripcion();
        d.setTd(new DTipus());
        ((DTipus) d.getTd()).setTipo(new ttabla(new ts_array(), 0));
        ttabla t = (((ttabla) ((DTipus) d.getTd()).getTipo()));
        ts_array f = ((ts_array) t.getTsb());
        ts.poner(arbol.valor(), d);

        for (int i = arbol.nSons() - 1; i >= 0; i--) {
            arbol = nodo;
            switch (arbol.getSon(i).tipoNodo()) {
                case INT:
                    if (this.gestionElementalArray(arbol.getSon(i), arbol)) {
                        arbol = nodo;
                        if (ocupacion == 0) {
                            ocupacion = (arbol.getSon(i).getInfoArray().getNcomp());
                        } else {
                            ocupacion = ocupacion * (arbol.getSon(i).getInfoArray().getNcomp());
                        }

                    } else {
                        this.report_error("Tipo no válido en declaración de array", nodo);

                    }
                    break;
                case ID:
                    if (i != 0) {
                        if (this.gestionIdArray(arbol.getSon(i), arbol)) {
                            arbol = nodo;
                            if (ocupacion == 0) {
                                ocupacion = (arbol.getSon(i).getInfoArray().getNcomp());
                            } else {
                                ocupacion = ocupacion * (arbol.getSon(i).getInfoArray().getNcomp());
                            }
                        } else {
                            this.report_error("variable con nombre " + arbol.getSon(i).valor() + "ya declarada previamente", arbol.getSon(i));
                            numErrors++;
                        }
                        break;
                    } else {
                        this.report_error("Dimensión de un aray debe ser un entero", arbol.getSon(i));
                    }

            }
        }
        arbol = nodo;

        if (b.getTipo() instanceof tInt) {
            f.setTipoElemental(tt.valor());
            arbol.getInfoArray().setNcomp(ocupacion * (((tInt) b.getTipo()).getOcupacion()));
            Descripcion des = ts.consultar(nodo.valor());
            ((ttabla) ((DTipus) des.getTd()).getTipo()).setOcupacion(ocupacion * (((tInt) b.getTipo()).getOcupacion()));
            ts.actualiza(nodo.valor(), des);
        } else if (b.getTipo() instanceof tBool) {
            f.setTipoElemental(tt.valor());
            arbol.getInfoArray().setNcomp(ocupacion * (((tBool) b.getTipo()).getOcupacion()));
            Descripcion des = ts.consultar(nodo.valor());
            ((ttabla) ((DTipus) des.getTd()).getTipo()).setOcupacion(ocupacion * (((tBool) b.getTipo()).getOcupacion()));
            ts.actualiza(nodo.valor(), des);
        } else {
            f.setTipoElemental(tt.valor());
            arbol.getInfoArray().setNcomp(ocupacion * (((tChar) b.getTipo()).getOcupacion()));
            Descripcion des = ts.consultar(nodo.valor());
            ((ttabla) ((DTipus) des.getTd()).getTipo()).setOcupacion(ocupacion * (((tChar) b.getTipo()).getOcupacion()));
            ts.actualiza(nodo.valor(), des);
        }

        return true;
    }

    public boolean gestionIdArray(Nodo nodo, Nodo id) {
        arbol = nodo;
        Descripcion d = ts.consultar(arbol.valor());
        if (d != null && d.td instanceof DVar) {
            DVar td = (DVar) d.td;
            if (td.getTipo().equals("Int")) {
                arbol.getInfoNodo().setTipo(td.getTipo());
                arbol.getInfoNodo().setMvp(Modo.VAR);
                Descripcion d2 = new Descripcion();
                d2.setTd(new DIndex());
                ((DIndex) d2.getTd()).setTipo(td.getTipo());
                ts.ponerIndice(id.valor(), d);
                arbol.getInfoArray().setIdArray(id.valor());
                arbol.getInfoArray().setNcomp(Integer.parseInt(nodo.valor()));

            } else {
                return false;
            }

        }

        return true;

    }

    private boolean gestionElementalArray(Nodo nodo, Nodo id) {
        arbol = nodo;
        if (nodo.tipoNodo() == Tipo.INT) {
            arbol.getInfoNodo().setTipo("Int");
            // nodo.getInfoNodo().setTsb(tsb);
            arbol.getInfoNodo().setMvp(Modo.CONST);
            arbol.getInfoNodo().setValid(true);

            Descripcion d2 = new Descripcion();
            d2.setTd(new DIndex());
            ((DIndex) d2.getTd()).setTipo("Int");
            ((DIndex) d2.getTd()).setSize(Integer.parseInt(nodo.valor()));
            ts.ponerIndice(id.valor(), d2);
            arbol.setInfoArray(new infoNodoArr());
            arbol.getInfoArray().setIdArray(id.valor());
            arbol.getInfoArray().setNcomp(Integer.parseInt(nodo.valor()));
        } else {
            this.report_error("Indice de array no es Int", nodo);

            return false;
        }

        return true;
    }

    public void report_error(String message, Nodo info) {
        String msg = "ERROR";
        if (info instanceof Symbol) {
//            ComplexSymbol token = (ComplexSymbol) info;

            if (info != null) {
                msg += " (fila: ";
                msg += info.getLinea();
                msg += ", columna: ";
                msg += info.getColumna();
                msg += ")";
                msg += "\n";
            }
        }
        msg += ": " + message;
        errores.add(msg);
//        errores.add((msg);
//        numErrores++;
    }
}

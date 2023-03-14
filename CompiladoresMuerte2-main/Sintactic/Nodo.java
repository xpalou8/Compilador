package CompiladoresMuerte2.Sintactic;

import CompiladoresMuerte2.Semantic.infoNodoArr;
import CompiladoresMuerte2.Semantic.infoNodoVar_Exp;
import CompiladoresMuerte2.Semantic.semanticResponse;
import java.util.ArrayList;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
/**
 *
 * @authors Antonio Borrás Kostov, Xavier Matas Perelló, Xavier Palou y Gerard Medina Martorell
 * 
 */
public class Nodo extends ComplexSymbol {

    private Tipo tipoNodo;
    private String valor;
    private ArrayList<Nodo> listaNodos;
    private int id, linea, columna;
    String resultado ="";
    private semanticResponse infoNodo;
    private infoNodoArr infoArray;
    private infoNodoVar_Exp infCod3dir;
    
    //Constructor nodo
    public Nodo(Tipo t, String v, int id, ArrayList<Nodo> Lnodos) {
        super(t.toString(), id);
        listaNodos = new ArrayList<Nodo>();
        tipoNodo = t;
        valor = v;
        setHijos(Lnodos);
        this.id = id;
        infoNodo = new semanticResponse();
    }
    
    public Nodo(Tipo t, String v, int id, int l, int c, ArrayList<Nodo> Lnodos) {
        super(t.toString(), id);
        listaNodos = new ArrayList<Nodo>();
        tipoNodo = t;
        valor = v;
        linea = l;
        columna = c;
        setHijos(Lnodos);
        this.id = id;
        infoNodo = new semanticResponse();
    }
    
    public Nodo() {
        super("vacio", -1);
    }

    public semanticResponse getInfoNodo() {
        return infoNodo;
    }

    public void setInfoNodo(semanticResponse infoNodo) {
        this.infoNodo = infoNodo;
    }

    
    public void setHijos(ArrayList<Nodo> Lnodos) {
        if (Lnodos != null) {
            while (!Lnodos.isEmpty()) {
                listaNodos.add(Lnodos.remove(0));
            }
        }
    }

    public void addSon(Object n) {
        listaNodos.add((Nodo) n);
    }

    public Nodo getHijo(int i) {
        return listaNodos.get(i);
    }
    
    public Nodo getSon(int i) {
        return listaNodos.get(listaNodos.size() - i - 1);
    }

    public int nSons() {
        return listaNodos.size();
    }

    public Tipo tipoNodo() {
        return tipoNodo;
    }

    public String valor() {
        return valor;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Nodo> getArray() {
        return this.listaNodos;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String get_resultado() {
        return resultado;
    }

    //metodo para mostrar los hijos del arbol
    public void print_sons(Nodo nodo, int nivel) throws Exception {
        //imprimos el nodo main
        for (int i = 0; i < nivel; i++) {// asteriscos para diferenciar el nivel
            resultado += "*";
        }
        resultado += nodo.getId()+ " tipo: " + nodo.tipoNodo() + " " + " valor: " + nodo.valor() + " nivel: " + nivel + " --->\n";
        if (!nodo.getArray().isEmpty()) {
            //Imprimimos por el final porque los hijos se guardan en ese orden
            for (int i = (nodo.getArray().size() - 1); i >= 0; i--) {
                Nodo hijo = nodo.getSon(i);
                print_sons(hijo, nivel + 1);
            }
        }
    }

    public infoNodoArr getInfoArray() {
        return infoArray;
    }

    public void setInfoArray(infoNodoArr infoArray) {
        this.infoArray = infoArray;
    }

    public infoNodoVar_Exp getInfCod3dir() {
        return infCod3dir;
    }

    public void setInfCod3dir(infoNodoVar_Exp infCod3dir) {
        this.infCod3dir = infCod3dir;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }
    
    
}



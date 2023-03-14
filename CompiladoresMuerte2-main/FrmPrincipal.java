/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CompiladoresMuerte2;

//import compilador.Codi.Codi68k;
import CompiladoresMuerte2.Codi.Codi68k;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.JFileChooser;
import CompiladoresMuerte2.Lexic.*;
import CompiladoresMuerte2.Semantic.AnalisisSemantico;
import CompiladoresMuerte2.Semantic.Generacion_C3Dir;
import CompiladoresMuerte2.Semantic.Generacion_C3Dir_Opt;
//import CompiladoresMuerte2.semantic.AnalisisSemantico;
//import CompiladoresMuerte2.semantic.Generacion_Codi3Dir;
import CompiladoresMuerte2.Sintactic.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.SymbolFactory;
import jflex.exceptions.SilentExit;

/**
 *
 * @authors Juan Francisco Sánchez García Xavier Matas Perelló Xisco Cerdó
 * Bibiloni
 */
public class FrmPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form FrmPrincipal
     */
    public FrmPrincipal() {
        try {
            String[] ruta1 = new String[1];
            ruta1[0] = "src/CompiladoresMuerte2/Lexic/Lexic.flex";
            jflex.Main.generate(ruta1);
            initComponents();
            this.setLocationRelativeTo(null);
        } catch (SilentExit ex) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RESET = ANSI_BLACK;
    /**
     * CAMBIAR LA SIGUIENTE RUTA POR LA RUTA EN LA QUE SE GUARDE EL PROYECTO,
     * TAL QUE APUNTE DONDE SE QUIERAN GUARDAR LOS FICHEROS GENERADOS, Y
     * ASEGURARSE DE QUE HAY UNA CARPETA LLAMADA ficheros_generados ALLÍ
     */
    private String rutaProyecto = "src/CompiladoresMuerte2";

    private int contarFicheros(String s) {
        int count = 0;
        File directory = new File(rutaProyecto + "/ficheros_generados/");
        for (File file : directory.listFiles()) {
            if (file.getName().startsWith(s)) {
                count++;
            }
        }
        return count;
    }

    private void analizarLexico() throws IOException {
        try {
            String resultado = "";
            String errores = "";
            String ST = txtResultado.getText();
            Scanner lexer = new Scanner(new StringReader(ST));
            File lexicFile = new File(rutaProyecto + "/ficheros_generados/Lexic.txt");
            BufferedWriter bw;
            if (!lexicFile.exists()) {
                bw = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/Lexic.txt"));
            } else {
                bw = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/Lexic" + (contarFicheros("Lexic") +1)+ ".txt"));
            }

            while (true) {
                Symbol sym = lexer.next_token();
                if (sym.sym == ParserSym.EOF) {
                    break;
                }
                switch (sym.sym) {
                    case ParserSym.Coma:
                        resultado += "  <Coma>\t\t" + ParserSym.terminalNames[ParserSym.Coma] + "\n";
                        break;
                    case ParserSym.Print:
                        resultado += "  <Print>\t\t" + ParserSym.terminalNames[ParserSym.Print] + "\n";
                        break;
                    case ParserSym.ReadInt:
                        resultado += "  <Input>\t\t" + ParserSym.terminalNames[ParserSym.ReadInt] + "\n";
                        break;
                    case ParserSym.ReadString:
                        resultado += "  <Input>\t\t" + ParserSym.terminalNames[ParserSym.ReadString] + "\n";
                        break;
                    case ParserSym.Int:
                        resultado += "  <Tipo de dato>\t" + ParserSym.terminalNames[ParserSym.Int] + "\n";
                        break;
                    case ParserSym.Bool:
                        resultado += "  <Tipo de dato>\t" + ParserSym.terminalNames[ParserSym.Bool] + "\n";
                        break;
                    case ParserSym.String:
                        resultado += "  <Tipo de dato>\t" + ParserSym.terminalNames[ParserSym.String] + "\n";
                        break;
                    case ParserSym.If:
                        resultado += "  <Reservada if>\t" + ParserSym.terminalNames[ParserSym.If] + "\n";
                        break;
                    case ParserSym.Else:
                        resultado += "  <Reservada else>\t" + ParserSym.terminalNames[ParserSym.Else] + "\n";
                        break;
                    case ParserSym.While:
                        resultado += "  <Reservada while>\t" + ParserSym.terminalNames[ParserSym.While] + "\n";
                        break;
                    case ParserSym.Igual:
                        resultado += "  <Operador igual>\t" + ParserSym.terminalNames[ParserSym.Igual] + "\n";
                        break;
                    case ParserSym.Suma:
                        resultado += "  <Operador suma>\t" + ParserSym.terminalNames[ParserSym.Suma] + "\n";
                        break;
                    case ParserSym.Resta:
                        resultado += "  <Operador resta>\t" + ParserSym.terminalNames[ParserSym.Resta] + "\n";
                        break;
                    case ParserSym.Mult:
                        resultado += "  <Operador resta>\t" + ParserSym.terminalNames[ParserSym.Mult] + "\n";
                        break;
                    case ParserSym.Div:
                        resultado += "  <Operador resta>\t" + ParserSym.terminalNames[ParserSym.Div] + "\n";
                        break;
                    case ParserSym.Const:
                        resultado += "  <Operador resta>\t" + ParserSym.terminalNames[ParserSym.Const] + "\n";
                        break;
                    case ParserSym.Mayor:
                        resultado += "  <Operador relacional Mayor>\t" + ParserSym.terminalNames[ParserSym.Mayor] + "\n";
                        break;
                    case ParserSym.Mayor_equal:
                        resultado += "  <Operador relacional Mayor_equal>\t" + ParserSym.terminalNames[ParserSym.Mayor_equal] + "\n";
                        break;
                    case ParserSym.Menor:
                        resultado += "  <Operador relacional Menor>\t" + ParserSym.terminalNames[ParserSym.Menor] + "\n";
                        break;
                    case ParserSym.Menor_equal:
                        resultado += "  <Operador relacional Menor_equal>\t" + ParserSym.terminalNames[ParserSym.Menor_equal] + "\n";
                        break;
                    case ParserSym.Equal:
                        resultado += "  <Operador relacional Equal>\t" + ParserSym.terminalNames[ParserSym.Equal] + "\n";
                        break;
                    case ParserSym.Not_equal:
                        resultado += "  <Operador relacional Not Equal>\t" + ParserSym.terminalNames[ParserSym.Not_equal] + "\n";
                        break;
                    case ParserSym.And:
                        resultado += "  <Operador booleano And>\t" + ParserSym.terminalNames[ParserSym.And] + "\n";
                        break;
                    case ParserSym.Or:
                        resultado += "  <Operador booleano Or>\t" + ParserSym.terminalNames[ParserSym.Or] + "\n";
                        break;
                    case ParserSym.Parentesis_a:
                        resultado += "  <Parentesis de apertura>\t" + ParserSym.terminalNames[ParserSym.Parentesis_a] + "\n";
                        break;
                    case ParserSym.Parentesis_c:
                        resultado += "  <Parentesis de cierre>\t" + ParserSym.terminalNames[ParserSym.Parentesis_c] + "\n";
                        break;
                    case ParserSym.Llave_a:
                        resultado += "  <Llave de apertura>\t" + ParserSym.terminalNames[ParserSym.Llave_a] + "\n";
                        break;
                    case ParserSym.Llave_c:
                        resultado += "  <Llave de cierre>\t" + ParserSym.terminalNames[ParserSym.Llave_c] + "\n";
                        break;
                    case ParserSym.Corchete_a:
                        resultado += "  <Corchete de apertura>\t" + ParserSym.terminalNames[ParserSym.Corchete_a] + "\n";
                        break;
                    case ParserSym.Corchete_c:
                        resultado += "  <Corchete de cierre>\t" + ParserSym.terminalNames[ParserSym.Corchete_c] + "\n";
                        break;
                    case ParserSym.Main:
                        resultado += "  <Reservada main>\t" + ParserSym.terminalNames[ParserSym.Main] + "\n";
                        break;
                    case ParserSym.Function:
                        resultado += "  <Reservada function>\t" + ParserSym.terminalNames[ParserSym.Function] + "\n";
                        break;
                    case ParserSym.Procedure:
                        resultado += "  <Reservada procedure>\t" + ParserSym.terminalNames[ParserSym.Procedure] + "\n";
                        break;
                    case ParserSym.Return:
                        resultado += "  <Reservada return>\t" + ParserSym.terminalNames[ParserSym.Return] + "\n";
                        break;
                    case ParserSym.P_coma:
                        resultado += "  <Punto y coma>\t" + ParserSym.terminalNames[ParserSym.P_coma] + "\n";
                        break;
                    case ParserSym.id:
                        resultado += "  <Identificador>\t\t" + ParserSym.terminalNames[ParserSym.id] + "\n";
                        break;
                    case ParserSym.Entero:
                        resultado += "  <Numero>\t\t" + ParserSym.terminalNames[ParserSym.Entero] + "\n";
                        break;
                    case ParserSym.Cadena:
                        resultado += "  <Cadena>\t\t" + ParserSym.terminalNames[ParserSym.Cadena] + "\n";
                        break;
                    case ParserSym.True:
                        resultado += "  <Op true>\t\t" + ParserSym.terminalNames[ParserSym.True] + "\n";
                        break;
                    case ParserSym.False:
                        resultado += "  <Op false>\t\t" + ParserSym.terminalNames[ParserSym.False] + "\n";
                        break;
                    case ParserSym.For:
                        resultado += "  <Reservada for>\t" + ParserSym.terminalNames[ParserSym.For] + "\n";
                        break;
                    case ParserSym.Switch:
                        resultado += "  <Reservada switch>\t" + ParserSym.terminalNames[ParserSym.Switch] + "\n";
                        break;
                    case ParserSym.Case:
                        resultado += "  <Reservada case>\t" + ParserSym.terminalNames[ParserSym.Case] + "\n";
                        break;
                    case ParserSym.Break:
                        resultado += "  <Reservada break>\t" + ParserSym.terminalNames[ParserSym.Break] + "\n";
                        break;
                    case ParserSym.Dos_puntos:
                        resultado += "  <Dos puntos>\t\t" + ParserSym.terminalNames[ParserSym.Dos_puntos] + "\n";
                        break;
                    case ParserSym.Increment:
                        resultado += "  <Incremento>\t\t" + ParserSym.terminalNames[ParserSym.Increment] + "\n";
                        break;
                    case ParserSym.Decrement:
                        resultado += "  <Decremento>\t" + ParserSym.terminalNames[ParserSym.Decrement] + "\n";
                        break;
                    case ParserSym.Default:
                        resultado += "  <Reservada default>\t" + ParserSym.terminalNames[ParserSym.Default] + "\n";
                        break;
                    case ParserSym.error:
                        resultado += "<Error léxico: " + ParserSym.terminalNames[ParserSym.error] + " produccion o simbolo no identificado >\n";
                        break;
                    default:
                        resultado += "< >\n";
                        break;
                }
            }
            this.txtAnalizarLex.setText(resultado);
            bw.write(resultado);
            bw.close();

        } catch (Exception ex) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Analizador = new javax.swing.JPanel();
        btnArchivo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAnalizarLex = new javax.swing.JTextArea();
        btnAnalizarLex = new javax.swing.JButton();
        btnLimpiarLex = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAnalizarSin = new javax.swing.JTextArea();
        btnAnalizarSin = new javax.swing.JButton();
        btnLimpiarSin = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtarbol = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        arbol = new javax.swing.JScrollPane();
        txtgramatica = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtTaulaSimbols = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        txt3dir = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        txt68k = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Analizador.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Analizador", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18)));
        Analizador.setBackground(new java.awt.Color(255, 0, 0));

        btnArchivo.setBackground(new java.awt.Color(255, 255, 255));
        btnArchivo.setFont(new java.awt.Font("Ebrima", 1, 18)); // NOI18N
        btnArchivo.setText("Abrir archivo");
        btnArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchivoActionPerformed(evt);
            }
        });

        txtResultado.setColumns(20);
        txtResultado.setRows(5);
        txtResultado.setDoubleBuffered(true);
        jScrollPane1.setViewportView(txtResultado);

        txtAnalizarLex.setEditable(false);
        txtAnalizarLex.setColumns(20);
        txtAnalizarLex.setRows(5);
        jScrollPane2.setViewportView(txtAnalizarLex);

        btnAnalizarLex.setBackground(new java.awt.Color(255, 255, 255));
        btnAnalizarLex.setFont(new java.awt.Font("Ebrima", 1, 18)); // NOI18N
        btnAnalizarLex.setText("Lexico");
        btnAnalizarLex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarLexActionPerformed(evt);
            }
        });

        btnLimpiarLex.setBackground(new java.awt.Color(255, 255, 255));
        btnLimpiarLex.setFont(new java.awt.Font("Ebrima", 1, 18)); // NOI18N
        btnLimpiarLex.setText("Limpiar");
        btnLimpiarLex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarLexActionPerformed(evt);
            }
        });

        txtAnalizarSin.setEditable(false);
        txtAnalizarSin.setColumns(20);
        txtAnalizarSin.setRows(5);
        jScrollPane3.setViewportView(txtAnalizarSin);

        btnAnalizarSin.setBackground(new java.awt.Color(255, 255, 255));
        btnAnalizarSin.setFont(new java.awt.Font("Ebrima", 1, 18)); // NOI18N
        btnAnalizarSin.setText("Sintaxis y Semantica");
        btnAnalizarSin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnalizarSinActionPerformed(evt);
            }
        });

        btnLimpiarSin.setBackground(new java.awt.Color(255, 255, 255));
        btnLimpiarSin.setFont(new java.awt.Font("Ebrima", 1, 18)); // NOI18N
        btnLimpiarSin.setText("Limpiar");
        btnLimpiarSin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarSinActionPerformed(evt);
            }
        });

        txtarbol.setColumns(20);
        txtarbol.setRows(5);
        jScrollPane4.setViewportView(txtarbol);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Ebrima", 1, 24)); // NOI18N
        jLabel1.setText("Árbol de sintaxis");

        txtgramatica.setColumns(20);
        txtgramatica.setRows(5);
        arbol.setViewportView(txtgramatica);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Ebrima", 1, 24)); // NOI18N
        jLabel2.setText("Tabla de Símbolos");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Ebrima", 1, 24)); // NOI18N
        jLabel3.setText("Gramática");

        txtTaulaSimbols.setColumns(20);
        txtTaulaSimbols.setRows(5);
        jScrollPane5.setViewportView(txtTaulaSimbols);

        txt3dir.setColumns(20);
        txt3dir.setRows(5);
        jScrollPane6.setViewportView(txt3dir);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Ebrima", 1, 24)); // NOI18N
        jLabel4.setText("Código ensamblador");

        txt68k.setColumns(20);
        txt68k.setRows(5);
        jScrollPane7.setViewportView(txt68k);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Ebrima", 1, 24)); // NOI18N
        jLabel5.setText("Código 3 direcciones");

        javax.swing.GroupLayout AnalizadorLayout = new javax.swing.GroupLayout(Analizador);
        Analizador.setLayout(AnalizadorLayout);
        AnalizadorLayout.setHorizontalGroup(
            AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AnalizadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(arbol)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addComponent(btnArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AnalizadorLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(btnAnalizarLex)
                        .addGap(66, 66, 66)
                        .addComponent(btnLimpiarLex))
                    .addGroup(AnalizadorLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(AnalizadorLayout.createSequentialGroup()
                        .addComponent(btnAnalizarSin)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimpiarSin))
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane6))
                .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AnalizadorLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AnalizadorLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(86, 86, 86))))
            .addGroup(AnalizadorLayout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(jLabel3)
                .addGap(191, 191, 191)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(129, 129, 129)
                .addComponent(jLabel4)
                .addGap(63, 63, 63))
        );
        AnalizadorLayout.setVerticalGroup(
            AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AnalizadorLayout.createSequentialGroup()
                .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(AnalizadorLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnArchivo)
                            .addComponent(btnAnalizarLex)
                            .addComponent(btnLimpiarLex)
                            .addComponent(btnAnalizarSin)
                            .addComponent(btnLimpiarSin)
                            .addComponent(jLabel1))
                        .addGap(29, 29, 29)
                        .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(AnalizadorLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(AnalizadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                    .addComponent(arbol, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                    .addComponent(jScrollPane5))
                .addGap(21, 21, 21))
        );

        btnAnalizarLex.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Analizador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 13, Short.MAX_VALUE)
                .addComponent(Analizador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Analizador.getAccessibleContext().setAccessibleName("");
        Analizador.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivoActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(null);
        File archivo = null;
        if (chooser.getSelectedFile() != null) {
            archivo = new File(chooser.getSelectedFile().getAbsolutePath());
        }

        try {
            if (archivo != null) {
                String ST = new String(Files.readAllBytes(archivo.toPath()));
                txtResultado.setText(ST);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnArchivoActionPerformed

    private void btnLimpiarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarLexActionPerformed
        // TODO add your handling code here:
        txtAnalizarLex.setText(null);
    }//GEN-LAST:event_btnLimpiarLexActionPerformed

    private void btnLimpiarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarSinActionPerformed
        // TODO add your handling code here:
        txtAnalizarSin.setText(null);
        this.txtTaulaSimbols.setText(null);
        this.txtgramatica.setText(null);
        this.txt68k.setText(null);
        this.txtarbol.setText(null);
        this.txt3dir.setText(null);
        
    }//GEN-LAST:event_btnLimpiarSinActionPerformed

    private void btnAnalizarLexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarLexActionPerformed
        try {
            analizarLexico();
        } catch (IOException ex) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAnalizarLexActionPerformed

    private void btnAnalizarSinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnalizarSinActionPerformed
        BufferedWriter bw = null;
        BufferedWriter bw2 = null;
        BufferedReader bf = null;
        // TODO add your handling code here:
        Nodo nodo;
        String ST = txtResultado.getText();
        SymbolFactory sf = new ComplexSymbolFactory();
        Scanner lexer = new Scanner(new StringReader(ST));
        Parser s = new Parser(lexer, sf);
        try {

            //analisis sintactico
            s.parse();

            if (s.get_errores() > 0) {
                this.txtAnalizarSin.setText(s.get_Errores());
                File errorFile = new File(rutaProyecto + "/ficheros_generados/ErrorSintactic.txt");
                if (!errorFile.exists()) {
                    bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/ErrorSintactic.txt"));
                } else {
                    bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/ErrorSintactic" + (contarFicheros("ErrorSintactic") + 1) + ".txt"));
                }
                bw2.write(s.get_Errores());
                bw2.close();
                txtAnalizarSin.setForeground(new Color(255, 0, 0));
                return;
            } else {
                txtAnalizarSin.setText("Analisis Sintactico realizado correctamente");
                txtAnalizarSin.setForeground(new Color(25, 111, 61));
            }
            //generacion de arbol sintactico
            nodo = s.getInitNodo();
            String resultado = "";
            nodo.print_sons(nodo, 0);
            resultado = nodo.get_resultado();
//            bw2.write(resultado);
            this.txtarbol.setText(resultado);
            File treeFile = new File(rutaProyecto + "/ficheros_generados/ÁrbolSintactico.txt");
            if (!treeFile.exists()) {
                bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/ÁrbolSintactico.txt"));
            } else {
                bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/ÁrbolSintactico" + (contarFicheros("ÁrbolSintactico") + 1) + ".txt"));
            }
            bw2.write(resultado);
            bw2.close();

            //analisis semantico
            AnalisisSemantico as = new AnalisisSemantico(nodo);
            as.analisis_previo(nodo);
            String errores = "";
            if (as.getErrores().size() > 0 /*|| as.ts.get_errores() > 0*/) {
                String st = "";
                for (int i = 0; i < as.getErrores().size(); i++) {
                    st += as.getErrores().get(i) + "\n";
                }
                txtgramatica.setText(st);

                errores += as.getNumErrors();
                //errores += as.ts.get_Errores();
                File errorFile = new File(rutaProyecto + "/ficheros_generados/ErrorSemantic.txt");
                if (!errorFile.exists()) {
                    bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/ErrorSemantic.txt"));
                } else {
                    bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/ErrorSemantic" + (contarFicheros("ErrorSemantic") + 1) + ".txt"));
                }
                bw2.write(as.getErrores().toString());
                bw2.close();
//                this.txtgramatica.setText(errores);
                txtgramatica.setForeground(new Color(255, 0, 0));
                return;
            } else {
                txtgramatica.setText("Analisis Semantico realizado correctamente");
                txtgramatica.setForeground(new Color(25, 111, 61));
                this.txtTaulaSimbols.setText(as.getTs().imprimirTablaSymbols());
                File ts = new File(rutaProyecto + "/ficheros_generados/TaulaSimbol.txt");
                if (!ts.exists()) {
                    bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/TaulaSimbol.txt"));
                } else {
                    bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/TaulaSimbol" + (contarFicheros("TaulaSimbol") + 1) + ".txt"));
                }
                String str = as.getTs().imprimirTablaSymbols();
                bw2.write(str);
                bw2.close();
            }

            //generacion de codigo intermedio
            Generacion_C3Dir g3dir = new Generacion_C3Dir(as);
            g3dir.genera(nodo);
            txt3dir.setText(g3dir.getCodi().show_3dircode());
            File dirFile = new File(rutaProyecto + "/ficheros_generados/Codi3dir.txt");
            if (!dirFile.exists()) {
                bw2 = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(rutaProyecto + "/ficheros_generados/Codi3dir.txt"), StandardCharsets.UTF_8)));
            } else {
                bw2 = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(rutaProyecto + "/ficheros_generados/Codi3dir" + (contarFicheros("Codi3dir") + 1) + ".txt"), StandardCharsets.UTF_8)));
            }
            bw2.write(g3dir.get_codi());
            bw2.close();

            //generación codigo ensamblador sin optimizar
            Codi68k assembler = new Codi68k(g3dir.getCodi(), g3dir);
            assembler.genera();
            this.txt68k.setText(assembler.getResultado());
            File ass = new File(rutaProyecto + "/ficheros_generados/Assembler.x68");
            if (!ass.exists()) {
                bw2 = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(rutaProyecto + "/ficheros_generados/Assembler.x68"), StandardCharsets.UTF_8)));
            } else {
                bw2 = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(rutaProyecto + "/ficheros_generados/Assembler" + (contarFicheros("Assembler") + 1) + ".x68"), StandardCharsets.UTF_8)));
            }
            bw2.write(assembler.getResultado());
            bw2.close();
            
            //tabla de variables y procedimientos
            File tvFile = new File(rutaProyecto + "/ficheros_generados/TV.txt");
            if (!tvFile.exists()) {
                bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/TV.txt"));
            } else {
                bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/TV" + (contarFicheros("TV") + 1) + ".txt"));
            }
            bw2.write(g3dir.getTv().imprimirTV());
            bw2.close();
            File tpFile = new File(rutaProyecto + "/ficheros_generados/TP.txt");
            if (!tpFile.exists()) {
                bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/TP.txt"));
            } else {
                bw2 = new BufferedWriter(new FileWriter(rutaProyecto + "/ficheros_generados/TP" + (contarFicheros("TP") + 1) + ".txt"));
            }
            bw2.write(g3dir.getTp().imprimirTP());
            bw2.close();

            //generacion de codigo intermedio optimizado
            Generacion_C3Dir_Opt g3dirOpt = new Generacion_C3Dir_Opt(g3dir);
            g3dirOpt.optimitzar();
//            txt3dir.setText(g3dirOpt.getCodi().show_3dircode());

            File dirFile2 = new File(rutaProyecto + "/ficheros_generados/Codi3dirOpt.txt");
            if (!dirFile2.exists()) {
                bw2 = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(rutaProyecto + "/ficheros_generados/OptCodi3dir.txt"), StandardCharsets.UTF_8)));
            } else {
                bw2 = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(rutaProyecto + "/ficheros_generados/OptCodi3dir" + (contarFicheros("OptCodi3dir") + 1) + ".txt"), StandardCharsets.UTF_8)));
            }
            bw2.write(g3dirOpt.getCodi().show_3dircode());
            bw2.close();

            //generación codigo ensamblador optimizado
            Codi68k assemblerOpt = new Codi68k(g3dirOpt.getCodi(), g3dirOpt.getGc3dir());
            assemblerOpt.genera();
//            this.txt68k.setText(assemblerOpt.getResultado());
            File assOpt = new File(rutaProyecto + "/ficheros_generados/OptAssembler.x68");
            if (!assOpt.exists()) {
                bw2 = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(rutaProyecto + "/ficheros_generados/OptAssembler.x68"), StandardCharsets.UTF_8)));
            } else {
                bw2 = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(rutaProyecto + "/ficheros_generados/OptAssembler" + (contarFicheros("OptAssembler") + 1) + ".x68"), StandardCharsets.UTF_8)));
            }
            bw2.write(assemblerOpt.getResultado());
            bw2.close();

            

        } catch (Exception e) {
            Logger.getLogger(FrmPrincipal.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_btnAnalizarSinActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JPanel Analizador;
    private javax.swing.JScrollPane arbol;
    private javax.swing.JButton btnAnalizarLex;
    private javax.swing.JButton btnAnalizarSin;
    private javax.swing.JButton btnArchivo;
    private javax.swing.JButton btnLimpiarLex;
    private javax.swing.JButton btnLimpiarSin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea txt3dir;
    private javax.swing.JTextArea txt68k;
    private javax.swing.JTextArea txtAnalizarLex;
    private javax.swing.JTextArea txtAnalizarSin;
    private javax.swing.JTextArea txtResultado;
    private javax.swing.JTextArea txtTaulaSimbols;
    private javax.swing.JTextArea txtarbol;
    private javax.swing.JTextArea txtgramatica;
    // End of variables declaration//GEN-END:variables
}

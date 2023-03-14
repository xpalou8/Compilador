/**
 * Assignatura 21780 - Compiladors
 * Estudis: Grau en Informàtica 
 * Itinerari: Intel·ligència Artificial i Computació
 *
 * Autors:
 * Antonio Borrás Kostov
 * Xavier Matas Perelló
 * Gerard Medina Martorell
 * Xavier Palou Oliver
 *
 */
package CompiladoresMuerte2.Lexic;

import java.io.*;
import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import CompiladoresMuerte2.Sintactic.ParserSym;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%
%standalone
%cup

%public
%class Scanner

//Tipo tokens identificados
%char
%line
%column

//End of File
%eofval{
  return symbol(ParserSym.EOF);
%eofval}

//Declaraciones

espacio = [' '|'\t'|'\r'|'\n']+
digits = [0-9][0-9]*

/* Palabras reservadas */
Int = int
String = string
Bool = bool
Const = const
If = if
Else = else
Switch = switch
Case = case
Break = break
Default = default
For = for
While = while
Procedure = procedure
Function = function
Return = return
End = end
Print = print
ReadInt = readint
ReadString = readstring
True = true
False = false


/* Operaciones */
Igual = \=
Suma = \+
Resta = \-
Mult = \*
Div = \/
And = and
Or = or
Mayor = \>
Menor = \<
Equal = ==
Not_equal = \!=
Mayor_equal = \>=
Menor_equal = \<=
Increment = \++
Decrement = \--

/* Símbolos */
Parentesis_a = \(
Parentesis_c = \)
Llave_a = \{
Llave_c = \}
Corchete_a = \[
Corchete_c = \]

Main = main

P_coma = ;
Coma = ,
Dos_puntos = :
Comillas = \"

Cadena = {Comillas}.*{Comillas}
id = [A-Za-z_][A-Za-z0-9_]*
Entero = [-]?{digits}

%{
    /***
       Mecanismo de gestión de símbolos basado en ComplexSymbol
     ***/
    /**
       Construcción de un símbolo sin valor asociado.
     **/
    private ComplexSymbol symbol(int type) {
        Location l = new Location(yyline+1, yycolumn+1); // primera posiciÃ³ del token
        Location r = new Location(yyline+1, yycolumn+1+yylength()); // ultima posiciÃ³ del token
        ComplexSymbol val = new ComplexSymbol(ParserSym.terminalNames[type], type, l, r);
        ComplexSymbol c = new ComplexSymbol(ParserSym.terminalNames[type], type, l, r, val);
        c.left = yyline+1;
        c.right = yycolumn;
        return  c;
    }
    
    /**
       Construcción de un simbolo con atributo asociado.
     **/
    private ComplexSymbol symbol(int type, Object value){
        Location l = new Location(yyline+1, yycolumn+1); // primera posiciÃ³ del token
        Location r = new Location(yyline+1, yycolumn+1+yylength()); // ultima posiciÃ³ del token
        ComplexSymbol c = new ComplexSymbol(ParserSym.terminalNames[type], type, l, r, value);
        c.left = yyline+1;
        c.right = yycolumn;
        return  c;
    }
    
    public int lexema;
%}

%%
// Reglas/acciones
{Int}           { return symbol(ParserSym.Int, this.yytext()); }
{String}        { return symbol(ParserSym.String, this.yytext()); }
{Bool}          { return symbol(ParserSym.Bool, this.yytext()); }
{Const}         { return symbol(ParserSym.Const, this.yytext()); }
{If}            { return symbol(ParserSym.If, this.yytext()); }
{Else}          { return symbol(ParserSym.Else, this.yytext()); }
{Switch}        { return symbol(ParserSym.Switch, this.yytext()); }
{Case}          { return symbol(ParserSym.Case, this.yytext()); }
{Break}         { return symbol(ParserSym.Break, this.yytext()); }
{Default}       { return symbol(ParserSym.Default, this.yytext()); }
{For}           { return symbol(ParserSym.For, this.yytext()); }
{While}         { return symbol(ParserSym.While, this.yytext()); }
{Procedure}     { return symbol(ParserSym.Procedure, this.yytext()); }
{Function}      { return symbol(ParserSym.Function, this.yytext()); }
{Return}        { return symbol(ParserSym.Return, this.yytext()); }
{End}           { return symbol(ParserSym.End, this.yytext()); }
{Print}         { return symbol(ParserSym.Print, this.yytext()); }
{ReadInt}       { return symbol(ParserSym.ReadInt, this.yytext()); }
{ReadString}    { return symbol(ParserSym.ReadString, this.yytext()); }
{Igual}         { return symbol(ParserSym.Igual, this.yytext()); }
{Suma}          { return symbol(ParserSym.Suma, this.yytext()); }
{Resta}         { return symbol(ParserSym.Resta, this.yytext()); }
{Mult}          { return symbol(ParserSym.Mult, this.yytext()); }
{Div}           { return symbol(ParserSym.Div, this.yytext()); }
{And}           { return symbol(ParserSym.And, this.yytext()); }
{Or}            { return symbol(ParserSym.Or, this.yytext()); }
{Mayor}         { return symbol(ParserSym.Mayor, this.yytext()); }
{Menor}         { return symbol(ParserSym.Menor, this.yytext()); }
{Equal}         { return symbol(ParserSym.Equal, this.yytext()); }
{Not_equal}     { return symbol(ParserSym.Not_equal, this.yytext()); }
{Mayor_equal}   { return symbol(ParserSym.Mayor_equal, this.yytext()); }
{Menor_equal}   { return symbol(ParserSym.Menor_equal, this.yytext()); }
{Increment}     { return symbol(ParserSym.Increment, this.yytext()); }
{Decrement}     { return symbol(ParserSym.Decrement, this.yytext()); }
{Parentesis_a}  { return symbol(ParserSym.Parentesis_a, this.yytext()); }
{Parentesis_c}  { return symbol(ParserSym.Parentesis_c, this.yytext()); }
{Llave_a}       { return symbol(ParserSym.Llave_a, this.yytext()); }
{Llave_c}       { return symbol(ParserSym.Llave_c, this.yytext()); }
{Corchete_a}    { return symbol(ParserSym.Corchete_a, this.yytext()); }
{Corchete_c}    { return symbol(ParserSym.Corchete_c, this.yytext()); }
{True}          { return symbol(ParserSym.True, this.yytext()); }
{False}         { return symbol(ParserSym.False, this.yytext()); }

{Main}          { return symbol(ParserSym.Main, this.yytext()); }

{P_coma}        { return symbol(ParserSym.P_coma, this.yytext()); }
{Dos_puntos}    { return symbol(ParserSym.Dos_puntos, this.yytext()); }
{Coma}          { return symbol(ParserSym.Coma, this.yytext()); }

/* id es la última porque podría hacer incompatibles las palabras reservadas */
/* al contener todos los caractéres */
{Cadena}        { return symbol(ParserSym.Cadena, this.yytext()); }
{id}            { return symbol(ParserSym.id, this.yytext()); }
{Entero}        { return symbol(ParserSym.Entero, Integer.parseInt(this.yytext())); }
{espacio}       { /* Ignorar los espacios */  }

[^]             { return symbol(ParserSym.error); }

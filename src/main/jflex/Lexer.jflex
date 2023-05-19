
package at.ac.tuwien.sepm.groupphase.backend.service.impl.logic;
import java_cup.runtime.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.LexicalException;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.logic.sym;
import at.ac.tuwien.sepm.groupphase.backend.exception.LexicalException;

%%

%class Lexer
%unicode
%cup

LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [   \t\f]

%%

"("                      { return new Symbol(sym.BL); }
")"                      { return new Symbol(sym.BR); }
"AND"                    { return new Symbol(sym.AND); }
"NOT"                    { return new Symbol(sym.NOT); }
"OR"                     { return new Symbol(sym.OR); }
"TRUE"                   { return new Symbol(sym.LITERAL, true); }
"FALSE"                  { return new Symbol(sym.LITERAL, false); }
","                      { return new Symbol(sym.COMMA); }
">"                      { return new Symbol(sym.GREATER); }
"<"                      { return new Symbol(sym.SMALLER); }
"="                      { return new Symbol(sym.EQUALS); }
"["                      { return new Symbol(sym.AL); }
"]"                      { return new Symbol(sym.AR); }
[0-9]+                   { return new Symbol(sym.NUMBER, Integer.parseInt(yytext())); }
q[0-9]+                  { return new Symbol(sym.VARIABLE, Long.parseLong(yytext().substring(1))); }
m[0-9]+                  { return new Symbol(sym.VARIABLE, Long.parseLong(yytext().substring(1))); }

{WhiteSpace}             { /* ignore */}
.                        { throw new ParsingException("Illegal character <"+yytext()+">", FrontendErrorCode.LOGIC_PARSING_ERROR); }
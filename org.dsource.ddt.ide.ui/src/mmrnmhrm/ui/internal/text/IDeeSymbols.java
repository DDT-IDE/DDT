package mmrnmhrm.ui.internal.text;

import org.eclipse.dltk.ruby.internal.ui.text.ISymbols;

/**
 * Symbols for the heuristic dee scanner.
 *
 */
public interface IDeeSymbols extends ISymbols {
	
	/*
	int TokenIDENTIFIER = 15;
	int TokenBACKSLASH = 16;
	int TokenSLASH = 17;
	int TokenPLUS = 18;
	int TokenMINUS = 19;
	int TokenSTAR = 20;
	*/
	
	int TokenIF= TokenUserDefined + 9;
	int TokenDO= TokenUserDefined + 10;
	int TokenFOR= TokenUserDefined + 11;
	int TokenTRY= TokenUserDefined + 12;
	int TokenCASE= TokenUserDefined + 13;
	int TokenELSE= TokenUserDefined + 14;
	int TokenBREAK= TokenUserDefined + 15;
	int TokenCATCH= TokenUserDefined + 16;
	int TokenWHILE= TokenUserDefined + 17;
	int TokenRETURN= TokenUserDefined + 18;
	int TokenSTATIC= TokenUserDefined + 19;
	int TokenSWITCH= TokenUserDefined + 20;
	int TokenFINALLY= TokenUserDefined + 21;
	int TokenSYNCHRONIZED= TokenUserDefined + 22;
	int TokenGOTO= TokenUserDefined + 23;
	int TokenDEFAULT= TokenUserDefined + 24;
	int TokenNEW= TokenUserDefined + 25;
	int TokenCLASS= TokenUserDefined + 26;
	int TokenINTERFACE= TokenUserDefined + 27;
	int TokenENUM= TokenUserDefined + 28;
	int TokenIDENT= TokenUserDefined + 200;
}

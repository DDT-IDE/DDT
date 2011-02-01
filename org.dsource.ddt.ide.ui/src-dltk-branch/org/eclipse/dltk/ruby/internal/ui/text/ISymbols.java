package org.eclipse.dltk.ruby.internal.ui.text;

/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * 
 ******************************************************************************/

public interface ISymbols {
	int TokenEOF = -1;
	int TokenLBRACE = 1;
	int TokenRBRACE = 2;
	int TokenLBRACKET = 3;
	int TokenRBRACKET = 4;
	int TokenLPAREN = 5;
	int TokenRPAREN = 6;
	int TokenSEMICOLON = 7;
	int TokenCOLON = 8;
	int TokenCOMMA = 9;
	int TokenQUESTIONMARK = 10;
	int TokenEQUAL = 11;
	int TokenLESSTHAN = 12;
	int TokenGREATERTHAN = 13;
	int TokenOTHER = 14;
	int TokenIDENTIFIER = 15;
	int TokenBACKSLASH = 16;
	int TokenSLASH = 17;
	int TokenPLUS = 18;
	int TokenMINUS = 19;
	int TokenSTAR = 20;
	
	/**
	 * Used as base id for language dependent tokens
	 */
	int TokenUserDefined = 1000;
}

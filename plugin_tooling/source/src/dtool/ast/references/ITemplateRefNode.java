/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.ast.references;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.engine.resolver.IReference;

/** Marker interface for nodes that can appear as template references in template instance. 
 * Must be a {@link Reference}.*/
public interface ITemplateRefNode extends IASTNode, IReference { }
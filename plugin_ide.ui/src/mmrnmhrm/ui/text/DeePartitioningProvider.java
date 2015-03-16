/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.text;

import mmrnmhrm.core.text.DeePartitionScanner;
import mmrnmhrm.core.text.DeePartitions;

import org.eclipse.dltk.ui.text.IPartitioningProvider;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

public class DeePartitioningProvider implements IPartitioningProvider {
	
	private static final DeePartitioningProvider _instance = new DeePartitioningProvider();
	
	public static DeePartitioningProvider getInstance() {
		return _instance;
	}
	
	@Override
	public String getPartitioning() {
		return DeePartitions.PARTITIONING_ID;
	}
	
	@Override
	public String[] getPartitionContentTypes() {
		return DeePartitions.DEE_PARTITION_TYPES;
	}
	
	@Override
	public IPartitionTokenScanner createPartitionScanner() {
		return new DeePartitionScanner();
	}
	
}

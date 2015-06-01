/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package _org.eclipse.dltk.ui.text;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

public interface IPartitioningProvider {

	String getPartitioning();

	String[] getPartitionContentTypes();

	IPartitionTokenScanner createPartitionScanner();

}

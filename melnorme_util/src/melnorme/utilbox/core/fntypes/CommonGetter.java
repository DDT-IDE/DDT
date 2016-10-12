/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.core.fntypes;

import melnorme.utilbox.core.CommonException;

public interface CommonGetter<RET> extends Getter<RET, CommonException> {
	
	/** @return the result of given getter, null if an exception was thrown. */
	static <T> T getOrNull(CommonGetter<T> getter) {
		try {
			return getter.get();
		} catch(CommonException e) {
			return null;
		}
	}
	
}
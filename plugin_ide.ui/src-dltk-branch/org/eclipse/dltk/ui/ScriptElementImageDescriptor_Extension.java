/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * Extends functionality from {@link ScriptElementImageDescriptor_Fix}:
 *  - keeps track of drawing positions 
 */
public class ScriptElementImageDescriptor_Extension extends CompositeImageDescriptor
{
	/** Flag to render the abstract adornment. */
	public final static int ABSTRACT= 		0x001;
	
	/** Flag to render the final adornment. */
	public final static int FINAL=			0x002;
	
	/** Flag to render the static adornment. */
	public final static int STATIC=			0x008;
	
	/** Flag to render the 'constructor' adornment. */
	public final static int CONSTRUCTOR= 	0x200;
	
	/** Flag to render the 'override' adornment. */
	public final static int OVERRIDES = 0x080;

	/** Flag to render the 'implements' adornment. */
	public final static int IMPLEMENTS = 0x100;

	/** Flag to render the warning adornment. */
	public final static int WARNING=			0x020;
	
	/** Flag to render the error adornment. */
	public final static int ERROR=			0x040;	
	
	private Point fSize;
	private int fFlags;
	
	protected int fBottomRightPos;
	protected int fBottomLeftPos;
	protected int fTopRightPos;
	
	final ImageDescriptor fBaseImage;

	public ScriptElementImageDescriptor_Extension( ImageDescriptor baseImageDescriptor, int flags, Point size ) {
		fBaseImage= baseImageDescriptor;
		Assert.isNotNull(fBaseImage);
		fFlags= flags;
		Assert.isTrue(fFlags >= 0);
		fSize= size;
		Assert.isNotNull(fSize);
	}

	/**
	 * Sets the size of the image created by calling <code>createImage()</code>.
	 * 
	 * @param size the size of the image returned from calling <code>createImage()</code>
	 * @see ImageDescriptor#createImage()
	 */
	public void setImageSize(Point size) {
		Assert.isNotNull(size);
		Assert.isTrue(size.x >= 0 && size.y >= 0);
		fSize= size;
	}
	
	/**
	 * Returns the size of the image created by calling <code>createImage()</code>.
	 * 
	 * @return the size of the image created by calling <code>createImage()</code>
	 * @see ImageDescriptor#createImage()
	 */
	public Point getImageSize() {
		return new Point(fSize.x, fSize.y);
	}
	
	/* (non-Javadoc)
	 * Method declared in CompositeImageDescriptor
	 */
	@Override
	protected Point getSize() {
		return fSize;
	}
	
	/* (non-Javadoc)
	 * Method declared on Object.
	 */
	@Override
	public boolean equals(Object object) {		
		if (this == object)
			return true;
		if (object == null || this.getClass() != object.getClass())		
			return false;
		
		return equalsPeer((ScriptElementImageDescriptor_Extension) object);
	}
	
	public boolean equalsPeer(ScriptElementImageDescriptor_Extension other) {
		assertNotNull(this.fBaseImage);
		//==== Original DLTK Code: ====
		// return (other.fFlags == this.fFlags) && (fBaseImage.equals(other.fBaseImage) == fSize.equals(other.fSize));
		return (other.fFlags == this.fFlags) && (fSize.equals(other.fSize)) && (fBaseImage.equals(other.fBaseImage)) ;
	}
	
	/* (non-Javadoc)
	 * Method declared on Object.
	 */
	@Override
	public int hashCode() {
		if( this.fBaseImage != null ) {
			return fBaseImage.hashCode() | fSize.hashCode() + this.fFlags;
		}
		return fSize.hashCode();
	}

	protected ImageData getImageData( ImageDescriptor descriptor ) {

		if( this.fBaseImage != null ) {
			ImageData data = descriptor.getImageData( ); // see bug 51965: getImageData can return null
			if( data == null ) {
				data = DEFAULT_IMAGE_DATA;
				System.err.println( "Image data not available: " + descriptor.toString( ) ); //$NON-NLS-1$
				// DLTKUIPlugin.logErrorMessage("Image data not available: " + descriptor.toString()); //$NON-NLS-1$
			}
			return data;
		}
		else {			
			System.err.println( "Image data not available: " + descriptor.toString( ) ); //$NON-NLS-1$
			return DEFAULT_IMAGE_DATA;
		}
	}

	
	@Override
	protected void drawCompositeImage( int width, int height ) {
		ImageData bg= getImageData(fBaseImage);
		
		if( bg != null ) {
			drawImage(bg, 0, 0);
		}
		drawTopRight();
		drawBottomRight();
		drawBottomLeft();
	}
	
	private void drawBottomLeft() {
		Point size= getSize();
		int x= 0;
		if ((fFlags & ERROR) != 0) {
			ImageData data= getImageData(DLTKPluginImages.DESC_OVR_ERROR);
			drawImage(data, x, size.y - data.height);
			x += data.width;
		}
		if ((fFlags & WARNING) != 0) {
			ImageData data= getImageData(DLTKPluginImages.DESC_OVR_WARNING);
			drawImage(data, x, size.y - data.height);
			x+= data.width;
		}
		
		fBottomLeftPos = x;
	}		
	
	private void drawTopRight() {
		Point pos = new Point(getSize().x, 0);
		if ((fFlags & ABSTRACT) != 0) {
			addTopRightImage(DLTKPluginImages.DESC_OVR_ABSTRACT, pos);
		}
		if ((fFlags & CONSTRUCTOR) != 0) {
			addTopRightImage(DLTKPluginImages.DESC_OVR_CONSTRUCTOR, pos);
		}
		if ((fFlags & FINAL) != 0) {
			addTopRightImage(DLTKPluginImages.DESC_OVR_FINAL, pos);
		}
		if ((fFlags & STATIC) != 0) {
			addTopRightImage(DLTKPluginImages.DESC_OVR_STATIC, pos);
		}

		fTopRightPos = pos.x;
	}

	private void drawBottomRight() {
		Point size = getSize();
		Point pos = new Point(size.x, size.y);

		int flags = fFlags;

		if ((flags & OVERRIDES) != 0) {
			addBottomRightImage(DLTKPluginImages.DESC_OVR_OVERRIDES, pos);
		}
		if ((flags & IMPLEMENTS) != 0) {
			addBottomRightImage(DLTKPluginImages.DESC_OVR_IMPLEMENTS, pos);
		}
		
		fBottomRightPos = pos.x;
	}

	protected void addTopRightImage(ImageDescriptor desc, Point pos) {
		ImageData data = getImageData(desc);
		int x = pos.x - data.width;
		if (x >= 0) {
			drawImage(data, x, pos.y);
			pos.x = x;
		}
	}

	protected void addBottomRightImage(ImageDescriptor desc, Point pos) {
		ImageData data = getImageData(desc);
		int x = pos.x - data.width;
		int y = pos.y - data.height;
		if (x >= 0 && y >= 0) {
			drawImage(data, x, y);
			pos.x = x;
		}
	}

}

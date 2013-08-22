package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.arrayI;
import static melnorme.utilbox.core.CoreUtil.downCast;

import java.util.Arrays;

import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor_Extension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;

import dtool.ast.declarations.AttribProtection.EProtection;

public class DeeElementImageDescriptor extends ScriptElementImageDescriptor_Extension {
	
	protected final DefElementDescriptor elementDesc;
	protected final EProtection prot;
	
	public DeeElementImageDescriptor(ImageDescriptor baseImage, DefElementDescriptor elementDesc, EProtection prot, 
		Point size) {
		super(baseImage, getImageAdornmentFlags(elementDesc.elementFlags), size);
		this.elementDesc = assertNotNull(elementDesc);
		this.prot = prot;
	}
	
	@Override
	public boolean equalsPeer(ScriptElementImageDescriptor_Extension object) {
		DeeElementImageDescriptor other = downCast(object);
		return this.prot == other.prot
			&& this.elementDesc.elementFlags == other.elementDesc.elementFlags
			&& super.equalsPeer(other);
	}
	
	@Override
	public int hashCode() {
		int protHashCode = prot == null ? 0 : prot.hashCode();
		return Arrays.hashCode(arrayI(protHashCode, elementDesc.elementFlags, super.hashCode()));
	}
	
	
	protected static int getImageAdornmentFlags(int elementFlags) {
		int imageFlags = 0;
		
		if (new DefElementDescriptor(elementFlags).isConstructor()) {
			imageFlags |= ScriptElementImageDescriptor.CONSTRUCTOR; // TODO: this should be its own base image
		}
		
		if(Flags.isAbstract(elementFlags)) {
			imageFlags |= ScriptElementImageDescriptor.ABSTRACT;
		}
		if(Flags.isFinal(elementFlags)) {
			imageFlags |= ScriptElementImageDescriptor.FINAL;
		}
		if(Flags.isStatic(elementFlags)) {
			imageFlags |= ScriptElementImageDescriptor.STATIC;
		}
		
		return imageFlags;
	}
	
	@Override
	protected void drawCompositeImage(int width, int height) {
		super.drawCompositeImage(width, height);
		if(prot == null)
			return;
		
		if(elementDesc.isImmutable()) {
			addTopRightImage(DeePluginImages.DESC_OVR_IMMUTABLE,  new Point(fTopRightPos, 0));
		}
		if(elementDesc.isConst()) {
			addTopRightImage(DeePluginImages.DESC_OVR_CONST,  new Point(fTopRightPos, 0));
		}
		
		Point pos;
		switch (prot) {
		
		case PRIVATE:
			pos = new Point(fBottomRightPos, getSize().y);
			addBottomRightImage(DeePluginImages.DESC_OVR_PRIVATE, pos);
			break;
			
		case PROTECTED:
			pos = new Point(fBottomRightPos, getSize().y);
			addBottomRightImage(DeePluginImages.DESC_OVR_PROTECTED, pos);
			break;
		
		case PACKAGE:
			pos = new Point(fBottomRightPos, getSize().y);
			addBottomRightImage(DeePluginImages.DESC_OVR_DEFAULT, pos);
			break;
		
		case PUBLIC:
		case EXPORT: // TODO?
			break;
		}
		
		if(elementDesc.isOverride()) {
			// Don't add because icon is ugly with function icon
			
//			pos = new Point(15, getSize().y);
//			addBottomRightImage(DeePluginImages.DESC_OVR_OVERRIDE, pos);
		}
		
	}
	
}
package mmrnmhrm.ui.views;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.arrayI;
import static melnorme.utilbox.core.CoreUtil.downCast;

import java.util.Arrays;

import mmrnmhrm.core.model_elements.DefElementDescriptor;
import mmrnmhrm.core.model_elements.DefElementFlagConstants;
import mmrnmhrm.ui.DeePluginImages;

import org.eclipse.dltk.ui.ScriptElementImageDescriptor;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor_Extension;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

import dtool.ast.declarations.AttribProtection.EProtection;
import dtool.ast.definitions.EArcheType;

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
			imageFlags |= ScriptElementImageDescriptor.CONSTRUCTOR;
		}
		
		return imageFlags;
	}
	
	@Override
	protected void drawCompositeImage(int width, int height) {
		if(elementDesc.isOverride()) {
			// Don't add because icon is ugly combined with function icon
			
//			addTopRightImage(DeePluginImages.DESC_OVR_OVERRIDE, new Point(getSize().x, 0));
		}
		
		super.drawCompositeImage(width, height);
		
		
		drawProtectionAdornment();
		
		Point topRightPoint = new Point(fTopRightPos, 0);
		if(elementDesc.isFlag(DefElementFlagConstants.FLAG_STATIC)) {
			addTopRightImage(DeePluginImages.DESC_OVR_STATIC, topRightPoint);
		}
		
		if(elementDesc.isFlag(DefElementFlagConstants.FLAG_FINAL)) {
			addTopRightImage(DeePluginImages.DESC_OVR_FINAL, topRightPoint);
		} else if(elementDesc.isFlag(DefElementFlagConstants.FLAG_ABSTRACT)) {
			addTopRightImage(DeePluginImages.DESC_OVR_ABSTRACT, topRightPoint);
		}
		
		if(elementDesc.isImmutable()) {
			addTopRightImage(DeePluginImages.DESC_OVR_IMMUTABLE, topRightPoint);
		} else if(elementDesc.isConst()) {
			addTopRightImage(DeePluginImages.DESC_OVR_CONST, topRightPoint);
		}
		
		if(elementDesc.isFlag(DefElementFlagConstants.FLAG_TEMPLATED)) {
			int x = 0;
			ImageData data = getImageData(DeePluginImages.DESC_OVR_TEMPLATED);
			drawImage(data, x, 0);
		}
		
		if(elementDesc.getArcheType() == EArcheType.Alias) {
			int x = 0;
			ImageData data = getImageData(DeePluginImages.DESC_OVR_ALIAS);
			drawImage(data, x, getSize().y - data.height);
		}
		
	}
	
	public void drawProtectionAdornment() {
		if(prot == null)
			return;
		
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
	}
	
}
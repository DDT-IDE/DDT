package dtool.tests.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.refmodel.NodeUtil;

public class FindDef__Common {
	
	protected static Module parseNeoModuleNode(String filepath) {
		ISourceModule sourceModuleDLTK = SampleMainProject.getSourceModule(filepath);
		return SampleMainProject.parsedDeeModule(sourceModuleDLTK).neoModule;
	}
	
	
	public static int counter = -666;
	
	protected static void staticTestInit(String testfile) {
		counter = -1;
		System.out.println("======== " + testfile + " ========");
	}
	
	protected Module sourceModule;
	protected int offset;
	protected Module targetModule;
	protected int targetOffset;
	
	protected int getMarkerEndOffset(String marker) throws ModelException {
		String source = sourceModule.getModuleUnit().getSource();
		return source.indexOf(marker) + marker.length();
	}
	
	protected int getMarkerStartOffset(String marker) throws ModelException {
		String source = sourceModule.getModuleUnit().getSource();
		return source.indexOf(marker);
	}
	
	
	public static void assertFindReF(Module srcMod, int offset, Module targetMod, int targetOffset)
			throws ModelException {
		
		counter++;
		
		Module newSrcModule = SampleMainProject.parsedDeeModule(srcMod.getModuleUnit()).neoModule;
		ASTNeoNode node = ASTNodeFinder.findElement(newSrcModule, offset);
		Reference ref = (Reference) node;
		
		Collection<DefUnit> defunits = ref.findTargetDefUnits(true);
		
		if(defunits == null || defunits.isEmpty()) {
			if(targetOffset == -1)
				return; // Ok, it matches the expected
			assertFail(" Find Ref got no DefUnit.");
		}
		DefUnit defunit = defunits.iterator().next();
		
		assertTrue(defunit != null, " defunit = null");
		
		Module obtainedModule = NodeUtil.getParentModule(defunit);
		assertTrue(equalModule(targetMod, obtainedModule),
				" Find Ref got wrong target module.");
		
		assertTrue(defunit.defname.getStartPos() == targetOffset,
				" Find Ref went to wrong offset: " + defunit.defname.getStartPos());
	}
	
	private static boolean equalModule(Module targetMod, Module obtainedModule) {
		return targetMod == obtainedModule || targetMod.getModuleUnit().equals(obtainedModule.getModuleUnit());
	}
	
}

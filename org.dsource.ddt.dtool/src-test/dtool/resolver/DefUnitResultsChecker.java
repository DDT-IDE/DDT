package dtool.resolver;

import static dtool.resolver.CompareDefUnits.fnDefUnitToStringAsName;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.misc.CollectionUtil.createArrayList;
import static melnorme.utilbox.misc.StringUtil.collToString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.IDefElement;
import dtool.ast.definitions.Module;
import dtool.ast.util.NodeUtil;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.CommonTestUtils;

public class DefUnitResultsChecker extends CommonTestUtils {
	
	protected final Collection<IDefElement> resultDefUnits;
	
	public DefUnitResultsChecker(Collection<? extends IDefElement> resultDefUnits) {
		this.resultDefUnits = createArrayList(resultDefUnits);
	}
	
	public void removeIgnoredDefUnits(boolean ignoreDummyResults, boolean ignorePrimitives) {
		removeIgnoredDefUnits(resultDefUnits, ignoreDummyResults, ignorePrimitives);
	}
	
	public static void removeIgnoredDefUnits(Collection<IDefElement> resultDefUnits, 
		boolean ignoreDummyResults, boolean ignorePrimitives) {
		for (Iterator<IDefElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
			IDefElement defElement = iterator.next();
			
			if(ignoreDummyResults && 
				(defElement.getName().equals("_dummy") || defElement.getName().equals("_ignore"))) {
				iterator.remove();
			}
			
			if(ignorePrimitives && defElement.isLanguageIntrinsic()) {
				iterator.remove();
			}
		}
	}
	
	public void checkResults(String[] expectedResults, Map<String, MetadataEntry> markers) {
		HashSet<String> expectedResultsDeduplicated = hashSet(expectedResults);
		
		for (String expectedTarget : expectedResultsDeduplicated) {
			if(expectedTarget.startsWith("@") ) {
				String markerName = expectedTarget.substring(1);
				MetadataEntry marker = assertNotNull(markers.get(markerName));
				DefUnitResultsChecker.removeDefUnitByMarker(resultDefUnits, marker);
			} else {
				removeDefUnit(expectedTarget);
			}
		}
		
		if(!resultDefUnits.isEmpty()) {
			String resultDefUnitsStr = collToString(strmap(resultDefUnits, fnDefUnitToStringAsName()), "\n");
			System.out.println("--- Unexpected elements ("+resultDefUnits.size()+") : ---\n" + resultDefUnitsStr);
		}
		assertTrue(resultDefUnits.isEmpty());
	}
	
	public void removeDefUnit(String expectedTarget) {
		String moduleName = StringUtil.segmentUntilMatch(expectedTarget, "/");
		String defUnitModuleQualifiedName = StringUtil.substringAfterMatch(expectedTarget, "/");
		
		boolean removed = false;
		if(moduleName == null ) {
			for (Iterator<IDefElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
				IDefElement defElement = iterator.next();
				
				if(defElement.getName().equals(expectedTarget)) {
					iterator.remove();
					removed = true;
				}
			}
		} else {
			String expectedFullyTypedQualification = moduleName + 
				(defUnitModuleQualifiedName != null ? "/" + defUnitModuleQualifiedName : "");
			
			for (Iterator<IDefElement> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
				IDefElement defElement = iterator.next();
				
				String defUnitTypedQualification = getDefUnitTypedQualification(defElement);
				if(defUnitTypedQualification.equals(expectedFullyTypedQualification)) {
					iterator.remove();
					removed = true;
				} else {
					continue; // Not a match
				}
			}
		}
		if(removed == false) {
			System.out.println(" > Not Found: " + expectedTarget);
			assertFail(); // Must find a matching result
		}
	}
	
	/**
	 * Return a name identifying this defUnit in the projects source code.
	 * It's similar to a fully qualified name, but has some more information on the name about
	 * the containing defunits.
	 * (the name is not enough to uniquely locate a defUnit in a project. That's the goal anyways)
	 */
	public static String getDefUnitTypedQualification(IDefElement defElement) {
		String base = getDefUnitTypeQualificationBase(defElement);
		switch(defElement.getArcheType()) {
		case Package:
			base += "/";
			break;
		default:
		}
		return base;
	}
	
	public static String getDefUnitTypeQualificationBase(IDefElement defElement) {
		if(defElement.getArcheType() == EArcheType.Module) {
			return defElement.getModuleFullyQualifiedName() + "/";
		}
		
		if(defElement.isLanguageIntrinsic()) { 
			return NATIVES_ROOT + defElement.getName();
		}
		
		IDefElement parentNamespace = defElement.getParentNamespace();
		if(parentNamespace == null) {
			return defElement.getName();
		} else {
			String sep = parentNamespace.getArcheType() == EArcheType.Module  ? "" : ".";
			String parentQualifedName = getDefUnitTypeQualificationBase(parentNamespace);
			String qualification = parentQualifedName + sep;
			return qualification + defElement.getName();
		}
	}
	
	public static String NATIVES_ROOT = "/";
	
	public static String getDefUnitModuleQualifedName(DefUnit defUnit) {
		if(defUnit instanceof Module) {
			return "";
		}
		DefUnit parentDefUnit = NodeUtil.getParentDefUnit(defUnit);
		String parentQualifedName = getDefUnitModuleQualifedName(parentDefUnit);
		if(parentQualifedName == "") {
			return defUnit.getName();
		}
		return parentQualifedName + "." + defUnit.getName();
	}
	
	
	
	/* ------ */
	
	public static void removeDefUnitByMarker(Collection<IDefElement> resolvedDefUnits, MetadataEntry marker) {
		for (Iterator<IDefElement> iterator = resolvedDefUnits.iterator(); iterator.hasNext(); ) {
			IDefElement defElement = iterator.next();
			if(defElement instanceof DefUnit) {
				DefUnit defNode = (DefUnit) defElement;
				if(defNode.defname.getEndPos() == marker.offset || defNode.defname.getStartPos() == marker.offset) {
					iterator.remove();
					return;
				}
			}
		}
		assertFail();
	}
	
}
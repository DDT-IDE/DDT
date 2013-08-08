package dtool.resolver;

import static dtool.tests.MiscDeeTestUtils.fnDefUnitToStringAsName;
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
import dtool.ast.NodeUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule.LiteModuleDummy;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.CommonTestUtils;

public class DefUnitResultsChecker extends CommonTestUtils {
	
	protected final Collection<DefUnit> resultDefUnits;
	
	public DefUnitResultsChecker(Collection<DefUnit> resultDefUnits) {
		this.resultDefUnits = createArrayList(resultDefUnits);
	}
	
	public void checkResults(String[] expectedResults, Map<String, MetadataEntry> markers) {
		HashSet<String> expectedResultsDeduplicated = hashSet(expectedResults);
		
		for (String expectedTarget : expectedResultsDeduplicated) {
			if(expectedTarget.startsWith("@") ) {
				String markerName = expectedTarget.substring(1);
				MetadataEntry marker = assertNotNull(markers.get(markerName));
				DefUnitResultsChecker.removedDefUnitByMarker(resultDefUnits, marker);
			} else {
				removeDefUnit(expectedTarget);
			}
		}
		
		if(!resultDefUnits.isEmpty()) {
			String resultDefUnitsStr = collToString(strmap(resultDefUnits, fnDefUnitToStringAsName(0)), "\n");
			System.out.println("--- Unexpected elements ("+resultDefUnits.size()+") : ---\n" + resultDefUnitsStr);
		}
		assertTrue(resultDefUnits.isEmpty());
	}
	
	public void removeDefUnit(String expectedTarget) {
		String moduleName = StringUtil.segmentUntilMatch(expectedTarget, "/");
		String defUnitModuleQualifiedName = StringUtil.substringAfterMatch(expectedTarget, "/");
		
		boolean removed = false;
		if(moduleName == null ) {
			for (Iterator<DefUnit> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
				DefUnit defUnit = iterator.next();
				
				if(defUnit.getName().equals(expectedTarget)) {
					iterator.remove();
					removed = true;
				}
			}
		} else {
			String expectedFullyTypedName = moduleName + 
				(defUnitModuleQualifiedName != null ? "/" + defUnitModuleQualifiedName : "");
			
			for (Iterator<DefUnit> iterator = resultDefUnits.iterator(); iterator.hasNext(); ) {
				DefUnit defUnit = iterator.next();
				
				String defUnitFullyTypedName = getDefUnitFullyTypedName(defUnit);
				if(defUnitFullyTypedName.equals(expectedFullyTypedName)) {
					iterator.remove();
					removed = true;
				} else {
					continue; // Not a match
				}
			}
		}
		assertTrue(removed); // Must find a matching result
	}
	
	// TODO: review this
	public static String getDefUnitFullyTypedName(DefUnit defUnit) {
		String base = getDefUnitFullyQualifedName(defUnit);
		switch(defUnit.getArcheType()) {
		case Package:
			base += "/";
			break;
		default:
		}
		return base;
	}
	
	public static String getDefUnitFullyQualifedName(DefUnit defUnit) {
		// TODO: this could use some cleanup
		if(defUnit instanceof LiteModuleDummy) {
			return ((LiteModuleDummy) defUnit).getName() + "/";
		}
		if(defUnit instanceof Module) {
			return ((Module) defUnit).getFullyQualifiedName() + "/";
		}
		
		DefUnit parentDefUnit = NodeUtil.getParentDefUnit(defUnit);
		if(parentDefUnit == null) {
			return defUnit.getName();
		}
		String sep = parentDefUnit instanceof Module ? "" : ".";
		String parentQualifedName = getDefUnitFullyQualifedName(parentDefUnit);
		return parentQualifedName  + sep + defUnit.getName();
	}
	
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
	
	public static void removedDefUnitByMarker(Collection<DefUnit> resolvedDefUnits, MetadataEntry marker) {
		for (Iterator<DefUnit> iterator = resolvedDefUnits.iterator(); iterator.hasNext(); ) {
			DefUnit defUnit = iterator.next();
			if(defUnit.defname.getEndPos() == marker.offset || defUnit.defname.getStartPos() == marker.offset) {
				iterator.remove();
				return;
			}
		}
		assertFail();
	}
	
}
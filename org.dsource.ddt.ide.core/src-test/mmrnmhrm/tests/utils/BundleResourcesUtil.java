package mmrnmhrm.tests.utils;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

import melnorme.utilbox.misc.StreamUtil;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;


public class BundleResourcesUtil {
	
	public static void copyDirContents(String bundleId, String dirPath, final File targetDir) throws IOException {
		
		// normalize path with regards to separators
		final String normalizedBasePath = new Path(dirPath).makeRelative().addTrailingSeparator().toString(); 
		
		final Bundle bundle = Platform.getBundle(bundleId);
		
		BundleResourcesIterator bundleResourcesPrinter = new BundleResourcesIterator() {
			@Override
			protected void handleFile(String srcPath) {
				URL sourceFile = FileLocator.find(bundle, new Path(srcPath), null);
				InputStream inputStream = null;
				try {
					inputStream = sourceFile.openConnection().getInputStream();
					
					String srcSubPath = determineSubPath(srcPath);
					System.out.println(srcSubPath);
					
					File newFile = new File(targetDir, srcSubPath);
					newFile.getParentFile().mkdirs(); // Likely not necessary, but do it just in case
					newFile.createNewFile();
					StreamUtil.copyStream(inputStream, new FileOutputStream(newFile));
				} catch (IOException e) {
					throw new RuntimeException(e); 
				} finally {
					StreamUtil.uncheckedClose(inputStream, true);
				}
			}
			
			@Override
			protected void handleDirectory(String srcPath) {
				String srcSubPath = determineSubPath(srcPath);
				System.out.println(srcSubPath);
				
				File newDir = new File(targetDir, srcSubPath);
				if(!newDir.exists()) {
					boolean result = newDir.mkdirs();
					if(result == false) {
						throw new RuntimeException(new IOException("Could not create directory"));
					}
				}
			}
			
			protected String determineSubPath(String srcPath) {
				assertTrue(srcPath.startsWith(normalizedBasePath));
				return srcPath.substring(normalizedBasePath.length());
			}
			
		};
		
		try {
			bundleResourcesPrinter.traversePath(bundleId, normalizedBasePath);
		} catch (RuntimeException e) {
			if(e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw e;
			}
		}
	}
	
	/** Return a URI for given url, which must comply to RFC 2396. */
	public static URI getURIFromProperURL(URL validUrl) {
		try {
			return validUrl.toURI();
		} catch(URISyntaxException e) {
			throw assertFail();
		}
	}
	
	public static class BundleResourcesIterator {
		
		public void traversePath(String bundleId, String path) {
			Enumeration<String> childPaths = Platform.getBundle(bundleId).getEntryPaths(path);
			if(childPaths == null)
				return;
			while(childPaths.hasMoreElements()) {
				String childPath = childPaths.nextElement();
				if(childPath.endsWith("/")) {
					handleDirectory(childPath);
					traversePath(bundleId, childPath);
				} else {
					handleFile(childPath);
				}
			}
		}
		
		@SuppressWarnings("unused")
		protected void handleCommon(String path) {
		}
		
		protected void handleFile(String path) {
			handleCommon(path);
		}
		
		protected void handleDirectory(String path) {
			handleCommon(path);
		}
	}
	
}

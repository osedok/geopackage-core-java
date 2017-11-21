package mil.nga.geopackage.projection;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class ProjectionFactoryTest {

	private final String authority = "Test";
	private final long code = 100001;

	@Before
	public void clear() {
		ProjectionFactory.clear();
		ProjectionRetriever.clear();
	}

	@Test
	public void temp(){
		
		//Projection testProjection = ProjectionFactory.getProjection(ProjectionConstants.EPSG_WEB_MERCATOR);
		
		//testAuthority(ProjectionConstants.AUTHORITY_EPSG, testProjection);
		//testAuthority(ProjectionConstants.AUTHORITY_NONE, testProjection);
		
		Projection wgs84 = ProjectionFactory.getProjection(ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM);
		Projection wgs84_3d = ProjectionFactory.getProjection(ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM_GEOGRAPHICAL_3D);
		
		String wkt = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]";
		
		Projection wgs84Manual = ProjectionFactory.getProjection(ProjectionConstants.AUTHORITY_EPSG, 1234, null,
				wkt);
				
		TestCase.assertNotNull(wgs84_3d);
		ProjectionTransform to3d = wgs84Manual.getTransformation(wgs84_3d);
		ProjectionTransform to84 = wgs84_3d.getTransformation(wgs84Manual);
		
		double x = 95.0;
		double y = 50.0;
		
		double[] transformed = to3d.transform(x, y);
		double[] transformed2 = to84.transform(transformed[0], transformed[1]);
		
		TestCase.assertEquals(x, transformed2[0]);
		TestCase.assertEquals(y, transformed2[1]);
		
		String wkt1 = "GEODCRS[\"WGS 84\",DATUM[\"World Geodetic System 1984\",ELLIPSOID[\"WGS 84\",6378137,298.257223563,LENGTHUNIT[\"metre\",1.0]]],CS[ellipsoidal,2],AXIS[\"Geodetic latitude (Lat)\",north],AXIS[\"Geodetic longitude (Long)\",east],ANGLEUNIT[\"degree\",0.0174532925199433],ID[\"EPSG\",4326]]";
		
		String wkt2 = "VERTCRS[\"EGM2008 geoid height\",VDATUM[\"EGM2008 geoid\",ANCHOR[\"WGS 84 ellipsoid\"]],CS[vertical,1],AXIS[\"Gravity-related height (H)\",up],LENGTHUNIT[\"metre\",1.0]ID[\"EPSG\",\"3855\"]]";
				
		String compoundWkt = "COMPOUNDCRS[“WGS84 Height (EGM08)”,FIRST,SECOND,ID[“NSG”,”8101 ”]]";
				
		Projection manual1 = ProjectionFactory.getProjection(ProjectionConstants.AUTHORITY_EPSG, 12345, null,
				wkt1);
		
		Projection manual2 = ProjectionFactory.getProjection(ProjectionConstants.AUTHORITY_EPSG, 12346, null,
				wkt2);
		
		Projection compound = ProjectionFactory.getProjection(ProjectionConstants.AUTHORITY_EPSG, 12347, null,
				compoundWkt);
		
	}
	
	private void testAuthority(String authority, Projection testProjection){
		
		double x = ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH / 2.0;
		double y = ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH / 2.0;
		
		Properties propertyCodes = ProjectionRetriever.getOrCreateProjections(authority);
		
		for(Object code: propertyCodes.keySet()){
			Projection projection = ProjectionFactory.getProjection(authority, code.toString());
			TestCase.assertNotNull(projection);
			ProjectionTransform toProjection = testProjection.getTransformation(projection);
			TestCase.assertNotNull(toProjection);
			ProjectionTransform fromProjection = projection.getTransformation(testProjection);
			TestCase.assertNotNull(fromProjection);
			
			double[] transformed = toProjection.transform(x, y);
			double[] transformed2 = fromProjection.transform(transformed[0], transformed[1]);
			
			//TestCase.assertEquals(x, transformed2[0]);
			//TestCase.assertEquals(y, transformed2[1]);
		}
		
	}
	
	
	@Test
	public void testCustomProjection() {

		long authorityCode = code;

		Projection projection = ProjectionFactory
				.getProjection(
						authority,
						authorityCode++,
						"+proj=tmerc +lat_0=0 +lon_0=121 +k=1 +x_0=500000 +y_0=0 +ellps=krass +units=m +no_defs");
		TestCase.assertNotNull(projection);

		String[] params = new String[] { "+proj=tmerc", "+lat_0=0",
				"+lon_0=121", "+k=1", "+x_0=500000", "+y_0=0", "+ellps=krass",
				"+units=m", "+no_defs" };
		Projection projection2 = ProjectionFactory.getProjection(authority,
				authorityCode++, params);
		TestCase.assertNotNull(projection2);

		try {
			ProjectionFactory
					.getProjection(
							authority,
							authorityCode++,
							"+proj=tmerc +lat_0=0 +lon_0=121 +k=1 +x_0=500000 +y_0=0 +ellps=krass +units=m +no_defs +invalid");
			TestCase.fail("Invalid projection did not fail");
		} catch (Exception e) {
			// pass
		}

		try {
			String[] params2 = Arrays.copyOf(params, params.length + 1);
			params2[params2.length - 1] = "+invalid";
			ProjectionFactory
					.getProjection(authority, authorityCode++, params2);
			TestCase.fail("Invalid projection did not fail");
		} catch (Exception e) {
			// pass
		}

		try {
			ProjectionFactory.getProjection(authorityCode++);
			TestCase.fail("Invalid projection did not fail");
		} catch (Exception e) {
			// pass
		}

	}

	@Test
	public void testAddingProjectionToAuthority() {

		try {
			ProjectionFactory.getProjection(ProjectionConstants.AUTHORITY_NONE,
					code);
			TestCase.fail("Missing projection did not fail");
		} catch (Exception e) {
			// pass
		}

		ProjectionRetriever
				.setProjection(
						ProjectionConstants.AUTHORITY_NONE,
						code,
						"+proj=tmerc +lat_0=0 +lon_0=121 +k=1 +x_0=500000 +y_0=0 +ellps=krass +units=m +no_defs");

		Projection projection = ProjectionFactory.getProjection(
				ProjectionConstants.AUTHORITY_NONE, code);
		TestCase.assertNotNull(projection);
	}

	@Test
	public void testAddingAuthorityProjections() {

		// Make sure 4 projections do not exist
		for (long i = code; i < code + 4; i++) {
			try {
				ProjectionFactory.getProjection(authority, i);
				TestCase.fail("Missing projection did not fail");
			} catch (Exception e) {
				// pass
			}
		}

		// Add 3 custom projections to the new authority
		Properties properties = new Properties();
		properties
				.setProperty(
						String.valueOf(code),
						"+proj=tmerc +lat_0=0 +lon_0=121 +k=1 +x_0=500000 +y_0=0 +ellps=krass +units=m +no_defs");
		properties.setProperty(String.valueOf(code + 1),
				"+proj=longlat +datum=WGS84 +no_defs");
		properties
				.setProperty(
						String.valueOf(code + 2),
						"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs");
		ProjectionRetriever.setProjections(authority, properties);

		// Verify first 3 projections exist, last still does not
		for (long i = code; i < code + 4; i++) {

			if (i < code + 3) {
				Projection projection = ProjectionFactory.getProjection(
						authority, i);
				TestCase.assertNotNull(projection);
			} else {
				try {
					ProjectionFactory.getProjection(authority, i);
					TestCase.fail("Missing projection did not fail");
				} catch (Exception e) {
					// pass
				}
			}
		}

		// Clear authority code from retriever but not from factory cache
		ProjectionRetriever.clear(authority, code);
		Projection projection = ProjectionFactory
				.getProjection(authority, code);
		TestCase.assertNotNull(projection);

		// Clear authority code from factory cache and verify no longer exists
		ProjectionFactory.clear(authority, code);
		try {
			ProjectionFactory.getProjection(authority, code);
			TestCase.fail("Missing projection did not fail");
		} catch (Exception e) {
			// pass
		}

		// Set projection back into the retriever and verify factory creates it
		ProjectionRetriever.setProjection(authority, code,
				"+proj=longlat +datum=WGS84 +no_defs");
		projection = ProjectionFactory.getProjection(authority, code);
		TestCase.assertNotNull(projection);

	}
}

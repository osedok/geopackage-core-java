/* Copyright 2013 The jeo project. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mil.nga.geopackage.projection.wkt;

import static org.osgeo.proj4j.parser.Proj4Keyword.k_0;
import static org.osgeo.proj4j.parser.Proj4Keyword.lat_0;
import static org.osgeo.proj4j.parser.Proj4Keyword.lon_0;
import static org.osgeo.proj4j.parser.Proj4Keyword.x_0;
import static org.osgeo.proj4j.parser.Proj4Keyword.y_0;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.Registry;
import org.osgeo.proj4j.datum.Datum;
import org.osgeo.proj4j.datum.Ellipsoid;
import org.osgeo.proj4j.proj.LongLatProjection;
import org.osgeo.proj4j.proj.Projection;
import org.osgeo.proj4j.units.Unit;
import org.osgeo.proj4j.units.Units;

public class ProjWKTParser {

	private enum Param {
		central_meridian(lon_0), latitude_of_origin(lat_0), scale_factor(k_0), false_easting(
				x_0), false_northing(y_0);

		String proj4;

		Param(String proj4) {
			this.proj4 = proj4;
		}

	}

	final static String NAME_KEY = "name";
	final static String IDENTIFIERS_KEY = "identifiers";

	public static CoordinateReferenceSystem parse(String wkt)
			throws ParseException {
		Element element = new Element(wkt);
		CoordinateReferenceSystem crs = parseCRS(element);
		return crs;
	}

	private static CoordinateReferenceSystem parseCRS(Element element)
			throws ParseException {

		CoordinateReferenceSystem crs = null;

		switch (element.keyword) {

		case "GEOGCS":
			crs = parseGeoGCS(element);
			break;

		case "PROJCS":
			crs = parseProjCS(element);
			break;

		case "GEOCCS":
			crs = parseGeoCCS(element);
			break;

		case "VERT_CS":
			// TODO crs = parseVertCS(element);
			// break;

		case "LOCAL_CS":
			// TODO crs = parseLocalCS(element);
			// break;

		case "COMPD_CS":
			// TODO crs = parseCompdCS(element);
			// break;

		case "FITTED_CS":
			// TODO crs = parseFittedCS(element);
			// break;

		default:
			throw element.parseFailed(null, "Unsupported WKT keyword: "
					+ element.keyword);
		}

		return crs;
	}

	private static CoordinateReferenceSystem parseGeoGCS(Element element)
			throws ParseException {

		String name = element.pullString("name");
		Map<String, ?> properties = parseAuthority(element, name);
		Unit angularUnit = parseUnit(element, Units.RADIANS);

		Object meridian = parsePrimem(element, angularUnit);
		Datum datum = parseDatum(element, meridian);

		LongLatProjection proj = new LongLatProjection();

		proj.initialize();

		return new CoordinateReferenceSystem(name, null, datum, proj);
	}

	private static Map<String, Object> parseAuthority(final Element parent,
			final String name) throws ParseException {

		final boolean isRoot = parent.isRoot();
		final Element element = parent.pullOptionalElement("AUTHORITY");
		Map<String, Object> properties;
		if (element == null) {
			if (isRoot) {
				properties = new HashMap<String, Object>(4);
				properties.put(NAME_KEY, name);
			} else {
				properties = Collections.singletonMap(NAME_KEY, (Object) name);
			}
		} else {
			final String auth = element.pullString("name");
			// the code can be annotation marked but could be a number to
			String code = element.pullOptionalString("code");
			if (code == null) {
				int codeNumber = element.pullInteger("code");
				code = String.valueOf(codeNumber);
			}
			element.close();

			properties = new HashMap<String, Object>(4);
			properties.put(NAME_KEY, auth + ":" + name);
			properties.put(IDENTIFIERS_KEY, auth + ":" + code);
		}

		return properties;
	}

	private static Unit parseUnit(final Element parent, final Unit unit)
			throws ParseException {
		final Element element = parent.pullElement("UNIT");
		final String name = element.pullString("name");
		final double factor = element.pullDouble("factor");

		final Map<String, ?> properties = parseAuthority(element, name);
		element.close();

		if (name != null) {
			Unit u = Units.findUnits(name.toLowerCase(Locale.ROOT));
			if (u != null) {
				return u;
			}
		}

		return (factor != 1) ? times(unit, factor) : unit;
	}

	private static Unit times(Unit u, double factor) {
		throw new UnsupportedOperationException();
	}

	private static Object parsePrimem(final Element parent,
			final Unit angularUnit) throws ParseException {
		final Element element = parent.pullElement("PRIMEM");
		final String name = element.pullString("name");
		final double longitude = element.pullDouble("longitude");
		final Map<String, ?> properties = parseAuthority(element, name);
		element.close();

		return null;
	}

	private static Datum parseDatum(final Element parent, final Object meridian)
			throws ParseException {
		Element element = parent.pullElement("DATUM");
		String name = element.pullString("name");
		Ellipsoid ellipsoid = parseSpheroid(element);

		double[] toWGS84 = parseToWGS84(element); // Optional; may be
													// null.
		Map<String, Object> properties = parseAuthority(element, name);
		if (true/* ALLOW_ORACLE_SYNTAX */&& (toWGS84 == null)
				&& (element.peek() instanceof Number)) {
			toWGS84 = new double[7];
			toWGS84[0] = element.pullDouble("dx");
			toWGS84[1] = element.pullDouble("dy");
			toWGS84[2] = element.pullDouble("dz");
			toWGS84[3] = element.pullDouble("ex");
			toWGS84[4] = element.pullDouble("ey");
			toWGS84[5] = element.pullDouble("ez");
			toWGS84[6] = element.pullDouble("ppm");
		}
		element.close();

		return new Datum(name, toWGS84, ellipsoid, name);
	}

	private static Ellipsoid parseSpheroid(final Element parent)
			throws ParseException {
		Element element = parent.pullElement("SPHEROID");
		String name = element.pullString("name");
		double semiMajorAxis = element.pullDouble("semiMajorAxis");
		double inverseFlattening = element.pullDouble("inverseFlattening");
		Map<String, ?> properties = parseAuthority(element, name);
		element.close();
		if (inverseFlattening == 0) {
			// Inverse flattening null is an OGC convention for a sphere.
			inverseFlattening = Double.POSITIVE_INFINITY;
		}

		return new Ellipsoid(name, semiMajorAxis, 0, inverseFlattening, name);
	}

	private static double[] parseToWGS84(final Element parent)
			throws ParseException {
		final Element element = parent.pullOptionalElement("TOWGS84");
		if (element == null) {
			return null;
		}

		double dx = element.pullDouble("dx");
		double dy = element.pullDouble("dy");
		double dz = element.pullDouble("dz");

		try {
			if (element.peek() != null) {
				double ex = element.pullDouble("ex");
				double ey = element.pullDouble("ey");
				double ez = element.pullDouble("ez");
				double ppm = element.pullDouble("ppm");
				return new double[] { dx, dy, dz, ex, ey, ez, ppm };
			} else {
				return new double[] { dx, dy, dz };
			}
		} finally {
			element.close();
		}
	}

	private static CoordinateReferenceSystem parseProjCS(Element e)
			throws ParseException {

		// parse manually
		String name = e.pullString("name");
		CoordinateReferenceSystem geo = parseGeoGCS(e.pullElement("GEOGCS"));

		Projection proj = parseProjection(e);
		String[] params = parseParameters(e);

		return new CoordinateReferenceSystem(name, params, geo.getDatum(), proj);
	}

	private static Projection parseProjection(Element e) throws ParseException {
		Element p = e.pullElement("PROJECTION");
		String name = p.pullString("name");

		Projection proj = new Registry().getProjection(name);
		if (proj == null) {
			throw new IllegalArgumentException("Unsupported projection: "
					+ name);
		}

		return proj;
	}

	private static String[] parseParameters(Element e) throws ParseException {
		Element p = null;
		List<String> params = new ArrayList<String>();
		while ((p = e.pullOptionalElement("PARAMETER")) != null) {
			String key = p.pullString("name");
			Double val = p.pullDouble("value");

			Param param = Param.valueOf(key);
			if (param == null) {
				throw new IllegalArgumentException(
						"Unsupported projection parameter: " + key);
			}

			params.add(String.format(Locale.ROOT, "%s=%f", param.proj4, val));

		}

		return params.toArray(new String[params.size()]);
	}

	private static CoordinateReferenceSystem parseGeoCCS(Element e)
			throws ParseException {
		throw new UnsupportedOperationException();
	}

}
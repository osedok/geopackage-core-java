package mil.nga.geopackage.db;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

import mil.nga.geopackage.GeoPackageConstants;
import mil.nga.geopackage.GeoPackageException;

import com.j256.ormlite.support.ConnectionSource;

/**
 * GeoPackage Connection used to define common functionality within different
 * connection types
 * 
 * @author osbornb
 */
public abstract class GeoPackageCoreConnection implements Closeable {

	/**
	 * Get a connection source
	 * 
	 * @return connection source
	 */
	public abstract ConnectionSource getConnectionSource();

	/**
	 * Execute the sql
	 * 
	 * @param sql
	 *            sql statement
	 */
	public abstract void execSQL(String sql);

	/**
	 * Convenience method for deleting rows in the database.
	 * 
	 * @param table
	 *            table name
	 * @param whereClause
	 *            where clause
	 * @param whereArgs
	 *            where arguments
	 * @return rows deleted
	 */
	public abstract int delete(String table, String whereClause,
			String[] whereArgs);

	/**
	 * Get a count of results
	 * 
	 * @param table
	 *            table name
	 * @param where
	 *            where clause
	 * @param args
	 *            arguments
	 * @return count
	 */
	public abstract int count(String table, String where, String[] args);

	/**
	 * Get the min result of the column
	 * 
	 * @param table
	 *            table name
	 * @param column
	 *            column name
	 * @param where
	 *            where clause
	 * @param args
	 *            where arguments
	 * @return min or null
	 * @since 1.1.1
	 */
	public abstract Integer min(String table, String column, String where,
			String[] args);

	/**
	 * Get the max result of the column
	 * 
	 * @param table
	 *            table name
	 * @param column
	 *            column name
	 * @param where
	 *            where clause
	 * @param args
	 *            where arguments
	 * @return max or null
	 * @since 1.1.1
	 */
	public abstract Integer max(String table, String column, String where,
			String[] args);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void close();

	/**
	 * Check if the table exists
	 * 
	 * @param tableName
	 *            table name
	 * @return true if exists
	 */
	public boolean tableExists(String tableName) {
		return count("sqlite_master", "tbl_name = ?",
				new String[] { tableName }) > 0;
	}

	/**
	 * Check if the table column exists
	 * 
	 * @param tableName
	 *            table name
	 * @param columnName
	 *            column name
	 * @return true if column exists
	 * @since 1.1.8
	 */
	public abstract boolean columnExists(String tableName, String columnName);

	/**
	 * Add a new column to the table
	 * 
	 * @param tableName
	 *            table name
	 * @param columnName
	 *            column name
	 * @param columnDef
	 *            column definition
	 * @since 1.1.8
	 */
	public void addColumn(String tableName, String columnName, String columnDef) {
		execSQL("ALTER TABLE " + CoreSQLUtils.quoteWrap(tableName)
				+ " ADD COLUMN " + CoreSQLUtils.quoteWrap(columnName) + " "
				+ columnDef + ";");
	}

	/**
	 * Query the SQL for a single result object in the first column
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            sql arguments
	 * @return single result object
	 * @since 3.1.0
	 */
	public Object querySingleResult(String sql, String[] args) {
		return querySingleResult(sql, args, 0);
	}

	/**
	 * Query the SQL for a single result typed object in the first column
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            sql arguments
	 * @return single result object
	 * @since 3.1.0
	 */
	public <T> T querySingleTypedResult(String sql, String[] args) {
		@SuppressWarnings("unchecked")
		T result = (T) querySingleResult(sql, args);
		return result;
	}

	/**
	 * Query the SQL for a single result object in the first column with the
	 * expected data type
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            sql arguments
	 * @param dataType
	 *            GeoPackage data type
	 * @return single result object
	 * @since 3.1.0
	 */
	public Object querySingleResult(String sql, String[] args,
			GeoPackageDataType dataType) {
		return querySingleResult(sql, args, 0, dataType);
	}

	/**
	 * Query the SQL for a single result typed object in the first column with
	 * the expected data type
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            sql arguments
	 * @param dataType
	 *            GeoPackage data type
	 * @return single result object
	 * @since 3.1.0
	 */
	public <T> T querySingleTypedResult(String sql, String[] args,
			GeoPackageDataType dataType) {
		@SuppressWarnings("unchecked")
		T result = (T) querySingleResult(sql, args, dataType);
		return result;
	}

	/**
	 * Query the SQL for a single result object
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @return result, null if no result
	 * @since 3.1.0
	 */
	public Object querySingleResult(String sql, String[] args, int column) {
		return querySingleResult(sql, args, column, null);
	}

	/**
	 * Query the SQL for a single result typed object
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @return result, null if no result
	 * @since 3.1.0
	 */
	public <T> T querySingleTypedResult(String sql, String[] args, int column) {
		@SuppressWarnings("unchecked")
		T result = (T) querySingleResult(sql, args, column);
		return result;
	}

	/**
	 * Query the SQL for a single result object with the expected data type
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @param dataType
	 *            GeoPackage data type
	 * @return result, null if no result
	 * @since 3.1.0
	 */
	public abstract Object querySingleResult(String sql, String[] args,
			int column, GeoPackageDataType dataType);

	/**
	 * Query the SQL for a single result typed object with the expected data
	 * type
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @param dataType
	 *            GeoPackage data type
	 * @return result, null if no result
	 * @since 3.1.0
	 */
	public <T> T querySingleTypedResult(String sql, String[] args, int column,
			GeoPackageDataType dataType) {
		@SuppressWarnings("unchecked")
		T result = (T) querySingleResult(sql, args, column, dataType);
		return result;
	}

	/**
	 * Query for values from the first column
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            sql arguments
	 * @return single column values
	 * @since 3.1.0
	 */
	public List<Object> querySingleColumnResults(String sql, String[] args) {
		return querySingleColumnResults(sql, args, 0, null, null);
	}

	/**
	 * Query for values from the first column
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            sql arguments
	 * @return single column values
	 * @since 3.1.0
	 */
	public <T> List<T> querySingleColumnTypedResults(String sql, String[] args) {
		@SuppressWarnings("unchecked")
		List<T> result = (List<T>) querySingleColumnResults(sql, args);
		return result;
	}

	/**
	 * Query for values from the first column
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataType
	 *            GeoPackage data type
	 * @return single column results
	 * @since 3.1.0
	 */
	public List<Object> querySingleColumnResults(String sql, String[] args,
			GeoPackageDataType dataType) {
		return querySingleColumnResults(sql, args, 0, dataType, null);
	}

	/**
	 * Query for typed values from the first column
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataType
	 *            GeoPackage data type
	 * @return single column results
	 * @since 3.1.0
	 */
	public <T> List<T> querySingleColumnTypedResults(String sql, String[] args,
			GeoPackageDataType dataType) {
		@SuppressWarnings("unchecked")
		List<T> result = (List<T>) querySingleColumnResults(sql, args, dataType);
		return result;
	}

	/**
	 * Query for values from a single column
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @return single column results
	 * @since 3.1.0
	 */
	public List<Object> querySingleColumnResults(String sql, String[] args,
			int column) {
		return querySingleColumnResults(sql, args, column, null, null);
	}

	/**
	 * Query for typed values from a single column
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @return single column results
	 * @since 3.1.0
	 */
	public <T> List<T> querySingleColumnTypedResults(String sql, String[] args,
			int column) {
		@SuppressWarnings("unchecked")
		List<T> result = (List<T>) querySingleColumnResults(sql, args, column);
		return result;
	}

	/**
	 * Query for values from a single column
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @param dataType
	 *            GeoPackage data type
	 * @return single column results
	 * @since 3.1.0
	 */
	public List<Object> querySingleColumnResults(String sql, String[] args,
			int column, GeoPackageDataType dataType) {
		return querySingleColumnResults(sql, args, column, dataType, null);
	}

	/**
	 * Query for typed values from a single column
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @param dataType
	 *            GeoPackage data type
	 * @return single column results
	 * @since 3.1.0
	 */
	public <T> List<T> querySingleColumnTypedResults(String sql, String[] args,
			int column, GeoPackageDataType dataType) {
		@SuppressWarnings("unchecked")
		List<T> result = (List<T>) querySingleColumnResults(sql, args, column,
				dataType);
		return result;
	}

	/**
	 * Query for values from a single column up to the limit
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param limit
	 *            result row limit
	 * @param column
	 *            column index
	 * @return single column results
	 * @since 3.1.0
	 */
	public List<Object> querySingleColumnResults(String sql, String[] args,
			int column, Integer limit) {
		return querySingleColumnResults(sql, args, column, null, limit);
	}

	/**
	 * Query for typed values from a single column up to the limit
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param limit
	 *            result row limit
	 * @param column
	 *            column index
	 * @return single column results
	 * @since 3.1.0
	 */
	public <T> List<T> querySingleColumnTypedResults(String sql, String[] args,
			int column, Integer limit) {
		@SuppressWarnings("unchecked")
		List<T> result = (List<T>) querySingleColumnResults(sql, args, column,
				limit);
		return result;
	}

	/**
	 * Query for values from a single column up to the limit
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @param dataType
	 *            GeoPackage data type
	 * @param limit
	 *            result row limit
	 * @return single column results
	 * @since 3.1.0
	 */
	public abstract List<Object> querySingleColumnResults(String sql,
			String[] args, int column, GeoPackageDataType dataType,
			Integer limit);

	/**
	 * Query for typed values from a single column up to the limit
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param column
	 *            column index
	 * @param dataType
	 *            GeoPackage data type
	 * @param limit
	 *            result row limit
	 * @return single column results
	 * @since 3.1.0
	 */
	public <T> List<T> querySingleColumnTypedResults(String sql, String[] args,
			int column, GeoPackageDataType dataType, Integer limit) {
		@SuppressWarnings("unchecked")
		List<T> result = (List<T>) querySingleColumnResults(sql, args, column,
				dataType, limit);
		return result;
	}

	/**
	 * Query for values
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @return results
	 * @since 3.1.0
	 */
	public List<List<Object>> queryResults(String sql, String[] args) {
		return queryResults(sql, args, null, null);
	}

	/**
	 * Query for typed values
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @return results
	 * @since 3.1.0
	 */
	public <T> List<List<T>> queryTypedResults(String sql, String[] args) {
		@SuppressWarnings("unchecked")
		List<List<T>> result = (List<List<T>>) (Object) queryResults(sql, args);
		return result;
	}

	/**
	 * Query for values
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataTypes
	 *            column data types
	 * @return results
	 * @since 3.1.0
	 */
	public List<List<Object>> queryResults(String sql, String[] args,
			GeoPackageDataType[] dataTypes) {
		return queryResults(sql, args, dataTypes, null);
	}

	/**
	 * Query for typed values
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataTypes
	 *            column data types
	 * @return results
	 * @since 3.1.0
	 */
	public <T> List<List<T>> queryTypedResults(String sql, String[] args,
			GeoPackageDataType[] dataTypes) {
		@SuppressWarnings("unchecked")
		List<List<T>> result = (List<List<T>>) (Object) queryResults(sql, args,
				dataTypes);
		return result;
	}

	/**
	 * Query for values in a single (first) row
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @return single row results
	 * @since 3.1.0
	 */
	public List<Object> querySingleRowResults(String sql, String[] args) {
		return querySingleRowResults(sql, args, null);
	}

	/**
	 * Query for typed values in a single (first) row
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @return single row results
	 * @since 3.1.0
	 */
	public <T> List<T> querySingleRowTypedResults(String sql, String[] args) {
		@SuppressWarnings("unchecked")
		List<T> result = (List<T>) querySingleRowResults(sql, args);
		return result;
	}

	/**
	 * Query for values in a single (first) row
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataTypes
	 *            column data types
	 * @return single row results
	 * @since 3.1.0
	 */
	public List<Object> querySingleRowResults(String sql, String[] args,
			GeoPackageDataType[] dataTypes) {
		List<List<Object>> results = queryResults(sql, args, dataTypes, 1);
		List<Object> singleRow = null;
		if (!results.isEmpty()) {
			singleRow = results.get(0);
		}
		return singleRow;
	}

	/**
	 * Query for typed values in a single (first) row
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataTypes
	 *            column data types
	 * @return single row results
	 * @since 3.1.0
	 */
	public <T> List<T> querySingleRowTypedResults(String sql, String[] args,
			GeoPackageDataType[] dataTypes) {
		@SuppressWarnings("unchecked")
		List<T> result = (List<T>) querySingleRowResults(sql, args, dataTypes);
		return result;
	}

	/**
	 * Query for values
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param limit
	 *            result row limit
	 * @return results
	 * @since 3.1.0
	 */
	public List<List<Object>> queryResults(String sql, String[] args,
			Integer limit) {
		return queryResults(sql, args, null, limit);
	}

	/**
	 * Query for typed values
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param limit
	 *            result row limit
	 * @return results
	 * @since 3.1.0
	 */
	public <T> List<List<T>> queryTypedResults(String sql, String[] args,
			Integer limit) {
		@SuppressWarnings("unchecked")
		List<List<T>> result = (List<List<T>>) (Object) queryResults(sql, args,
				limit);
		return result;
	}

	/**
	 * Query for values up to the limit
	 * 
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataTypes
	 *            column data types
	 * @param limit
	 *            result row limit
	 * @return results
	 * @since 3.1.0
	 */
	public abstract List<List<Object>> queryResults(String sql, String[] args,
			GeoPackageDataType[] dataTypes, Integer limit);

	/**
	 * Query for typed values up to the limit
	 * 
	 * @param <T>
	 *            result value type
	 * @param sql
	 *            sql statement
	 * @param args
	 *            arguments
	 * @param dataTypes
	 *            column data types
	 * @param limit
	 *            result row limit
	 * @return results
	 * @since 3.1.0
	 */
	public <T> List<List<T>> queryTypedResults(String sql, String[] args,
			GeoPackageDataType[] dataTypes, Integer limit) {
		@SuppressWarnings("unchecked")
		List<List<T>> result = (List<List<T>>) (Object) queryResults(sql, args,
				dataTypes, limit);
		return result;
	}

	/**
	 * Set the GeoPackage application id
	 */
	public void setApplicationId() {
		setApplicationId(GeoPackageConstants.APPLICATION_ID);
	}

	/**
	 * Set the GeoPackage application id
	 *
	 * @param applicationId
	 *            application id
	 * @since 1.2.1
	 */
	public void setApplicationId(String applicationId) {
		// Set the application id as a GeoPackage
		int applicationIdInt = ByteBuffer.wrap(applicationId.getBytes())
				.asIntBuffer().get();
		execSQL(String.format("PRAGMA application_id = %d;", applicationIdInt));
	}

	/**
	 * Get the application id
	 *
	 * @return application id
	 * @since 1.2.1
	 */
	public String getApplicationId() {
		String applicationId = null;
		Integer applicationIdObject = querySingleTypedResult(
				"PRAGMA application_id", null, GeoPackageDataType.MEDIUMINT);
		if (applicationIdObject != null) {
			try {
				applicationId = new String(ByteBuffer.allocate(4)
						.putInt(applicationIdObject).array(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new GeoPackageException(
						"Unexpected application id character encoding", e);
			}
		}
		return applicationId;
	}

	/**
	 * Set the GeoPackage user version
	 * 
	 * @since 1.2.1
	 */
	public void setUserVersion() {
		setUserVersion(GeoPackageConstants.USER_VERSION);
	}

	/**
	 * Set the user version
	 *
	 * @param userVersion
	 *            user version
	 * @since 1.2.1
	 */
	public void setUserVersion(int userVersion) {
		execSQL(String.format("PRAGMA user_version = %d;", userVersion));
	}

	/**
	 * Get the user version
	 *
	 * @return user version
	 * @since 1.2.1
	 */
	public int getUserVersion() {
		int userVersion = -1;
		Integer userVersionObject = querySingleTypedResult(
				"PRAGMA user_version", null, GeoPackageDataType.MEDIUMINT);
		if (userVersionObject != null) {
			userVersion = userVersionObject;
		}
		return userVersion;
	}

}

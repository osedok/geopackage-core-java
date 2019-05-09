package mil.nga.geopackage.db;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mil.nga.geopackage.user.UserColumn;
import mil.nga.geopackage.user.UserTable;

/**
 * Mapping between column names being mapped to and the mapped column
 * information
 * 
 * @author osbornb
 * @since 3.2.1
 */
public class TableMapping {

	/**
	 * From table name
	 */
	private String fromTable;

	/**
	 * To table name
	 */
	private String toTable;

	/**
	 * Mapping between column names and mapped columns
	 */
	private final Map<String, MappedColumn> columns = new LinkedHashMap<>();

	/**
	 * Dropped columns from the previous table version
	 */
	private final Set<String> droppedColumns = new HashSet<>();

	/**
	 * Constructor
	 */
	public TableMapping() {

	}

	/**
	 * Constructor
	 * 
	 * @param tableName
	 *            table name
	 * @param columns
	 *            user columns
	 */
	public TableMapping(String tableName, List<? extends UserColumn> columns) {
		this.fromTable = tableName;
		this.toTable = tableName;
		for (UserColumn column : columns) {
			addColumn(new MappedColumn(column));
		}
	}

	/**
	 * Constructor
	 * 
	 * @param table
	 *            user table
	 */
	public TableMapping(UserTable<? extends UserColumn> table) {
		this(table.getTableName(), table.getColumns());
	}

	/**
	 * Constructor
	 * 
	 * @param table
	 *            user table
	 * @param droppedColumnNames
	 *            dropped column names
	 */
	public TableMapping(UserTable<? extends UserColumn> table,
			Collection<String> droppedColumnNames) {
		this(table);
		for (String droppedColumnName : droppedColumnNames) {
			addDroppedColumn(droppedColumnName);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param table
	 *            user table
	 * @param newTableName
	 *            new table name
	 */
	public TableMapping(UserTable<? extends UserColumn> table,
			String newTableName) {
		this(table);
		this.toTable = newTableName;
	}

	/**
	 * Get the from table name
	 * 
	 * @return from table name
	 */
	public String getFromTable() {
		return fromTable;
	}

	/**
	 * Set the from table name
	 * 
	 * @param fromTable
	 *            from table name
	 */
	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}

	/**
	 * Get the to table name
	 * 
	 * @return to table name
	 */
	public String getToTable() {
		return toTable;
	}

	/**
	 * Set the to table name
	 * 
	 * @param toTable
	 *            to table name
	 */
	public void setToTable(String toTable) {
		this.toTable = toTable;
	}

	/**
	 * Check if the table mapping is to a new table
	 * 
	 * @return true if a new table
	 */
	public boolean isNewTable() {
		return toTable != null && !toTable.equals(fromTable);
	}

	/**
	 * Add a column
	 * 
	 * @param column
	 *            mapped column
	 */
	public void addColumn(MappedColumn column) {
		columns.put(column.getToColumn(), column);
	}

	/**
	 * Add a column
	 * 
	 * @param columnName
	 *            column name
	 */
	public void addColumn(String columnName) {
		columns.put(columnName, new MappedColumn(columnName));
	}

	/**
	 * Get the column names
	 * 
	 * @return column names
	 */
	public Set<String> getColumnNames() {
		return columns.keySet();
	}

	/**
	 * Get the columns as an entry set
	 * 
	 * @return columns
	 */
	public Set<Entry<String, MappedColumn>> getColumns() {
		return columns.entrySet();
	}

	/**
	 * Get the mapped column values
	 * 
	 * @return columns
	 */
	public Collection<MappedColumn> getMappedColumns() {
		return columns.values();
	}

	/**
	 * Get the mapped column for the column name
	 * 
	 * @param columnName
	 *            column name
	 * @return mapped column
	 */
	public MappedColumn getColumn(String columnName) {
		return columns.get(columnName);
	}

	/**
	 * Add a dropped column
	 * 
	 * @param columnName
	 *            column name
	 */
	public void addDroppedColumn(String columnName) {
		droppedColumns.add(columnName);
	}

	/**
	 * Get a set of dropped columns
	 * 
	 * @return dropped columns
	 */
	public Set<String> getDroppedColumns() {
		return droppedColumns;
	}

	/**
	 * Check if the column name is a dropped column
	 * 
	 * @param columnName
	 *            column name
	 * @return true if a dropped column
	 */
	public boolean isDroppedColumn(String columnName) {
		return droppedColumns.contains(columnName);
	}

}
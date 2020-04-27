package mil.nga.geopackage.extension.vector_tiles;

import com.j256.ormlite.support.ConnectionSource;
import mil.nga.geopackage.db.GeoPackageDao;

import java.sql.SQLException;

/**
 * @author jyutzler
 */
public class VectorTilesFieldsDao extends GeoPackageDao<VectorTilesFields, Long> {

    /**
     * Constructor, required by ORMLite
     *
     * @param connectionSource
     *            connection source
     * @param dataClass
     *            data class
     * @throws SQLException
     *             upon failure
     */
    public VectorTilesFieldsDao(ConnectionSource connectionSource,
                                Class<VectorTilesFields> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
}
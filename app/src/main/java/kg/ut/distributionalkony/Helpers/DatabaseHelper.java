package kg.ut.distributionalkony.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import kg.ut.distributionalkony.Dto.DiscountDto;
import kg.ut.distributionalkony.Dto.OutletDto;
import kg.ut.distributionalkony.Dto.OutletVisit;
import kg.ut.distributionalkony.Dto.PriceListElementDto;
import kg.ut.distributionalkony.Dto.RouteListDto;
import kg.ut.distributionalkony.Dto.RouteListElementDto;
import kg.ut.distributionalkony.Dto.RouteListIdsToSend;
import kg.ut.distributionalkony.Dto.StorageDto;
import kg.ut.distributionalkony.Dto.StorageItemDto;
import kg.ut.distributionalkony.Models.DayOfWeek;
import kg.ut.distributionalkony.Models.OrderCoordinate;

/**
 * Created by Nurs on 10.08.2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "distribution-alkoni.db";

    private static final int DATABASE_VERSION = 1;

    private Dao<RouteListDto, Integer> routeListDao;
    private Dao<RouteListElementDto, Integer> routeListElementDao;
    private Dao<PriceListElementDto, Integer> priceListElementDao;
    private Dao<StorageDto, Integer> storageDao;
    private Dao<StorageItemDto, Integer> storageItemDao;
    private Dao<DiscountDto, Integer> discountDao;
    private Dao<OutletDto, Integer> outletDao;
    private Dao<DayOfWeek, Integer> dayOfWeekDao;
    private Dao<OutletVisit, Integer> outletVisitDao;
    private Dao<RouteListIdsToSend, Integer> routeListIdsToSendDao;
    private Dao<OrderCoordinate, Integer> orderCoordinateDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, RouteListDto.class);
            TableUtils.createTable(connectionSource, RouteListElementDto.class);
            TableUtils.createTable(connectionSource, PriceListElementDto.class);
            TableUtils.createTable(connectionSource, StorageDto.class);
            TableUtils.createTable(connectionSource, StorageItemDto.class);
            TableUtils.createTable(connectionSource, DiscountDto.class);
            TableUtils.createTable(connectionSource, OutletDto.class);
            TableUtils.createTable(connectionSource, DayOfWeek.class);
            TableUtils.createTable(connectionSource, OutletVisit.class);
            TableUtils.createTable(connectionSource, RouteListIdsToSend.class);
            TableUtils.createTable(connectionSource, OrderCoordinate.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, RouteListDto.class, true);
            TableUtils.dropTable(connectionSource, RouteListElementDto.class, true);
            TableUtils.dropTable(connectionSource, PriceListElementDto.class, true);
            TableUtils.dropTable(connectionSource, StorageDto.class, true);
            TableUtils.dropTable(connectionSource, StorageItemDto.class, true);
            TableUtils.dropTable(connectionSource, DiscountDto.class, true);
            TableUtils.dropTable(connectionSource, OutletDto.class, true);
            TableUtils.dropTable(connectionSource, DayOfWeek.class, true);
            TableUtils.dropTable(connectionSource, OutletVisit.class, true);
            TableUtils.dropTable(connectionSource, RouteListIdsToSend.class, true);
            TableUtils.dropTable(connectionSource, OrderCoordinate.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTables(){
        try {
            TableUtils.clearTable(getConnectionSource(), RouteListDto.class);
            TableUtils.clearTable(getConnectionSource(), RouteListElementDto.class);
            TableUtils.clearTable(getConnectionSource(), PriceListElementDto.class);
            TableUtils.clearTable(getConnectionSource(), StorageDto.class);
            TableUtils.clearTable(getConnectionSource(), StorageItemDto.class);
            TableUtils.clearTable(getConnectionSource(), DiscountDto.class);
            TableUtils.clearTable(getConnectionSource(), OutletDto.class);
            TableUtils.clearTable(getConnectionSource(), DayOfWeek.class);
            TableUtils.clearTable(getConnectionSource(), OutletVisit.class);
            TableUtils.clearTable(getConnectionSource(), RouteListIdsToSend.class);
            TableUtils.clearTable(getConnectionSource(), OrderCoordinate.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public Dao<RouteListDto, Integer> getRouteListDao() throws SQLException{
        if(routeListDao == null){
            routeListDao = getDao(RouteListDto.class);
        }
        return routeListDao;
    }

    public Dao<RouteListElementDto, Integer> getRouteListElementDao() throws SQLException{
        if(routeListElementDao == null){
            routeListElementDao = getDao(RouteListElementDto.class);
        }
        return routeListElementDao;
    }

    public Dao<PriceListElementDto, Integer> getPriceListElementDao() throws SQLException{
        if(priceListElementDao == null){
            priceListElementDao = getDao(PriceListElementDto.class);
        }
        return priceListElementDao;
    }

    public Dao<StorageDto, Integer> getStorageDao() throws SQLException{
        if(storageDao == null){
            storageDao = getDao(StorageDto.class);
        }
        return storageDao;
    }

    public Dao<StorageItemDto, Integer> getStorageItemDao() throws SQLException{
        if(storageItemDao == null){
            storageItemDao = getDao(StorageItemDto.class);
        }
        return storageItemDao;
    }

    public Dao<DiscountDto, Integer> getDiscountDao() throws SQLException{
        if(discountDao == null){
            discountDao = getDao(DiscountDto.class);
        }
        return discountDao;
    }

    public Dao<OutletDto, Integer> getOutletDao() throws SQLException{
        if(outletDao == null){
            outletDao = getDao(OutletDto.class);
        }
        return outletDao;
    }

    public Dao<DayOfWeek, Integer> getDayOfWeekDao() throws SQLException{
        if(dayOfWeekDao == null){
            dayOfWeekDao = getDao(DayOfWeek.class);
        }
        return dayOfWeekDao;
    }

    public Dao<OutletVisit, Integer> getOutletVisitDao() throws SQLException{
        if(outletVisitDao == null) {
            outletVisitDao = getDao(OutletVisit.class);
        }
        return outletVisitDao;
    }

    public Dao<RouteListIdsToSend, Integer> getRouteListIdsToSendDao() throws SQLException{
        if(routeListIdsToSendDao == null) {
            routeListIdsToSendDao = getDao(RouteListIdsToSend.class);
        }
        return routeListIdsToSendDao;
    }

    public Dao<OrderCoordinate, Integer> getOrderCoordinateDao() throws SQLException{
        if(orderCoordinateDao == null){
            orderCoordinateDao = getDao(OrderCoordinate.class);
        }
        return orderCoordinateDao;
    }
}

package kg.ut.distributionalkony.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Nurs on 19.08.2015.
 */
@DatabaseTable(tableName = "OrderCoordinate")
public class OrderCoordinate {
    @DatabaseField(allowGeneratedIdInsert = true, generatedId = true)
    public int id;
    @DatabaseField
    public int OutletId;
    @DatabaseField
    public double LatitudeStart;
    @DatabaseField
    public double LongitudeStart;
    @DatabaseField
    public String UserId;
    @DatabaseField
    public double LatitudeFinish;
    @DatabaseField
    public double LongitudeFinish;
    @DatabaseField
    public long DateTimeStart;
    @DatabaseField
    public long DateTimeFinish;
    @DatabaseField
    public String ProviderStart;
    @DatabaseField
    public String ProviderFinish;
}

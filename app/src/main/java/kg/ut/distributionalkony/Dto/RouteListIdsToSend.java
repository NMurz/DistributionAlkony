package kg.ut.distributionalkony.Dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Nurs on 17.08.2015.
 */
@DatabaseTable(tableName = "RouteListIdsToSend")
public class RouteListIdsToSend {
    @DatabaseField(allowGeneratedIdInsert = true, generatedId = true)
    public int id;
    @DatabaseField
    public int RouteListId;
    @DatabaseField
    public int OutletId;


}

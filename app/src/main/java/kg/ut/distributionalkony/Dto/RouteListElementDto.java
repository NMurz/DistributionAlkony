package kg.ut.distributionalkony.Dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Nurs on 07.08.2015.
 */
@DatabaseTable(tableName = "RouteListElement")
public class RouteListElementDto implements Comparable<RouteListElementDto>{
    @DatabaseField(allowGeneratedIdInsert = true, generatedId = true)
    public int Id;
    @DatabaseField
    public int PriceListElementId;
    @DatabaseField
    public int OutletId;
    @DatabaseField
    public int RouteListId;
    @DatabaseField
    public int CountSingle;
    @DatabaseField
    public int CountPack;


    @Override
    public int compareTo(RouteListElementDto routeListElementDto) {
        int compareId = routeListElementDto.PriceListElementId;
        return this.PriceListElementId - compareId;
    }
}

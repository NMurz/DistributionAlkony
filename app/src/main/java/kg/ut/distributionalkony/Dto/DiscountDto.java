package kg.ut.distributionalkony.Dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Nurs on 07.08.2015.
 */
@DatabaseTable(tableName = "Discount")
public class DiscountDto implements Comparable<DiscountDto>{
    @DatabaseField(id = true)
    public int Id;
    @DatabaseField
    public double Percent;
    @DatabaseField
    public int OutletId;
    @DatabaseField
    public int PriceListElementId;
    @DatabaseField
    public double Sum;

    @Override
    public int compareTo(DiscountDto discount) {
        int compareId = discount.PriceListElementId;
        return this.Id - compareId;
    }
}

package kg.ut.distributionalkony.Dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Nurs on 10.08.2015.
 */
@DatabaseTable(tableName = "PriceListElements")
public class PriceListElementDto implements Comparable<PriceListElementDto>{
    @DatabaseField(id = true)
    public int Id;
    @DatabaseField
    public int ProductTypeId;
    @DatabaseField
    public String Name;
    @DatabaseField
    public int CountInPack;
    @DatabaseField
    public String Gross;
    @DatabaseField
    public int IndexNumber;

    @Override
    public int compareTo(PriceListElementDto priceListElementDto) {
        int compareId = priceListElementDto.IndexNumber;
        return this.IndexNumber - compareId;
    }
}


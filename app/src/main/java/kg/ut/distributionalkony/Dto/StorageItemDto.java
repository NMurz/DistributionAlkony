package kg.ut.distributionalkony.Dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Nurs on 07.08.2015.
 */
@DatabaseTable(tableName = "StorageItem")
public class StorageItemDto implements Comparable<StorageItemDto>{
    @DatabaseField(id = true)
    public int Id;
    @DatabaseField
    public int StorageId;
    @DatabaseField
    public int ProductId;
    @DatabaseField
    public int Quantity;
    @DatabaseField
    public double PackPrice;

    @Override
    public int compareTo(StorageItemDto storageItemDto) {
        int compareId = storageItemDto.ProductId;
        return this.ProductId - compareId;
    }
}

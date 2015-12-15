package kg.ut.distributionalkony.Dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Nurs on 07.08.2015.
 */
@DatabaseTable(tableName = "Storage")
public class StorageDto {
    @DatabaseField(id = true)
    public int Id;
    @DatabaseField
    public int DistributorId;
}

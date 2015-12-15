package kg.ut.distributionalkony.Dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Nurs on 11.08.2015.
 */
@DatabaseTable(tableName = "OutletVisit")
public class OutletVisit {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public int OutletId;
    @DatabaseField
    public int DayOfWeek;
}

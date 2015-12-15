package kg.ut.distributionalkony.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "DayOfWeek")
public class DayOfWeek {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public int DayNumber;
    @DatabaseField
    public String DayName;
}

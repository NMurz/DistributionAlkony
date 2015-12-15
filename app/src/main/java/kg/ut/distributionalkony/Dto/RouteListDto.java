package kg.ut.distributionalkony.Dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Nurs on 07.08.2015.
 */
@DatabaseTable(tableName = "RouteList")
public class RouteListDto {
    @DatabaseField(allowGeneratedIdInsert = true, generatedId = true)
    public int Id;
    @DatabaseField
    public String DealerId;
    @DatabaseField
    public String ListDate;
    @DatabaseField
    public Integer DayOfWeek;

    public int getId() {
        return Id;
    }

    @Override
    public boolean equals(Object o) {
        /*if(o == this) return true;
        if(!(o instanceof RouteListDto)) return false;*/
        RouteListDto other = (RouteListDto) o;
        return this.DayOfWeek.equals(other.DayOfWeek);
    }

    @Override
    public int hashCode() {
        return DayOfWeek.hashCode();
    }
}

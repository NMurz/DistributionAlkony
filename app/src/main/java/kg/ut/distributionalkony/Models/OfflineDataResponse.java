package kg.ut.distributionalkony.Models;

import java.util.List;

import kg.ut.distributionalkony.Dto.DiscountDto;
import kg.ut.distributionalkony.Dto.OutletDto;
import kg.ut.distributionalkony.Dto.OutletVisit;
import kg.ut.distributionalkony.Dto.PriceListElementDto;
import kg.ut.distributionalkony.Dto.RouteListDto;
import kg.ut.distributionalkony.Dto.RouteListElementDto;
import kg.ut.distributionalkony.Dto.StorageDto;
import kg.ut.distributionalkony.Dto.StorageItemDto;

/**
 * Created by Nurs on 10.08.2015.
 */
public class OfflineDataResponse {
    public Status Status;
    public List<RouteListDto> RouteLists;
    public List<RouteListElementDto> RouteListElements;
    public List<PriceListElementDto> PriceListElements;
    public List<StorageDto> Storages;
    public List<StorageItemDto> StorageItems;
    public List<DiscountDto> Discounts;
    public List<OutletDto> Outlets;
    public List<DayOfWeek> DaysOfWeek;
    public List<OutletVisit> OutletVisits;
}

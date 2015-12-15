package kg.ut.distributionalkony.Models;

import java.util.ArrayList;
import java.util.List;

import kg.ut.distributionalkony.Dto.RouteListDto;
import kg.ut.distributionalkony.Dto.RouteListElementDto;

/**
 * Created by Nurs on 18.08.2015.
 */
public class UpdateRouteListDetail {
    public List<RouteListDto> RouteLists;
    public List<RouteListElementDto> RouteListElements;
    public List<OrderCoordinate> OrderCoordinates;

    public UpdateRouteListDetail(List<RouteListDto> routeLists, List<RouteListElementDto> routeListElements, List<OrderCoordinate> orderCoordinates) {
        RouteLists = routeLists;
        RouteListElements = routeListElements;
        OrderCoordinates = orderCoordinates;
    }

    public UpdateRouteListDetail(){
        RouteLists = new ArrayList<RouteListDto>();
        RouteListElements = new ArrayList<RouteListElementDto>();
        OrderCoordinates = new ArrayList<OrderCoordinate>();
    }
}

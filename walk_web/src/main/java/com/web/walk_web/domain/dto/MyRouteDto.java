package com.web.walk_web.domain.dto;


import com.web.walk_web.domain.entity.MyRoute;
import com.web.walk_web.domain.entity.AiRouteRecommend;
import lombok.Getter;

@Getter
public class MyRouteDto {
    private final Long myRouteId, routeId;
    private final String routeTitle;
    private final Integer walkCount, rating;
    private final Double distanceInKm;
    private final Boolean isFavorite;

    public MyRouteDto(MyRoute myRoute) {
        this.myRouteId = myRoute.getId();
        this.routeId = myRoute.getRoute().getId();
        this.routeTitle = myRoute.getRoute().getAiRouteRecommend().getTitle();
        this.walkCount = myRoute.getWalkCount();
        this.rating = myRoute.getRating();
        this.isFavorite = myRoute.getIsFavorite();
        this.distanceInKm=myRoute.getRoute().getAiRouteRecommend().getDistanceInKm();
    }
}
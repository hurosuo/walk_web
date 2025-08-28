package com.web.walk_web.location;

import com.web.walk_web.domain.dto.TMapDto;
import com.web.walk_web.location.tmap.TmapClient;
import com.web.walk_web.location.tmap.TmapFullTextResponse;
import com.web.walk_web.location.tmap.TmapReverseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final TmapClient tmapClient;

    public TMapDto getNow(double lat, double lon) {
        TmapReverseResponse resp = tmapClient.reverseGeocode(lat, lon).block();

        if (resp == null || resp.getAddressInfo() == null) {
            throw new IllegalStateException("Tmap reverse geocoding 실패");
        }

        var info = resp.getAddressInfo();
        String full = info.getFullAddress();
        double rlat = info.getLat() != null ? info.getLat() : lat;
        double rlon = info.getLon() != null ? info.getLon() : lon;

        return TMapDto.builder()
                .jibunAddress(full)
                .latitude(rlat)
                .longitude(rlon)
                .isDongdaemoon(TMapDto.isDongdaemunByAddress(full))
                .build();
    }

    public List<TMapDto> search(String query, int limit) {
        TmapFullTextResponse resp = tmapClient.fullTextGeocode(query).block();
        if (resp == null || resp.getSearchResult() == null || resp.getSearchResult().getPointAddress() == null) {
            throw new IllegalStateException("Tmap full text geocoding 실패");
        }

        return resp.getSearchResult().getPointAddress().stream()
                // 동대문구 우선 정렬(있다면 위로)
                .sorted(Comparator.comparing((TmapFullTextResponse.PointAddress p) ->
                        p.getGu_gun() != null && p.getGu_gun().contains("동대문구") ? 0 : 1))
                .limit(limit)
                .map(p -> {
                    String addr = p.getFullAddress();
                    double lat = p.getFrontLat() != null ? p.getFrontLat() : 0d;
                    double lon = p.getFrontLon() != null ? p.getFrontLon() : 0d;
                    return TMapDto.builder()
                            .jibunAddress(addr)
                            .latitude(lat)
                            .longitude(lon)
                            .isDongdaemoon(TMapDto.isDongdaemunByAddress(addr))
                            .build();
                })
                .collect(Collectors.toList());
    }
}

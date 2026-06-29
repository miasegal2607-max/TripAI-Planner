package com.tripai.service;
import com.tripai.dto.request.CreateTripRequest;
import com.tripai.dto.response.*;
import com.tripai.model.*;
import com.tripai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final TripDayRepository tripDayRepository;
    private final PlaceRepository placeRepository;
    private final AIService aiService;

    @Transactional
    public TripResponse createTrip(CreateTripRequest req, User user) {
        Trip trip = Trip.builder().user(user).destination(req.getDestination()).numDays(req.getNumDays())
                .startDate(req.getStartDate()).budget(req.getBudget()).style(req.getStyle())
                .interests(req.getInterests()!=null?String.join(",",req.getInterests()):"").build();
        trip = tripRepository.save(trip);
        List<TripDay> days = new ArrayList<>(); List<List<Place>> placesPerDay = new ArrayList<>();
        aiService.generateItinerary(trip, days, placesPerDay);
        for (int i=0; i<days.size(); i++) {
            TripDay day = tripDayRepository.save(days.get(i));
            List<Place> places = placesPerDay.get(i);
            places.forEach(p -> p.setTripDay(day));
            placeRepository.saveAll(places);
        }
        return getTrip(trip.getId(), user.getId());
    }

    @Transactional(readOnly=true)
    public List<TripResponse> getUserTrips(Long userId) {
        return tripRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(t->toResponse(t,false)).collect(Collectors.toList());
    }

    @Transactional(readOnly=true)
    public TripResponse getTrip(Long tripId, Long userId) {
        Trip t = tripRepository.findByIdAndUserId(tripId,userId).orElseThrow(()->new IllegalArgumentException("Trip not found"));
        return toResponse(t, true);
    }

    @Transactional
    public void deleteTrip(Long tripId, Long userId) {
        Trip t = tripRepository.findByIdAndUserId(tripId,userId).orElseThrow(()->new IllegalArgumentException("Trip not found"));
        tripRepository.delete(t);
    }

    private TripResponse toResponse(Trip t, boolean includeDays) {
        var b = TripResponse.builder().id(t.getId()).destination(t.getDestination()).numDays(t.getNumDays())
                .startDate(t.getStartDate()).budget(t.getBudget()).style(t.getStyle()).interests(t.getInterests()).createdAt(t.getCreatedAt());
        if (includeDays) b.days(tripDayRepository.findByTripIdOrderByDayNumberAsc(t.getId()).stream().map(d->
                TripDayDto.builder().id(d.getId()).dayNumber(d.getDayNumber()).summary(d.getSummary())
                        .places(placeRepository.findByTripDayIdOrderByVisitOrderAsc(d.getId()).stream().map(p->
                                PlaceDto.builder().id(p.getId()).name(p.getName()).address(p.getAddress()).rating(p.getRating())
                                        .openingHours(p.getOpeningHours()).priceLevel(p.getPriceLevel()).website(p.getWebsite())
                                        .mapsUrl(p.getMapsUrl()).placeType(p.getPlaceType()).visitOrder(p.getVisitOrder())
                                        .visitTime(p.getVisitTime()).latitude(p.getLatitude()).longitude(p.getLongitude()).notes(p.getNotes()).build())
                                .collect(Collectors.toList())).build()).collect(Collectors.toList()));
        return b.build();
    }
}

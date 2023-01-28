package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;


    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user = userRepository3.findById(userId).get();
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        Reservation reservation = new Reservation();
        // parkingLotId, Integer numberOfWheels, Integer pricePerHour
        List<Spot> spotList = parkingLot.getSpotList();
        Collections.sort(spotList, (s1, s2) -> s1.getPricePerHour() - s2.getPricePerHour());
        boolean reservedSpot = false;
        for(Spot spot : spotList){
            int wheels = spot.getSpotType() == SpotType.FOUR_WHEELER ? 4 :
                    spot.getSpotType() == SpotType.TWO_WHEELER ? 2 : Integer.MAX_VALUE;
            if(numberOfWheels <= wheels && !spot.isOccupied()){
                reservedSpot = true;
                reservation.setUser(user);
                reservation.setSpot(spot);
                reservation.setNumberOfHours(timeInHours);
                spot.setOccupied(true);
                spot.getReservationList().add(reservation);
                user.getReservationList().add(reservation);
                reservationRepository3.save(reservation);
                userRepository3.save(user);
                spotRepository3.save(spot);
                break;
            }
        }
        if(!reservedSpot){
            throw new Exception("reservation cannot be made");
        }
        return reservation;
    }
}

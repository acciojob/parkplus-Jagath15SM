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
import java.util.Optional;

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
        try {

            if (!userRepository3.findById(userId).isPresent() || !parkingLotRepository3.findById(parkingLotId).isPresent()) {
                throw new Exception("Cannot make reservation");
            }
            User user = userRepository3.findById(userId).get();
            ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

            List<Spot> spotList = parkingLot.getSpotList();
            boolean checkForSpots = false;
            for (Spot spot : spotList) {
                if (!spot.getOccupied()) {
                    checkForSpots = true;
                    break;
                }
            }

            if (!checkForSpots) {
                throw new Exception("Cannot make reservation");
            }


            SpotType requestSpotType;

            if (numberOfWheels > 4) {
                requestSpotType = SpotType.OTHERS;
            } else if (numberOfWheels > 2) {
                requestSpotType = SpotType.FOUR_WHEELER;
            } else requestSpotType = SpotType.TWO_WHEELER;


            int minimumPrice = Integer.MAX_VALUE;

            checkForSpots = false;

            Spot spotChosen = null;

            for (Spot spot : spotList) {
                if (requestSpotType.equals(SpotType.OTHERS) && spot.getSpotType().equals(SpotType.OTHERS)) {
                    if (spot.getPricePerHour() * timeInHours < minimumPrice && !spot.getOccupied()) {
                        minimumPrice = spot.getPricePerHour() * timeInHours;
                        checkForSpots = true;
                        spotChosen = spot;
                    }
                } else if (requestSpotType.equals(SpotType.FOUR_WHEELER) && spot.getSpotType().equals(SpotType.OTHERS) ||
                        spot.getSpotType().equals(SpotType.FOUR_WHEELER)) {
                    if (spot.getPricePerHour() * timeInHours < minimumPrice && !spot.getOccupied()) {
                        minimumPrice = spot.getPricePerHour() * timeInHours;
                        checkForSpots = true;
                        spotChosen = spot;
                    }
                } else if (requestSpotType.equals(SpotType.TWO_WHEELER) && spot.getSpotType().equals(SpotType.OTHERS) ||
                        spot.getSpotType().equals(SpotType.FOUR_WHEELER) || spot.getSpotType().equals(SpotType.TWO_WHEELER)) {
                    if (spot.getPricePerHour() * timeInHours < minimumPrice && !spot.getOccupied()) {
                        minimumPrice = spot.getPricePerHour() * timeInHours;
                        checkForSpots = true;
                        spotChosen = spot;
                    }
                }

            }

            if (!checkForSpots) {
                throw new Exception("Cannot make reservation");
            }

            spotChosen.setOccupied(true);

            Reservation reservation = new Reservation();
            reservation.setNumberOfHours(timeInHours);
            reservation.setSpot(spotChosen);
            reservation.setUser(user);

            //Bidirectional
            spotChosen.getReservationList().add(reservation);
            user.getReservationList().add(reservation);

            userRepository3.save(user);
            spotRepository3.save(spotChosen);

            return reservation;
        } catch (Exception e) {
            return null;
        }
    }
    /*
    Q : reserveSpot :
            if(userRepository3.findById(userId) == null) return null;
        if(parkingLotRepository3.findById(parkingLotId) == null) return null;
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
            if(numberOfWheels <= wheels && !spot.getOccupied()){
                reservedSpot = true;
                reservation.setUser(user);
                reservation.setSpot(spot);
                reservation.setNumberOfHours(timeInHours);
                spot.setOccupied(true);
                spot.getReservationList().add(reservation);
                user.getReservationList().add(reservation);
                // reservationRepository3.save(reservation);
                userRepository3.save(user);
                spotRepository3.save(spot);
                break;
            }
        }
        if(!reservedSpot){
            throw new Exception("reservation cannot be made");
        }
        return reservation;

     */
}

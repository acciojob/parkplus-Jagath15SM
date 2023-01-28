package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName(name);
        parkingLot.setAddress(address);
        parkingLot.setSpotList(new ArrayList<>());
        parkingLotRepository1.save(parkingLot);
        return parkingLot;
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        Spot spot = new Spot();
        spot.setPricePerHour(pricePerHour);
        SpotType spotType;
        if(numberOfWheels <= 2){
            spotType = SpotType.TWO_WHEELER;
        }
        else if(numberOfWheels <= 4){
            spotType = SpotType.FOUR_WHEELER;
        }
        else{
            spotType = SpotType.OTHERS;
        }
        spot.setSpotType(spotType);
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        parkingLot.getSpotList().add(spot);
        spot.setParkingLot(parkingLot);
        parkingLotRepository1.save(parkingLot);
        return spot;
    }

    @Override
    public void deleteSpot(int spotId) {
        spotRepository1.deleteById(spotId);
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
        if(spotRepository1.findById(spotId) == null) return null;
        Spot spot = spotRepository1.findById(spotId).get();
        ParkingLot parkingLot = parkingLotRepository1.findById(parkingLotId).get();
        spot.setPricePerHour(pricePerHour);
        for (Spot spots : parkingLot.getSpotList()) {
            if (spots.getId() == spotId) {
                spots.setPricePerHour(pricePerHour);
            }
        }
        spot.setParkingLot(parkingLot);
        parkingLotRepository1.save(parkingLot);
        spotRepository1.save(spot);
        return spot;
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        parkingLotRepository1.deleteById(parkingLotId);
    }
}

package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Payment payment = reservation.getPayment();
        int reservationPrice = reservation.getNumberOfHours() * reservation.getSpot().getPricePerHour();
        if(amountSent < reservationPrice){
            throw new Exception("Insufficient Amount");
        }
        try {
            payment.setPaymentMode(PaymentMode.valueOf(mode.toUpperCase()));
        }
        catch (Exception e){
            throw new Exception("Payment mode not detected");
        }
        payment.setPaymentCompleted(true);
        paymentRepository2.save(payment);
        payment.setReservation(reservation);
        reservation.getSpot().setOccupied(true);
        return payment;
    }
}

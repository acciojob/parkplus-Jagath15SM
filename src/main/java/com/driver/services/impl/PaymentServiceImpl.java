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
        Payment payment = new Payment();
        if(mode == null || PaymentMode.valueOf(mode.toUpperCase()) == null){
            throw new Exception("Payment mode not detected");
        }

        Reservation reservation = reservationRepository2.findById(reservationId).get();
        int reservationPrice = reservation.getNumberOfHours() * reservation.getSpot().getPricePerHour();
        if(amountSent < reservationPrice){
            throw new Exception("Insufficient Amount");
        }
        payment.setPaymentMode(PaymentMode.valueOf(mode.toUpperCase()));
        payment.setPaymentCompleted(true);
        payment.setReservation(reservation);
        reservation.setPayment(payment);

        paymentRepository2.save(payment);
        return payment;
    }
}

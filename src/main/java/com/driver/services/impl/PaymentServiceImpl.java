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
        Payment payment = new Payment();
        String[] payments = {"CASH", "CARD", "UPI"};
        mode = mode.toUpperCase();
        if(reservation.getNumberOfHours() * reservation.getSpot().getPricePerHour() > amountSent){
            throw new Exception("Insufficient Amount");
        }

        if(mode.equals(payments[0])){
            payment.setPaymentMode(PaymentMode.CASH);
        }
        else if(mode.equals(payments[1]))  {
            payment.setPaymentMode(PaymentMode.CARD);
        }
        else if (mode.equals(payments[2])){
            payment.setPaymentMode(PaymentMode.UPI);
        }
        else{
            throw new Exception("Payment mode not detected");
        }
        payment.setPaymentCompleted(true);
        payment.setReservation(reservation);
        reservation.setPayment(payment);
        reservation.getSpot().setOccupied(true);
        reservationRepository2.save(reservation);

        return payment;
    }
}

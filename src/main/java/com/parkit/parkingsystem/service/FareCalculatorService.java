package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

import java.util.Date;


public class FareCalculatorService {
    private TicketDAO ticketDAO;

    public FareCalculatorService() {
        ticketDAO = new TicketDAO();
    }

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        double outHour = ticket.getOutTime().getTime();
        double inHour = ticket.getInTime().getTime();
        double duration = (outHour - inHour)/3600000;
        if (duration <= 0.5){
            duration = 0;
        } else if (duration> 0.5){
            duration = duration - 0.5;
        }
        if (ticketDAO.checkFidelity(ticket)) {
            duration = duration * 0.95;
        }
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public void test(String req, int resp){

    }
}
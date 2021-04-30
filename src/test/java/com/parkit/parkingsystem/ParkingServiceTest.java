package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            //when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            //when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            //when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            //when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    /*@Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }*/

    @Test
    public void getNextParkingCarNumberIfAvailableTest(){
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        ParkingSpot parking = parkingService.getNextParkingNumberIfAvailable();
        assertEquals(1, parking.getId());
        assertEquals(true, parking.isAvailable());
        assertEquals(ParkingType.CAR,parking.getParkingType());
    }
    @Test
    public void getNextParkingBikeNumberIfAvailableTest(){
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(4);
        when(inputReaderUtil.readSelection()).thenReturn(2);
        ParkingSpot parking = parkingService.getNextParkingNumberIfAvailable();
        assertEquals(4, parking.getId());
        assertEquals(true, parking.isAvailable());
        assertEquals(ParkingType.BIKE,parking.getParkingType());
    }

    @Test
    public void getNextParkingCarNumberIfAvailableErrorTest(){
        when(inputReaderUtil.readSelection()).thenReturn(3);
        ParkingSpot parking = parkingService.getNextParkingNumberIfAvailable();
        assertNull(parking);
    }

    @Test
    public void getNextParkingCarNumberIfAvailableIsFullTest(){
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(0);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        ParkingSpot parking = parkingService.getNextParkingNumberIfAvailable();
        assertNull(parking);
    }


}

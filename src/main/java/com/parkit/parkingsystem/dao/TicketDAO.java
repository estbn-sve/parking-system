package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    public boolean saveTicket(Ticket ticket) throws SQLException {
        Connection con = null;
        PreparedStatement ps =null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5,(ticket.getOutTime()==null)?null:(new Timestamp(ticket.getOutTime().getTime())));
            return ps.execute();
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            if (ps != null) {
                ps.close();
            }
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE")
    public Ticket getTicket(String vehicleRegNumber) throws SQLException {
        Connection con = null;
        Ticket ticket = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.GET_TICKET);
            ps.setString(1,vehicleRegNumber);
            rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            assert rs != null;
            assert ps != null;
            rs.close();
            ps.close();
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    public boolean updateTicket(Ticket ticket) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            assert ps != null;
            ps.close();
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }


    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    public boolean checkFidelity(Ticket ticket) {
        Connection con = null;
        Statement stmt = null;
        try {
        con = dataBaseConfig.getConnection();;
        String vehicleRegNumber = ticket.getVehicleRegNumber();
        String request = "SELECT VEHICLE_REG_NUMBER FROM prod.ticket WHERE VEHICLE_REG_NUMBER = "+ vehicleRegNumber;
        stmt = con.createStatement();
        ResultSet resultSql= stmt.executeQuery(request);
            if (resultSql != null) {
                return true;
            }
        } catch (Exception ex) {
            logger.error("Error check ticket fidelity",ex);
        } finally {
            try{
                assert stmt != null;
                stmt.close();
            }catch (SQLException throwables){
                throwables.printStackTrace();
            }
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
}
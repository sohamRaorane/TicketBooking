package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String name;
    private String password;
    private String hashPassword;
    private List<Ticket> ticketsBooked;
    private String userId ;


    public User(String name, String password, String hashPassword, List<Ticket> tickets, String userId) {
        this.name = name;
        this.password = password;
        this.hashPassword = hashPassword;
        this.ticketsBooked = tickets;
        this.userId = userId;
    }
    //Default constructor
    public User(){}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public List<Ticket> getTicketsBooked() {
        return ticketsBooked;
    }
    public void printTickets(){
        if(ticketsBooked == null || ticketsBooked.isEmpty()){
            System.out.println("  No tickets booked");
            return;
        }
        for(Ticket ticket:ticketsBooked){
            System.out.println("  " + ticket.getTicketInfo());
        }
    }

    public void setTickets(List<Ticket> tickets) {
        this.ticketsBooked = tickets;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}

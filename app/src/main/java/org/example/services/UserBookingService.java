package org.example.services;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Utils.UserServiceUtil;
import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserBookingService {
    //Globally passed username
    private final User user;
    //kyuki constructor ke pass bhi list of users rehni chahiye
    private List<User> userList;
    /*
    Constructor isliye set kiya kyuki har ek chiz
    ke liye vhohi same user use hoga and vho usse
    hum baar baar fetch aur puchna nahi padega
    hence jab bhi user booking call hogi tab aur jab obj
    create hoga tab automatically user intialized ho jayega
     */
    //static hai isliye kisi bhi dusre class mei a without obj create karke use kar sakte hai
    private static final String USERS_PATH = "C:\\Users\\adity\\Desktop\\Ticket\\app\\src\\main\\resources\\user.json";

    private static final ObjectMapper mapper = new ObjectMapper();


    public UserBookingService(User user1) throws IOException {
        this.user = user1;
        this.userList = loadUsers();
    }


    public UserBookingService() throws IOException {
        this.user = null;
        this.userList = loadUsers();
    }

    public List<User> loadUsers() throws IOException {
        File users = new File(USERS_PATH);
        return mapper.readValue(users, new TypeReference<List<User>>() {
        });
    }


    public boolean loginUser() {
        //The advantage of using the optional is avoiding the NPE it automatically returns false when the user is not present
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public boolean signUp(User newUser) {
        try {
            userList.add(newUser);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File userFile = new File(USERS_PATH);
        //writeValue() serializes a Java object into JSON and writes it to a destination.
        // writeValue(Destination , Object)
        mapper.writeValue(userFile, userList);
    }

    public void fetchBookings() throws IOException {
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName());
        }).findFirst();
        
        if(userFetched.isPresent()){
            User fetchedUser = userFetched.get();
            System.out.println("\n=== Your Bookings ===");
            if(fetchedUser.getTicketsBooked() == null || fetchedUser.getTicketsBooked().isEmpty()){
                System.out.println("No bookings found");
            } else {
                System.out.println("Total bookings: " + fetchedUser.getTicketsBooked().size());
                fetchedUser.printTickets();
            }
            System.out.println("====================\n");
        } else {
            System.out.println("User not found in database");
        }
    }


    //If nothing is found out the Optional returns Optional.empty() and if something is found it returns Optional.of(foundObject)
    public boolean cancelBooking(String ticketId) {

        boolean removed = user.getTicketsBooked()
                .removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (!removed) {
            return false;
        }

        userList.stream()
                .filter(u -> u.getName().equals(user.getName()))
                .findFirst()
                .ifPresent(storedUser ->
                        storedUser.getTicketsBooked()
                                .removeIf(ticket ->
                                        ticket.getTicketId().equals(ticketId)));

        try {
            saveUserListToFile();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<Train> searchTrains(String source, String destination)   {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train ){
        return train.getSeats();

    }

    public Boolean bookTrainSeat(Train train , int row  , int seat )  {
        try{
            if(train == null || train.getTrainId() == null){
                return Boolean.FALSE;
            }
            
            TrainService trainService = new TrainService();

            List<List<Integer>> seats = train.getSeats();
            if(seats == null){
                return Boolean.FALSE;
            }

            //row and the seat should be valid
            if(row >= 0   && row < seats.size() && seat >=0 && seat < seats.get(row).size()){
                //if the seat is available then book it and return true otherwise return false
                if(seats.get(row).get(seat) == 0 ){
                    seats.get(row).set(seat , 1);
                    train.setSeats(seats);
                    //and update that booking in to the train
                    trainService.addTrains(train);
                    
                    // Add ticket to user's bookings only if user is logged in
                    if(user != null && user.getUserId() != null){
                        Ticket ticket = new Ticket(
                            UUID.randomUUID().toString(),
                            user.getUserId(),
                            train.getStations().get(0),
                            train.getStations().get(train.getStations().size() - 1),
                            new java.util.Date(),
                            train
                        );
                        user.getTicketsBooked().add(ticket);
                        saveUserListToFile();
                    }
                    
                    return Boolean.TRUE;//Seat is booked
                }
                else{
                    return Boolean.FALSE; //Seat is already booked
                }

            }
            else{
                return Boolean.FALSE; //Invalid row or seat index
            }


        }
        catch(IOException e){
            return Boolean.FALSE;

        }


    }

}

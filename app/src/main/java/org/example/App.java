package org.example;

import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.UserBookingService;
import org.example.Utils.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        Train trainSelectedForBooking = new Train();
        UserBookingService userBookingService;
        try{
            userBookingService = new UserBookingService();
        }catch(IOException ex){
            System.out.println("Error initializing UserBookingService: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }
        while(option!=7){
            System.out.println("Choose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");
            option = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (option){
                case 1:
                    System.out.println("Enter the username to signup");
                    String nameToSignUp = scanner.nextLine().trim();
                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = scanner.nextLine().trim();
                    User userToSignup = new User(nameToSignUp, passwordToSignUp, UserServiceUtil.hashPassword(passwordToSignUp), new ArrayList<>(), UUID.randomUUID().toString());
                    boolean signedUp = userBookingService.signUp(userToSignup);
                    if(signedUp){
                        System.out.println("Sign up successful!");
                    }else{
                        System.out.println("Sign up failed!");
                    }
                    break;
                case 2:
                    System.out.println("Enter the username to Login");
                    String nameToLogin = scanner.nextLine().trim();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = scanner.nextLine().trim();
                    User userToLogin = new User(nameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin), new ArrayList<>(), UUID.randomUUID().toString());
                    try{
                        userBookingService = new UserBookingService(userToLogin);
                        if(userBookingService.loginUser()){
                            System.out.println("Login successful!");
                        }else{
                            System.out.println("Invalid credentials");
                        }
                    }catch (IOException ex){
                        return;
                    }
                    break;
                case 3:
                    System.out.println("Fetching your bookings");
                    try{
                        if(userBookingService == null){
                            System.out.println("Please login first");
                        } else {
                            userBookingService.fetchBookings();
                        }
                    }catch(IOException ex){
                        System.out.println("Error fetching bookings: " + ex.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Type your source station");
                    String source = scanner.nextLine().trim();
                    System.out.println("Type your destination station");
                    String dest = scanner.nextLine().trim();
                    List<Train> trains = userBookingService.searchTrains(source, dest);
                    if(trains.isEmpty()){
                        System.out.println("No trains found for the given route");
                        break;
                    }
                    int index = 1;
                    for (Train t: trains){
                        System.out.println(index+" Train id : "+t.getTrainId()+" Train No : "+t.getTrainNo());
                        for (Map.Entry<String, String> entry: t.getStationTimes().entrySet()){
                            System.out.println("  station "+entry.getKey()+" time: "+entry.getValue());
                        }
                        index++;
                    }
                    System.out.println("Select a train by typing 1,2,3...");
                    try{
                        int trainChoice = scanner.nextInt() - 1;
                        if(trainChoice >= 0 && trainChoice < trains.size()){
                            trainSelectedForBooking = trains.get(trainChoice);
                            System.out.println("Train selected: " + trainSelectedForBooking.getTrainId());
                        }else{
                            System.out.println("Invalid train selection");
                        }
                    }catch(Exception e){
                        System.out.println("Invalid input");
                        scanner.nextLine();
                    }
                    break;
                case 5:
                    if(trainSelectedForBooking == null || trainSelectedForBooking.getTrainId() == null){
                        System.out.println("Please select a train first (use option 4)");
                        break;
                    }
                    System.out.println("Select a seat out of these seats (0=available, 1=booked)");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                    if(seats == null || seats.isEmpty()){
                        System.out.println("No seats available");
                        break;
                    }
                    for (int i = 0; i < seats.size(); i++){
                        System.out.print("Row " + i + ": ");
                        for (int j = 0; j < seats.get(i).size(); j++){
                            System.out.print(seats.get(i).get(j)+" ");
                        }
                        System.out.println();
                    }
                    System.out.println("Select the seat by typing the row and column");
                    System.out.println("Enter the row");
                    int row = scanner.nextInt();
                    System.out.println("Enter the column");
                    int col = scanner.nextInt();
                    System.out.println("Booking your seat....");
                    Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                    if(booked.equals(Boolean.TRUE)){
                        System.out.println("Booked! Enjoy your journey");
                    }else{
                        System.out.println("Can't book this seat");
                    }
                    break;
                case 6:
                    System.out.println("Enter the ticket ID to cancel");
                    String ticketIdToCancel = scanner.nextLine().trim();
                    boolean cancelled = userBookingService.cancelBooking(ticketIdToCancel);
                    if(cancelled){
                        System.out.println("Booking cancelled successfully");
                    }else{
                        System.out.println("Failed to cancel booking");
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
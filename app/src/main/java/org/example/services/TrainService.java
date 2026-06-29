package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService  {
    private List<Train> trainList;
    private ObjectMapper mapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "C:\\Users\\adity\\Desktop\\Ticket\\app\\src\\main\\resources\\train.json";
    
    public TrainService() throws IOException {
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        loadTrains();
    }

    private void loadTrains() throws IOException {
        File file = new File(TRAIN_DB_PATH);
        trainList = mapper.readValue(file, new TypeReference<List<Train>>() {});
    }
    //for searching the trains

    public List<Train> searchTrains(String source, String destination) throws IOException {
        return trainList.stream().filter(train -> validTrain(train , source , destination)).collect(Collectors.toList());

    }

    public void addTrains(Train train) throws IOException {
        //check if the train already exsists or not
        Optional<Train> exsistingTrain  = trainList.stream()
                .filter(train1 ->train1.getTrainId().equals(train.getTrainId()) )
                .findFirst();

        //optional unlocks two methods -> Optional.isPresent() and empty wala
        if(exsistingTrain.isPresent()){
            //Update the train instead of adding a new train
            updateTrain(train);
        }
        else{
            //otherwise add the new train to the list
            trainList.add(train);
            saveTrainListToFile();

        }
    }

    private void saveTrainListToFile() {
        try {
            mapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace();  //handle the exception based on your applications requirement
        }
    }

    private void updateTrain(Train updatedTrain) {
        //If the train with the same train id exsists -> replace it with the new train id
        //otherwise just add that train

        //So instead od streaming on the train list we are streaming on the index of the list
        OptionalInt index = IntStream.range(0, trainList.size())
                .filter(i -> trainList.get(i).getTrainId().equals(updatedTrain.getTrainId()))
                .findFirst();

        if(index.isPresent()){
            trainList.set(index.getAsInt(), updatedTrain);
            saveTrainListToFile();
        }
        else{
            trainList.add(updatedTrain);
        }

    }


    private boolean validTrain(Train train , String source , String destination){
        List<String> stationOrder = train.getStations();
        int srcIndex = -1;
        int destIndex = -1;
        
        for(int i = 0; i < stationOrder.size(); i++){
            if(stationOrder.get(i).equalsIgnoreCase(source)){
                srcIndex = i;
            }
            if(stationOrder.get(i).equalsIgnoreCase(destination)){
                destIndex = i;
            }
        }

        return srcIndex != -1 && destIndex != -1 && srcIndex < destIndex;
    }
}

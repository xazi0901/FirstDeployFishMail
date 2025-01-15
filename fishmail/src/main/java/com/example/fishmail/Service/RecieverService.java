package com.example.fishmail.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fishmail.Models.CampaignModel;
import com.example.fishmail.Models.RecieversModel;
import com.example.fishmail.Repository.RecieversRepository;

@Service
public class RecieverService {
    
    @Autowired
    private RecieversRepository recieversRepository;
    @Autowired
    private CampaingService campaingService;

    // public RecieversModel addReciver(String sendingPerson,String campaingId){
    //     Optional<CampaignModel> optionalCampaing = campaingService.findOneById(campaingId);
    //     if(optionalCampaing.isPresent()){
    //         CampaignModel campaingToSave = optionalCampaing.get();
    //         Optional<RecieversModel> optionalRecivers = recieversRepository.findOneByCampaingId(String.valueOf(campaingToSave.getId()));
    //         RecieversModel reciverToSave = optionalRecivers.get();
    //         reciverToSave.setCampaing(campaingToSave);
    //         // recievier.setCampaing(campaingToSave);
    //         return recieversRepository.save(reciverToSave);
    //     } else {
    //         throw new RuntimeException("Nie znaleziono kampanii do przypisania!");
    //     }

    
    public RecieversModel addReciver(RecieversModel recivers){
        // Optional<CampaignModel> optionalCampaing = campaingService.findOneById(campaingId);
        // if(optionalCampaing.isPresent()){
        //     CampaignModel campaingToSave = optionalCampaing.get();
        //     Optional<RecieversModel> optionalRecivers = recieversRepository.findOneByCampaingId(String.valueOf(campaingToSave.getId()));
        //     RecieversModel reciverToSave = optionalRecivers.get();
        //     reciverToSave.setCampaing(campaingToSave);
        //     // recievier.setCampaing(campaingToSave);
        //     return recieversRepository.save(reciverToSave);
        // } else {
        //     throw new RuntimeException("Nie znaleziono kampanii do przypisania!");
        // }
        return recieversRepository.save(recivers);
      
    }

    public RecieversModel changeRecivers(RecieversModel recieversModel){
        // CampaignModel reciversCampaingToChange = campaingService.findOneById(campaingId).orElseThrow(() -> new RuntimeException("Nie znaleziono kampanii"));
        // reciversCampaingToChange.setRecivers(recieversModel);
        return recieversRepository.save(recieversModel);
    }

    // EXCEL
    public void saveRecievers(List<String> emails){
        List<RecieversModel> recievers = emails.stream().map(recieverEmail -> new RecieversModel(recieverEmail)).collect(Collectors.toList());
        recieversRepository.saveAll(recievers);
    }

    public List<RecieversModel> getRecieversListFromCampaing(CampaignModel campaignModel){
        return recieversRepository.findAllByCampaign(campaignModel);
    }
}

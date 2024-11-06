package com.bapunmalik.voting_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bapunmalik.voting_system.repository.UserRepository;

import java.util.Random;

@Service
public class VoterIdGeneratorService {

    @Autowired
    private UserRepository userRepository;

    public String generateVoterId(String state) {
        String stateCode = getStateCode(state);
        char randomLetter = getRandomLetter();
        long count = userRepository.countByState(state);
        String uniqueId = String.format("%07d", count + 1); // Ensures 5-digit number

        return stateCode + randomLetter + uniqueId;
    }

    private String getStateCode(String state) {
        switch (state) {
            case "Andhra Pradesh": return "AP";
            case "Arunachal Pradesh": return "AR";
            case "Assam": return "AS";
            case "Bihar": return "BR";
            case "Chhattisgarh": return "CG";
            case "Goa": return "GA";
            case "Gujarat": return "GJ";
            case "Haryana": return "HR";
            case "Himachal Pradesh": return "HP";
            case "Jharkhand": return "JH";
            case "Karnataka": return "KA";
            case "Kerala": return "KL";
            case "Madhya Pradesh": return "MP";
            case "Maharashtra": return "MH";
            case "Manipur": return "MN";
            case "Meghalaya": return "ML";
            case "Mizoram": return "MZ";
            case "Nagaland": return "NL";
            case "Odisha": return "OD";
            case "Punjab": return "PB";
            case "Rajasthan": return "RJ";
            case "Sikkim": return "SK";
            case "Tamil Nadu": return "TN";
            case "Telangana": return "TG";
            case "Tripura": return "TR";
            case "Uttar Pradesh": return "UP";
            case "Uttarakhand": return "UK";
            case "West Bengal": return "WB";
            case "Andaman and Nicobar Islands": return "AN";
            case "Chandigarh": return "CH";
            case "Dadra and Nagar Haveli and Daman and Diu": return "DN";
            case "Lakshadweep": return "LD";
            case "Delhi": return "DL";
            case "Puducherry": return "PY";
            case "Ladakh": return "LA";
            case "Jammu and Kashmir": return "JK";
            default: return "UN"; // Unknown state code
        }
    }
    

    private char getRandomLetter() {
        Random random = new Random();
        return (char) (random.nextInt(26) + 'A'); // Random letter from A to Z
    }
}

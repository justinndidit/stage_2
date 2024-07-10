package com.surgee.stage_2.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.surgee.stage_2.DAO.UserRepository;
import com.surgee.stage_2.DAO.OrganizationRepository;
import com.surgee.stage_2.DTOs.HttpFailedDTO;
import com.surgee.stage_2.DTOs.HttpResponse;
import com.surgee.stage_2.DTOs.HttpSuccessDTO;
import com.surgee.stage_2.data.UserData;
import com.surgee.stage_2.model.User;
import com.surgee.stage_2.util.IdGenerator;
import com.surgee.stage_2.model.Organization;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class APIService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final JwtService jwtService;
    private final IdGenerator idGenerator;


    public ResponseEntity<?> getUserProfile(String userId) {
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String status = responseStatus.getReasonPhrase();
        try {
            Optional<User> userRepoOptional = userRepository.findById(userId);
            if (!userRepoOptional.isPresent()) {
                responseStatus = HttpStatus.NOT_FOUND;
                status = responseStatus.getReasonPhrase();
                throw new IllegalStateException("User with ID not found.");
            }

            User userRepo = userRepoOptional.get();
           
            UserData userData = UserData.builder()
                                .userId(userRepo.getUserId())
                                .firstName(userRepo.getFirstName())
                                .lastName(userRepo.getLastName())
                                .email(userRepo.getEmail())
                                .phone(userRepo.getPhone())
                                .build();
            
            status = "Success";
            responseStatus = HttpStatus.OK;
            
            HttpSuccessDTO successResponse = HttpSuccessDTO.builder()
                                                    .status(status)
                                                    .message("User profile fetched successfully")
                                                    .data(userData)
                                                    .build();
            HttpResponse response=  HttpResponse.builder()
                                                .response(successResponse)
                                                .build();
    
            return new ResponseEntity<>(response, responseStatus);
        } catch(IllegalStateException e) {
            HttpFailedDTO failedResponse = HttpFailedDTO.builder()
                                                        .status(status)
                                                        .message(e.getMessage())
                                                        .statusCode(responseStatus.value())
                                                        .build();
            HttpResponse response = HttpResponse.builder().response(failedResponse).build();
            return new ResponseEntity<>(response, responseStatus);
        } 
    }

    public ResponseEntity<?> getUserOrganizations(String token) {
       HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
       String status =  responseStatus.getReasonPhrase();
        
       try {
            String jwtToken = token.substring(7);

            String email = jwtService.extractEmail(jwtToken);

            Optional<User> userOptional = userRepository.findByEmail(email);

            if(email == null || !userOptional.isPresent()) {
                responseStatus = HttpStatus.FORBIDDEN;
                status = responseStatus.getReasonPhrase();
                throw new IllegalStateException("token is Invalid");
            }
            User user = userOptional.get();

            if(user.getOrganizations() == null) {
                responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                status = responseStatus.getReasonPhrase();
                throw new IllegalStateException("Something went wrong");
            }

            List<Organization> organisations = user.getOrganizations();
                   
            status = "Success";
            responseStatus = HttpStatus.OK;
            
            HttpSuccessDTO successResponse = HttpSuccessDTO.builder()
                                                    .status(status)
                                                    .message("<message>")
                                                    .data(organisations)
                                                    .build();
            HttpResponse response=  HttpResponse.builder()
                                                .response(successResponse)
                                                .build();
    
            return new ResponseEntity<>(response, responseStatus);
        
       } catch (IllegalStateException e) {
            HttpFailedDTO failedResponse = HttpFailedDTO.builder()
                                                        .status(status)
                                                        .message(e.getMessage())
                                                        .statusCode(responseStatus.value())
                                                        .build();
            HttpResponse response = HttpResponse.builder().response(failedResponse).build();
            return new ResponseEntity<>(response, responseStatus);

       }
    }

    public ResponseEntity<?> getOrganizationById(String orgId) {
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String status = responseStatus.getReasonPhrase();
         
        try {
             
             if(orgId == null) {
                 responseStatus = HttpStatus.BAD_REQUEST;
                 status = responseStatus.getReasonPhrase();
                 throw new IllegalStateException("Invalid organisation ID");
             }
             
             Optional<Organization> organizationOptional = organizationRepository.findById(orgId);
            
             if(!organizationOptional.isPresent()) {
                 responseStatus = HttpStatus.NOT_FOUND;
                 status = responseStatus.getReasonPhrase();
                 throw new IllegalStateException("NO organization with id " + orgId );
             }
 
             Organization organisation = organizationOptional.get();
                    
             status = "Success";
             responseStatus = HttpStatus.OK;
             
             HttpSuccessDTO successResponse = HttpSuccessDTO.builder()
                                                     .status(status)
                                                     .message("<message>")
                                                     .data(organisation)
                                                     .build();
             HttpResponse response=  HttpResponse.builder()
                                                 .response(successResponse)
                                                 .build();
     
             return new ResponseEntity<>(response, responseStatus);
         
        } catch (IllegalStateException e) {
             HttpFailedDTO failedResponse = HttpFailedDTO.builder()
                                                         .status(status)
                                                         .message(e.getMessage())
                                                         .statusCode(responseStatus.value())
                                                         .build();
             HttpResponse response = HttpResponse.builder().response(failedResponse).build();
             return new ResponseEntity<>(response, responseStatus);
 
        }
     }

     public ResponseEntity<?> createOrganization(String name, String description) {
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        String status = responseStatus.getReasonPhrase();
         
        try {

            final String orgId = idGenerator.generateId();
            Organization organization = Organization.builder()
                                                    .orgId(orgId)
                                                    .name(name)
                                                    .description(description)
                                                    .build();
            
            Organization organizationRepo = organizationRepository.save(organization);
                    
             status = "Success";
             responseStatus = HttpStatus.CREATED;
             
             HttpSuccessDTO successResponse = HttpSuccessDTO.builder()
                                                     .status(status)
                                                     .message("Organisation Created")
                                                     .data(organizationRepo)
                                                     .build();
             HttpResponse response=  HttpResponse.builder()
                                                 .response(successResponse)
                                                 .build();
     
             return new ResponseEntity<>(response, responseStatus);
         
        } catch (Exception e) {
             HttpFailedDTO failedResponse = HttpFailedDTO.builder()
                                                         .status(status)
                                                         .message(e.getMessage())
                                                         .statusCode(responseStatus.value())
                                                         .build();
             HttpResponse response = HttpResponse.builder().response(failedResponse).build();
             return new ResponseEntity<>(response, responseStatus);
 
        }
     }

     public ResponseEntity<?> addUserToOrganization(String orgId, String userId) {
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String status = responseStatus.getReasonPhrase();

        System.out.println(userId);
        System.out.println(orgId);

         
        try {

            if(orgId == null || userId == null){
                responseStatus = HttpStatus.BAD_REQUEST;
                status = responseStatus.getReasonPhrase();
                throw new IllegalStateException("Invalid requst paramter");
            }
            
            Optional<Organization> organizationOptional = organizationRepository.findById(orgId);

            if(!organizationOptional.isPresent()){
                responseStatus = HttpStatus.NOT_FOUND;
                status = responseStatus.getReasonPhrase();
                throw new IllegalStateException("Specified Organization not found.");

            }

            Organization organizationRepo = organizationOptional.get();

            Optional<User> userOptional = userRepository.findById(userId.trim());
            

            if(!userOptional.isPresent()){
                responseStatus = HttpStatus.NOT_FOUND;
                status = responseStatus.getReasonPhrase();
                throw new IllegalStateException("User not found.");
            }

            User userRepo = userOptional.get();

            List<User> orgUser = organizationRepo.getUsers();

            


            organizationRepo.getUsers().add(userRepo);

            if(orgUser.contains(userRepo)) {
                responseStatus = HttpStatus.CONFLICT;
                status = responseStatus.getReasonPhrase();
                throw new IllegalStateException("User already exists in organisation");            
            }                   
            
            organizationRepository.save(organizationRepo);


                    
             status = "Success";
             responseStatus = HttpStatus.CREATED;
             
             HttpSuccessDTO successResponse = HttpSuccessDTO.builder()
                                                     .status(status)
                                                     .message("User added to Organization successfully")
                                                     .build();
             HttpResponse response=  HttpResponse.builder()
                                                 .response(successResponse)
                                                 .build();
     
             return new ResponseEntity<>(response, responseStatus);
         
        } catch (IllegalStateException e) {
             HttpFailedDTO failedResponse = HttpFailedDTO.builder()
                                                         .status(status)
                                                         .message(e.getMessage())
                                                         .statusCode(responseStatus.value())
                                                         .build();
             HttpResponse response = HttpResponse.builder().response(failedResponse).build();
             return new ResponseEntity<>(response, responseStatus);
 
        }
     }
}

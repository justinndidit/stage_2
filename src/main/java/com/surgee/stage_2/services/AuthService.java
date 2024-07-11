package com.surgee.stage_2.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surgee.stage_2.DAO.OrganizationRepository;
import com.surgee.stage_2.DAO.UserRepository;
import com.surgee.stage_2.DTOs.HttpFailedDTO;
import com.surgee.stage_2.DTOs.HttpResponse;
import com.surgee.stage_2.DTOs.HttpSuccessDTO;
import com.surgee.stage_2.data.UserCreatedData;
import com.surgee.stage_2.data.UserData;
import com.surgee.stage_2.model.User;
import com.surgee.stage_2.util.IdGenerator;
import com.surgee.stage_2.model.Organization;
import com.surgee.stage_2.model.Role;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Builder
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OrganizationRepository organizationRepository;
    private final IdGenerator idGenerator;

    @Transactional
    public ResponseEntity<HttpResponse> registerUser(
                                        String firstName,
                                        String lastName,
                                        String email,
                                        String password,
                                        String phone,
                                        Role role) {

        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String status = responseStatus.getReasonPhrase();

        try {
            log.info("Starting registration for user with email: {}", email);
            if(userRepository.findByEmail(email).isPresent()) {
                responseStatus = HttpStatus.CONFLICT;
                throw new IllegalStateException("User already exists");
            }

            role = role == null ? Role.USER : role;
            final String orgId = idGenerator.generateId();
            final String userId = idGenerator.generateId();
            String organizationName = firstName + "'s Organisation";
            log.info("Creating organization with name: {}", organizationName);

            Organization organization = Organization.builder()
                                                    .orgId(orgId)
                                                    .name(organizationName)
                                                    .description(firstName + "'s new organisation")
                                                    .build();

            User user = User.builder()
                        .userId(userId)
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .phone(phone)
                        .role(role)
                        .password(password)
                        .build();
            log.debug("User object created: {}", user);

            log.debug("Added user to organization: {}", user);

            Organization organizationRepo = organizationRepository.save(organization);
            log.debug("Organization saved: {}", organizationRepo);

            user.addOrganization(organizationRepo);
            log.debug("Added organization to user: {}", user);

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            log.debug("Encoded user password");

            User userRepo = userRepository.save(user);
            String token = jwtService.generateToken(userRepo);
            log.info("User registration successful, token generated");

            responseStatus = HttpStatus.CREATED;
            status = "Successful";

            UserData userData = UserData.builder()
                                .userId(userRepo.getUserId())
                                .firstName(userRepo.getFirstName())
                                .lastName(userRepo.getLastName())
                                .email(userRepo.getEmail())
                                .phone(userRepo.getPhone())
                                .build();

            UserCreatedData userCreatedData = UserCreatedData.builder()
                                                            .accessToken(token)
                                                            .user(userData)
                                                            .build();

            HttpSuccessDTO successResponse = HttpSuccessDTO.builder()
                                                    .status(status)
                                                    .message("Registration successful")
                                                    .data(userCreatedData)
                                                    .build();
            HttpResponse response=  HttpResponse.builder()
                                                .response(successResponse)
                                                .build();

            return new ResponseEntity<>(response, responseStatus);
        } catch(IllegalStateException e) {
            log.error("Error occurred during user registration: {}", e.getMessage(), e);
            HttpFailedDTO failedResponse = HttpFailedDTO.builder()
                                                        .status(status)
                                                        .message(e.getMessage())
                                                        .statusCode(responseStatus.value())
                                                        .build();
            HttpResponse response = HttpResponse.builder().response(failedResponse).build();
            return new ResponseEntity<>(response, responseStatus);
        }
    }

    public ResponseEntity<?> loginUser(String email, String password) {
        HttpStatus responseStatus = HttpStatus.UNAUTHORIZED;
        String status = responseStatus.getReasonPhrase();
        try {
            log.info("Attempting login for user with email: {}", email);
            if(!userRepository.findByEmail(email).isPresent()) {
                responseStatus = HttpStatus.NOT_FOUND;
                status = responseStatus.getReasonPhrase();
                throw new IllegalStateException("Authentication Failed");
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        
            User userRepo = userRepository.findByEmail(email).get();

            responseStatus = HttpStatus.OK;
            String token = jwtService.generateToken(userRepo);
            status = "Successful";

            UserData userData = UserData.builder()
                                .userId(userRepo.getUserId())
                                .firstName(userRepo.getFirstName())
                                .lastName(userRepo.getLastName())
                                .email(userRepo.getEmail())
                                .phone(userRepo.getPhone())
                                .build();

            UserCreatedData userCreatedData = UserCreatedData.builder()
                                                            .accessToken(token)
                                                            .user(userData)
                                                            .build();

            HttpSuccessDTO successResponse = HttpSuccessDTO.builder()
                                                    .status(status)
                                                    .message("Login successful")
                                                    .data(userCreatedData)
                                                    .build();
            HttpResponse response = HttpResponse.builder()
                                                .response(successResponse)
                                                .build();
    
            return new ResponseEntity<>(response, responseStatus);
        
       } catch (IllegalStateException | AuthenticationException e) {
            log.error("Error occurred during user login: {}", e.getMessage(), e);
            String message = e.getMessage().equals("Bad Credentials") ? "Authentication Failed" : e.getMessage();
            HttpFailedDTO failedResponse = HttpFailedDTO.builder()
                                                        .status(status)
                                                        .message(message)
                                                        .statusCode(responseStatus.value())
                                                        .build();
            HttpResponse response = HttpResponse.builder().response(failedResponse).build();
            return new ResponseEntity<>(response, responseStatus);
       }
    }
}

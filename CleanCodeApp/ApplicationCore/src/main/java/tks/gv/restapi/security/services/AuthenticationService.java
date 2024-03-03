package tks.gv.restapi.security.services;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Service;

import tks.gv.model.logic.users.Client;

import tks.gv.restapi.data.mappers.ClientMapper;
import tks.gv.restapi.security.dto.AuthenticationRequest;
import tks.gv.restapi.security.dto.TokenResponse;
import tks.gv.restapi.security.dto.ClientRegisterDTORequest;

import tks.gv.restapi.services.userservice.ClientService;

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class AuthenticationService {
    private ClientService clientService;
    private UserDetailsService userDetailsService;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public TokenResponse register(ClientRegisterDTORequest requestDto) {
        Client clientReg = ClientMapper.fromJsonUser(
                clientService.registerClient(
                        requestDto.getFirstName(),
                        requestDto.getLastName(),
                        requestDto.getLogin(),
                        requestDto.getPassword(),
                        "normal")
        );
        String jwtToken = jwtService.generateToken(Map.of("authorities", clientReg.getAuthorities()), clientReg);
        return new TokenResponse(jwtToken);
    }

    public TokenResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        var userDetails = userDetailsService.loadUserByUsername(request.getLogin());

        String jwtToken = jwtService.generateToken(Map.of("authorities", userDetails.getAuthorities()), userDetails);
        return new TokenResponse(jwtToken);
    }
}

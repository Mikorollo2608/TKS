package tks.gv.restapi.security.config;

import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import tks.gv.model.logic.users.User;
import tks.gv.restapi.data.mappers.AdminMapper;
import tks.gv.restapi.data.mappers.ClientMapper;
import tks.gv.restapi.data.mappers.ResourceAdminMapper;
import tks.gv.restapi.services.userservice.AdminService;
import tks.gv.restapi.services.userservice.ClientService;
import tks.gv.restapi.services.userservice.ResourceAdminService;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class AuthenticationConfig {

    private ClientService clientService;
    private AdminService adminService;
    private ResourceAdminService resourceAdminService;
    private PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
                User user = null;

                user = ClientMapper.fromJsonUser(clientService.getClientByLogin(login));
                if (user == null) {
                    user = AdminMapper.fromJsonUser(adminService.getAdminByLogin(login));
                }
                if (user == null) {
                    user = ResourceAdminMapper.fromJsonUser(resourceAdminService.getResourceAdminByLogin(login));
                }
                if (user == null) {
                    throw new UsernameNotFoundException("Brak uzytkownika o loginie \"%s\"!".formatted(login));
                }

                return user;
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

package com.googongill.aditory.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Conditional(ClientsConfiguredCondition.class)
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class Oauth2ClientRegistrationRepositoryConfiguration {

    private final OAuth2ClientProperties properties;

    Oauth2ClientRegistrationRepositoryConfiguration(OAuth2ClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public InMemoryClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();
        for (String client : properties.getRegistration().keySet()) {
            registrations.add(convertToClientRegistration(client));
        }
        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration convertToClientRegistration(String clientId) {
        OAuth2ClientProperties.Registration registration = properties.getRegistration().get(clientId);
        OAuth2ClientProperties.Provider provider = properties.getProvider().get(clientId);
        return ClientRegistration.withRegistrationId(clientId)
                .clientId(registration.getClientId())
                .redirectUri(registration.getRedirectUri())
                .clientAuthenticationMethod(new ClientAuthenticationMethod(registration.getClientAuthenticationMethod()))
                .clientSecret(registration.getClientSecret())
                .authorizationGrantType(new AuthorizationGrantType(registration.getAuthorizationGrantType()))
                .scope(registration.getScope().toArray(new String[0]))
                .authorizationUri(provider.getAuthorizationUri())
                .tokenUri(provider.getTokenUri())
                .userInfoUri(provider.getUserInfoUri())
                .userNameAttributeName(provider.getUserNameAttribute())
                .build();
    }
}

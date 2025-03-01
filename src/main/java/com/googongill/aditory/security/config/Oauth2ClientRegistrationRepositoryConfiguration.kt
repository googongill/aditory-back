package com.googongill.aditory.security.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod

@Configuration
@Conditional(ClientsConfiguredCondition::class)
@EnableConfigurationProperties(OAuth2ClientProperties::class)
class Oauth2ClientRegistrationRepositoryConfiguration internal constructor(val properties: OAuth2ClientProperties) {

    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository::class)
    fun clientRegistrationRepository(): InMemoryClientRegistrationRepository {
        val registrations: MutableList<ClientRegistration> = ArrayList()
        for (client in properties.registration.keys) {
            registrations.add(convertToClientRegistration(client))
        }
        return InMemoryClientRegistrationRepository(registrations)
    }

    private fun convertToClientRegistration(clientId: String): ClientRegistration {
        val registration = properties.registration[clientId]
        val provider = properties.provider[clientId]
        return ClientRegistration.withRegistrationId(clientId)
            .clientId(registration!!.clientId)
            .redirectUri(registration.redirectUri)
            .clientAuthenticationMethod(ClientAuthenticationMethod(registration.clientAuthenticationMethod))
            .clientSecret(registration.clientSecret)
            .authorizationGrantType(AuthorizationGrantType(registration.authorizationGrantType))
            .scope(*registration.scope.toTypedArray<String>())
            .authorizationUri(provider!!.authorizationUri)
            .tokenUri(provider.tokenUri)
            .userInfoUri(provider.userInfoUri)
            .userNameAttributeName(provider.userNameAttribute)
            .build()
    }

}

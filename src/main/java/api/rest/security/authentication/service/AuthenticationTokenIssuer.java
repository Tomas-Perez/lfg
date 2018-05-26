package api.rest.security.authentication.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Date;

@Dependent
class AuthenticationTokenIssuer {

    @Inject
    private AuthenticationTokenSettings settings;

    public String issueToken(AuthenticationTokenDetails authenticationTokenDetails){
        return Jwts.builder()
                .setId(authenticationTokenDetails.getId())
                .setIssuer(settings.getIssuer())
                .setAudience(settings.getAudience())
                .setSubject(authenticationTokenDetails.getEmail())
                .setIssuedAt(Date.from(authenticationTokenDetails.getIssuedDate().toInstant()))
                .setExpiration(Date.from(authenticationTokenDetails.getExpirationDate().toInstant()))
                .claim(settings.getAuthoritiesClaimName(), authenticationTokenDetails.getAuthorities())
                .claim(settings.getRefreshCountClaimName(), authenticationTokenDetails.getRefreshCount())
                .claim(settings.getRefreshLimitClaimName(), authenticationTokenDetails.getRefreshLimit())
                .signWith(SignatureAlgorithm.HS256, settings.getSecret())
                .compact();
    }
}

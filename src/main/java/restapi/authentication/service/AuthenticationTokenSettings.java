package restapi.authentication.service;

/**
 * Settings for signing and verifying JWT tokens.
 *
 */

class AuthenticationTokenSettings {

    /**
     * Secret for signing and verifying the token signature.
     */
    private String secret = "secret";

    /**
     * Allowed clock skew for verifying the token signature (in seconds).
     */
    private Long clockSkew = 10L;

    /**
     * Identifies the recipients that the JWT token is intended for.
     */
    private String audience = "http://example.org";

    /**
     * Identifies the JWT token issuer.
     */
    private String issuer = "http://example.org";

    /**
     * JWT claim for the authorities.
     */
    private String authoritiesClaimName = "authorities";

    /**
     * JWT claim for the token refreshment count.
     */
    private String refreshCountClaimName = "refreshCount";

    /**
     * JWT claim for the maximum times that a token can be refreshed.
     */
    private String refreshLimitClaimName = "refreshLimit";

    public String getSecret() {
        return secret;
    }

    public Long getClockSkew() {
        return clockSkew;
    }

    public String getAudience() {
        return audience;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAuthoritiesClaimName() {
        return authoritiesClaimName;
    }

    public String getRefreshCountClaimName() {
        return refreshCountClaimName;
    }

    public String getRefreshLimitClaimName() {
        return refreshLimitClaimName;
    }
}

package me.untoldstories.be.user.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import me.untoldstories.be.config.ConfigurationManager;
import me.untoldstories.be.config.pojos.JWTConfiguration;
import me.untoldstories.be.user.UserDescriptor;

import java.util.Map;


public class TokenManager {
    private static final TokenManager instance = new TokenManager();
    public static TokenManager getInstance() {
        return instance;
    }

    private final JWTConfiguration configuration;
    private final Algorithm signingAlgorithm;
    private final JWTVerifier tokenVerifier;

    private TokenManager() {
        configuration = ConfigurationManager.getJWTConfiguration();
        signingAlgorithm = Algorithm.HMAC256(configuration.secretKey);
        tokenVerifier = JWT.require(signingAlgorithm)
                .withIssuer(configuration.issuer)
                .build();
    }

    public String generateToken(long userID, String userName) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer(configuration.issuer)
                    .withClaim("userID", userID)
                    .withClaim("userName", userName)
                    .sign(signingAlgorithm);
        } catch (JWTCreationException exception){
            exception.printStackTrace();
        }

        return token;
    }

    public UserDescriptor verifyTokenAndDecodeData(String token) {
        if (token == null) return null;

        try {
            DecodedJWT decodedToken = tokenVerifier.verify(token);
            Map<String, Claim> claimMap = decodedToken.getClaims();
            long userID = claimMap.get("userID").asLong();
            String userName = claimMap.get("userName").asString();

            return new UserDescriptor(userID, userName);
        } catch (JWTCreationException | JWTDecodeException exception){
            return null;
        }
    }
}

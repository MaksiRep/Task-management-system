package ru.maksirep.core.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import ru.maksirep.api.dto.JwtResponse;
import ru.maksirep.api.dto.LoginRequest;
import ru.maksirep.config.authorization.AuthorizeConfig;
import ru.maksirep.core.entity.Users;
import ru.maksirep.core.error.ErrorCode;
import ru.maksirep.core.error.ServiceException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthorizeService {

    private final AuthorizeConfig authorizeConfig;
    private final UsersService usersService;

    public AuthorizeService(AuthorizeConfig authorizeConfig, UsersService usersService) {
        this.authorizeConfig = authorizeConfig;
        this.usersService = usersService;
    }

    public JwtResponse login(LoginRequest loginRequest) {
        String encryptedPassword = encryptPassword(loginRequest.password());
        Users user = usersService.getUserByEmailAndPassword(loginRequest.email(), encryptedPassword);
        HashMap<String, Integer> claims = new HashMap<>();
        claims.put("userId", user.getId());
        return new JwtResponse(createToken(String.valueOf(user.getEmail()), claims));
    }


    public int validateTokenAndGetUserId(String token) {
        String splittedToken = token.contains(" ") ? token.split(" ")[1] : token;
        try {
            Clock clock = Clock.systemDefaultZone();
            if (Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(splittedToken)
                    .getPayload()
                    .getExpiration()
                    .before(new Date(clock.millis()))) {
                throw new ServiceException("Время авторизации истекло", ErrorCode.UNAUTHORIZED);
            }
        } catch (Exception e) {
            throw new ServiceException("Некорректный токен", ErrorCode.FORBIDDEN);
        }
        Claims claims = getClaims(splittedToken);
        Users user = usersService.getUserById((Integer) claims.get("userId"));
        if (!claims.getSubject().equals(user.getEmail())) {
            throw new ServiceException("У вас нет прав доступа", ErrorCode.FORBIDDEN);
        }
        return user.getId();
    }


    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), ErrorCode.INVALID_TOKEN);
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(authorizeConfig.getSecret()));
    }

    private String createToken(String subject, Map<String, Integer> claims) {
        Clock clock = Clock.systemDefaultZone();
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date(clock.millis()))
                .expiration(
                        Date.from(
                                new Date(clock.millis())
                                        .toInstant()
                                        .plus(authorizeConfig.getExpirationTime())))
                .signWith(getSigningKey())
                .compact();
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("Ошибка со стороны сервера", ErrorCode.CRITICAL);
        }
    }
}

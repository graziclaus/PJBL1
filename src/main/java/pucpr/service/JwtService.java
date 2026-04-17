package pucpr.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import pucpr.model.Usuario;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtService {

    private final String SECRET_KEY = System.getenv("JWT_SECRET");

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(Usuario user) {

        String secret = System.getenv("JWT_SECRET");
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .claim("name", user.getNomeCompleto())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        // Base64(header) + "." + Base64(payload) + "." + assinatura
        // header.payload.signature
    }

    public String extractEmail(String token) {

        try {

            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

        } catch (ExpiredJwtException e) {

            System.out.println("Token expirado: " + e.getMessage());
            return null;

        } catch (SignatureException e) {

            System.out.println("Assinatura inválida: " + e.getMessage());
            return null;

        } catch (MalformedJwtException e) {

            System.out.println("Token mal formatado: " + e.getMessage());
            return null;

        } catch (Exception e) {

            System.out.println("Token inválido: " + e.getMessage());
            return null;

        }
    }

    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (ExpiredJwtException e) {

            System.out.println("Token expirado: " + e.getMessage());
            return false;

        } catch (SignatureException e) {

            System.out.println("Assinatura inválida: " + e.getMessage());
            return false;

        } catch (MalformedJwtException e) {

            System.out.println("Token mal formatado: " + e.getMessage());
            return false;

        } catch (Exception e) {

            System.out.println("Token inválido: " + e.getMessage());
            return false;
        }
    }
}

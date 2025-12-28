
package com.assessai.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret:assessai-secret-key-for-jwt-token-generation-minimum-256-bits}")
    private String jwtSecret;

    /**
     * Endpoints PUBLICS (sans authentification)
     */

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/actuator",
            "/fallback",
            "/health",
            // ======== AssessAI public APIs ========
            "/ocr",
            "/nlp",
            "/scoring",
            "/feedback"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // ✅ Autoriser endpoints publics
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        // 🔐 Vérifier header Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (isValidToken(token)) {
                Claims claims = getClaimsFromToken(token);

                // Passer infos utilisateur aux microservices (optionnel)
                if (claims != null) {
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-User-Id", claims.getSubject())
                            .header("X-User-Email", claims.get("email", String.class))
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                }

                return chain.filter(exchange);
            }
        }

        // ❌ Rejet si non authentifié
        return unauthorized(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private boolean isValidToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null && claims.getExpiration().after(new java.util.Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String body = """
                {
                  "error": "Unauthorized",
                  "message": "Missing or invalid authentication token"
                }
                """;

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8)))
        );
    }

    @Override
    public int getOrder() {
        return -100; // priorité élevée
    }
}

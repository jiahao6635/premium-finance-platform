package com.company.platform.rbac.security;

import com.company.platform.rbac.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/**
 * JWT 令牌服务，负责访问令牌/刷新令牌的签发、解析与过期计算。
 * <p>边界：只处理 JWT 编码和声明读取，不负责鉴权决策；依赖配置中心中的密钥与过期时间。</p>
 */
@Component
public class JwtTokenService {
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成访问令牌。
     *
     * @param userDetails 当前登录用户的安全主体信息
     * @return 用于访问受保护接口的 access token
     */
    public String generateAccessToken(PlatformUserDetails userDetails) {
        return generateToken(userDetails, TOKEN_TYPE_ACCESS, properties.getAccessTokenExpireSeconds());
    }

    /**
     * 生成刷新令牌。
     *
     * @param userDetails 当前登录用户的安全主体信息
     * @return 用于换取新令牌的 refresh token
     */
    public String generateRefreshToken(PlatformUserDetails userDetails) {
        return generateToken(userDetails, TOKEN_TYPE_REFRESH, properties.getRefreshTokenExpireSeconds());
    }

    private String generateToken(PlatformUserDetails userDetails, String tokenType, long expiresInSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
            .issuer(properties.getIssuer())
            .subject(userDetails.getUsername())
            // uid/typ 为安全关键声明：用于后续权限主体定位与令牌类型校验。
            .claims(Map.of("uid", userDetails.getUserId(), "typ", tokenType))
            .id(UUID.randomUUID().toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expiresInSeconds)))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * 解析并校验 JWT 声明。
     *
     * @param token JWT 字符串
     * @return 解析后的声明体
     * @throws io.jsonwebtoken.JwtException 当签名非法、令牌过期或结构错误时抛出
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * 获取 JWT 的唯一 ID（jti）。
     *
     * @param token JWT 字符串
     * @return token id，用于黑名单控制
     */
    public String getTokenId(String token) {
        return parseClaims(token).getId();
    }

    /**
     * 获取令牌类型声明。
     *
     * @param token JWT 字符串
     * @return access 或 refresh
     */
    public String getTokenType(String token) {
        return parseClaims(token).get("typ", String.class);
    }

    /**
     * 获取用户名主体。
     *
     * @param token JWT 字符串
     * @return subject 中记录的用户名
     */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 计算令牌剩余有效秒数。
     *
     * @param token JWT 字符串
     * @return 非负秒数；已过期时返回 0
     */
    public long getRemainingSeconds(String token) {
        Date expiration = parseClaims(token).getExpiration();
        long remaining = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        return Math.max(remaining, 0);
    }
}

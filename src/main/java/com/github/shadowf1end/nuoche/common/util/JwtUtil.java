package com.github.shadowf1end.nuoche.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

/**
 * @author Sun
 * @date 2018/5/29
 */
public class JwtUtil {

    private static final String KEY_STRING = "192&&Eurjefwgsdk'gs3DFL134%&@:N,";
    private static final byte[] KEY_BYTE_ARRAY;
    private static final SecretKey JWT_SIGN_KEY;

    static {
        KEY_BYTE_ARRAY = KEY_STRING.getBytes();
        JWT_SIGN_KEY = Keys.hmacShaKeyFor(KEY_BYTE_ARRAY);
    }

    public static String generateJwt(Long id) {
        return Jwts.builder()
                .setSubject(id.toString())
                .signWith(JWT_SIGN_KEY, SignatureAlgorithm.HS256)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    public static Claims parseJwt(String jwt) {
        return Jwts.parser().setSigningKey(JWT_SIGN_KEY).parseClaimsJws(jwt).getBody();
    }

    public static void main(String[] args) {
        System.out.println(generateJwt(1073053118280761344L));
    }
}

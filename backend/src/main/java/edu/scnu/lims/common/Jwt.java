package edu.scnu.lims.common;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

/**
 * @author ZhuangJieYing
 */
public class Jwt {
    public static final SecretKey KEY = Keys.hmacShaKeyFor("12345678-12345678-12345678-12345678".getBytes());
}

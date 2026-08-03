package utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    public static final String SECRET_KEY = "kskblzdjdwkzkblmpwzbyqslnzzyswzdysbllkskblzdjdwkzkblmpwzbyqslnzzyswzdysbll";
    public static final long EXPIRE_TIME = 1000 * 60 * 60 * 2;

    //生成jwt令牌
    public static String generateJwt(Map<String, Object> dataMap) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .addClaims(dataMap)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .compact();
    }

    //解析jwt令牌
    public static Map<String, Object> parseJwt(String jwt) {
        return Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static String verifyToken(String token) {
        try {
            Map<String, Object> claims = JwtUtils.parseJwt(token);
            Integer id = Integer.valueOf(claims.get("id").toString());
            return id.toString();
        } catch (Exception e) {
            throw new RuntimeException("token解析失败,获取用户ID失败", e);
        }
    }

    public static String getRole(String token) {
        try {
            Map<String, Object> claims = JwtUtils.parseJwt(token);
            return claims.get("role").toString();
        } catch (Exception e) {
            throw new RuntimeException("token解析失败,获取角色失败", e);
        }
    }
}

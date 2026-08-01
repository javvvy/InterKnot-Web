import org.junit.jupiter.api.Test;
import utils.JwtUtils;

import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void testJwt() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", 123);
        dataMap.put("role", "admin");
        String token = JwtUtils.generateJwt(dataMap);
        System.out.println(token);
        Map<String, Object> claims = JwtUtils.parseJwt(token);
        System.out.println(Integer.valueOf(claims.get("id").toString()));
        System.out.println(claims.get("username"));
    }
}

package fa.training.pizza_store_api.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtTokenProvider {
    private String secretKey;
    private long expiration;
    private long refreshExpiration;

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    private Key key;

    @PostConstruct
    public void init() {
        //Khởi tạo key từ secretKey
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String authorities = authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.joining(" "));

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) //Lưu username vào subject của token
                .claim("scope", authorities) // Lưu quyền hạn (roles) vào claim scope
                .setIssuedAt(now) //Thời gian tạo token
                .setExpiration(expiryDate) //Thời gian hết hạn token
                .signWith(key, SignatureAlgorithm.HS256) //Ký token bằng key đã khởi tạo
                .compact();
    }

    //2.Lấy Username từ token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) //Sử dụng key để giải mã token
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); //Lấy username từ subject của token
    }

    //3.Kiểm tra tính hợp lệ của Token (Hết hạn, sai chữ ký, cấu trúc lỗi...)
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.debug("JWT token validated");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired");
            return false;
        } catch (JwtException e) {
            log.error("Invalid JWT token", e);
            return false;
        }
    }

    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(username) //Lưu username vào subject của token
                .setIssuedAt(now) //Thời gian tạo token
                .setExpiration(expiryDate) //Thời gian hết hạn token
                .signWith(key, SignatureAlgorithm.HS256) //Ký token bằng key đã khởi tạo
                .compact();
    }
}

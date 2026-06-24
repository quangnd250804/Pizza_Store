package fa.training.pizza_store_api.config;

import fa.training.pizza_store_api.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("#{'${app.cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //Dùng để mã hóa mật khẩu trước khi lưu vào database
    }

    //Cấu hình để Spring Security biết chỗ để lấy User và cách so khớp mật khẩu mã hóa
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    //Cấu hình AuthenticationManager Bean để dùng cho việc xử lý Login ở tầng Service
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable() //CSRF = Cross Site Request Forgery
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Không sử dụng session, mỗi request phải có token
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll() //Cho phép các api auth mà không cần xác thực
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() //Cho phép Swagger UI
                .antMatchers("/api/v1/payment/**").permitAll() // Cho phép IPN VNPAY và Create Payment
                .antMatchers("/api/v1/products/menu", "/api/v1/categories/active-categories", "/api/v1/toppings/active-toppings", "/api/v1/combos/active-combos").permitAll() //Khach hàng có thể xem menu và danh mục mà không cần đăng nhập
                .antMatchers(HttpMethod.POST, "/api/v1/categories/**", "/api/v1/products/**", "/api/v1/toppings/**", "/api/v1/combos/**", "/api/v1/upload/**").hasRole("ADMIN") // Chỉ Admin được tạo
                .antMatchers(HttpMethod.PUT, "/api/v1/categories/**", "/api/v1/products/**", "/api/v1/toppings/**", "/api/v1/combos/**").hasRole("ADMIN")  // Chỉ Admin được sửa
                .antMatchers(HttpMethod.DELETE, "/api/v1/categories/**", "/api/v1/products/**", "/api/v1/toppings/**", "/api/v1/combos/**").hasRole("ADMIN") // Chỉ Admin được xóa
                .antMatchers("/api/v1/users/**").hasRole("ADMIN") // Chỉ Admin quản lý user
                .anyRequest().authenticated(); //Các api khác phải xác thực mới được truy cập

        // CHÈN BỘ LỌC JWT vào trước bộ lọc UsernamePasswordAuthenticationFilter mặc định
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins.stream()
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList()));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

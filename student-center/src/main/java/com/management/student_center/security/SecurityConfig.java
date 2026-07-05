package com.management.student_center.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final CorsConfigurationSource corsConfigurationSource;

	public SecurityConfig(JwtFilter jwtFilter, CorsConfigurationSource corsConfigurationSource) {
		this.jwtFilter = jwtFilter;
		this.corsConfigurationSource = corsConfigurationSource;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("https://edu-center-liard.vercel.app",
				"https://manage-educenter.vercel.app", "http://localhost:3000", "http://localhost:5173",
				"http://127.0.0.1:5173", "http://127.0.0.1:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		System.out.println("=== SecurityConfig === Configuring security filter chain");

		http.csrf(csrf -> csrf.disable()); // tắt CSRF
		http.cors(cors -> cors.configurationSource(corsConfigurationSource)); // CHỈNH ĐÚNG

		http.authorizeHttpRequests(auth -> auth.requestMatchers("/uploads/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/v1/api/login").permitAll().requestMatchers("/v1/api/logout")
				.permitAll().requestMatchers("/v1/api/forgot-password").permitAll() // gửi OTP
				.requestMatchers("/v1/api/forgot-password/verify-otp").permitAll() // verify OTP
				.requestMatchers("/v1/api/reset-password").permitAll().requestMatchers("/v1/api/subjects/**")
				.permitAll().requestMatchers("/v1/api/subjects").permitAll().requestMatchers("/v1/api/teachers/**")
				.permitAll().requestMatchers("/v1/api/session/**").permitAll().requestMatchers("/v1/api/schedule/**")
				.permitAll().requestMatchers("/v1/api/rooms/**").permitAll()
				.requestMatchers("/v1/api/subject-schedules").permitAll().requestMatchers("/v1/api/subject-students")
				.permitAll().requestMatchers("/v1/api/subject-students/**").permitAll()
				.requestMatchers("/v1/api/students/**").permitAll().requestMatchers("/v1/api/materials/**").permitAll()
				.requestMatchers("/v1/api/subject/**").permitAll().requestMatchers("/v1/api/attendance/**").permitAll()
				.requestMatchers("/v1/api/assignments/**").permitAll().requestMatchers("/v1/api/assign/**").permitAll()
				.requestMatchers("/v1/api/by-assignment/**").permitAll().requestMatchers("/v1/api/assign/update/**")
				.permitAll().requestMatchers("/v1/api/announcements/**").permitAll()
				.requestMatchers("/v1/api/attendance/**").permitAll().requestMatchers("/v1/api/teacher-leaves/**")
				.permitAll().requestMatchers("/v1/api/payroll/**").permitAll().requestMatchers("/v1/api/curriculums/**")
				.permitAll().requestMatchers("/v1/api/session-details/**").permitAll()
				.requestMatchers("/v1/api/student-evaluations/**").permitAll()
				.requestMatchers("/api/auth/**", "/login", "/public/**").permitAll()

				.requestMatchers(HttpMethod.GET, "/v1/api/teachers").permitAll()
				.requestMatchers(HttpMethod.POST, "/v1/api/teachers").permitAll()
				.requestMatchers(HttpMethod.PUT, "/v1/api/teachers/**").permitAll()
				.requestMatchers(HttpMethod.DELETE, "/v1/api/teachers/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/v1/api/teachers/delete-multiple").permitAll()
				.requestMatchers(HttpMethod.GET, "/v1/api/teachers/export").permitAll()

				.requestMatchers(HttpMethod.GET, "/v1/api/students").permitAll()
				.requestMatchers(HttpMethod.GET, "/v1/api/students/**").permitAll() // Get by ID
				.requestMatchers(HttpMethod.POST, "/v1/api/students").permitAll()
				.requestMatchers(HttpMethod.PUT, "/v1/api/students/**").permitAll()
				.requestMatchers(HttpMethod.DELETE, "/v1/api/students/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/v1/api/students/delete-multiple").permitAll()
				.requestMatchers(HttpMethod.GET, "/v1/api/students/export").permitAll()

				.requestMatchers("/v1/api/teacher-subjects/**").permitAll()

				.requestMatchers("/v1/api/payments/**").permitAll()

				.requestMatchers("/v1/api/tuitions/**").permitAll()

				.requestMatchers("/v1/api/teacher-attendance/**").permitAll()

				.requestMatchers("/v1/api/statistics/**").permitAll()

				.anyRequest().authenticated());

		http.httpBasic(httpBasic -> httpBasic.disable());
		http.formLogin(form -> form.disable());

		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}

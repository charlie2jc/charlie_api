package com.charlie.api.jwt.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.charlie.api.jwt.config.JwtTokenUtil;
import com.charlie.api.jwt.model.JwtRequest;
import com.charlie.api.jwt.model.JwtResponse;
import com.charlie.api.jwt.model.JwtUserInfo;
import com.charlie.api.jwt.service.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@RequestMapping(value = "/api/signup", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody JwtUserInfo user) throws Exception {
		jwtUserDetailsService.save(user);
		final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new JwtResponse(token));
	}
	
	@RequestMapping(value = "/api/signin", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String token = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new JwtResponse(token));
	}
	
	@RequestMapping(value = "/api/refresh", method = RequestMethod.POST)
	public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request) {
		try {
			final String requestTokenHeader = request.getHeader("Authorization");
			if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
				String userName = jwtTokenUtil.getUsernameFromToken(requestTokenHeader.substring(7));
				final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userName);
				String token = jwtTokenUtil.refreshToken(userDetails);
				return ResponseEntity.ok(new JwtResponse(token));
//				MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//		        headers.add("Authorization", token);
//		        return new ResponseEntity<String>("Success Refresh Token", headers, HttpStatus.OK);
			}else {
				return new ResponseEntity<String>("JWT Token does not begin with Bearer String", HttpStatus.FORBIDDEN);
			}
        } catch (IllegalArgumentException e) {
        	return new ResponseEntity<String>("Unable to get JWT Token", HttpStatus.FORBIDDEN);
		} catch (ExpiredJwtException e) {
			String userName = e.getClaims().getSubject();
			final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userName);
			String token = jwtTokenUtil.refreshToken(userDetails);
			return ResponseEntity.ok(new JwtResponse(token));
//			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//	        headers.add("Authorization", token);
//	        return new ResponseEntity<String>("Success Refresh Token", headers, HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<String>("Fail Refresh Token", HttpStatus.FORBIDDEN);
		}
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}

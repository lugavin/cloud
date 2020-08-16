package com.gavin.cloud.auth.web;

import com.gavin.cloud.auth.core.config.properties.JwtExtProperties;
import com.gavin.cloud.auth.core.dto.AuthTokenDTO;
import com.gavin.cloud.auth.core.service.AuthService;
import com.gavin.cloud.auth.web.dto.KeyAndPasswordDTO;
import com.gavin.cloud.auth.web.dto.LoginDTO;
import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.JwtProperties;
import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.web.annotation.RequiresGuest;
import com.gavin.cloud.common.web.util.WebUtils;
import com.gavin.cloud.sys.api.AccountApi;
import com.gavin.cloud.sys.api.RoleApi;
import com.gavin.cloud.sys.api.UserApi;
import com.gavin.cloud.sys.pojo.Role;
import com.gavin.cloud.sys.pojo.User;
import com.gavin.cloud.sys.pojo.dto.RegisterDTO;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gavin.cloud.auth.core.enums.AuthProblemType.*;
import static com.gavin.cloud.common.base.util.Constants.REGEX_LOGIN_TYPE;

@RestController
public class AuthResource {

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JwtExtProperties jwtProperties;
    private final AuthService authService;
    private final AccountApi accountApi;
    private final UserApi userApi;
    private final RoleApi roleApi;

    public AuthResource(JwtExtProperties jwtProperties,
                        AuthService authService,
                        AccountApi accountApi,
                        UserApi userApi,
                        RoleApi roleApi) {
        this.jwtProperties = jwtProperties;
        this.authService = authService;
        this.accountApi = accountApi;
        this.userApi = userApi;
        this.roleApi = roleApi;
    }

    /**
     * POST  /login/{type} : User login.
     *
     * @param loginDTO loginDTO
     * @param type     {1:USERNAME, 2:PHONE, 3:EMAIL}
     * @return Login user
     */
    @RequiresGuest
    @PostMapping("/login/{type:" + REGEX_LOGIN_TYPE + "}")
    public ResponseEntity<AuthTokenDTO> login(@Valid @RequestBody LoginDTO loginDTO, @PathVariable int type,
                                              HttpServletRequest request, HttpServletResponse response) {
        User user = Optional.ofNullable(userApi.getUser(loginDTO.getUsername(), type))
                .orElseThrow(() -> new AppException(ACCOUNT_NOT_FOUND_TYPE));
        if (!Md5Hash.hash(loginDTO.getPassword(), user.getSalt()).equals(user.getPassword())) {
            throw new AppException(INVALID_PASSWORD_TYPE);
        }
        if (!BooleanUtils.isTrue(user.getActivated())) {
            throw new AppException(ACCOUNT_NOT_ACTIVATED_TYPE);
        }
        List<String> roles = roleApi.getRoles(user.getId()).stream().map(Role::getCode).collect(Collectors.toList());
        ActiveUser activeUser = new ActiveUser(user.getId(), user.getUsername(), WebUtils.getClientIP(request), roles);
        AuthTokenDTO authToken = authService.createAuthToken(activeUser);
        if (jwtProperties.isEnableCookie()) {
            createCookie(request, response, authToken.getAccessToken());
        }
        return ResponseEntity.ok(authToken);
    }

    @RequiresGuest
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        authService.rejectRefreshToken(refreshToken);
        if (jwtProperties.isEnableCookie()) {
            clearCookie(request, response);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/token/{refreshToken}")
    public ResponseEntity<String> getNewAccessToken(@PathVariable String refreshToken, @RequestParam Long uid,
                                                    HttpServletRequest request, HttpServletResponse response) {
        User user = Optional.ofNullable(userApi.getUser(uid))
                .orElseThrow(() -> new AppException(ACCOUNT_NOT_FOUND_TYPE));
        List<String> roles = roleApi.getRoles(user.getId()).stream().map(Role::getCode).collect(Collectors.toList());
        ActiveUser activeUser = new ActiveUser(user.getId(), user.getUsername(), WebUtils.getClientIP(request), roles);
        AuthTokenDTO authToken = authService.createAuthToken(activeUser, refreshToken);
        if (jwtProperties.isEnableCookie()) {
            createCookie(request, response, authToken.getAccessToken());
        }
        return ResponseEntity.ok(authToken.getAccessToken());
    }

    @GetMapping("/account/{accessToken}")
    public ResponseEntity<?> getAccount(@PathVariable String accessToken, @RequestParam(required = false) String jsonpCallback) {
        ActiveUser activeUser = authService.verifyAccessToken(accessToken);
        if (StringUtils.isEmpty(jsonpCallback)) {
            return ResponseEntity.ok(activeUser);
        }
        MappingJacksonValue jacksonValue = new MappingJacksonValue(activeUser);
        jacksonValue.setJsonpFunction(jsonpCallback);
        return ResponseEntity.ok(jacksonValue);
    }

    @RequiresGuest
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid RegisterDTO registerDTO) {
        // TODO 通过消息异步通知发送邮件
        //User user = accountApi.register(registerDTO);
        //Map<String, Object> variables = new HashMap<>();
        //variables.put(USER, user);
        //variables.put(BASE_URL, mailProperties.getBaseUrl());
        //Locale locale = StringUtils.isNotBlank(user.getLangKey()) ? Locale.forLanguageTag(user.getLangKey()) : Locale.getDefault();
        //mailService.sendEmailFromTemplate(MailTemplateEnum.ACCOUNT_ACTIVATION, registerDTO.getEmail(), variables, locale);
    }

    @RequiresGuest
    @GetMapping("/account/activate")
    public void activateAccount(@RequestParam String key) {
        accountApi.activateRegistration(key);
    }

    @RequiresGuest
    @PostMapping("/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        // TODO 通过消息异步通知发送邮件
        // User user = accountApi.requestPasswordReset(mail);
        // Map<String, Object> variables = new HashMap<>();
        // variables.put(USER, user);
        // variables.put(BASE_URL, mailProperties.getBaseUrl());
        // Locale locale = StringUtils.isNotBlank(user.getLangKey()) ? Locale.forLanguageTag(user.getLangKey()) : Locale.getDefault();
        // mailService.sendEmailFromTemplate(MailTemplateEnum.PASSWORD_RESET, mail, variables, locale);
    }

    @RequiresGuest
    @PostMapping("/account/reset-password/finish")
    public void finishPasswordReset(@Valid @RequestBody KeyAndPasswordDTO keyAndPasswordDTO) {
        accountApi.finishPasswordReset(keyAndPasswordDTO.getKey(), keyAndPasswordDTO.getNewPassword());
    }

    private void createCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        CookieGenerator cookieGenerator = createCookieGenerator(request);
        cookieGenerator.setCookieMaxAge(jwtProperties.getCookie().getMaxAge());
        cookieGenerator.addCookie(response, token);
    }

    private void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieGenerator cookieGenerator = createCookieGenerator(request);
        cookieGenerator.setCookieMaxAge(0);
        cookieGenerator.addCookie(response, "");
    }

    private CookieGenerator createCookieGenerator(HttpServletRequest request) {
        CookieGenerator cookieGenerator = new CookieGenerator();
        JwtProperties.Cookie cookie = jwtProperties.getCookie();
        cookieGenerator.setCookieName(cookie.getName());
        cookieGenerator.setCookiePath(cookie.getPath());
        String cookieDomain = cookie.getDomain();
        if (StringUtils.isEmpty(cookieDomain)) {
            cookieDomain = WebUtils.getCookieDomain(request);
        }
        if (StringUtils.isNotEmpty(cookieDomain)) {
            cookieGenerator.setCookieDomain(cookieDomain);
        }
        cookieGenerator.setCookieHttpOnly(cookie.isHttpOnly());
        cookieGenerator.setCookieSecure(cookie.isSecure());
        return cookieGenerator;
    }

}

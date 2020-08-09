package com.gavin.cloud.auth.web;

import com.gavin.cloud.auth.core.config.properties.JwtExtProperties;
import com.gavin.cloud.auth.core.problem.AccountNotActivatedException;
import com.gavin.cloud.auth.core.problem.AccountNotFoundException;
import com.gavin.cloud.auth.core.problem.InvalidPasswordException;
import com.gavin.cloud.auth.core.service.AuthService;
import com.gavin.cloud.auth.core.dto.AuthTokenDTO;
import com.gavin.cloud.auth.web.dto.KeyAndPasswordDTO;
import com.gavin.cloud.auth.web.dto.LoginDTO;
import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.web.annotation.RequiresGuest;
import com.gavin.cloud.common.web.util.WebUtils;
import com.gavin.cloud.sys.api.AccountApi;
import com.gavin.cloud.sys.api.RoleApi;
import com.gavin.cloud.sys.api.UserApi;
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
import java.util.Optional;

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
                .orElseThrow(AccountNotFoundException::new);
        if (!Md5Hash.hash(loginDTO.getPassword(), user.getSalt()).equals(user.getPassword())) {
            throw new InvalidPasswordException();
        }
        if (!BooleanUtils.isTrue(user.getActivated())) {
            throw new AccountNotActivatedException();
        }
        AuthTokenDTO authToken = authService.createAuthToken(ActiveUser.builder()
                .uid(user.getId())
                .username(user.getUsername())
                .clientIP(WebUtils.getClientIP(request))
                .roles(roleApi.getRoles(user.getId()))
                .build());
        createCookie(request, response, authToken.getAccessToken());
        return ResponseEntity.ok(authToken);
    }

    @GetMapping("/token/{refreshToken}")
    public ResponseEntity<String> getNewAccessToken(@PathVariable String refreshToken, @RequestParam Long uid,
                                                    HttpServletRequest request, HttpServletResponse response) {
        User user = Optional.ofNullable(userApi.getUser(uid))
                .orElseThrow(AccountNotFoundException::new);
        ActiveUser activeUser = ActiveUser.builder()
                .uid(user.getId())
                .username(user.getUsername())
                .clientIP(WebUtils.getClientIP(request))
                .roles(roleApi.getRoles(user.getId()))
                .build();
        AuthTokenDTO authToken = authService.createAuthToken(activeUser, refreshToken);
        createCookie(request, response, authToken.getAccessToken());
        return ResponseEntity.ok(authToken.getAccessToken());
    }

    @RequiresGuest
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        authService.rejectRefreshToken(refreshToken);
        clearCookie(request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/account/{token}")
    public ResponseEntity<?> getAccount(@PathVariable("token") String token, @RequestParam(required = false) String jsonpCallback) {
        ActiveUser activeUser = JwtHelper.verifyToken(token, jwtProperties.getPublicKey());
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
        // TODO 通过消息通知发送邮件
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
        // TODO 通过消息通知发送邮件
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
        CookieGenerator cookieGenerator = new CookieGenerator();
        setCookieProperties(request, cookieGenerator);
        cookieGenerator.setCookieMaxAge(jwtProperties.getCookieMaxAge());
        cookieGenerator.addCookie(response, token);
    }

    private void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieGenerator cookieGenerator = new CookieGenerator();
        setCookieProperties(request, cookieGenerator);
        cookieGenerator.setCookieMaxAge(0);
        cookieGenerator.addCookie(response, "");
    }

    private void setCookieProperties(HttpServletRequest request, CookieGenerator cookieGenerator) {
        cookieGenerator.setCookiePath(jwtProperties.getCookiePath());
        String cookieDomain = jwtProperties.getCookieDomain();
        if (StringUtils.isEmpty(cookieDomain)) {
            cookieDomain = WebUtils.getCookieDomain(request);
        }
        if (StringUtils.isNotEmpty(cookieDomain)) {
            cookieGenerator.setCookieDomain(cookieDomain);
        }
        cookieGenerator.setCookieHttpOnly(jwtProperties.isUseHttpOnlyCookie());
        cookieGenerator.setCookieName(jwtProperties.getCookieName());
        cookieGenerator.setCookieSecure(jwtProperties.isUseSecureCookie());
    }

}

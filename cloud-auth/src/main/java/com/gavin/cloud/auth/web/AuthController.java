package com.gavin.cloud.auth.web;

import com.gavin.cloud.auth.client.AccountClient;
import com.gavin.cloud.auth.client.PermissionClient;
import com.gavin.cloud.auth.client.RoleClient;
import com.gavin.cloud.auth.client.UserClient;
import com.gavin.cloud.auth.config.properties.JwtExtProperties;
import com.gavin.cloud.auth.dto.KeyAndPasswordDTO;
import com.gavin.cloud.auth.dto.LoginDTO;
import com.gavin.cloud.auth.problem.AccountNotActivatedException;
import com.gavin.cloud.auth.problem.AccountNotFoundException;
import com.gavin.cloud.auth.problem.InvalidPasswordException;
import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.web.annotation.RequiresGuest;
import com.gavin.cloud.common.web.util.RequestUtils;
import com.gavin.cloud.sys.api.dto.RegisterDTO;
import com.gavin.cloud.sys.api.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
public class AuthController {

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    @Autowired
    private JwtExtProperties jwtProperties;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RoleClient roleClient;

    @Autowired
    private PermissionClient permissionClient;

    /**
     * POST  /login/{type} : User login.
     *
     * @param loginDTO loginDTO
     * @param type     {1:USERNAME, 2:PHONE, 3:EMAIL}
     * @return Login user
     */
    @RequiresGuest
    @PostMapping("/login/{type:" + Constants.REGEX_LOGIN_TYPE + "}")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginDTO loginDTO, @PathVariable int type,
                                      HttpServletRequest request, HttpServletResponse response) {
        User user = userClient.getUser(loginDTO.getUsername(), type);
        if (user == null) {
            throw new AccountNotFoundException();
        }
        if (!Md5Hash.hash(loginDTO.getPassword(), user.getSalt()).equals(user.getPassword())) {
            throw new InvalidPasswordException();
        }
        if (user.getActivated() == null || !user.getActivated()) {
            throw new AccountNotActivatedException();
        }

        List<String> roles = roleClient.getRoles(user.getId());
        String clientIP = RequestUtils.getClientIP(request);
        ActiveUser activeUser = new ActiveUser(user.getId(), user.getUsername(), clientIP, roles);
        String token = createToken(activeUser);

        handleCookie(token, request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 检查用户登录状态, 如果已登录, 则刷新Token; 如果未登录或登录过期, 则调整到登录页面(前端)
     */
    @GetMapping("/account/verify")
    public ResponseEntity<ActiveUser> verify(@CookieValue("accessToken") String accessToken,
                                             HttpServletRequest request, HttpServletResponse response) {
        ActiveUser activeUser = JwtHelper.verifyToken(accessToken, jwtProperties.getPublicKey());
        // 刷新Token(重新生成)
        String token = createToken(activeUser);
        handleCookie(token, request, response);
        return ResponseEntity.ok(activeUser);
    }

    @RequiresGuest
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        handleCookie(null, request, response);
    }

    //@RequiresUser
    //@GetMapping("/menus")
    //public ResponseEntity<List<Permission>> getMenus() {
    //    return ResponseEntity.ok(subjectService.getSubject().getMenus());
    //}

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
        //User user = accountClient.register(registerDTO);
        //Map<String, Object> variables = new HashMap<>();
        //variables.put(USER, user);
        //variables.put(BASE_URL, mailProperties.getBaseUrl());
        //Locale locale = StringUtils.isNotBlank(user.getLangKey()) ? Locale.forLanguageTag(user.getLangKey()) : Locale.getDefault();
        //mailService.sendEmailFromTemplate(MailTemplateEnum.ACCOUNT_ACTIVATION, registerDTO.getEmail(), variables, locale);
    }

    @RequiresGuest
    @GetMapping("/account/activate")
    public void activateAccount(@RequestParam String key) {
        accountClient.activateRegistration(key);
    }

    @RequiresGuest
    @PostMapping("/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        // TODO 通过消息通知发送邮件
        // User user = accountClient.requestPasswordReset(mail);
        // Map<String, Object> variables = new HashMap<>();
        // variables.put(USER, user);
        // variables.put(BASE_URL, mailProperties.getBaseUrl());
        // Locale locale = StringUtils.isNotBlank(user.getLangKey()) ? Locale.forLanguageTag(user.getLangKey()) : Locale.getDefault();
        // mailService.sendEmailFromTemplate(MailTemplateEnum.PASSWORD_RESET, mail, variables, locale);
    }

    @RequiresGuest
    @PostMapping("/account/reset-password/finish")
    public void finishPasswordReset(@Valid @RequestBody KeyAndPasswordDTO keyAndPasswordDTO) {
        accountClient.finishPasswordReset(keyAndPasswordDTO.getKey(), keyAndPasswordDTO.getNewPassword());
    }

    private String createToken(ActiveUser activeUser) {
        return JwtHelper.createToken(activeUser, jwtProperties.getPrivateKey(), jwtProperties.getValidityInSeconds());
    }

    private void handleCookie(String token, HttpServletRequest request, HttpServletResponse response) {
        CookieGenerator cookieGenerator = new CookieGenerator();
        cookieGenerator.setCookiePath(jwtProperties.getCookiePath());
        String domain = jwtProperties.getCookieDomain();
        if (StringUtils.isEmpty(domain)) {
            domain = RequestUtils.getDomainName(request);
        }
        if (!StringUtils.isEmpty(domain)) {
            cookieGenerator.setCookieDomain(domain);
        }
        cookieGenerator.setCookieHttpOnly(jwtProperties.isUseHttpOnlyCookie());
        cookieGenerator.setCookieMaxAge(jwtProperties.getCookieMaxAge());
        cookieGenerator.setCookieName(jwtProperties.getCookieName());
        cookieGenerator.setCookieSecure(jwtProperties.isUseSecureCookie());
        cookieGenerator.addCookie(response, token);
    }

}

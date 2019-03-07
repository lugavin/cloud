package com.gavin.cloud.auth.web;

import com.gavin.cloud.auth.client.AccountClient;
import com.gavin.cloud.auth.client.PermissionClient;
import com.gavin.cloud.auth.client.UserClient;
import com.gavin.cloud.auth.dto.KeyAndPasswordDTO;
import com.gavin.cloud.auth.dto.LoginDTO;
import com.gavin.cloud.auth.enums.AuthMessageType;
import com.gavin.cloud.auth.enums.MailTemplateEnum;
import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.base.subject.Permission;
import com.gavin.cloud.common.base.subject.Subject;
import com.gavin.cloud.common.base.subject.SubjectService;
import com.gavin.cloud.common.base.util.Constants;
import com.gavin.cloud.common.base.util.Md5Hash;
import com.gavin.cloud.common.web.auth.RequiresGuest;
import com.gavin.cloud.common.web.auth.RequiresUser;
import com.gavin.cloud.common.web.config.AppWebProperties;
import com.gavin.cloud.common.web.mail.MailService;
import com.gavin.cloud.sys.api.dto.RegisterDTO;
import com.gavin.cloud.sys.api.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
public class AuthController {

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    @Autowired
    private AppWebProperties appWebProperties;

    @Autowired
    private MailService mailService;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private PermissionClient permissionClient;

    @Autowired
    private SubjectService subjectService;

    /**
     * POST  /login/{type} : User login.
     *
     * @param loginDTO loginDTO
     * @param type     {1:USERNAME, 2:PHONE, 3:EMAIL}
     * @return Login user
     */
    @RequiresGuest
    @PostMapping("/login/{type:" + Constants.REGEX_LOGIN_TYPE + "}")
    public ResponseEntity<Void> login(@Valid LoginDTO loginDTO, @PathVariable int type, HttpServletResponse response) {
        User user = userClient.getUser(loginDTO.getUsername(), type);
        if (user == null) {
            throw new AppException(AuthMessageType.ERR_ACCOUNT_NOT_FOUND);
        }
        if (!Md5Hash.hash(loginDTO.getPassword(), user.getSalt()).equals(user.getPassword())) {
            throw new AppException(AuthMessageType.ERR_INVALID_PASSWORD);
        }
        if (user.getActivated() == null || !user.getActivated()) {
            throw new AppException(AuthMessageType.ERR_ACCOUNT_NOT_ACTIVATED);
        }
        // File file = ResourceUtils.getFile("classpath:cert.jks");
        // byte[] privateKey = null;
        // String token = Jwt.builder()
        //         .withSub(user.getId())
        //         .withExp(Long.toString(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 1800)) // 半小时有效
        //         .sign(privateKey, Algorithm.Type.RS256);
        AppWebProperties.Cookie cookie = appWebProperties.getCookie();
        CookieGenerator cookieGenerator = new CookieGenerator();
        cookieGenerator.setCookiePath(cookie.getPath());
        cookieGenerator.setCookieDomain(cookie.getDomain());
        cookieGenerator.setCookieHttpOnly(cookie.isHttpOnly());
        cookieGenerator.setCookieMaxAge(cookie.getMaxAge());
        cookieGenerator.setCookieName(cookie.getName());
        cookieGenerator.setCookieSecure(cookie.isSecure());
        // cookieGenerator.addCookie(response, token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequiresGuest
    @GetMapping("/logout")
    public void logout() {
        subjectService.removeSubject();
    }

    @RequiresUser
    @GetMapping("/menus")
    public ResponseEntity<List<Permission>> getMenus() {
        return ResponseEntity.ok(subjectService.getSubject().getMenus());
    }

    @GetMapping
    public ResponseEntity<?> getAccount(@RequestParam(required = false) String jsonpCallback) {
        Subject subject = subjectService.getSubject();
        if (StringUtils.isEmpty(jsonpCallback)) {
            return ResponseEntity.ok(subject);
        }
        MappingJacksonValue jacksonValue = new MappingJacksonValue(subject);
        jacksonValue.setJsonpFunction(jsonpCallback);
        return ResponseEntity.ok(jacksonValue);
    }

    @RequiresGuest
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid RegisterDTO registerDTO) {
        User user = accountClient.register(registerDTO);
        Map<String, Object> variables = new HashMap<>();
        variables.put(USER, user);
        variables.put(BASE_URL, appWebProperties.getMail().getBaseUrl());
        Locale locale = StringUtils.isNotBlank(user.getLangKey()) ? Locale.forLanguageTag(user.getLangKey()) : Locale.getDefault();
        mailService.sendEmailFromTemplate(MailTemplateEnum.ACCOUNT_ACTIVATION, registerDTO.getEmail(), variables, locale);
    }

    @RequiresGuest
    @GetMapping("/activate")
    public void activateAccount(@RequestParam String key) {
        accountClient.activateRegistration(key);
    }

    @RequiresGuest
    @PostMapping("/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        User user = accountClient.requestPasswordReset(mail);
        Map<String, Object> variables = new HashMap<>();
        variables.put(USER, user);
        variables.put(BASE_URL, appWebProperties.getMail().getBaseUrl());
        Locale locale = StringUtils.isNotBlank(user.getLangKey()) ? Locale.forLanguageTag(user.getLangKey()) : Locale.getDefault();
        mailService.sendEmailFromTemplate(MailTemplateEnum.PASSWORD_RESET, mail, variables, locale);
    }

    @RequiresGuest
    @PostMapping("/reset-password/finish")
    public void finishPasswordReset(@Valid @RequestBody KeyAndPasswordDTO keyAndPasswordDTO) {
        accountClient.finishPasswordReset(keyAndPasswordDTO.getKey(), keyAndPasswordDTO.getNewPassword());
    }

    // @RequiresGuest
    // @GetMapping("/{token}")
    // public ResponseEntity<Subject> getAccountByToken(@PathVariable String token) {
    //     return ResponseEntity.ok(subjectService.getSubject(token));
    // }

    // static class AuthToken {
    //     private final String token;
    //     public AuthToken(String token) {
    //         this.token = token;
    //     }
    //     @JsonProperty("x-auth-token")
    //     public String getToken() {
    //         return token;
    //     }
    // }

}

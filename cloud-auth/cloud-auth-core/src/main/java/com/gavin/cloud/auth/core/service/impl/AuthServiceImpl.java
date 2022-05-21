package com.gavin.cloud.auth.core.service.impl;

import com.gavin.cloud.auth.core.config.properties.JwtExtProperties;
import com.gavin.cloud.auth.core.dto.AuthTokenDTO;
import com.gavin.cloud.auth.core.dao.ext.AuthTokenExtDao;
import com.gavin.cloud.auth.core.service.AuthService;
import com.gavin.cloud.auth.pojo.AuthToken;
import com.gavin.cloud.auth.pojo.AuthTokenExample;
import com.gavin.cloud.common.base.auth.ActiveUser;
import com.gavin.cloud.common.base.auth.JwtHelper;
import com.gavin.cloud.common.base.problem.AppBizException;
import com.gavin.cloud.common.base.util.NanoIdUtils;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.gavin.cloud.common.base.problem.DefaultProblemType.AUTHENTICATION_FAILED_TYPE;

/**
 * JWT的最大优势是服务端不再需要存储Session, 使得服务端认证鉴权业务可以方便扩展, 避免存储Session所需要引入的第三方组件(如Redis),
 * 降低了系统架构复杂度, 但这也是JWT最大的劣势, 由于有效期存储在Token中, Token一旦签发, 就会在有效期内一直可用, 无法在服务端废止,
 * 当用户进行登出操作, 只能依赖客户端删除掉本地存储的Token, 如果需要禁用用户, 单纯使用JWT就无法做到了.
 * <p>
 * 前面讲的Token其实是AccessToken, 也就是访问资源接口时所需要的Token, 还有另外一种Token - RefreshToken,
 * 通常情况下, RefreshToken的有效期会比较长, 而AccessToken的有效期比较短, 当AccessToken由于过期而失效时,
 * 使用RefreshToken就可以获取到新的AccessToken, 如果RefreshToken也失效了, 用户就只能重新登录了.
 * <p>
 * 在JWT实践中, 引入RefreshToken, 将会话管理流程改进如下:
 * (1)客户端使用用户名密码进行认证
 * (2)服务端生成有效时间较短的AccessToken(例如10分钟), 和有效时间较长的RefreshToken(例如7天)
 * (3)客户端访问需要认证的接口时, 携带AccessToken
 * (4)如果AccessToken没有过期, 服务端鉴权后返回给客户端需要的数据
 * (5)如果携带AccessToken访问需要认证的接口时鉴权失败(例如返回401错误), 则客户端使用RefreshToken向刷新接口申请新的AccessToken
 * (6)如果RefreshToken没有过期, 服务端向客户端下发新的AccessToken
 * <p>
 * 将生成的RefreshToken以及过期时间存储在服务端的数据库中, 由于RefreshToken不会在客户端请求业务接口时验证, 只有在申请新的AccessToken时才会验证,
 * 所以将RefreshToken存储在数据库中, 不会对业务接口的响应时间造成影响, 也不需要像Session一样一直保持在内存中以应对大量的请求.
 * <p>
 * 上述的架构, 提供了服务端禁用用户Token的方式, 当需要登出或禁用用户时, 只需要将服务端的RefreshToken禁用或删除, 用户就会在AccessToken过期后,
 * 由于无法获取到新的AccessToken而再也无法访问需要认证的接口, 这样的方式虽然会有一定的窗口期(取决于AccessToken的失效时间),
 * 但是结合用户登出时客户端删除AccessToken的操作, 基本上可以适应常规情况下对用户认证鉴权的精度要求.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthTokenExtDao authTokenMapperExt;
    private final JwtExtProperties jwtProperties;

    @Override
    @Transactional
    public AuthTokenDTO createAuthToken(ActiveUser activeUser) {
        String accessToken = JwtHelper.createToken(activeUser, jwtProperties.getPrivateKey(), jwtProperties.getAccessTokenExpires());
        String refreshToken = NanoIdUtils.randomNanoId();
        AuthToken authToken = new AuthToken();
        authToken.setId(SnowflakeIdWorker.getInstance().nextId());
        authToken.setUid(activeUser.getUid());
        // authToken.setNonce(RandomStringUtils.random(8));
        authToken.setClientIp(activeUser.getClientIP());
        authToken.setRefreshToken(refreshToken);
        authToken.setCreatedAt(Date.from(Instant.now()));
        authToken.setExpiredAt(Date.from(Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpires())));
        authTokenMapperExt.insert(authToken);
        return new AuthTokenDTO(accessToken, refreshToken, jwtProperties.getAccessTokenExpires());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthTokenDTO createAuthToken(ActiveUser activeUser, String refreshToken) {
        AuthTokenExample example = new AuthTokenExample();
        example.createCriteria().andRefreshTokenEqualTo(refreshToken);
        List<AuthToken> authTokens = authTokenMapperExt.selectByExample(example);
        if (!authTokens.isEmpty()) {
            AuthToken authToken = authTokens.get(0);
            if (authToken.getUid().equals(activeUser.getUid()) && authToken.getExpiredAt().after(Date.from(Instant.now()))) {
                String accessToken = JwtHelper.createToken(activeUser, jwtProperties.getPrivateKey(), jwtProperties.getAccessTokenExpires());
                return new AuthTokenDTO(accessToken, refreshToken, jwtProperties.getAccessTokenExpires());
            }
        }
        throw new AppBizException(AUTHENTICATION_FAILED_TYPE, "The refresh token has expired!");
    }

    @Override
    public ActiveUser verifyAccessToken(String accessToken) {
        return JwtHelper.verifyToken(accessToken, jwtProperties.getPublicKey());
    }

    @Override
    @Transactional
    public void rejectRefreshToken(String refreshToken) {
        AuthTokenExample example = new AuthTokenExample();
        example.createCriteria().andRefreshTokenEqualTo(refreshToken);
        authTokenMapperExt.deleteByExample(example);
    }

}

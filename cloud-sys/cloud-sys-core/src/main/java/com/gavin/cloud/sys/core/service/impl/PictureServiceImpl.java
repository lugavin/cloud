package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.sys.core.service.PictureService;
import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.base.exception.CommonMessageType;
import com.gavin.cloud.common.base.util.FtpUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService {

    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private Integer port;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.basePath}")
    private String basePath;

    @Value("${oss.baseURL}")
    private String baseURL;

    @Override
    public String upload(InputStream local) {
        FTPClient ftpClient = FtpUtils.connect(host, port);
        if (!FtpUtils.login(ftpClient, username, password)) {
            throw new AppException(CommonMessageType.ERR_SERVER, "Ftp service is not available.");
        }
        String filePath = new SimpleDateFormat("/yyyy/MM/dd/").format(new Date());
        String filename = UUID.randomUUID().toString();
        FtpUtils.uploadFile(ftpClient, basePath + filePath, filename, local);
        return baseURL + filePath + filename;
    }

}

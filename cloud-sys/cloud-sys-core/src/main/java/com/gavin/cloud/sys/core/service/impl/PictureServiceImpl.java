package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.util.SftpHelper;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.config.properties.OssProperties;
import com.gavin.cloud.sys.core.config.properties.SftpProperties;
import com.gavin.cloud.sys.core.service.PictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    private final SftpProperties sftpProperties;
    private final OssProperties ossProperties;

    //@Override
    //public String upload(InputStream local) {
    //    FTPClient ftpClient = FtpUtils.connect(sftpProperties.getHost(), sftpProperties.getPort());
    //    if (!FtpUtils.login(ftpClient, sftpProperties.getUsername(), sftpProperties.getPassword())) {
    //        throw new InternalServerErrorException("Ftp service is not available.");
    //    }
    //    String filePath = LocalDate.now().format(DateTimeFormatter.ofPattern("/yyyy/MM/dd/"));
    //    String filename = Long.toString(new SnowflakeIdWorker().nextId());
    //    FtpUtils.uploadFile(ftpClient, sftpProperties.getBasePath() + filePath, filename, local);
    //    return ossProperties.getBaseURL() + filePath + filename;
    //}

    @Override
    public String upload(InputStream local) {
        SftpHelper sftpHelper = new SftpHelper(
                sftpProperties.getUsername(),
                sftpProperties.getPassword(),
                sftpProperties.getHost(),
                sftpProperties.getPort(),
                sftpProperties.getTimeout()
        );
        try {
            sftpHelper.login();
            String filePath = LocalDate.now().format(DateTimeFormatter.ofPattern("/yyyy/MM/dd/"));
            String filename = Long.toString(SnowflakeIdWorker.getInstance().nextId());
            sftpHelper.upload(sftpProperties.getBasePath() + filePath, filename, local);
            return ossProperties.getBaseURL() + filePath + filename;
        } finally {
            sftpHelper.logout();
        }
    }

}

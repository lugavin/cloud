package com.gavin.cloud.sys.core.service.impl;

import com.gavin.cloud.common.base.exception.AppException;
import com.gavin.cloud.common.base.util.SftpUtils;
import com.gavin.cloud.common.base.util.SnowflakeIdWorker;
import com.gavin.cloud.sys.core.config.properties.OssProperties;
import com.gavin.cloud.sys.core.config.properties.SftpProperties;
import com.gavin.cloud.sys.core.service.PictureService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.gavin.cloud.common.base.exception.DefaultProblemType.INTERNAL_SERVER_ERROR_TYPE;

@Service
public class PictureServiceImpl implements PictureService {

    private final SftpProperties sftpProperties;
    private final OssProperties ossProperties;

    public PictureServiceImpl(SftpProperties sftpProperties,
                              OssProperties ossProperties) {
        this.sftpProperties = sftpProperties;
        this.ossProperties = ossProperties;
    }

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
        SftpUtils sftpUtils = null;
        try {
            sftpUtils = new SftpUtils(
                    sftpProperties.getUsername(),
                    sftpProperties.getPassword(),
                    sftpProperties.getHost(),
                    sftpProperties.getPort(),
                    sftpProperties.getTimeout()
            );
            if (!sftpUtils.login()) {
                throw new AppException(INTERNAL_SERVER_ERROR_TYPE, "Ftp service is not available.");
            }
            String filePath = LocalDate.now().format(DateTimeFormatter.ofPattern("/yyyy/MM/dd/"));
            String filename = Long.toString(SnowflakeIdWorker.getInstance().nextId());
            sftpUtils.upload(sftpProperties.getBasePath() + filePath, filename, local);
            return ossProperties.getBaseURL() + filePath + filename;
        } finally {
            Optional.ofNullable(sftpUtils).ifPresent(SftpUtils::logout);
        }
    }

}

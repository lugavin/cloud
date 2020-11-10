package com.gavin.cloud.common.base.util;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * @see <a href="https://github.com/alibaba/DataX/blob/master/ftpreader/src/main/java/com/alibaba/datax/plugin/reader/ftpreader/SftpHelper.java">SftpHelper</a>
 */
@Slf4j
public class SftpHelper {

    private static final String CHANNEL_TYPE_SFTP = "sftp";

    private Session session;
    private ChannelSftp channelSftp;

    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final int timeout;

    public SftpHelper(String username, String password, String host, int port, int timeout) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public void login() {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(timeout);
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel(CHANNEL_TYPE_SFTP);
            channelSftp.connect();
        } catch (JSchException e) {
            throw new RuntimeException("Login sftp server failed", e);
        }
    }

    public void logout() {
        Optional.ofNullable(channelSftp).ifPresent(ChannelSftp::disconnect);
        Optional.ofNullable(session).ifPresent(Session::disconnect);
    }

    public void upload(String remotePath, String fileName, InputStream src) {
        try {
            channelSftp.cd(remotePath);
            channelSftp.put(src, fileName);
        } catch (SftpException e) {
            log.error("upload failed", e);
            throw new RuntimeException("Upload failed", e);
        }
    }

    public void download(String remotePath, String fileName, OutputStream dst) {
        try {
            this.channelSftp.cd(remotePath);
            this.channelSftp.get(fileName, dst);
        } catch (SftpException e) {
            throw new RuntimeException("Download failed", e);
        }
    }

    public void delete(String remotePath, String fileName) {
        try {
            this.channelSftp.cd(remotePath);
            this.channelSftp.rm(fileName);
        } catch (SftpException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

}

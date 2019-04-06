package com.gavin.cloud.common.base.util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class SftpUtils {

    private static final String CHANNEL_TYPE_SFTP = "sftp";

    private Session sshSession;
    private ChannelSftp sftp;

    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final int timeout;

    public SftpUtils(String username, String password, String host, int port, int timeout) {
        this.username = Objects.requireNonNull(username);
        this.password = password;
        this.host = Objects.requireNonNull(host);
        this.port = port;
        this.timeout = timeout;
    }

    public boolean login() {
        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, host, port);
            Optional.ofNullable(password).ifPresent(sshSession::setPassword);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(config);
            sshSession.setTimeout(timeout);
            sshSession.connect();
            sftp = (ChannelSftp) sshSession.openChannel(CHANNEL_TYPE_SFTP);
            sftp.connect();
            return true;
        } catch (JSchException e) {
            log.error("sftp login failed", e);
            return false;
        }
    }

    public void logout() {
        if (sftp != null && sftp.isConnected()) {
            sftp.disconnect();
        }
        if (sshSession != null && sshSession.isConnected()) {
            sshSession.disconnect();
        }
    }

    public boolean upload(String remotePath, String fileName) {
        try {
            File file = new File(fileName);
            return upload(remotePath, file.getName(), new FileInputStream(file));
        } catch (FileNotFoundException e) {
            log.error("upload failed", e);
            return false;
        }
    }

    public boolean upload(String remotePath, String fileName, InputStream local) {
        try {
            sftp.cd(remotePath);
            sftp.put(local, fileName);
            return true;
        } catch (SftpException e) {
            log.error("upload failed", e);
            return false;
        }
    }

    public boolean download(String remotePath, String localPath, String fileName) {
        try {
            this.sftp.cd(remotePath);
            File localFile = new File(localPath, fileName);
            this.sftp.get(fileName, new FileOutputStream(localFile));
            return true;
        } catch (SftpException | FileNotFoundException e) {
            log.error("download failed", e);
            return false;
        }
    }

    public boolean delete(String remotePath, String fileName) {
        try {
            this.sftp.cd(remotePath);
            this.sftp.rm(fileName);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

}

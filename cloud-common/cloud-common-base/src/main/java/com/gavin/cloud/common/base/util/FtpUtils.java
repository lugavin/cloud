package com.gavin.cloud.common.base.util;

import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class FtpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtils.class);

    public static final int DEFAULT_PORT = 21;

    public static final int MODE_TYPE_LOCAL_PASSIVE = 1;  // 本地被动模式
    public static final int MODE_TYPE_LOCAL_ACTIVE = 2;   // 本地主动模式
    public static final int MODE_TYPE_REMOTE_PASSIVE = 3; // 远程被动模式

    public static FTPClient connect(String hostname) {
        return connect(hostname, DEFAULT_PORT);
    }

    public static FTPClient connect(String hostname, int port) {
        return connect(hostname, port, MODE_TYPE_LOCAL_PASSIVE, UTF_8);
    }

    public static FTPClient connect(String hostname, int port, int mode) {
        return connect(hostname, port, mode, UTF_8);
    }

    public static FTPClient connect(String hostname, int port, int mode, Charset charset) {

        FTPClient ftpClient = null;

        try {
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding(charset.name());
            ftpClient.connect(hostname, port);

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            switch (mode) {
                case MODE_TYPE_LOCAL_PASSIVE:
                    ftpClient.enterLocalPassiveMode();
                    break;
                case MODE_TYPE_LOCAL_ACTIVE:
                    ftpClient.enterLocalActiveMode();
                    break;
                case MODE_TYPE_REMOTE_PASSIVE:
                    ftpClient.enterRemotePassiveMode();
                    break;
                default:
                    throw new IOException("can not support mode type " + mode);
            }

            return ftpClient;
        } catch (Exception e) {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void disconnect(FTPClient ftpClient) {
        if (ftpClient == null) {
            return;
        }
        if (!ftpClient.isConnected()) {
            return;
        }
        try {
            ftpClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean login(FTPClient ftpClient, String username, String password) {
        try {
            ftpClient.login(username, password);
            return isLogin(ftpClient);
        } catch (Exception ignored) {
        }
        return false;

    }

    public static boolean isLogin(FTPClient ftpClient) {
        return FTPReply.isPositiveCompletion(ftpClient.getReplyCode());
    }

    public static void logout(FTPClient ftpClient) {
        if (ftpClient == null) {
            return;
        }
        if (isLogin(ftpClient)) {
            try {
                ftpClient.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean uploadFile(FTPClient ftpClient, String remotePath, String localPath, String filename) {
        return uploadFile(ftpClient, remotePath, localPath, filename, UTF_8);
    }

    public static boolean uploadFile(FTPClient ftpClient, String remotePath, String localPath, String filename, Charset charset) {
        try {

            boolean ok = ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(charset), ISO_8859_1));
            if (!ok) {
                LOGGER.error("can not change working directory {}", remotePath);
                return false;
            }

            File file = new File(localPath, filename);
            return ftpClient.storeFile(new String(filename.getBytes(charset), ISO_8859_1), FileUtils.openInputStream(file));
        } catch (Exception e) {
            LOGGER.error("upload file error.", e);
        }
        return false;
    }

    public static boolean uploadFile(FTPClient ftpClient, String remotePath, String filename, InputStream local) {
        return uploadFile(ftpClient, remotePath, filename, local, UTF_8);
    }

    public static boolean uploadFile(FTPClient ftpClient, String remotePath, String filename, InputStream local, Charset charset) {
        try {

            boolean ok = ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(charset), ISO_8859_1));
            if (!ok) {
                LOGGER.error("can not change working directory {}", remotePath);
                return false;
            }

            return ftpClient.storeFile(new String(filename.getBytes(charset), ISO_8859_1), local);
        } catch (Exception e) {
            LOGGER.error("upload file error.", e);
        }
        return false;
    }

    public static Result uploadFolder(FTPClient ftpClient, String remotePath, String localPath) {
        return uploadFolder(ftpClient, remotePath, localPath, UTF_8);
    }

    public static Result uploadFolder(FTPClient ftpClient, String remotePath, String localPath, Charset charset) {
        Result result = new Result();
        int success = 0;
        int error = 0;

        try {
            File dir = new File(localPath);
            if (!dir.exists()) {
                throw new IOException("please set valid localpath");
            }

            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean ok = uploadFile(ftpClient, remotePath, localPath, file.getName(), charset);
                        if (ok) {
                            success += 1;
                        } else {
                            error += 1;
                        }
                    } else if (file.isDirectory()) {
                        Result r = uploadFolder(ftpClient, remotePath + "/" + file.getName(), localPath + "/" + file.getName(), charset);
                        success += r.getSuccess();
                        error += r.getError();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("upload folder error.", e);
        }
        result.setSuccess(success);
        result.setError(error);
        return result;
    }

    public static boolean downloadFile(FTPClient ftpClient, String remotePath, String localPath, String filename) {
        return downloadFile(ftpClient, remotePath, localPath, filename, UTF_8);
    }

    public static boolean downloadFile(FTPClient ftpClient, String remotePath, String localPath, String filename, Charset charset) {
        createFilepath(localPath);
        try {

            boolean ok = ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(charset), ISO_8859_1));
            if (!ok) {
                LOGGER.error("can not change working directory {}", remotePath);
                return false;
            }

            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.getName().equals(filename)) {
                    if (ftpFile.isFile()) {
                        File localFile = new File(localPath, filename);
                        OutputStream is = new FileOutputStream(localFile);
                        ok = ftpClient.retrieveFile(ftpFile.getName(), is);
                        is.close();
                        return ok;
                    } else if (ftpFile.isDirectory()) {
                        LOGGER.error("{} is folder.", remotePath + "/" + filename);
                        return false;
                    }
                }
            }
            LOGGER.error("{} is not exists", remotePath + "/" + filename);
        } catch (Exception e) {
            LOGGER.error("download file error.", e);
        }
        return false;

    }

    public static Result downloadFolder(FTPClient ftpClient, String remotePath, String localPath) {
        return downloadFolder(ftpClient, remotePath, localPath, UTF_8);
    }

    public static Result downloadFolder(FTPClient ftpClient, String remotePath, String localPath, Charset charset) {
        createFilepath(localPath);
        Result result = new Result();
        int success = 0;
        int error = 0;

        try {

            boolean ok = ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(charset), ISO_8859_1));
            if (!ok) {
                LOGGER.error("can not change working directory {}", remotePath);
                return result;
            }

            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                String name = ftpFile.getName();
                if (ftpFile.isFile()) {
                    ok = downloadFile(ftpClient, remotePath, localPath, name, charset);
                    if (ok) {
                        success += 1;
                    } else {
                        error += 1;
                    }
                } else if (ftpFile.isDirectory()) {
                    Result r = downloadFolder(ftpClient, remotePath + "/" + name, localPath + "/" + name, charset);
                    success += r.getSuccess();
                    error += r.getError();
                }
            }
        } catch (Exception e) {
            LOGGER.error("download folder error.", e);
        }
        result.setSuccess(success);
        result.setError(error);
        return result;

    }

    private static void createFilepath(String filepath) {
        File dir = new File(filepath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Data
    static class Result {
        private int success;
        private int error;
    }

}

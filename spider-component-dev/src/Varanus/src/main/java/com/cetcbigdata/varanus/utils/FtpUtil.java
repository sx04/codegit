package com.cetcbigdata.varanus.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;

/**
 * Created with IDEA
 * author:Matthew
 * Date:2019-5-6
 * Time:16:56
 */
public class FtpUtil {

    private final static Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    /**
     * 获取FTPClient对象
     * @param ftpHost       FTP主机服务器
     * @param ftpPort       FTP端口 默认为21
     * @return
     */
    public static FTPClient getFTPClient(String ftpHost,int ftpPort) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHost, ftpPort);              // 连接FTP服务器
            //ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                logger.info("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                logger.info("FTP连接成功。");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            logger.info("FTP的IP地址可能错误，请正确配置。");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("FTP的端口错误,请正确配置。");
        }
        return ftpClient;
    }

    /*
     * 从FTP服务器下载文件
     * @param ftpHost             FTP IP地址
     * @param ftpUserName         FTP 用户名
     * @param ftpPassword         FTP用户名密码
     * @param ftpPort             FTP端口
     * @param ftpPath             FTP服务器中文件所在路径 格式： ftptest/aa
     * @param localPath           下载到本地的位置 格式：H:/download
     * @param fileName            FTP服务器上要下载的文件名称
     * @param targetFileName      FTP服务器上要下载的文件名称
     */
    public static void downloadFtpFile(String ftpHost, int ftpPort, String ftpPath, String localPath, String fileName, String targetFileName) {

        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient(ftpHost, ftpPort);
            ftpClient.changeWorkingDirectory(ftpPath);

            String f_ame = new String(fileName.getBytes("GBK"), FTP.DEFAULT_CONTROL_ENCODING);	//编码文件格式,解决中文文件名
            File localFile = new File(localPath + File.separatorChar + targetFileName);
            OutputStream os = new FileOutputStream(localFile);
            ftpClient.retrieveFile(f_ame, os);
            os.close();
            ftpClient.logout();

        } catch (FileNotFoundException e) {
            logger.error("没有找到" + ftpPath + "文件");
            e.printStackTrace();
        } catch (SocketException e) {
            logger.error("连接FTP失败.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("文件读取错误。");
            e.printStackTrace();
        }
    }
}

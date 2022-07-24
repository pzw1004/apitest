package com.example.apitest.utils;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @Author 宋宗垚
 * @Date 2019/8/14 10:32
 * @Description 用于连接FTP服务器，下载文件的工具类
 */
public class FTPTool {

    /**
     * 通过ip，端口号，用户名和密码，获取FTP连接
     * @return 返回一个FtpClient形式的ftp连接对象
     */
    public static FtpClient getFtpClent(){

        EnvironmentPath environmentPath = EnvironmentPath.getInstance();
        String ip = environmentPath.getFtpServerIP();
        Integer port = environmentPath.getFtpServerPort();
        String userName = environmentPath.getFtpServerUserName();
        String passWord = environmentPath.getFtpServerPassWord();
        FtpClient ftpClient = null;
        try {
            SocketAddress address = new InetSocketAddress(ip,port);
            ftpClient = FtpClient.create();
            ftpClient.connect(address);
            ftpClient.login(userName,passWord.toCharArray());
            ftpClient.setBinaryType();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FtpProtocolException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }

    /**
     * 通过ftp连接将 remotePath 的文件下载到本地的 localPath 路径上
     * @param remotePath ftp服务器上远程文件的路径（是相对于ftp共享文件夹的相对路径）
     * @param localPath 想要下载到本地的本地文件路径
     * @param ftp ftp连接对象
     * @return 返回下载是否成功的boolean变量
     */
    public static boolean downLoad(String remotePath,String localPath,FtpClient ftp){
        if (ftp==null || localPath == null || remotePath == null){
            return false;
        }
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = ftp.getFileStream(remotePath);
            br = new BufferedReader(new InputStreamReader(is));
            int index;
            byte[] bytes = new byte[1024];
            File localFile = new File(localPath);
            FileOutputStream downloadFile = new FileOutputStream(localFile);
            while ((index = is.read(bytes))!=-1){
                downloadFile.write(bytes,0,index);
                downloadFile.flush();
            }
            downloadFile.close();
            br.close();
            is.close();
        } catch (FtpProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

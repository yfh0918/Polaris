package com.polaris.comm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author:Winning
 *
 * Description:
 *
 */
public class NetUtils  {
	private static LogUtil logger = LogUtil.getInstance(NetUtils.class);
    private static volatile String LOCAL_MAC = null;
    private static final String DEFAULT_MAC = "E0-94-67-CE-04-60";
    public static final String LOCALHOST = "127.0.0.1";
    /**
     * 获取本机的Mac地址
     *
     * @return 获取本机的Mac地址
     */
    public static String getLocalMac() {
    	if (LOCAL_MAC != null) {
    		return LOCAL_MAC;
    	}
    	try {
    		LOCAL_MAC = getLocalMac0();
		} catch (SocketException e) {
			LOCAL_MAC = null;
		} catch (UnknownHostException e) {
			LOCAL_MAC = null;
		}
    	return LOCAL_MAC;
    }
    private static String getLocalMac0() throws SocketException, UnknownHostException {

		//windows下获取mac地址
		StringBuffer sb = new StringBuffer("");
    	try {
    		InetAddress ia = InetAddress.getLocalHost();
    		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
    		if (mac != null && mac.length > 0) {
    			for(int i=0; i<mac.length; i++) {
    				if(i!=0) {
    					sb.append("-");
    				}

    				//字节转换为整数
    				int temp = mac[i]&0xff;
    				String str = Integer.toHexString(temp);
    				if(str.length()==1) {
    					sb.append("0"+str);
    				}else {
    					sb.append(str);
    				}
    			}
    		}
    	} catch (Exception ex) {
    	}
		
		//unix下获取mac地址
		if(StringUtil.isEmpty(sb.toString())) {
			String result = getUnixMACAddress();
			if (StringUtil.isNotEmpty(result)) {
				return result;
			}
		} else {
			return sb.toString().toUpperCase();
		}
		
		//返回默认值
		return DEFAULT_MAC;
	}
	
	//unix下的mac地址
	private static String getUnixMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {


			//mac os下的命令 一般取ether 作为本地主网卡 显示信息中包含有MAC地址信息
			process = Runtime.getRuntime().exec("ifconfig");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				index = line.toLowerCase().indexOf("ether");
				if (index >= 0) {
					mac = line.substring(index + "ether".length() + 1).trim();
					index = mac.toLowerCase().indexOf("hwaddr");// 寻找标示字符串[hwaddr]
					if (index >= 0) {// 找到了
						mac = mac.substring(index + "hwaddr".length() + 1).trim();// 取出mac地址并去除2边空格
						return mac.toUpperCase();
					}
					break;
				}
			}

        } catch (IOException e) {
        	logger.error(e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
				logger.error(e1);
            }
            bufferedReader = null;
            process = null;
        }
		return null;
	}
}

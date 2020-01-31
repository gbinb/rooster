package cn.fetosoft.rooster.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author guobingbing
 * @create 2020/1/28 16:55
 */
public class NetUtil {

	/**
	 * 获取本机IP地址
	 * @return
	 */
	public static List<String> getLocalIP() {
		List<String> hosts = new ArrayList<>();
		try{
			Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()){
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				String name = netInterface.getDisplayName().toLowerCase();
				if(!name.contains("virtual")) {
					Enumeration addresses = netInterface.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress ip = (InetAddress) addresses.nextElement();
						if (ip != null && ip instanceof Inet4Address) {
							String localHost = ip.getHostAddress();
							if (!localHost.trim().startsWith("127")) {
								hosts.add(localHost);
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return hosts;
	}

	/**
	 * 将ip转换成long型数值
	 * @author guobingbing
	 * @date 2020/1/28 18:58
	 * @param ip
	 * @return long
	 */
	public static long ipToLong(String ip){
		long iplong = 0;
		String[] ipArr = ip.split("\\.");
		for(int i=0; i<ipArr.length; i++){
			long tmp = Long.parseLong(ipArr[i]);
			iplong += tmp << ((ipArr.length-1-i)*8);
		}
		return iplong;
	}

	/**
	 * 将long型数值转换成ip串
	 * @author guobingbing
	 * @date 2020/1/28 18:58
	 * @param ip
	 * @return java.lang.String
	 */
	public static String longToIp(long ip){
		StringBuilder sb = new StringBuilder();
		sb.append((ip >> 24) & 0xFF).append(".");
		sb.append((ip >> 16) & 0xFF).append(".");
		sb.append((ip >> 8) & 0xFF).append(".");
		sb.append(ip & 0xFF);
		return sb.toString();
	}

	public static void main(String[] args) {
		List<String> list = getLocalIP();
		for(String ip : list){
			System.out.println(ip);
		}

		System.out.println(ipToLong(list.get(0)));
		System.out.println(longToIp(3232235781L));
	}
}

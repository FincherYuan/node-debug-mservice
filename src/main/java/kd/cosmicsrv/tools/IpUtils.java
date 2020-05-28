package kd.cosmicsrv.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import kd.bos.dataentity.utils.StringUtils;
import kd.bos.script.annotations.KSMethod;
import kd.bos.script.annotations.KSObject;

@KSObject
public class IpUtils {
	public static final String ipBlacklistPattern = "172.19.0.0/16, 172.17.0.0/16, 172.24.0.0/16, 192.168.7.0/24, 192.168.61.0/24, 192.168.62.0/24, 192.168.63.0/24";

	/**
	 * ip黑名单校验,格式172.19.0.0/16,172.17.0.0/16
	 *
	 * @param ip
	 * @param ipPatterns
	 * @return
	 * @throws SocketException
	 */
	@KSMethod
	public static boolean isIpBlacklistByPatterns(String ip, String ipPatterns) {
		if (StringUtils.isBlank(ip) || StringUtils.isBlank(ipPatterns)) {
			return true;
		}
		// 如果是本地调用，直接返回false
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress inetAddress;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					inetAddress = addresses.nextElement();
					if (inetAddress.getHostAddress().equals(ip)) {
						return false;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		String[] patternList = ipPatterns.split(",");

		for (String pattern : patternList) {
			if ("".equals(pattern.trim())) {
				continue;
			}

			if (isInRangeByPattern(ip, pattern.trim())) {
				return true;
			}
		}

		return false;
	}

	@KSMethod
	public static boolean isInRangeByPattern(String ip, String ipPattern) {
		String[] ips = ip.split("\\.");
		int ipAddr = (Integer.parseInt(ips[0]) << 24) | (Integer.parseInt(ips[1]) << 16)
				| (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
		int type = Integer.parseInt(ipPattern.replaceAll(".*/", ""));
		int mask = 0xFFFFFFFF << (32 - type);
		String cidrIp = ipPattern.replaceAll("/.*", "");
		String[] cidrIps = cidrIp.split("\\.");
		int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24) | (Integer.parseInt(cidrIps[1]) << 16)
				| (Integer.parseInt(cidrIps[2]) << 8) | Integer.parseInt(cidrIps[3]);
		return (ipAddr & mask) == (cidrIpAddr & mask);
	}

	public static void main(String[] args) {
		System.out.println(IpUtils.isIpBlacklistByPatterns("172.19.75.58", IpUtils.ipBlacklistPattern));
	}
}

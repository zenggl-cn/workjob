package com.pak.ai.work.manage;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;

public class NetTelnet {
	private TelnetClient telnet =null;
	private InputStream in;
	private PrintStream out;
	private char prompt = '%';
	private static final String ORIG_CODEC = "ISO8859-1";
	private static final String TRANSLATE_CODEC = "GBK";

	// ��ͨ�û�����
	public NetTelnet(String ip, int port, String user, String password) {
		try {
			telnet=new TelnetClient("VT100");
			telnet.connect(ip, port);
			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());
			// ����root�û����ý�����
			this.prompt = user.equals("root") ? '#' : '%';
			login(user, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** * ��¼ * * @param user * @param password */
	public void login(String user, String password) {
		readUntil("ogin:");
		write(user);
		readUntil("assword:");
		write(password);
		readUntil(prompt + " ");
	}

	/** * ��ȡ������� * * @param pattern * @return */
	public String readUntil(String str) {
		char last = str.charAt(str.length() - 1);
		String[] ss;
		StringBuffer sb = new StringBuffer();
		try {
			char c;
			int code = -1;
			boolean ansiControl = false;
			boolean start = true;
			while ((code = (in.read())) != -1) {
				c = (char) code;
				if (c == '\033') {// vt100�����붼����\033��ͷ�ġ�
					ansiControl = true;
					int code2 = in.read();
					char cc = (char) code2;
					if (cc == '[' || cc == '(') {
					}
				}
				if (!ansiControl) {
					if (c == '\r') {
						String olds = new String(sb.toString().getBytes(ORIG_CODEC), TRANSLATE_CODEC);
						//System.out.println(olds);
						if (sb.lastIndexOf(str) != -1) {
							break;
						}
						sb.delete(0, sb.length());
					} else if (c == '\n')
						;
					else
						sb.append(c);
					if (sb.lastIndexOf(str) != -1) {
						break;
					}
				}

				if (ansiControl) {
					if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')
							|| c == '"') {
						ansiControl = false;
					}
				}
			}
			System.out.println(new String(sb.toString().getBytes(ORIG_CODEC),TRANSLATE_CODEC));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** * д���� * * @param value */
	public void write(String value) {
		try {
			out.write(value.getBytes());
			out.flush();
			System.out.println(value);
		} catch (Exception e) {
		}
	}

	/** * ��Ŀ�귢�������ַ��� * * @param command * @return */
	public String sendCommand(String command) {
		try {
			write(command);
			return readUntil("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** * �ر����� */
	public void disconnect() {
		try {
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println("����Telnet...");
			String ip = "172.20.51.74";
			int port = 23;
			String user = "chenyg";
			String password = "abc123";
			NetTelnet telnet = new NetTelnet(ip, port, user, password);
			//telnet.sendCommand("export LANG=en");
			String r1 = telnet.sendCommand("cd /export/home/chenyg/scripts");
			String r2 = telnet.sendCommand("pwd");
			String r3 = telnet.sendCommand("sh a.sh");
			System.out.println("��ʾ���");
			System.out.println(r1);
			System.out.println(r2);
			System.out.println(r3);
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
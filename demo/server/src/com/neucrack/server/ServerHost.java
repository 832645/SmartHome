package com.neucrack.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class ServerHost {
	
	private static ArrayList<ServerThead> mScocketList = new ArrayList<ServerThead>();
	
	/*
	 * ����TCPЭ���Socketͨ�ţ�ʵ���û���¼ ��������
	 */
	public static void main(String[] args) {
		try {
			// 1.����һ����������Socket,��ServerSocket,ָ���󶨵Ķ˿ڣ��������˶˿�
			ServerSocket serverSocket = new ServerSocket(8090);
			Socket socket = null;

			// 2.����accept()������ʼ�������ȴ��ͻ��˵�����
			System.out.println("Server Started��waiting for connect ��");
			while (true) {// ѭ�������ȴ��ͻ��˵�����
				socket = serverSocket.accept();// ����accept()������ʼ�������ȴ��ͻ��˵�����
				ServerThead serverThead = new ServerThead(socket);// ����һ���µ��߳���Ӧ�ͻ��˵�����
				serverThead.start();// �����߳�
				mScocketList.add(serverThead);
				showInfo(socket);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void showInfo(Socket socket) {
		System.out.println("����������������" + mScocketList.size());
		InetAddress address = socket.getInetAddress();
		System.out.println("��������IP��ַ��" + address.getHostAddress());
	}
}

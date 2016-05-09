package com.neucrack.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.neucrack.tool.Session;


public class ServerHost {
	
	private static ArrayList<ServerToDeviceThead> mScocketList = new ArrayList<ServerToDeviceThead>();
	
	/*
	 * ����TCPЭ���Socketͨ�ţ�ʵ���û���¼ ��������
	 */
	public static void main(String[] args) {
		
			Session session = new Session();
			session.EnableAutoClear();
			
			//����һ���̼߳����û�������
			 new Thread(){

				@Override
				public void run() {
					try{
						// 1.����һ����������Socket,��ServerSocket,ָ���󶨵Ķ˿ڣ��������˶˿�
						ServerSocket serverSocket = new ServerSocket(8099);
						Socket socket = null;
	
						// 2.����accept()������ʼ�������ȴ��ͻ��˵�����
						System.out.println("Server Started��waiting for User request at port 8099: ");
						while (true) {// ѭ�������ȴ��ͻ��˵�����
							socket = serverSocket.accept();// ����accept()������ʼ�������ȴ��ͻ��˵�����
							ServerToUserThread userThread = new ServerToUserThread(mScocketList,socket);// ����һ���µ��߳���Ӧ�ͻ��˵�����
							userThread.start();// �����߳�
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				 
			 }.start();
			
			//�����豸������
		try {
			// 1.����һ����������Socket,��ServerSocket,ָ���󶨵Ķ˿ڣ��������˶˿�
			ServerSocket serverSocket = new ServerSocket(8090);
			Socket socket = null;

			// 2.����accept()������ʼ�������ȴ��ͻ��˵�����
			System.out.println("Server Started��waiting for devices connect at port 8090:");
			while (true) {// ѭ�������ȴ��ͻ��˵�����
				socket = serverSocket.accept();// ����accept()������ʼ�������ȴ��ͻ��˵�����
				ServerToDeviceThead serverThread = new ServerToDeviceThead(socket);// ����һ���µ��߳���Ӧ�ͻ��˵�����
				serverThread.start();// �����߳�
				mScocketList.add(serverThread);
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

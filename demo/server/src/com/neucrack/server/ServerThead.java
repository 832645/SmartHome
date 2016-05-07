package com.neucrack.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Time;

import org.omg.CORBA.DATA_CONVERSION;

import com.neucrack.communication.ToDevices;
import com.neucrack.devices.*;
import com.neucrack.tool.CRC;
import com.neucrack.tool.Date_TimeStamp;

/*
 * ���������̴߳�����
 */
public class ServerThead extends Thread {

	protected Socket socket = null;
	protected String recvData = null;
	protected DataInputStream mInStream=null;
	protected DataOutputStream mOutStream=null;
	
	public SignInfo mSignInfo = new SignInfo();
	
	public ToDevices mToDevices;
	
	private boolean mIsSginIn=false;

	public ServerThead(Socket socket) {
		this.socket = socket;
	}

	// �߳�ִ�еĲ�������Ӧ�ͻ��˵�����
	public void run() {
		//��¼ʱ���
		long startTime = Date_TimeStamp.timeStamp();
		// ��ȡ������������ȡ�ͻ�����Ϣ
		if(!getIn_Out_stream())
		{
			System.out.println("��ȡ�������������");
			Close();
			return;
		}
		
		long keepAliveTime = Date_TimeStamp.timeStamp();
		while(true){
			
			//��¼���
			if(!mIsSginIn){
				try {
					socket.setSoTimeout(20000);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//�ȴ���¼����
				if(mToDevices.WaitSignIn(mSignInfo)){
					System.out.println("�豸��¼���󵽴�豸�ţ�"+mSignInfo.device+"�û�����"+mSignInfo.userName);
					//�û����豸��֤
					if(!SignInVerify()){//��֤ʧ��
						System.out.println("��¼��֤ʧ�ܣ�������");
						break;
					}
					System.out.println("�豸��¼�ɹ�!!");
					mIsSginIn = true;
					try {
						socket.setSoTimeout(5000);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				if(Date_TimeStamp.timeStamp()-startTime>20000){//20s��û�е�¼��ر�����
					System.out.println("�ȴ���¼����ʱ");
					Close();
				}
				continue;
			}
			
			//�������ְ�,5����һ��
			if(Date_TimeStamp.timeStamp()-keepAliveTime>=300000){
				keepAliveTime = Date_TimeStamp.timeStamp();
				if(!mToDevices.KeepAlive(mSignInfo.device)){
					System.out.println("��������ʧ��");
					if(!mToDevices.KeepAlive(mSignInfo.device)){
						System.out.println("�ڶ�����������ʧ��!ʧȥ���ӣ�����������");
						Close();
						break;
					}
				}
				System.out.println("��·���ֳɹ���");
			}
			
			
		}
	}



	private boolean getIn_Out_stream(){
		try {
			mInStream = new DataInputStream(socket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// ��ȡ�����,��Ӧ�ͻ�������
		try {
			mOutStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		mToDevices = new ToDevices(mInStream,mOutStream);
		return true;
	}

	
	//��֤�豸���û�
	private boolean SignInVerify() {
		//�������ݿ����������Լ��豸��Ӧ�Ƿ���ȷ
		return true;
	}
	



	protected void Close() {
		try {
			if(mOutStream!=null)
				mOutStream.close();
			if(mInStream!=null)
				mInStream.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Abnormal , Socket : �ر�socket��Դ�쳣 ��");
			e.printStackTrace();
		}
	}

	 public static byte[] charToByte(char[] c) {
		 	byte[] b=new byte[c.length];
		 	for(int i=0;i<c.length;++i){
		 		b[i]=(byte) (c[i]&0x00ff);
		 	}
	        return b;
	    }


	 public static void PrintBytes(byte[] c,int size){
		 for(int i=0;i<size;++i)
				System.out.print(Integer.toHexString(c[i]&0xff)+" ");
			System.out.println();
	 }

}

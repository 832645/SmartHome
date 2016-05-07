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
import java.sql.Time;

import org.omg.CORBA.DATA_CONVERSION;

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
	
	public byte mUserName[]= new byte[11];
	public byte[] mDeviceNumber=new byte[6];
	public byte[] mUserPasswd  = new byte[16];
	public byte mDeviceType;
	
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
			close();
			return;
		}
		
		long keepAliveTime = Date_TimeStamp.timeStamp();
		while(true){
			
			//��¼���
			if(!mIsSginIn){
				//�ȴ���¼����
				if(WaitForSignIn()){
					System.out.println("�豸��¼���󵽴�豸�ţ�"+Byte6ToMac(mDeviceNumber)+"�û�����"+BytesToString(mUserName,11));
					//�û����豸��֤
					if(!SignInVerify()){//��֤ʧ��
						System.out.println("��¼��֤ʧ�ܣ�������");
						break;
					}
					System.out.println("�豸��¼�ɹ�!!");
					mIsSginIn = true;
					break;
				}
				System.out.println("fail");
				if(Date_TimeStamp.timeStamp()-startTime>20000){//20s��û�е�¼��ر�����
					System.out.println("�ȴ���¼����ʱ");
					close();
				}
				continue;
			}
			
			//�������ְ�
			if(Date_TimeStamp.timeStamp()-keepAliveTime==5){
				keepAliveTime = Date_TimeStamp.timeStamp();
				if(!KeepAlive()){
					System.out.println("��������ʧ��");
					if(!KeepAlive()){
						System.out.println("�ڶ�����������ʧ��!ʧȥ���ӣ�����������");
						break;
					}
				}
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
		return true;
	}
	//�ȴ��豸��¼
	private boolean WaitForSignIn(){
		byte dataToRead[] = new byte[1024] ;
		try {
			int size = mInStream.read(dataToRead);
			if(size>0){
				//֡ͷ
				if(((short)dataToRead[0]&0xff)!=0xab || ((short)dataToRead[1]&0xff)!=0xac)
					return false;
				int datalength = (short)dataToRead[17]<<8|dataToRead[18];
				//CRCУ��
				int parity = CRC.CRC16Calculate(dataToRead,datalength+19);
				int parity2 = (dataToRead[19+datalength]<<8|dataToRead[20+datalength])&0xffff;
				if(parity != parity2)
					return false;
				//У��ɹ�
				if(dataToRead[4]==1){
					System.arraycopy(dataToRead, 5, mDeviceNumber, 0, 6);
					System.arraycopy(dataToRead, 19, mUserName, 0, 11);
					System.arraycopy(dataToRead, 30, mUserPasswd, 0, 16);
					mDeviceType = (byte) dataToRead[46];
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	
	//��֤�豸���û�
	private boolean SignInVerify() {
		//�������ݿ����������Լ��豸��Ӧ�Ƿ���ȷ
		return true;
	}
	
	//��·����
	private boolean KeepAlive(){
		return true;
	}

	public void deal() {

	}

	protected void write() {
//		try {
//			
//			pw.write(backMsg);
//			pw.flush();// ����flush()�������������
//			System.out.println("Info �� Socket send :" + backMsg);
//		} catch (Exception e) {
//			System.out.println("Abnormal , Socket : д�������쳣 ��");
//			e.printStackTrace();
//		}

	}

	protected void close() {
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
	 public static String Byte6ToMac(byte[] c) {
	        return Integer.toHexString(((int)c[0]&0xff))+":"+Integer.toHexString(((int)c[1]&0xff))+":"+Integer.toHexString(((int)c[2]&0xff))+":"
	        		+((short)c[3]&0xff)+":"+Integer.toHexString(((int)c[4]&0xff))+":"+Integer.toHexString(((int)c[5]&0xff)); 
	    }
	 public static String BytesToString(byte[] b,int size){
		 PrintBytes(b,size);
		 String str= "";
		 for(int i=0;i<size;++i){
			 str+=b[i]-'0';
		 }
		 return str;
	 }
	 public static void PrintBytes(byte[] c,int size){
		 for(int i=0;i<size;++i)
				System.out.print(Integer.toHexString(c[i]&0xff)+" ");
			System.out.println();
	 }

}

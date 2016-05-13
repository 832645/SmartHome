/**
 * ���豸ͨ��
 */
package com.neucrack.communication;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.neucrack.devices.*;
import com.neucrack.tool.CRC;
import com.neucrack.tool.StringRelated;

public class ToDevices {
	private DataInputStream mInStream=null;
	private DataOutputStream mOutStream=null;
	
	public ToDevices(DataInputStream inStream,DataOutputStream outStream){
		mInStream = inStream;
		mOutStream = outStream;
	}
	
	public boolean WaitSignIn(SignInfo signIn){
		byte[] mDeviceNumber = new byte[6];
		byte[] mUserName = new byte[11];
		byte[] mUserPasswd = new byte[16];
		
		byte dataToRead[] = new byte[512] ;
		try {
			int size = mInStream.read(dataToRead);
			if(size>0){
				//֡ͷ
				if(((short)dataToRead[0]&0xff)!=0xab || ((short)dataToRead[1]&0xff)!=0xac)
					return false;
				int datalength = (short)dataToRead[17]<<8|dataToRead[18];
				//CRCУ��
				int parity = CRC.CRC16Calculate(dataToRead,datalength+19);
				int parity2 = ((int)dataToRead[19+datalength]<<8&0xff00|dataToRead[20+datalength]&0xff)&0xffff;
				if(parity != parity2)
					return false;
				//У��ɹ�
				if(dataToRead[4]!=1)//�����������¼
					return false;
				System.arraycopy(dataToRead, 5, mDeviceNumber, 0, 6);
				System.arraycopy(dataToRead, 19, mUserName, 0, 11);
				System.arraycopy(dataToRead, 30, mUserPasswd, 0, 16);
				signIn.device = StringRelated.Byte6ToMac(mDeviceNumber);
				signIn.userName = StringRelated.BytesToString(mUserName,11);
				signIn.userPasswd = StringRelated.MD5_32_BytesToString(mUserPasswd);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	//��·����������
	public boolean KeepAlive(String device){
		byte[] data = new byte[50];
		byte[] deviceNumber = StringRelated.MacToBytes(device);
		data[0] = (byte) 0xab;
		data[1] = (byte) 0xac;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x10;
		data[4] = (byte) 0x01;
		System.arraycopy(deviceNumber, 0, data, 5, 6);
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x01;
		data[19] = (byte) 0x01;
		long crc16 = CRC.CRC16Calculate(data, 20);
		data[20] = (byte) (crc16>>8&0xff);
		data[21] = (byte) (crc16&0xff);
		try {
			mOutStream.write(data, 0, 22);
			int size = mInStream.read(data);
			if(size>0){
				if(!VerifyFrame(data))
					return false;
				//У��ɹ�
				if(data[4]!=2)//��������Ӧ��·����
					return false;
				if(data[19]==1){//�豸
					
				}
				else if(data[20]==2){//�ֻ�
					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//�ƹ����
	public boolean LightControl(String device,boolean isOn){
		byte[] data = new byte[50];
		byte[] deviceNumber = StringRelated.MacToBytes(device);
		data[0] = (byte) 0xab;
		data[1] = (byte) 0xac;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x01;
		data[4] = (byte) 0x01;
		System.arraycopy(deviceNumber, 0, data, 5, 6);
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x04;
		data[19] = (byte)(isOn?1:0);
		data[20] = (byte) 0x00;
		data[21] = (byte) 0x01;
		data[22] = (byte) 0x01;//�ƹ�
		long crc16 = CRC.CRC16Calculate(data, 23);
		data[23] = (byte) (crc16>>8&0xff);
		data[24] = (byte) (crc16&0xff);
		try {
			mOutStream.write(data, 0, 25);;
			int size = mInStream.read(data);
			if(size>0){
				if(!VerifyFrame(data))
					return false;
				//У��ɹ�
				if(data[4]!=2 || data[22] != 0x01)//��������Ӧ��Ϣ���߲��ǵƹ���Ϣ
					return false;
				if(data[19]!=(isOn?1:0))//�Ƚ��Ƿ���Ƴɹ�
					return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//��������
	public boolean CurtainControl(String device,boolean isOn){
		byte[] data = new byte[50];
		byte[] deviceNumber = StringRelated.MacToBytes(device);
		data[0] = (byte) 0xab;
		data[1] = (byte) 0xac;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x01;
		data[4] = (byte) 0x01;
		System.arraycopy(deviceNumber, 0, data, 5, 6);
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x04;
		data[19] = (byte)(isOn?1:0);
		data[20] = (byte) 0x00;
		data[21] = (byte) 0x01;
		data[22] = (byte) 0x02;//����
		long crc16 = CRC.CRC16Calculate(data, 23);
		data[23] = (byte) (crc16>>8&0xff);
		data[24] = (byte) (crc16&0xff);
		try {
			mOutStream.write(data, 0, 25);;
			int size = mInStream.read(data);
			if(size>0){
				if(!VerifyFrame(data))
					return false;
				//У��ɹ�
				if(data[4]!=2 || data[22] != 0x02)//��������Ӧ��Ϣ���߲��Ǵ�����Ϣ
					return false;
				if(data[19]!=(isOn?1:0))//�Ƚ��Ƿ���Ƴɹ�
					return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//��������
	public boolean DoorControl(String device,boolean isOn){
		byte[] data = new byte[50];
		byte[] deviceNumber = StringRelated.MacToBytes(device);
		data[0] = (byte) 0xab;
		data[1] = (byte) 0xac;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x01;
		data[4] = (byte) 0x01;
		System.arraycopy(deviceNumber, 0, data, 5, 6);
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x04;
		data[19] = (byte)(isOn?1:0);
		data[20] = (byte) 0x00;
		data[21] = (byte) 0x01;
		data[22] = (byte) 0x03;//����
		long crc16 = CRC.CRC16Calculate(data, 23);
		data[23] = (byte) (crc16>>8&0xff);
		data[24] = (byte) (crc16&0xff);
		try {
			mOutStream.write(data, 0, 25);;
			int size = mInStream.read(data);
			if(size>0){
				if(!VerifyFrame(data))
					return false;
				//У��ɹ�
				if(data[4]!=2 || data[22] != 0x03)//��������Ӧ��Ϣ���߲��Ǵ�����Ϣ
					return false;
				if(data[19]!=(isOn?1:0))//�Ƚ��Ƿ���Ƴɹ�
					return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	//��ȡ�ƹ�״̬
	public boolean GetLightStatus(String device,Light light){
		byte[] data = new byte[50];
		byte[] deviceNumber = StringRelated.MacToBytes(device);
		data[0] = (byte) 0xab;
		data[1] = (byte) 0xac;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x01;
		data[4] = (byte) 0x03;//ѯ��
		System.arraycopy(deviceNumber, 0, data, 5, 6);
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x04;
		data[19] = (byte) 0;
		data[20] = (byte) 0x00;
		data[21] = (byte) 0x01;
		data[22] = (byte) 0x01;//�ƹ�
		long crc16 = CRC.CRC16Calculate(data, 23);
		data[23] = (byte) (crc16>>8&0xff);
		data[24] = (byte) (crc16&0xff);
		try {
			mOutStream.write(data, 0, 25);;
			int size = mInStream.read(data);
			if(size>0){
				if(!VerifyFrame(data))
					return false;
				//У��ɹ�
				if(data[4]!=2 || data[22] != 0x01)//��������Ӧ��Ϣ���߲��ǵƹ���Ϣ
					return false;
				if(data[19]==1)//�ƹ�״̬
					light.isOn = true;
				else
					light.isOn = false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//��ȡ����״̬
	public boolean GetCurtainStatus(String device,Curtain curtain){
		byte[] data = new byte[50];
		byte[] deviceNumber = StringRelated.MacToBytes(device);
		data[0] = (byte) 0xab;
		data[1] = (byte) 0xac;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x01;
		data[4] = (byte) 0x03;//ѯ��
		System.arraycopy(deviceNumber, 0, data, 5, 6);
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x04;
		data[19] = (byte) 0;
		data[20] = (byte) 0x00;
		data[21] = (byte) 0x01;
		data[22] = (byte) 0x02;//����
		long crc16 = CRC.CRC16Calculate(data, 23);
		data[23] = (byte) (crc16>>8&0xff);
		data[24] = (byte) (crc16&0xff);
		try {
			mOutStream.write(data, 0, 25);;
			int size = mInStream.read(data);
			if(size>0){
				if(!VerifyFrame(data))
					return false;
				//У��ɹ�
				if(data[4]!=2 || data[22] != 0x02)//��������Ӧ��Ϣ���߲��Ǵ�����Ϣ
					return false;
				if(data[19]==1)//�ƹ�״̬
					curtain.isOn = true;
				else
					curtain.isOn = false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//��ȡ������״̬
	public boolean GetDoorStatus(String device,Door door){
		byte[] data = new byte[50];
		byte[] deviceNumber = StringRelated.MacToBytes(device);
		data[0] = (byte) 0xab;
		data[1] = (byte) 0xac;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x01;
		data[4] = (byte) 0x03;//ѯ��
		System.arraycopy(deviceNumber, 0, data, 5, 6);
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x04;
		data[19] = (byte) 0;
		data[20] = (byte) 0x00;
		data[21] = (byte) 0x01;
		data[22] = (byte) 0x03;//����
		long crc16 = CRC.CRC16Calculate(data, 23);
		data[23] = (byte) (crc16>>8&0xff);
		data[24] = (byte) (crc16&0xff);
		try {
			mOutStream.write(data, 0, 25);;
			int size = mInStream.read(data);
			if(size>0){
				if(!VerifyFrame(data))
					return false;
				//У��ɹ�
				if(data[4]!=2 || data[22] != 0x03)//��������Ӧ��Ϣ���߲��Ǵ�����Ϣ
					return false;
				if(data[19]==1)//�ƹ�״̬
					door.isOn = true;
				else
					door.isOn = false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//��ȡ������״̬
	public boolean GetSensorData(String device,long SensorName,Sensor sensor){
		byte[] data = new byte[50];
		byte[] deviceNumber = StringRelated.MacToBytes(device);
		data[0] = (byte) 0xab;
		data[1] = (byte) 0xac;
		data[2] = (byte) 0x00;
		data[3] = (byte) 0x02;
		data[4] = (byte) 0x03;//ѯ��
		System.arraycopy(deviceNumber, 0, data, 5, 6);
		data[17] = (byte) 0x00;
		data[18] = (byte) 0x08;
		data[19] = (byte) (SensorName>>24&0xff);
		data[20] = (byte) (SensorName>>16&0xff);
		data[21] = (byte) (SensorName>>8&0xff);
		data[22] = (byte) (SensorName&0xff);
		data[23] = 01;
		data[24] = 00;
		data[25] = 00;
		data[26] = 00;
		
		long crc16 = CRC.CRC16Calculate(data, 27);
		data[27] = (byte) (crc16>>8&0xff);
		data[28] = (byte) (crc16&0xff);
		try {
			mOutStream.write(data, 0, 29);;
			int size = mInStream.read(data);
			if(size>0){
				if(!VerifyFrame(data))
					return false;
				//У��ɹ�
				if(data[4]!=2 || 
						data[19] != (byte) (SensorName>>24&0xff)||
						data[20] != (byte) (SensorName>>16&0xff)||
						data[21] != (byte) (SensorName>>8&0xff)||
						data[22] != (byte) (SensorName&0xff)
						)//��������Ӧ��Ϣ���߲��Ǹô�������Ϣ
					return false;
				sensor.value = (int)(data[24]&0xff);//��������ֵ
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean VerifyFrame(byte[] data){
		//֡ͷ
		if(((short)data[0]&0xff)!=0xab || ((short)data[1]&0xff)!=0xac)
			return false;
		int datalength = (short)data[17]<<8|data[18];
		//CRCУ��
		int parity = CRC.CRC16Calculate(data,datalength+19);
		int parity2 = (data[19+datalength]&0xff)<<8|(data[20+datalength]&0xff);
		if(parity != parity2)
			return false;
		return true;
	}

}

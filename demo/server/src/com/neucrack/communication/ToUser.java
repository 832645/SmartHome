/**
 * ���ƶ��ͻ���ͨ��
 * @author neucrack
 *
 */
package com.neucrack.communication;

import java.awt.color.ICC_ColorSpace;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.neucrack.DAO.DAOHelper;
import com.neucrack.devices.Curtain;
import com.neucrack.devices.Door;
import com.neucrack.devices.Light;
import com.neucrack.devices.Sensor;
import com.neucrack.devices.SignInfo;
import com.neucrack.entity.DeviceSwitch;
import com.neucrack.entity.User;
import com.neucrack.tool.CRC;
import com.neucrack.tool.Date_TimeStamp;
import com.neucrack.tool.Encrypt;
import com.neucrack.tool.Session;
import com.neucrack.tool.StringRelated;

public class ToUser {

	private DataInputStream mInStream=null;
	private DataOutputStream mOutStream=null;
	
	public ToUser(DataInputStream inStream,DataOutputStream outStream){
		mInStream = inStream;
		mOutStream = outStream;
	}


	public boolean VerifyFrame(byte[] dataToRead){
		//֡ͷ
		if(((short)dataToRead[0]&0xff)!=0xab || ((short)dataToRead[1]&0xff)!=0xac)
			return false;
		int datalength = (short)dataToRead[17]<<8|dataToRead[18];
		//CRCУ��
		int parity = CRC.CRC16Calculate(dataToRead,datalength+19);
		int parity2 = (dataToRead[19+datalength]<<8|dataToRead[20+datalength])&0xffff;
		if(parity != parity2)
			return false;
		return true;
	}
	public boolean CheckIfSignedIn(String session){
		//��֤�û�����sessionֵ
		if(null!=Session.getAttribute(session))
		{
			return false;
		}
		return true;
	}
	
	public int DealSignIn(User user){
		DAOHelper dao = new DAOHelper();
		int result = dao.VerifyUser(user);
		if(result <= 0){//��֤ʧ��
			System.out.println("��֤�û�ʧ�ܣ�������룺"+result);
		}		
		//�����+ʱ�����Ϊsession key
		long randomVal = (long)(1+Math.random()*(60000-1+1));
		String sessionKey =""+randomVal+Date_TimeStamp.timeStamp();
		sessionKey = Encrypt.md5(sessionKey);//��session key����MD5����
		Session.setAttribute(sessionKey, user);
		user.setSession(sessionKey);
		//��Ӧ�û��ĵ�¼��Ϣ
		SendToUserSignInResult(user,result);
		return result;
	}
	
	public int DealSignUp(User user){
		DAOHelper dao = new DAOHelper();
		int result = dao.QueryUser(user);
		if(result != 0){//�����û���������Ѿ�����
			System.out.println("�����û�����������룺"+result);
			if(result>0)
				result=0;
		}
		else{
			result = dao.AddUser(user);
		}
		//��Ӧ�û��ĵ�¼��Ϣ
		SendToUserSignUpResult(user,result);
		return result;
	}
	
	
	
	
	
	private boolean SendToUserSignInResult(User user,int data){
		byte d[] ={ (byte) (data&0xff)};
		return SendToUser((short)0x0011,(byte)0x02,user.getSession(),(short)31,d);
	}
	
	private boolean SendToUserSignUpResult(User user,int data) {
		byte d[] ={ (byte) (data&0xff)};
		return SendToUser((short)0x0012,(byte)0x02,user.getSession(),(short)31,d);
	}
	
	/**
	 * ��������֡���û��ͻ���
	 * @param type
	 * @param operationType
	 * @param session
	 * @param dataLength
	 * @param data
	 * @return
	 */
	private boolean  SendToUser(Short type,byte operationType,String session,Short dataLength,byte[] data) {
		byte[] dataToWrite = new byte[31+dataLength];
		byte[] sessionBytes = session.getBytes();
		dataToWrite[0] = (byte) 0xab;
		dataToWrite[1] = (byte) 0xac;
		dataToWrite[2] = (byte) (type>>8&0xff);
		dataToWrite[3] = (byte) (type&0xff);
		dataToWrite[4] = (byte) operationType;
		System.arraycopy(sessionBytes, 0, dataToWrite, 5, 16);
		dataToWrite[27] = (byte) (dataLength>>8&0xff);
		dataToWrite[28] = (byte) (dataLength&0xff);
		System.arraycopy(data, 0, dataToWrite, 29, dataLength);
		long crc16 = CRC.CRC16Calculate(dataToWrite, dataLength+29);
		dataToWrite[29+dataLength] = (byte) (crc16>>8&0xff);
		dataToWrite[30+dataLength] = (byte) (crc16&0xff);
		try {
			mOutStream.write(dataToWrite, 0, 31+dataLength); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		};
		return true;
	}


	public boolean ControlSwitch(ToDevices toDevice,User user,String device, long switchName, boolean isOn) {
		//��֤�豸�Ƿ����ڸ��û�
		DAOHelper daoHelper = new DAOHelper();
		if(!daoHelper.QueryOwnerOfDevice(device).equals(user.getmName()))
			return false;
		//��֤ͨ����ʹ��ToDevices���п���
		boolean result=false;
		if(switchName == 1){
			result = toDevice.LightControl(device, isOn);
		}
		else if(switchName ==2 ){
			result = toDevice.CurtainControl(device, isOn);
		}
		else if(switchName ==3 ){
			result = toDevice.DoorControl(device, isOn);
		}
		if(!result)
			return false;
		byte deviceBytes[] = StringRelated.MacToBytes(device);
		byte[] data = {0,0,0,0,0,0,0,0,0,1,(byte) (isOn?1:0),0,0};
		System.arraycopy(deviceBytes, 0, data, 0, 6);
		return SendToUser((short)0x0001, (byte)2, user.getSession(), (short)13, data);
	}


	public boolean QuerySwitch(ToDevices toDevice,User user, String device, long switchName) {
		//��֤�豸�Ƿ����ڸ��û�
		DAOHelper daoHelper = new DAOHelper();
		if(!daoHelper.QueryOwnerOfDevice(device).equals(user.getmName()))
			return false;
		//��֤ͨ����ʹ��ToDevices����״̬��ѯ
		boolean result=false;
		boolean isOn = false;
		if(switchName == 1){
			Light light = new Light();
			result = toDevice.GetLightStatus(device, light);
			isOn = light.isOn;
		}
		else if(switchName ==2 ){
			Curtain curtain = new Curtain();
			result = toDevice.GetCurtainStatus(device, curtain);
			isOn = curtain.isOn;
		}
		else if(switchName ==3 ){
			Door door = new Door();
			result = toDevice.GetDoorStatus(device, door);
			isOn = door.isOn;
		}
		if(!result)
			return false;
		byte deviceBytes[] = StringRelated.MacToBytes(device);
		byte[] data = {0,0,0,0,0,0,0,0,0,1,(byte) (isOn?1:0),0,0};
		System.arraycopy(deviceBytes, 0, data, 0, 6);
		return SendToUser((short)0x0001, (byte)2, user.getSession(), (short)13, data);
	}


	public boolean QuerySensor(ToDevices toDevice,User user, String device, long switchName) {
		//��֤�豸�Ƿ����ڸ��û�
		DAOHelper daoHelper = new DAOHelper();
		if(!daoHelper.QueryOwnerOfDevice(device).equals(user.getmName()))
			return false;
		//��֤ͨ����ʹ��ToDevices����״̬��ѯ
		boolean result=false;
		int value = 0;
		if(switchName == 1){//�⴫����
			Sensor sensor = new Sensor();			
			result = toDevice.GetSensorData(device, switchName, sensor);
			value = sensor.value;
		}
		if(!result)
			return false;
		byte deviceBytes[] = StringRelated.MacToBytes(device);
		byte[] data = {0,0,0,0,0,0,0,0,0,1,1,(byte)value,0,0};
		System.arraycopy(deviceBytes, 0, data, 0, 6);
		return SendToUser((short)0x0001, (byte)2, user.getSession(), (short)14, data);
	}
}

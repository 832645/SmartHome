package com.neucrack.DAO;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neucrack.entity.Device;
import com.neucrack.entity.DeviceDoor;
import com.neucrack.entity.DeviceSensor;
import com.neucrack.entity.DeviceSwitch;
import com.neucrack.entity.User;
import com.neucrack.server.HttpRequest;

public class DAOHelper {
	
	/**
	 * 
	 * @param user Ҫ���ҵ��û���
	 * @return 1:�ɹ�  0��û�и��û�   <0:��������
	 */
	public int QueryUser(User user) {
		String param = "username="+user.getmName()+"&password="+user.getmPasswd();
		JSONObject jsonObject =null;
		try {
			String result = HttpRequest.sendGet(DAOConnectionInfo.mUrl+"login", param);
			if(result == null){
				System.out.println("�û���¼�������ݴ���������ݷ�������Ӧʧ�ܣ��뿴���ص���Ӧ������400��Ϊ��������Ӧʧ�ܣ�������");
				return -1;
			}
			 jsonObject= new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
		try {
			user.setSession((String) jsonObject.get("sessionToken"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
	
	public int AdminSignIn(){
		String param = "username="+DAOConnectionInfo.adminName+"&password="+DAOConnectionInfo.adminPassword;
		JSONObject jsonObject =null;
		try {
			String result = HttpRequest.sendGet(DAOConnectionInfo.mUrl+"login", param);
			if(result == null){
				System.out.println("����Ա��¼�������ݴ���������ݷ�������Ӧʧ�ܣ��뿴���ص���Ӧ������400��Ϊ��������Ӧʧ�ܣ�������");
				return -1;
			}
			 jsonObject= new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
		try {
			DAOConnectionInfo.session = (String) jsonObject.get("sessionToken");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
	/**
	 * ��֤�û��û����������Ƿ���ȷ
	 * @param user Ҫ���ҵ��û���
	 * @return 1:�ɹ�  0��û�и��û�   <0:��������
	 */
	public int VerifyUser(User user) {
		String param = "username="+user.getmName()+"&password="+user.getmPasswd();
		JSONObject jsonObject =null;
		try {
			String result = HttpRequest.sendGet(DAOConnectionInfo.mUrl+"login", param);
			if(result == null){
				System.out.println("��֤�û���Ϣ�������ݴ���������ݷ�������Ӧʧ�ܣ��뿴���ص���Ӧ������400��Ϊ��������Ӧʧ�ܣ�������");
				return -1;
			}
			 jsonObject= new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
		try {
			user.setSession((String) jsonObject.get("sessionToken"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
	
	public int AddUser(User user){
		JSONObject json = new JSONObject();
		try {
			json.put("username",user.getmName());
			json.put("password", user.getmPasswd());
			json.put("mobilePhoneNumber", user.getmName());
			json.put("nickname", user.getmNickName());
			json.put("headImage", user.getmHeadPicture());
		} catch (JSONException e) {
			e.printStackTrace();
			return -2;
		}
		
		String param = json.toString();
		String result = HttpRequest.sendPost(DAOConnectionInfo.mUrl+"users", param);
		if(result==null)
			return -1;
		return 1;
	}
	
	public int DropUser(User user){
		//TODO δд��
		if(QueryUser(user)<=0)
			return -1;
		
		return -1;
	}
	
	public int EditUser(User oldOne,User newOne){
		//TODO δд��
		return -1;
	}
	public String QueryOwnerOfDevice(String deviceName){
		if(AdminSignIn()<=0)
			return null;
		String name=null;
		String param = "where={\"deviceName\":\""+deviceName+"\"}";
		JSONObject jsonObject =null;
		try {
			String result = HttpRequest.sendGet(DAOConnectionInfo.mUrl+"classes/Devices", param);
			if(result == null){
				System.out.println("��ȡ�豸��Ϣ�������ݴ���������ݷ�������Ӧʧ��");
				return null;
			}
			 jsonObject= (JSONObject)(new JSONObject(result)).getJSONArray("results").get(0);
			 if(!jsonObject.get("deviceName").equals(deviceName))
				 return  null;
			 name = (String) jsonObject.get("userName");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return name;
	}
	
	public int AddDevice(Device device,User user){
		if(AdminSignIn()<=0)
			return -1;
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			for(int i=0;i<device.getmSubDevices().size();++i){
				JSONObject json = new JSONObject();
				json.put("deviceType", device.getmSubDevices().get(i).getmType());
				json.put("subDeviceNumber", device.getmSubDevices().get(i).getmNumber());
				json.put("subDeviceNickName", device.getmSubDevices().get(i).getmNickName());
				JSONObject json2 = new JSONObject();
				if( device.getmSubDevices().get(i).getmType() == DeviceSwitch.DEVICE_TYPE){
					json2.put("status", ((DeviceSwitch)device.getmSubDevices().get(i).getmDeviceInfo()).getmStatus() );
				}
				else if(device.getmSubDevices().get(i).getmType() == DeviceSensor.DEVICE_TYPE){
					json2.put("value", ((DeviceSensor)device.getmSubDevices().get(i).getmDeviceInfo()).getmValue());
				}
				else if(device.getmSubDevices().get(i).getmType() == DeviceDoor.DEVICE_TYPE){
					
				}
				json.put("subDeviceData", json2);
				jsonArray.put(json);
			}
			jsonObject.put("deviceName", device.getmName());
			jsonObject.put("nickName", device.getmNickName());
			jsonObject.put("userName", device.getmOwner());
			jsonObject.put("comment", device.getmComment());
			jsonObject.put("data", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
		String param = jsonObject.toString();
		String result = HttpRequest.sendPost(DAOConnectionInfo.mUrl+"classes/Devices", param);
		if(result==null)
			return -1;
		
		return 1;
	}
}

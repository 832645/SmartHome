package com.neucrack.test;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.neucrack.DAO.DAOConnectionInfo;
import com.neucrack.DAO.DAOHelper;
import com.neucrack.communication.ToDevices;
import com.neucrack.devices.Curtain;
import com.neucrack.devices.Light;
import com.neucrack.entity.Device;
import com.neucrack.entity.DeviceSensor;
import com.neucrack.entity.DeviceSwitch;
import com.neucrack.entity.SubDevice;
import com.neucrack.entity.User;
import com.neucrack.server.HttpRequest;
import com.neucrack.tool.CRC;
import com.neucrack.tool.Session;
import com.neucrack.tool.StringRelated;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class Test {

	@org.junit.Test
	public void test() {
		DAOHelper dao = new DAOHelper();

		
		
	//	System.out.println(dao.QueryOwnerOfDevice("1:2:3:4:5:6"));
		
		
		
		
		//��������Ա
		User user = new User("15023490062", "1208077207", "admin", "/assets/pic/headImage/15023490062.png");
		System.out.println(dao.AddUser(user));
		
		
		//��������1
		Device device;
			device = new Device("1:2:3:4:5:6", "15023490062", StringRelated.newString_UTF_8("����1"), StringRelated.newString_UTF_8("����1"),
new SubDevice(DeviceSwitch.DEVICE_TYPE,(long) 1, StringRelated.newString_UTF_8("��"), new DeviceSwitch()),
new SubDevice(DeviceSwitch.DEVICE_TYPE,(long) 2, StringRelated.newString_UTF_8("����"), new DeviceSwitch()),
new SubDevice(DeviceSensor.DEVICE_TYPE,(long) 1, StringRelated.newString_UTF_8("�⴫����"), new DeviceSensor()));
				System.out.println(dao.AddDevice(device, user));
		//��������2
				device = new Device("1:2:3:4:5:7", "15023490062", StringRelated.newString_UTF_8("����2"), StringRelated.newString_UTF_8("����2"),
	new SubDevice(DeviceSwitch.DEVICE_TYPE,(long) 1, StringRelated.newString_UTF_8("��"), new DeviceSwitch()),
	new SubDevice(DeviceSwitch.DEVICE_TYPE,(long) 2, StringRelated.newString_UTF_8("����"), new DeviceSwitch()),
	new SubDevice(DeviceSensor.DEVICE_TYPE,(long) 1, StringRelated.newString_UTF_8("�⴫����"), new DeviceSensor()));
				System.out.println(dao.AddDevice(device, user));
		//��������3
		device = new Device("1:2:3:4:5:8", "15023490062", StringRelated.newString_UTF_8("����3"), StringRelated.newString_UTF_8("����3"),
	new SubDevice(DeviceSwitch.DEVICE_TYPE,(long) 1, StringRelated.newString_UTF_8("��"), new DeviceSwitch()),
	new SubDevice(DeviceSwitch.DEVICE_TYPE,(long) 2, StringRelated.newString_UTF_8("����"), new DeviceSwitch()),
	new SubDevice(DeviceSensor.DEVICE_TYPE,(long) 1, StringRelated.newString_UTF_8("�⴫����"), new DeviceSensor()));
				System.out.println(dao.AddDevice(device, user));
		
		
	}
	


}

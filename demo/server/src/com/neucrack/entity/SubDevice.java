package com.neucrack.entity;

public class SubDevice {
	short  mType;          //���豸����
	long mNumber;           //���豸�ӱ��
	String mNickName;      //�ǳ�
	Object mDeviceInfo;    //�豸������Ϣ
	
	public SubDevice(Short type,Long number,String nickName, Object deviceInfo){
		mType = type;
		mNumber = number;
		mNickName =nickName;
		mDeviceInfo = deviceInfo;
	}

	public short getmType() {
		return mType;
	}

	public void setmType(short mType) {
		this.mType = mType;
	}

	public long getmNumber() {
		return mNumber;
	}

	public void setmNumber(long mNumber) {
		this.mNumber = mNumber;
	}

	public String getmNickName() {
		return mNickName;
	}

	public void setmNickName(String mNickName) {
		this.mNickName = mNickName;
	}

	public Object getmDeviceInfo() {
		return mDeviceInfo;
	}

	public void setmDeviceInfo(Object mDeviceInfo) {
		this.mDeviceInfo = mDeviceInfo;
	}
	
	
}

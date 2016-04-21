#ifndef _F103_ADC_S_H_
#define _F103_ADC_S_H_


#include "stm32f10x.h"
#include "stm32f10x_gpio.h"
#include "stm32f10x_rcc.h"
#include "stm32f10x_tim.h"
#include "stm32f10x_adc.h"
#include "stm32f10x_dma.h"


class ADC
{
	public:

	/**
	 *@brief ��õ�ص�ѹֵ�ĺ���
	 *@param channel	  ѡ���ĸ�ͨ��������GetPowerAdc(1);//ѡ���ͨ��1
	 */
		void InitAdcDma(uint8_t* channelArr);
	
	/**
	 *@brief ��DMA��õ�ADCԭʼֵֵ��ŵ�����
	 */
	
		uint16_t  mAdcPrimordialValue[10];//�ȴ洢10��ͨ����ֵ������������
	
	/**
	 *@brief ���캯��
	 *@param channelv*	  ѡ���ĸ�ͨ��������ADC adc(0);//��ʼ��ͨ��0
	 *																		 ADC adc2(1,4,7,9)//��ʼ��ͨ��1,4,7,9
	 */
		ADC(uint8_t channelv0=255,
				uint8_t channelv1=255,
				uint8_t channelv2=255,
				uint8_t channelv3=255,
				uint8_t channelv4=255,
				uint8_t channelv5=255,
				uint8_t channelv6=255,
				uint8_t channelv7=255,
				uint8_t channelv8=255,
				uint8_t channelv9=255)
			{
				
			/*��ʼ����Ա��������*/
				
				maxAdcChannel = 10;
				mGpio					= mGpioArr;
				mPin					= mPinArr;
				mAdcChannel		= mAdcChannelArr;
				
			/*******************/
				ChannelToGpio(channelv0,
											channelv1,
											channelv2,
											channelv3,
											channelv4,
											channelv5,
											channelv6,
											channelv7,
											channelv8,
											channelv9);
				InitAdcDma(mAdcChannel);
			}

	private:

		uint8_t						 maxAdcChannel;		//����ͨ����
		GPIO_TypeDef**	 	 mGpio;						//�洢GPIO������
		uint16_t* 				 mPin;						//�洢GPIO����Ӧ��Pin����
		u8*								 mAdcChannel;			//�洢��Ҫ��ʼ����ͨ������255��־������
		u8 								 mAdcTotal;				//�洢һ���ж��ٸ���Чͨ��
	
		GPIO_TypeDef* 		 mGpioArr[10];
		uint16_t					 mPinArr[10];
		uint8_t						 mAdcChannelArr[10];
	/**
	 *@brief ��ѡ���ͨ����Ӧ����ʵ��GPIO��ADCͨ��
	 *@param channelv*	  ѡ���ĸ�ͨ��,��Χ��0~9������		ChannelToGpio(1,3,4)
	 */	
		void ChannelToGpio(					 uint8_t channelv0=255,
																 uint8_t channelv1=255,
																 uint8_t channelv2=255,
																 uint8_t channelv3=255,
																 uint8_t channelv4=255,
																 uint8_t channelv5=255,
																 uint8_t channelv6=255,
																 uint8_t channelv7=255,
																 uint8_t channelv8=255,
																 uint8_t channelv9=255);						//��ͨ���Ŷ�Ӧ��GPIO
};

#endif


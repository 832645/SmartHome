#include "F103_ADC_S.h"


#define ADC1_DR_Address ((u32)0x40012400+0x4c)/*��ADC��ַ���к궨��*/
static GPIO_TypeDef* gpioArr[] = {GPIOA,GPIOB,GPIOC};
													/*Pin0,  Pin1,  Pin2,  Pin3,  Pin4,  Pin5,  Pin6,  Pin7,  Pin0,  Pin1,  Pin0,  Pin1,  Pin2,  Pin3,  Pin4,  Pin5*/
static uint16_t pinArr[] = {0x0001,0x0002,0x0004,0x0008,0x0010,0x0020,0x0040,0x0080,0x0001,0x0002,0x0001,0x0002,0x0004,0x0008,0x0010,0x0020};




/**
 *@brief ��ͨ�������Ŷ�������
 *@param channel �û�ѡ���ͨ��
 */
void ADC::ChannelToGpio(				uint8_t channelv0,
																uint8_t channelv1,
																uint8_t channelv2,
																uint8_t channelv3,
																uint8_t channelv4,
																uint8_t channelv5,
																uint8_t channelv6,
																uint8_t channelv7,
																uint8_t channelv8,
																uint8_t channelv9)						//��ͨ���Ŷ�Ӧ��GPIO
{
	/*��ȷ��GPIO��ABC��*/
	
	uint8_t 	i = 0;																																	//��¼��������
	uint8_t		j = 0;																																	//��¼��Ա����mAdcChannel�����ֵ
	uint8_t		a[10];
	uint8_t*	channelArr = a;																															//ͨ������
	GPIO_InitTypeDef GPIO_InitStructure;																							//�������ڳ�ʼ��GPIO�Ľṹ��
	uint16_t pinA = 0x0000;																																		//�洢A��GPIO������
	uint16_t pinB = 0x0000;																																		//�洢B��GPIO������
	
	*channelArr 		= channelv0;
	*(channelArr+1) = channelv1;
	*(channelArr+2) = channelv2;
	*(channelArr+3) = channelv3;
	*(channelArr+4) = channelv4;
	*(channelArr+5) = channelv5;
	*(channelArr+6) = channelv6;
	*(channelArr+7) = channelv7;
	*(channelArr+8) = channelv8;
	*(channelArr+9) = channelv9;																											//�����������ͨ�����д洢
	
	while(i++ != 10)																																	//����10���ж��Ƿ����ͨ������Ч��
	{
		if(*(channelArr+i-1) <= maxAdcChannel || *(channelArr+i-1) >= 0)								//�����0~maxAdcChannel�����ֵ��������Чֵ
		{
			
			*(mAdcChannel+j) = *(channelArr+i-1);																					//����¼��ͨ��ֵ�洢�ڳ�Ա����ָ����
			if(*(mAdcChannel+j) <8 )
			{
				*(mGpio+j) = gpioArr[0];																										//�洢Pin��Ӧ��GPIO��
				*(mPin+j)  = pinArr[*(mAdcChannel+j)];																			//�洢Pin
				pinA |=  *(mPin+j);																													//��ÿ��GPIO���pinȷ������
				
			}else if(*(mAdcChannel+j) == 8 || *(mAdcChannel+j) == 9)
				{
					*(mGpio+j) = gpioArr[1];																									//�洢Pin��Ӧ��GPIO��
					*(mPin+j) = pinArr[*(mAdcChannel+j)];																			//�洢Pin
					pinB |=  *(mPin+j);																												//��ÿ��GPIO���pinȷ������
				}
			j++;
		}
	}
	mAdcTotal = j;																																		//�洢��Ч��ͨ����
	i = 0;
	j = 0;
	
	if(pinA != 0)
	{
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA,ENABLE);															//open the PinA Timer
		GPIO_InitStructure.GPIO_Pin = pinA;																								//����Pin
		GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AIN;																			//ѡ��ģ�����뷽ʽ
		GPIO_Init(GPIOA,&GPIO_InitStructure);																							//ѡ������GPIO����г�ʼ��
	}
	if(pinB != 0)
	{
		RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB,ENABLE);															//open the PinA Timer
		GPIO_InitStructure.GPIO_Pin = pinB;																								//����Pin
		GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AIN;																			//ѡ��ģ�����뷽ʽ
		GPIO_Init(GPIOB,&GPIO_InitStructure);																							//ѡ������GPIO����г�ʼ��
	}
	
}



void ADC::InitAdcDma(uint8_t* channelArr)
{
	ADC_InitTypeDef  ADC_InitStructure;
	DMA_InitTypeDef  DMA_InitStructure;
//	u8 adcChaTemp = 0;																															//�洢�����ADCͨ��
	
	
	//���ȳ�ʼ��ʱ��
	RCC_AHBPeriphClockCmd(RCC_AHBPeriph_DMA1, ENABLE); 																//��DMAʱ��
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_ADC1,ENABLE);																//ADC1��ʱ��
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_AFIO,ENABLE);//�ܽŸ���
	RCC_ADCCLKConfig(RCC_PCLK2_Div6);									 																//72M6��Ƶ=12M   ��ΪADC���ֻ��14M����Ƶ�ʣ�72M��Ƶ12M�����
	//����DMA������
	DMA_DeInit(DMA1_Channel1);
	DMA_InitStructure.DMA_PeripheralBaseAddr = ADC1_DR_Address;												//ADC�����ַ��ADC���ݵ�ַ+ƫ��ֵ
	DMA_InitStructure.DMA_MemoryBaseAddr = (u32)mAdcPrimordialValue;	                  //�ڴ��ַ����Ա����
	DMA_InitStructure.DMA_DIR = DMA_DIR_PeripheralSRC;																//������ΪDMAԴ
	DMA_InitStructure.DMA_BufferSize = mAdcTotal;                                     //����ת����ΪmAdcTotal��1·AD��
	DMA_InitStructure.DMA_PeripheralInc = DMA_PeripheralInc_Disable;	            		//�����ַ������
	DMA_InitStructure.DMA_MemoryInc = DMA_MemoryInc_Enable;  			    								//�ڴ��ַ������
	DMA_InitStructure.DMA_PeripheralDataSize = DMA_PeripheralDataSize_HalfWord;	    	//�������ĸ��ɼ�һ·ADһ��
	DMA_InitStructure.DMA_MemoryDataSize = DMA_MemoryDataSize_HalfWord;
	DMA_InitStructure.DMA_Mode = DMA_Mode_Circular;										
	DMA_InitStructure.DMA_Priority = DMA_Priority_High;
	DMA_InitStructure.DMA_M2M = DMA_M2M_Disable;
	DMA_Init(DMA1_Channel1, &DMA_InitStructure);
	/* ʹ��DMA1ͨ��1 */
	DMA_Cmd(DMA1_Channel1, ENABLE);																										//��ΪStm32f103c8ֻ��DMAͨ��1����ADC��ͨ��������ֱ�ӳ�ʼ��DMA1
	
	


		
	ADC_InitStructure.ADC_Mode = ADC_Mode_Independent;																//����ģʽ
	ADC_InitStructure.ADC_ScanConvMode = ENABLE;																			//ɨ��ģʽ
	ADC_InitStructure.ADC_ContinuousConvMode = ENABLE; 																//������ʽ, ����ת��
	ADC_InitStructure.ADC_ExternalTrigConv = ADC_ExternalTrigConv_None;								//�������
	ADC_InitStructure.ADC_DataAlign = ADC_DataAlign_Right;														//�Ҷ���
	ADC_InitStructure.ADC_NbrOfChannel = mAdcTotal;																		//ͨ����Ŀ
	
	ADC_Init(ADC1,&ADC_InitStructure);
	
	
	/*������(���Ҫ�����ţ���Ҫ��������������ͨ��),��ʼ��˼·ADͨ��*/
	while(mAdcTotal-- != 0)
	{
		ADC_RegularChannelConfig(ADC1,*(mAdcChannel+mAdcTotal),mAdcTotal+1,ADC_SampleTime_239Cycles5);								//ADC���1��ͨ��2(IN2)��������Ĳ���˳��Ϊ1��ָ��ADCͨ���Ĳ���ʱ��ֵ
	}


	
	/*ʹ��ADC1��DMA*/
	ADC_DMACmd(ADC1,ENABLE);
	/*ʹ��ADC*/
	ADC_Cmd(ADC1,ENABLE);
	ADC_ResetCalibration(ADC1);																												//��λADC1
	while(ADC_GetResetCalibrationStatus(ADC1));																				//��λ�Ƿ�����
	ADC_StartCalibration(ADC1);																												//У׼ 
	while(ADC_GetCalibrationStatus(ADC1));																						//У׼�Ƿ����
	ADC_SoftwareStartConvCmd(ADC1,ENABLE);
	

}




package com.xjtu.sglab.exp;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;
import com.sun.tools.corba.se.idl.constExpr.And;
import com.sun.tools.internal.xjc.reader.internalizer.DOMForest.Handler;
import com.xjtu.sglab.gateway.comm.ACctrl;
import com.xjtu.sglab.gateway.comm.ACctrl.AC_MODE;
import com.xjtu.sglab.gateway.comm.ArduinoSensorDAO;
import com.xjtu.sglab.gateway.comm.CurtainCtrl;
import com.xjtu.sglab.gateway.comm.LightCtrl;
import com.xjtu.sglab.gateway.comm.meter.EPM6000GEDAOFactory;
import com.xjtu.sglab.gateway.comm.meter.EPM7000GEDAOFactory;
import com.xjtu.sglab.gateway.comm.meter.IMeterDAO;
import com.xjtu.sglab.gateway.comm.plug.*;
import com.xjtu.sglab.gateway.entity.AirConditionStatus;
import com.xjtu.sglab.gateway.entity.CurtainStatus;
import com.xjtu.sglab.gateway.entity.ElectricityInfo;
import com.xjtu.sglab.gateway.entity.LampStatus;
import com.xjtu.sglab.gateway.entity.SheSwitchStatus;
import com.xjtu.sglab.gateway.entity.SocialInfo;
import com.xjtu.sglab.gateway.util.GsonUtil;
import com.xjtu.sglab.gateway.withserver.ApplianceComm;
import com.xjtu.sglab.gateway.withserver.MeterComm;
import com.xjtu.sglab.gateway.withserver.SensorComm;
import com.xjtu.sglab.gateway.entity.*;
import com.xjtu.sglab.gateway.util.GsonJsonProvider;

public class ExpRun {
	
	private static long pre_curtain_recordTime = new Timestamp(System.currentTimeMillis()).getTime();
	private static String pre_social_info_time = "";
	private static long[] pre_lamp_record_time = {0,0,0};
	private static final String HOST_IP = "202.117.14.247";
	
	
	public static void main(String[] args) {
		// Meter
		/*Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				IMeterDAO meterDAO = EPM7000GEDAOFactory.getInstance()
						.getGEDAO(Constants.Meter.METER_IP_104);
				ElectricityInfo electricityInfo = new ElectricityInfo();
				electricityInfo.setActivePower(meterDAO.getActivePower());
				electricityInfo.setReactivePower(meterDAO.getReactivePower());
				electricityInfo.setTotalConsumeEnergy(meterDAO.getEnergy());
				electricityInfo.setElectricityInfoCollectTime(new Timestamp(
						System.currentTimeMillis()));
				if (electricityInfo.getActivePower() != null
						&& electricityInfo.getReactivePower() != null
						&& electricityInfo.getTotalConsumeEnergy() != null) {
					MeterComm.getInstance().saveMeterInfo(electricityInfo,
							Constants.Meter.METER_ID_104);
				}
			}
		}, 1000, 600000);*/

		/*Timer timer2 = new Timer();
		timer2.schedule(new TimerTask() {
			@Override
			public void run() {
				IMeterDAO meterDAO = EPM6000GEDAOFactory.getInstance()
						.getGEDAO(Constants.Meter.METER_IP_103);
				ElectricityInfo electricityInfo = new ElectricityInfo();
				electricityInfo.setActivePower(meterDAO.getActivePower());
				electricityInfo.setReactivePower(meterDAO.getReactivePower());
				electricityInfo.setTotalConsumeEnergy(meterDAO.getEnergy());
				electricityInfo.setElectricityInfoCollectTime(new Timestamp(
						System.currentTimeMillis()));
				if (electricityInfo.getActivePower() != null
						&& electricityInfo.getReactivePower() != null
						&& electricityInfo.getTotalConsumeEnergy() != null) {
					MeterComm.getInstance().saveMeterInfo(electricityInfo,
							Constants.Meter.METER_ID_103);
				}
			}
		}, 1000, 600000);*/

		Timer timer3 = new Timer();
		timer3.schedule(new TimerTask() {
			@Override
			public void run() {
				IMeterDAO meterDAO = EPM6000GEDAOFactory.getInstance()
						.getGEDAO(Constants.Meter.METER_IP_102);
				ElectricityInfo electricityInfo = new ElectricityInfo();
				electricityInfo.setActivePower(meterDAO.getActivePower());
				electricityInfo.setReactivePower(meterDAO.getReactivePower());
				electricityInfo.setTotalConsumeEnergy(meterDAO.getEnergy());
				electricityInfo.setElectricityInfoCollectTime(new Timestamp(
						System.currentTimeMillis()));
				if (electricityInfo.getActivePower() != null
						&& electricityInfo.getReactivePower() != null
						&& electricityInfo.getTotalConsumeEnergy() != null) {
					MeterComm.getInstance().saveMeterInfo(electricityInfo,
							Constants.Meter.METER_ID_102);
				}
			}
		}, 1000, 600000);

		
		
		// Sensor
		Timer tiemr4 = new Timer();
		tiemr4.schedule(new TimerTask() {
			String ip=Constants.Sensor.IP;
			
			public String getIp() {
				return ip;
			}

			public void setIp(String ip) {
				this.ip = ip;
			}
			@Override
			public void run() {
				try {
					
					String result = ArduinoSensorDAO.gather(InetAddress
							.getByName(getIp()));
					System.out.println("sensor data"+result);
					String s[] = result.split(",");
					String temperature = s[0].split(":")[1];
					String humidity = s[1].split(":")[1];
					String light = s[2].split(":")[1];
					String gas = s[3].split(":")[1];
					String fire = s[4].split(":")[1];
					String human = s[5].split(":")[1];
					SensorComm.getInstance().saveFlameSensorInfo(
							Float.parseFloat(fire), Constants.Sensor.ID);
					SensorComm.getInstance().saveGasSensorInfo(
							Float.parseFloat(gas), Constants.Sensor.ID);
					SensorComm.getInstance().saveLightSensorInfo(
							Float.parseFloat(light), Constants.Sensor.ID);
					SensorComm.getInstance().savePlrSensorInfo(
							Float.parseFloat(human), Constants.Sensor.ID);
					SensorComm.getInstance().saveTemperatureSensorInfo(
							Float.parseFloat(temperature), Constants.Sensor.ID);
				} catch (Exception e) {
					setIp(ArduinoSensorDAO.sniffModules("192.168.1.255").get(0).getHostAddress());
				}
			}
		}, 1000, 600000);

		// Appliance AC
		Timer tiemr5 = new Timer();
		tiemr5.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					AirConditionStatus[] airConditionStatus = ApplianceComm
							.getInstance().queryAirConditionStatus(1);
					Gson gson=GsonUtil.create();
					System.out.println("ac"+gson.toJson(airConditionStatus));
					if (airConditionStatus.length == 1) {
						long recordTime = airConditionStatus[0]
								.getAirConditionStatusRecordTime().getTime();
						System.out.println(recordTime+"ac!!!!!!!!!!");
						if (Math.abs(recordTime - System.currentTimeMillis()) < 120000 && airConditionStatus[0].getIsAlreadyControlled() == false) {
							ACctrl.AC_MODE acMode;
							if (airConditionStatus[0].getAirConditionMode() == Constants.AirConditioner.SNOW) {
								acMode = ACctrl.AC_MODE.COLD;
							} else if (airConditionStatus[0]
									.getAirConditionMode() == Constants.AirConditioner.WARM) {
								acMode = ACctrl.AC_MODE.WARM;
							} else if (airConditionStatus[0]
									.getAirConditionMode() == Constants.AirConditioner.HUMID) {
								acMode = ACctrl.AC_MODE.DEHYDRATION;
							} else {
								acMode = ACctrl.AC_MODE.VENTILATION;
							}
							ACctrl.setACTemperatureMode(
									(int) ((float) airConditionStatus[0]
											.getAirConditionTemperature()),
									acMode);
							ApplianceComm.getInstance().saveAirConditionInfo(1, 1, (float)airConditionStatus[0].getAirConditionTemperature());
						}
					}
				} catch (Exception e) {
					
				}
			}
		}, 1000, 1000);

		// Appliance Switch
		Timer tiemr6 = new Timer();
		tiemr6.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					SheSwitchStatus[] sheSwitchStatus = ApplianceComm
							.getInstance().querySheSwitchStatus(1, 2);
					int num = 1;
					for (SheSwitchStatus sheSwitchStatusNow : sheSwitchStatus) {
						long recordTime = sheSwitchStatusNow
								.getSheSwitchStatusRecordTime().getTime();
						AbstractPlugCtrl abstractPlugCtrl;
						if (Math.abs(recordTime - System.currentTimeMillis()) < 10000000) {
							if (sheSwitchStatusNow.getSheSwitch()
									.getSheSwitchId() == 1) {
								abstractPlugCtrl = new PlugCtrl1(
										"192.168.1.255");
							} else
								abstractPlugCtrl = new PlugCtrl2(
										"192.168.1.255");
							if (sheSwitchStatusNow.getSheSwitchStatus() == true) {
								abstractPlugCtrl.switchOn();
							} else {
								abstractPlugCtrl.switchOff();
							}
						}
						
					}
				} catch (Exception e) {
					
				}
			}
		}, 1000, 1000);


		final InetAddress inetAddress = LightCtrl
				.sniffModules("192.168.1.255");
		LightCtrl.confirm(inetAddress);
		// Appliance Lamp
		Timer tiemr7 = new Timer();
		tiemr7.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
//					System.out.println("111111111");
					LampStatus[] lampStatus = ApplianceComm.getInstance()
							.queryLampStatusArray(1, 2, 3);
					for (LampStatus lampStatusNow : lampStatus) {
						
						long recordTime = lampStatusNow
								.getLampStatusRecordTime().getTime();
						System.out.println((recordTime- System.currentTimeMillis())+"lamp");
						if (Math.abs(recordTime - System.currentTimeMillis()) < 3000000
								&&lampStatusNow.getIsAlreadyControlled()==false) {
							System.out.println("333333");
							//InetAddress inetAddress = LightCtrl
							//		.sniffModules("192.168.1.255");
							//LightCtrl.confirm(inetAddress);
							System.out.println(lampStatusNow.getLamp()
									.getLampId()+"lamp+++++++");
							LightCtrl.confirm(inetAddress);
							if (lampStatusNow.getLampStatus() == 0) {
								for (int j = 0; j < 4; j++)
									LightCtrl.control(lampStatusNow.getLamp()
											.getLampId(), false, inetAddress);
							} else
								for (int j = 0; j < 4; j++)
									LightCtrl.control(lampStatusNow.getLamp()
											.getLampId(), true, inetAddress);
							ApplianceComm.getInstance().saveLampInfo(lampStatusNow.getLamp().getLampId(),lampStatusNow.getLampStatus());
						}
					}
				} catch (Exception e) {
					
				}
			}
		}, 1000, 1000);

		// Appliance Curtian
		Timer tiemr8 = new Timer();
		tiemr8.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					CurtainStatus[] curtainStatus = ApplianceComm.getInstance()
							.queryCurtainStatus(1);
				 	long cur_curtain_recordTime = curtainStatus[0]
							.getCurtainStatusRecordTime().getTime();
					 if(curtainStatus.length==1 && cur_curtain_recordTime != pre_curtain_recordTime){
							if (Math.abs(cur_curtain_recordTime - System.currentTimeMillis()) < 1000000) {
								if(curtainStatus[0].getCurtainStatus()==1){
									CurtainCtrl.upCurtain();
								}
								else if(curtainStatus[0].getCurtainStatus() == 2/*curtainStatus[0].getCurtainStatus()>0.4&&curtainStatus[0].getCurtainStatus()<0.6*/){
									CurtainCtrl.stopCurtain();
								}
								else if(curtainStatus[0].getCurtainStatus()==3){
									CurtainCtrl.downCurtain();
								}
							}
							pre_curtain_recordTime = cur_curtain_recordTime;
					 }
				} catch (Exception e) {
					
				}
			}
		}, 1000, 1000);
		
		
		
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(GsonJsonProvider.class);
		Client client = ClientBuilder.newClient(clientConfig);
		WebTarget webTarget = client.target("http://" + HOST_IP
				+ ":8080/smarthome/socialInfo/query");
		final Builder request = webTarget.request();
		WebTarget webTarget_wearble = client.target("http://202.117.14.247:8080/smarthome/wearableInfo/query");
		final Builder request_wearble = webTarget_wearble.request();
		
		
		//event detection
		Timer timer9 = new Timer();
		timer9.schedule(new TimerTask(){
			@Override
			public void run(){
				Response response = request.get();
				
				SocialInfo socialInfo = response
						.readEntity(SocialInfo.class);
				
				String cur_social_info_time = socialInfo.getStartTime();
				if(!cur_social_info_time.equals(pre_social_info_time)){
					System.out.println("event_detection");
					pre_social_info_time = cur_social_info_time;
					final Integer acType = socialInfo.getActivityType().getActivityTypeId();
					String start_time = socialInfo.getStartTime();
					String[] start_time_list = start_time.split("\\.");
					start_time = start_time_list[0];
					System.out.println("start time " + start_time);
					Timestamp start_time_stamp = new Timestamp(System.currentTimeMillis());
					start_time_stamp = start_time_stamp.valueOf(start_time);
					System.out.println("stamp" + start_time_stamp);
					long delay_time = start_time_stamp.getTime() - System.currentTimeMillis() + 60000;
					if (delay_time <= 0){
						return;
					}
					System.out.println("delay time " + delay_time);
					Timer curtain_start_timer = new Timer();
					Timer event_shut = new Timer();
					Timer lamp_start_timer = new Timer();
					curtain_start_timer.schedule(new TimerTask(){
						@Override
						public void run(){
							if (acType == 4){//上班
								CurtainCtrl.upCurtain();
								ACctrl.setACTemperatureMode(27,
										ACctrl.AC_MODE.WARM);
								
							}
							if (acType == 5){//下班
								CurtainCtrl.downCurtain();
								ACctrl.OffAC();
							}
						}
					}, 5000/*delay_time*/);
					
					lamp_start_timer.schedule(new TimerTask(){
						@Override
						public void run(){
							if (acType == 4){//上班
								LightCtrl.confirm(inetAddress);
								for (int i = 1; i <= 3; i++){
									for (int j = 0; j < 4; j++){
										LightCtrl.control(i, true, inetAddress);
									}
								}
							}
							if (acType == 5){//下班
								LightCtrl.confirm(inetAddress);
								for (int i = 1; i <= 3; i++){
									for (int j = 0; j < 4; j++){
										LightCtrl.control(i, false, inetAddress);}
								}
							}
						}
					}, 5000);
					
				}
			}
			
		}, 2000, 4000);
		
		
		// wearble event detection
		Runnable runnable_wearble = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					Response response_wearble = request_wearble.get();
					WearableDeviceInfo wearableDeviceInfo = response_wearble.readEntity(WearableDeviceInfo.class);
					if(wearableDeviceInfo.getSpeed() > 1.3){
						ACctrl.setACTemperatureMode(26, AC_MODE.WARM);
						try {
							Thread.sleep(1800000);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}else{
						try {
							Thread.sleep(4000);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		};
		Thread thread_wearbleThread = new Thread(runnable_wearble);
		thread_wearbleThread.start();
		
	}
}

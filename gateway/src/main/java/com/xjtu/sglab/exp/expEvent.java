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
import com.xjtu.sglab.gateway.util.GsonUtil;
import com.xjtu.sglab.gateway.withserver.ApplianceComm;
import com.xjtu.sglab.gateway.withserver.MeterComm;
import com.xjtu.sglab.gateway.withserver.SensorComm;
import com.xjtu.sglab.gateway.entity.*;
import com.xjtu.sglab.gateway.util.GsonJsonProvider;

public class expEvent {
	private static String pre_social_info_time = "";
	private static final String HOST_IP = "202.117.14.247";
	public static void main(String[] args){
	Timer timer9 = new Timer();
	ClientConfig clientConfig = new ClientConfig();
	clientConfig.register(GsonJsonProvider.class);

	Client client = ClientBuilder.newClient(clientConfig);

	WebTarget webTarget = client.target("http://" + HOST_IP
			+ ":8080/smarthome/socialInfo/query");
	final Builder request = webTarget.request();
	final InetAddress inetAddress = LightCtrl
			.sniffModules("192.168.1.255");
	LightCtrl.confirm(inetAddress);

	final Gson gson = GsonUtil.create();
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
				start_time_stamp = socialInfo.getActivitySentTime();
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
							for (int i = 1; i <= 3; i++){
								for (int j = 0; j < 4; j++){
									LightCtrl.control(i, true, inetAddress);}
							}
						}
						if (acType == 5){//下班
							for (int i = 1; i <= 3; i++){
								for (int j = 0; j < 4; j++){
									LightCtrl.control(i, false, inetAddress);}
							}
						}
					}
				}, 5000);
				/*event_shut.schedule(new TimerTask(){
					@Override
					public void run(){
						if (acType == 4){//上班
							//CurtainCtrl.stopCurtain();
						}
						if (acType == 5){//下班
							//CurtainCtrl.stopCurtain();
						}
					}
				}, delay_time + 15000);*/
				
			}
		}
		
	}, 2000, 4000);
	}
}

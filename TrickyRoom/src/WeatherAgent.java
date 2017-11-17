import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class WeatherAgent extends Agent {
	
	public static final int FORECAST_SIZE = 5000; //SECONDS DIVIDED BY 1000
	public static final boolean NOISY_FORECAST = false; //SHOULD PRINT DAY PHASES
	
	private static final long serialVersionUID = 1L;
	
	private int weather;
	private AID[] rooms;
	Random rnd;
	
	protected void setup() {
		System.out.println("Forecast: "+ getLocalName() + " started");
		weather = 0;
		
		Behaviour tickerB = new TickerBehaviour( this, FORECAST_SIZE ) {
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				changeWeather();
				
				// Update the list of room agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("room-service");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					rooms = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						rooms[i] = result[i].getName();
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				
				ACLMessage weatherInformMessage = new ACLMessage(ACLMessage.INFORM);
				for (int i = 0; i < rooms.length; ++i) {
					weatherInformMessage.addReceiver(rooms[i]);
					weatherInformMessage.setConversationId("weather-forecast");
				} 
				weatherInformMessage.setContent("" + weather);
				myAgent.send(weatherInformMessage);
	        }
	    };
	    addBehaviour(tickerB);
	}
	
	private void changeWeather () {
		rnd = new Random();
		if (rnd.nextDouble() <= 0.25) {
			if(weather == 0) {
				weather = 1;
				System.out.println("It's started to rain!");
			} else {
				weather = 0;
				System.out.println("The rain has stopped!");
			}
		   	printWeather(weather);
		} else {
			System.out.println("The weather remains " + getWeatherDescrption(weather));
		}
	}
	
	public static void printWeather (int weather) {
		if(NOISY_FORECAST) {
			System.out.println("Weather forecast: " + getWeatherDescrption(weather));
		} else {
			//do nothing
		}
	}
	
	public static String getWeatherDescrption (int weather) {
		String weatherDescrption = new String();
		switch (weather) {
			case 0:
				weatherDescrption = "dry";
				break;
			case 1:
				weatherDescrption = "rainny";
		}
		return weatherDescrption;
	}
}

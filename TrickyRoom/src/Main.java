import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {

	public static void main(String[] args) {
		
		//jade standard code
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		Profile profile = new ProfileImpl();

		//profile.setParameter( ... );
		//profile.setParameter(Profile.CONTAINER_NAME, "PutNameHere");
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.MAIN_PORT, "12344");

		AgentContainer container = runtime.createMainContainer( profile );
		
		//Instantiate agents
		Agent clock = new ClockAgent();
		Agent builder = new BuilderAgent();
		//Agent livingRoom = new RoomAgent();
		
		/*
		Object[] argslivingRoom = new Object[1];
		argslivingRoom[0] = "2";
		
		Object[] argsbedRoom = new Object[2];
		argsbedRoom[0] = "3";
		argsbedRoom[1] = "livingRoom";
		
		Object[] argsKitchen = new Object[3];
		argsKitchen[0] = "5";
		argsKitchen[1] = "livingRoom";
		argsKitchen[2] = "bedRoom";
		*/
		
		try {
		    AgentController rma = container.createNewAgent("rma", "jade.tools.rma.rma", null);
			AgentController clockAC = container.acceptNewAgent("clockAgent", clock);
			AgentController builderAC = container.acceptNewAgent("builderAgent", builder);
			//AgentController livingRoomAC = container.createNewAgent("livingRoom", "RoomAgent", argslivingRoom);
			//AgentController bedRoomAC = container.createNewAgent("bedRoom", "RoomAgent", argsbedRoom);
			//AgentController kitchenAC = container.createNewAgent("kitchen", "RoomAgent", argsKitchen);
			rma.start();
			clockAC .start();
			builderAC.start();
			//livingRoomAC.start();
			//bedRoomAC.start();
			//kitchenAC.start();
		} catch (StaleProxyException e) {
		    e.printStackTrace();
		}
	}

}

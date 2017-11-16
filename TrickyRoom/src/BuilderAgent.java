import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class BuilderAgent extends Agent{
	private static final long serialVersionUID = 1L;
	private static final int BUILDING_TIME = 5000; //SECONDS DIVIDED BY 1000
	
	protected void setup(){
		//jade standard code
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		Profile profile = new ProfileImpl();

		//profile.setParameter( ... );
		profile.setParameter(Profile.CONTAINER_NAME, "Building");
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.MAIN_PORT, "12345");

		AgentContainer container = runtime.createAgentContainer( profile );
		
		//Instantiate agents
		Agent clock = new ClockAgent();
		//Agent livingRoom = new RoomAgent();

		Object[] argsliving_Room = new Object[1];
		argsliving_Room[0] = "2";
		
		Object[] argsbed____Room = new Object[2];
		argsbed____Room[0] = "3";
		argsbed____Room[1] = "livingRoom";
		
		Object[] argsKitchen_Room = new Object[3];
		argsKitchen_Room[0] = "5";
		argsKitchen_Room[1] = "livingRoom";
		argsKitchen_Room[2] = "bedRoom";
		
		Behaviour buil = new TickerBehaviour( this, BUILDING_TIME ) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				/*
				try {
					AgentController livingRoomAC = container.createNewAgent("livingRoom", "RoomAgent", argslivingRoom);
					AgentController bedRoomAC = container.createNewAgent("bedRoom", "RoomAgent", argsbedRoom);
					AgentController kitchenAC = container.createNewAgent("kitchen", "RoomAgent", argsKitchen);
					livingRoomAC.start();
					bedRoomAC.start();
					kitchenAC.start();
				} catch (StaleProxyException e) {
				    e.printStackTrace();
				}
				*/
			}
		};
		
		Behaviour buildKitchen = new WakerBehaviour( this, BUILDING_TIME ) {
			private static final long serialVersionUID = 1L;
			
			protected void handleElapsedTimeout() {
				try {
					AgentController kitchenAC = container.createNewAgent("kitchen_Room", "RoomAgent", argsKitchen_Room);
					kitchenAC.start();
				} catch (StaleProxyException e) {
				    e.printStackTrace();
				}
      		}
		};
		
		Behaviour buildBedRoom = new WakerBehaviour( this, BUILDING_TIME ) {
			private static final long serialVersionUID = 1L;
			
			protected void handleElapsedTimeout() {
				try {
					AgentController bedRoomAC = container.createNewAgent("bed_____Room", "RoomAgent", argsbed____Room);
					bedRoomAC.start();
				} catch (StaleProxyException e) {
				    e.printStackTrace();
				}
		 		addBehaviour(buildKitchen);
      		}
		};
		
		Behaviour buildLivingRoom = new WakerBehaviour( this, BUILDING_TIME ) {
			private static final long serialVersionUID = 1L;
			
			protected void handleElapsedTimeout() {
				try {
					AgentController livingRoomAC = container.createNewAgent("living__Room", "RoomAgent", argsliving_Room);
					livingRoomAC.start();
				} catch (StaleProxyException e) {
				    e.printStackTrace();
				}
		 		addBehaviour(buildBedRoom);
      		}
		};
		
		addBehaviour(buildLivingRoom);
	}
}

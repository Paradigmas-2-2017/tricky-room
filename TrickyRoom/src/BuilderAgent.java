import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class BuilderAgent extends Agent{
	private static final long serialVersionUID = 1L;
	private static final int BUILDING_TIME = 2500; //SECONDS DIVIDED BY 1000
	
	protected void setup(){
		//jade standard code
		jade.core.Runtime runtime = jade.core.Runtime.instance();
		Profile profile = new ProfileImpl();

		//profile.setParameter( ... );
		profile.setParameter(Profile.CONTAINER_NAME, "Building");
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.MAIN_PORT, "12345");

		AgentContainer container = runtime.createAgentContainer( profile );
		
		Object[] argslivingRoom = new Object[1];
		argslivingRoom[0] = "2";
		
		Object[] argsbedRoom = new Object[2];
		argsbedRoom[0] = "3";
		argsbedRoom[1] = "livingRoom";
		
		Object[] argsKitchen = new Object[3];
		argsKitchen[0] = "5";
		argsKitchen[1] = "livingRoom";
		argsKitchen[2] = "bedRoom";
		
		Object[] argsBoy = new Object[1];
		argsBoy[0] = "livingRoom";
		
		Behaviour inviteBoy = new WakerBehaviour( this, BUILDING_TIME ) {
			private static final long serialVersionUID = 1L;
			
			protected void handleElapsedTimeout() {
				try {
					AgentController boyAC = container.createNewAgent("boy", "PersonAgent", argsBoy);
					boyAC.start();
				} catch (StaleProxyException e) {
				    e.printStackTrace();
				}
      		}
		};
		
		Behaviour buildKitchen = new WakerBehaviour( this, BUILDING_TIME ) {
			private static final long serialVersionUID = 1L;
			
			protected void handleElapsedTimeout() {
				try {
					AgentController kitchenAC = container.createNewAgent("kitchen", "RoomAgent", argsKitchen);
					kitchenAC.start();
				} catch (StaleProxyException e) {
				    e.printStackTrace();
				}
		 		addBehaviour(inviteBoy);
      		}
		};
		
		Behaviour buildBedRoom = new WakerBehaviour( this, BUILDING_TIME ) {
			private static final long serialVersionUID = 1L;
			
			protected void handleElapsedTimeout() {
				try {
					AgentController bedRoomAC = container.createNewAgent("bedRoom", "RoomAgent", argsbedRoom);
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
					AgentController livingRoomAC = container.createNewAgent("livingRoom", "RoomAgent", argslivingRoom);
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

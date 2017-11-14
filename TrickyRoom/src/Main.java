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
		AgentContainer container = runtime.createMainContainer( profile );
		
		//profile.setParameter(Profile.CONTAINER_NAME, "PutNameHere");
		//profile.setParameter(Profile.MAIN_HOST, "localhost");
		
		//Instantiate agent
		Agent clock = new ClockAgent();
		//agent.addBehaviour( ... );
		try {
			AgentController agentController = container.acceptNewAgent( "clock-agent", clock);
			agentController.start();
		} catch (StaleProxyException e) {
		    e.printStackTrace();
		}
	}

}

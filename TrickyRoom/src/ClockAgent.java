import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ClockAgent extends Agent {
	
	public static final int PHASE_SIZE = 10000; //SECONDS DIVIDED BY 1000
	
	private static final long serialVersionUID = 1L;
	
	private static int dayPhase;
	
	protected void setup() {
		dayPhase = 0;
		
		// Register the phase-marking service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("phase-marking");
		sd.setName("JADE-phase-marking");
		dfd.addServices(sd);	
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		Behaviour tickTac = new TickerBehaviour( this, PHASE_SIZE ) {
	         protected void onTick() {
	            System.out.println("Day phase: " + getPhaseName(dayPhase) );
	            phaseShift();
	         }
	    };
	    addBehaviour( tickTac );
	}
	
	public static String getPhaseName (int phaseCode) {
		String phaseName = new String();
		switch (phaseCode) {
			case 0:
				phaseName = "morning";
				break;
			case 1:
				phaseName = "afternoon";
				break;
			case 2:
				phaseName = "early night";
				break;
			case 3:
				phaseName = "late night";
		}
		return phaseName;
	}
	
	private static void phaseShift () {
		if(dayPhase < 3) {
			dayPhase++;
		} else {
			dayPhase = 0;
		}
	}
}

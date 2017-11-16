import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class ClockAgent extends Agent {
	
	public static final int PHASE_SIZE = 5000; //SECONDS DIVIDED BY 1000
	public static final boolean NOISY_CLOCK = false; //SHOULD PRINT DAY PHASES
	
	private static final long serialVersionUID = 1L;
	
	private int dayPhase;
	private AID[] rooms;
	
	protected void setup() {
		System.out.println("ClockAgent: "+ getLocalName() + " started");
		dayPhase = 0;
		
		Behaviour tickTac = new TickerBehaviour( this, PHASE_SIZE ) {
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				phaseShift();
				
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
				
				ACLMessage timeMessage = new ACLMessage(ACLMessage.INFORM);
				for (int i = 0; i < rooms.length; ++i) {
					timeMessage.addReceiver(rooms[i]);
					timeMessage.setOntology("day-phase");
				} 
				timeMessage.setContent("" + dayPhase);
				myAgent.send(timeMessage);
	        }
	    };
	    addBehaviour(tickTac);
	}
	
	private void phaseShift () {
		if(dayPhase < 3) {
			dayPhase++;
		} else {
			dayPhase = 0;
		}
	   	printDayPhase(dayPhase);
	}
	
	public static void printDayPhase (int phaseCode) {
		if(NOISY_CLOCK) {
			System.out.println("Day phase: " + getPhaseName(phaseCode));
		} else {
			//do nothing
		}
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
	/*
	private class TellTime extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(String.valueOf(dayPhase));
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}
	*/
}

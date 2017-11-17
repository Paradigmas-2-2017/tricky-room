import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class PersonAgent extends Agent{
	private static final long serialVersionUID = 1L;
	private String positionS = "livingRoom";
	private AID positionID;
	private AID[] rooms;

	protected void setup() {
		//Object[] args = getArguments();
		//positionS = args.toString();
		//Register the room-service service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("room-service");
		sd.setName("JADE-room-service");
		dfd.addServices(sd);
		try {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription ssd = new ServiceDescription();
			ssd.setType("room-service");
			template.addServices(ssd);
			DFAgentDescription[] result = DFService.search(this, template);
			rooms = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				if(result[i].getName().getLocalName() == positionS) {
					positionID = result[i].getName();
					System.out.println(getLocalName() + "'s positionS: " + positionS);
					System.out.println(getLocalName() + "'s positionID.getLocalName(): " + positionID.getLocalName());
				}
			}
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		Behaviour tickerB = new TickerBehaviour( this, 2500) {
			
			@Override
			protected void onTick() {
				addBehaviour(new Move());
				
			}
		};
		
		addBehaviour(tickerB);

	}
	
	private class Move extends Behaviour {
		private static final long serialVersionUID = 1L;
		private MessageTemplate mt; // The template to receive replies
		private AID[] neighbour;
		boolean finished = false;

		public void action() {
			//System.out.println(getLocalName() + "'s positionS: " + positionS);
			//System.out.println(getLocalName() + "'s positionID.getLocalName(): " + positionID.getLocalName());
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.addReceiver(positionID);
			request.setConversationId("neighbourhood");
			request.setReplyWith("request-neighbourhood"+System.currentTimeMillis()); // Unique value
			myAgent.send(request);
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("neighbourhood"),
					MessageTemplate.MatchInReplyTo(request.getReplyWith()));
			ACLMessage reply = myAgent.receive(mt);
			if (reply != null) {
				if (reply.getPerformative() == ACLMessage.INFORM) {
					try {
						neighbour = (AID[]) reply.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					System.out.println(getLocalName() + "'s neighbour.length" + neighbour.length);
					finished = true;
				}
			} else {
				block();
			}   
		}

		public boolean done() {
			return finished;
		}
	}
	
	
	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Person agent " + getAID().getLocalName() + " terminating.");
	}
}

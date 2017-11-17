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
	private AID positionId;

	protected void setup() {
		Object[] args = getArguments();
		//positionS = args.toString();
		//Register the room-service service in the yellow pages
		/*
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("person-service");
		sd.setName("JADE-room-service");
		dfd.addServices(sd);
		*/
		
		// Update the list of room agents
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sed = new ServiceDescription();
		sed.setType("room-service");
		template.addServices(sed);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			for (int i = 0; i < result.length; ++i) {
				if (args[0].toString().equals(result[i].getName().getLocalName())) {
					positionId = result[i].getName();
				}
			}
			System.out.println(getLocalName() + "'s position: " + positionId.getLocalName());
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		} catch (NullPointerException e) {
			e.printStackTrace();
			doDelete();
		}
		
		Behaviour tickerB = new TickerBehaviour( this, 5000) {
			
			@Override
			protected void onTick() {
				addBehaviour(new Walk());
				
			}
		};
		
		addBehaviour(tickerB);

	}
	
	private class Walk extends Behaviour {
		private static final long serialVersionUID = 1L;
		private MessageTemplate answerTemplate; // The template to receive replies
		private AID[] neighbour;
		boolean finished = false;

		public void action() {
			System.out.println(getLocalName() + " just started to walk");
			
			ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
			propose.addReceiver(positionId);
			propose.setConversationId("move-person");
			propose.setReplyWith("propose-move-person"+System.currentTimeMillis()); // Unique value

			//System.out.println(getLocalName() + "'s positionS: " + positionS);
			//System.out.println(getLocalName() + "'s positionId.getLocalName(): " + positionId.getLocalName());
			myAgent.send(propose);
			
			answerTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("move-person"),
					MessageTemplate.MatchInReplyTo(propose.getReplyWith()));
			
			ACLMessage proposeReply = myAgent.blockingReceive(answerTemplate);
			
			if (proposeReply != null) {
				if (proposeReply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					System.out.println(getLocalName() + "'s position should go to: " + proposeReply.getContent());
					
					// Update the list of room agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sed = new ServiceDescription();
					sed.setType("room-service");
					template.addServices(sed);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template);
						for (int i = 0; i < result.length; ++i) {
							if (proposeReply.getContent().equals(result[i].getName().getLocalName())) {
								positionId = result[i].getName();
							}
						}
						System.out.println(getLocalName() + "'s position: " + positionId.getLocalName());
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
						doDelete();
					} catch (NullPointerException e) {
						e.printStackTrace();
						doDelete();
					}
				} else {
					System.out.println(getLocalName() + "'s propose reply performative: " + proposeReply.getPerformative());
				}
			} else {
				System.out.println(getLocalName() + "'s propose reply = null");
			}
			
			/*
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("move-person"),
					MessageTemplate.MatchInReplyTo(propose.getReplyWith()));
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
			*/
			finished = true;
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

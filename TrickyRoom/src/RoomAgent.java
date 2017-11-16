import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RoomAgent extends Agent{
	private static final long serialVersionUID = 1L;
	private MessageTemplate timeTemplate = MessageTemplate.and( 
			MessageTemplate.MatchPerformative(ACLMessage.INFORM), 
			MessageTemplate.MatchOntology("day-phase") );

	private AID[] roomsId;
	private AID[] neighbourId;
	private int peopleLimit = 4; 
	private int peoplePresent = 1; 
	private static String dayPhase = "0";
	private boolean sunnyDay = true;
	
	private boolean openWindow = true;
	private boolean lightsOn = true;
	

	@Override
	protected void setup() {
		Object[] args = getArguments();
		setArgs(args);
		onEvent();
		//Register the room-service service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("room-service");
		sd.setName("JADE-room-service");
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		System.out.println("RoomAgent: "+ getLocalName() + " built");
		addBehaviour(new GetTime());
	}
	
	private void setArgs(Object[]args) {
		String[] neighbourS = new String[args.length]; 
		neighbourId = new AID[args.length - 1];
		int iD = 0;
		if(args.length > 0) {
			try {
				peopleLimit = Integer.parseInt(args[0].toString());
			} catch (Exception NumberFormatException) {
				System.out.println("Wrong input of " + getLocalName() + "'s people limt: " + args[0].toString());
			}
			if(args.length > 1) {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("room-service");
				template.addServices(sd);
				for(int i = 1; i < args.length; i++) {
					neighbourS[i - 1] = args[i].toString();
					System.out.println(getLocalName() + "'s neighbourS[" + (i - 1) + "]: " + neighbourS[i - 1]);
				}
				try {
					DFAgentDescription[] result = DFService.search(this, template);
					for (int i = 0; i < result.length; ++i) {
						System.out.println(getLocalName() + "'s result[" + i + "].getName().getLocalName(): " + result[i].getName().getLocalName());
						for(int j = 0; j < args.length - 1; j++) {
							if(neighbourS[j].equals(result[i].getName().getLocalName())) {
								neighbourId[iD] = result[i].getName();
								System.out.println(getLocalName() + "'s neighbourId[" + iD + "]: " + neighbourId[iD]);
								iD++;
							}
						}
					}
					if(neighbourId.length != (args.length -1)) {
						System.out.println("Wrong input of " + getLocalName() + "'s neighbours, neighbourID.lenght don't match (args.lenght - 1)");
						doDelete();	
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
					System.out.println("Wrong input of " + getLocalName() + "'s neighbours.");
					doDelete();
				}
			}
		}else {
			System.out.println("No input of " + getLocalName() + "'s arguments.");
		}
		return;
	}
	
	private void printRoomState(){
		System.out.println("\t" + getLocalName() + "\t  has " + (openWindow ? "open..." : "closed.") 
				+ "  windows,\t and turned " + (lightsOn ? "..on" : ".off") + " lights. \t "
				+ peoplePresent + "/" + peopleLimit + " people.");
		/*
		for(int i = 0; i < neighbourId.length; i++) {
			System.out.println(getLocalName() + "'s neighbourId[" + i + "]: " + neighbourId[i].getName());
		}
		*/
	}
	
	private void onEvent() {
		openWindow = ((dayPhase.equals("0") || dayPhase.equals("1")) && sunnyDay && peoplePresent > 0);
		lightsOn = (!openWindow && peoplePresent > 0);
		/*
		if (dayPhase.equals("0") || dayPhase.equals("1")) {
			if (sunnyDay) {
				if (peoplePresent > 0) {
					openWindow = true;
					lightsOn = false;
				}else {
					openWindow = false;
					lightsOn = true;
				}
			}else {
				openWindow = false;
				if (peoplePresent > 0) {
					lightsOn = true;
				}else {
					lightsOn = false;
				}
			}
		}else {
			System.out.println("test");
			openWindow = false;
			if (peoplePresent > 0) {
				lightsOn = true;
			}else {
				lightsOn = false;
			}
		}
		*/
		printRoomState();
	}
	
	private class GetTime extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(timeTemplate);
			if (msg != null) {
				dayPhase = msg.getContent();
				onEvent();
			}
			else {
				block();
			}
		}
	}
}

package com.system_creation;

import akka.actor.UntypedAbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import akka.event.Logging;
import akka.event.LoggingAdapter;


//#greeter-messages
public class Process extends UntypedAbstractActor {
	private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);  
    
    //static public Props props() {
    //  return Props.create(Process.class, () -> {
    //			return new Process();
    //  });
    //}
	
	static public Props props(String name) {
		return Props.create(Process.class, () -> new Process(name));
	}
 
	//#system-members
	static public class Members {
		public final int num;
		public final ActorRef[] members; 
		public Members(int num, ActorRef[] members) {
			this.num = num;
			this.members = members;
		}
	}
    
	private  Members mem;

	private final String name;

	public Process(String name) {
		this.name = name;
	}
    
	@Override
	public void onReceive(Object msg) throws Exception {
	    if (msg instanceof Members) {
		    this.mem = (Members) msg;
		    for(int x = 0; x <= this.mem.num-1; x = x + 1) {
			 log.info(this.name+": know member "+Integer.toString(x)); 
			 // log.info("Message to "+ this.mem.members[x],getSelf());
			 this.mem.members[x].tell("Message to "+ this.mem.members[1],getSelf()); 
		     }
	    } else
	     if (msg instanceof String) {
	    	 // log.info("I want to know "+ msg);
	    	 ActorRef actorRef = getSender();
	    	 log.info(this.name+": received new message '"+msg+"' from "+actorRef); 	     
	      }
	     else
			unhandled(msg);
	}

}


package com.paxos;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.Random;


public class Main {

    public static int N = 3; // The system size : number of processes
    public static int f = 1; // fault-prone mode
    public static double t_le = 0.5; // a fixed timeout to pick up a process
    public static void main(String[] args) throws InterruptedException {

        // Instantiate an actor system
        final ActorSystem system = ActorSystem.create("system");
        system.log().info("System started with N=" + N );

        ArrayList<ActorRef> references = new ArrayList<>();
        
        try
        
        {

        for (int i = 0; i < N; i++) {
            // Instantiate processes
            final ActorRef a = system.actorOf(Process.createActor(i, N), "" + i);
            references.add(a); // add processes into references
        }


        
        // Pick up the process of fault-prone mode
        // Shuffle the list of references
        Collections.shuffle(references);
        for(int i = 0; i < f ; ++i) {
        	references.get(i).tell(new Process.State(2), ActorRef.noSender()); // choose f processes to be faulty state
        	System.out.println("Process "+ references.get(i)+" is faulty");
        }
        for(int i = f; i < N; ++i) {
        	references.get(i).tell(new Process.State(1), ActorRef.noSender());
        }
        
        // Give each process a view of all the other processes
        Members m = new Members(references);
        for (ActorRef actor : references) {
            actor.tell(m, ActorRef.noSender()); // initialize the processes
        }
        
        // timeout of sleep
        Thread.sleep((long) t_le * 1000);
        
        // Emulate a leader election mechanism
        
        // Random leader
        
        Random rand =new Random(25);
        int leader;
        leader = rand.nextInt(N-f) + f;
        
        // After receiving a hold message, a process stops invoking propose operations.
        for(int i = 1; i < N; ++i) {
        	references.get(i).tell(new HoldMsg(),references.get(leader));
        }

//        OfconsProposerMsg opm = new OfconsProposerMsg("100");
//        references.get(0).tell(opm, ActorRef.noSender());
        
        
        System.out.println(">>> Press ENTER to exit <<<");
        System.in.read();
    
        } catch (IOException ioe) {} 
        finally {
        	system.terminate();
        }
    }
    
    
    
    
    
    
    
    
}
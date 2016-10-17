/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BSDSAssignment1;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 *
 * Simple client to test subscribing from CAServer over RMI
 */
public class CASubClient {
    private CASubClient() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
//        Scanner scan = new Scanner(System.in);
//        System.out.println("Please specify how many subscribers you want: ");
//        int threadNum = scan.nextInt();
        int threadNum = 10;
        for (int i = 0; i < threadNum; i++) {
//            System.out.println("Please enter a topic for subscriber " + i + ": ");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            String topic = scan.nextLine();
            SubClientThread thread = new SubClientThread("Topic" + i, host);
            new Thread(thread).start();
        }
    }
} 

class SubClientThread implements Runnable {
    private String host;
    private String topic;
    private static final int INITIAL_WAITING_TIME = 100; // Milliseconds

    public SubClientThread() {}
    public SubClientThread(String topic, String host) {
        this.topic = topic;
        this.host = host;
    }

    @Override
    public void run() {
        try {
            Registry registry = LocateRegistry.getRegistry(this.host);
            BSDSSubscribeInterface CAServerStub = (BSDSSubscribeInterface) registry.lookup("CAServerSubscriber");
            long startTime = System.currentTimeMillis();

            int id = CAServerStub.registerSubscriber(this.topic);
//            System.out.println("Subscriber id = " + Integer.toString(id));

            Thread.sleep(2000);

            int factor = 1;
            int messageCount = 0;
            while (true) {
                String message = CAServerStub.getLatestContent(id);
                if (message == null) {
                    Thread.sleep(INITIAL_WAITING_TIME * factor);
                    factor *= 2;

                    System.out.println("Thread " + Thread.currentThread().getId() + " runs for " + Long.toString(System.currentTimeMillis() - startTime));
                } else {
                    messageCount++;
//                    System.out.println("Thread " + Thread.currentThread().getId() + " received " + messageCount + " messages");
                    if (messageCount % 1000 == 0) {
                        System.out.println("Subscriber Thread " + Thread.currentThread().getId() + " received " + messageCount + " messages");
                    }
                    factor = 1;
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
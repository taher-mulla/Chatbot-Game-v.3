
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {

        //creating a server socket
//        try (ServerSocket serverSocket = new ServerSocket(5000)) {
//
//            while (true) {
//
//                //calling a client thread everytime a client tries to connect
//                new ToClient(serverSocket.accept()).start();
//            }
//
//        } catch (IOException e) {
//            System.out.println("server error : " + e.getMessage());
//        }
        Hashtable<String, Integer> ht1 = new Hashtable<>();
        ht1.put("2*2", 4);
        ht1.put("2*10", 20);
        ht1.put("2+2", 4);
        ht1.put("5*2", 10);
        ht1.put("10+20", 30);
        ht1.put("20-10", 10);
        ht1.put("2*5", 10);
        ht1.put("13-3", 10);
        ht1.put("90/10", 9);
        ht1.put("2*9", 18);
        int size = ht1.size();


        Scanner scanner = new Scanner(System.in);

        System.out.println("Pls enter the number of players : ");
        int players = scanner.nextInt();
        scanner.nextLine();

        //creating the class to keep the score
        System.out.println("creating the Score class");
        Score score = new Score(players);

        Socket[] clientSocket = new Socket[players];

        BufferedReader[] inputFromClient = new BufferedReader[players];
        PrintWriter[] outputToClient = new PrintWriter[players];




        try (ServerSocket serverSocket = new ServerSocket(5000)) {

            Thread[] t = new Thread[players];
            System.out.println("no of players is " + players);

            //accepting the number of players(clients)
            // we put the clients to  sleep in a thread, if they send a message they are told to wait
            System.out.println("Thread[] t of [players made] starting with the for loop to create the server socket");
            for (int i = 0; i < players; i++) {
                System.out.println("creating the client socket for " + i);
                clientSocket[i] = serverSocket.accept();
                System.out.println("client socket created now going to call the thread to make client sleep");
                inputFromClient[i] = new BufferedReader(new InputStreamReader(clientSocket[i].getInputStream()));
                outputToClient[i] = new PrintWriter(clientSocket[i].getOutputStream(), true);


                int finalI = i;
                System.out.println("finalI = " + finalI);
                t[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                System.out.println("in the thread to make client sleep client number is " + finalI);

                                System.out.println("the IP and OP buffer things have been ade now waiting for a message from client");
                                String fromClient = inputFromClient[finalI].readLine();
                                System.out.println("from client : "+fromClient);
                                outputToClient[finalI].println(players + ";-1;pls wait for all to connect;" + score.getScore());
                            }
                        } catch (IOException e) {
                            System.out.println("Error in wait thread");
                        }
                    }
                });
                t[i].start();
            }

            System.out.println("All clients here now we will interrupt them all");

            //once all clients connect we will stop all the threads
            for (int i = 0; i < players; i++) {
                System.out.println("interrupting client no" + i);
                t[i].stop();
            }


            //here we are putting the thread to sleep again till its turn comes
            for(int i=0; i<players; i++){
                System.out.println("putting clients to sleep till it their turn");

                int finalI = i;
                t[i]= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("in the thread to put client to sleep client no "+finalI);

                            while (true) {
                                String fromClient = inputFromClient[finalI].readLine();
                                System.out.println("from client : "+fromClient);
                                outputToClient[finalI].println(players+";-1;pls wait for your turn;"+score.getScore());
                                System.out.println("client "+finalI+" is waiting still and has been sent a wait message");
                            }

                        }catch (IOException e){
                            System.out.println("Error in wait thread");
                        }
                    }
                });
                t[i].start();
            }
            System.out.println("All clients sleeping and waiting for their turns");
            System.out.println("Ready going in while");

            //now the game starts

            while (true) {
                System.out.println("in while true");
                for (int i = 0; i < players; i++) {
                    System.out.println(" player no " + i + "is playing now");
                    //stopping the player whose turn it is and asking the question

                    t[i].stop();
//                    System.out.println("player has been interrupted " + i);

                    String[] s = ht1.keySet().toArray(new String[0]);
                    String question = s[new Random().nextInt(s.length)];
                    //sending the question to all clients
                    //question has the number of the client who is supposed to answer
                    System.out.println("sending message to all clients ");
                    System.out.println("message is " + question);
                    for (int j = 0; j < players; j++) {
                        outputToClient[j].println(players + ";-1;Question for player no " + i + " is : " + question + ";" + score.getScore());
                        System.out.println("message sent to client " + j);
                    }
                    System.out.println("all clients got messages");


                    String fromClient = inputFromClient[i].readLine();
                    System.out.println("Message from client : "+fromClient);

//                    String fromClient = inputFromClient.readLine();
//                    inputFromClient.close();
                    System.out.println("reply is " + fromClient);
                    //sending the clients reply to all clients
                    System.out.println("sending the reply to all clients");
                    for (int j = 0; j < players; j++) {
                        outputToClient[j].println(players + ";" + i + ";" + fromClient + ";" + score.getScore());
                    }

                    int answer = ht1.get(question);
                    //correct answer then add a point
                    System.out.println("checking answer");
                    if (answer == Integer.parseInt(fromClient)) {
                        System.out.println("correct");
                        score.updateScore(i);
                        for (int j = 0; j < players; j++) {
                            outputToClient[j].println(players + ";-1;Player no "+i+" is correct;" + score.getScore());

                        }
                    } else {
                        System.out.println("wrong");
                        for (int j = 0; j < players; j++) {
                            outputToClient[j].println(players + ";-1;Player no "+i+" is  wrong;" + score.getScore());

                        }
                    }


                    //again putting the thread to sleep till it is its turn
                    int finalI = i;
                    t[i]= new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                while(true){
                                    String fromClient = inputFromClient[finalI].readLine();
                                    System.out.println("from client : "+fromClient);
                                    outputToClient[finalI].println(players+";-1;pls wait for your turn;"+score.getScore());
                                }

                            }catch (IOException e){
                                System.out.println("Error in wait thread");
                            }
                        }
                    });
                    t[i].start();

                }
            }


        } catch (IOException e) {
            System.out.println("Error in main");
            e.printStackTrace();
        }


    }

    static class Score {
        int[] score;
        int players;


        public Score(int players) {
            this.players = players;
            this.score = new int[players];
            System.out.println("in the score constructor");
            for (int i = 0; i < players; i++) {
                System.out.println("0 assigned to player no " + i);
                this.score[i] = 0;
                System.out.println("0 assigned to player no " + i);
            }
        }

        public void updateScore(int player) {
            score[player]++;
        }


        public String getScore() {
            String s = "";
            for (int i = 0; i < players; i++) {
                s = s + score[i] + ";";
            }
            return s;
        }
    }
}

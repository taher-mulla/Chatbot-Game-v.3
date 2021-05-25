package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Controller {

    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField messageArea;
    @FXML
    private TextArea mainTextArea;


    private int botP = 0, userP = 0;
    private String botS = "Bot's points --> ", userS = "Users points are -->";
    private TextField botPoints = new TextField(botS + botP);
    private TextField userPoints = new TextField(userS + userP);
    private TextField[] points;
    private VBox vB;
//    private VBox vBox = new VBox(botPoints, userPoints);


    Socket socket;
    BufferedReader fromServer;
    PrintWriter toServer;

    String turn = "";
    int[] score = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};


    public void initialize() {



        //creating a connection with the server
        try {
            //socket, IP, and OP to connect to a server
            socket = new Socket("localHost", 5000);
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toServer = new PrintWriter(socket.getOutputStream(), true);

            //receiving text from the server and displaying it on the UI
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(true) {
                            String textFromServer = fromServer.readLine();
                            System.out.println("From server : "+textFromServer);
                            addToTextArea(textFromServer);
                        }
                    }catch (IOException e){
                        System.out.println("error in initialize ip fro srever");
                    }
                }
            });
            t.start();



        } catch (IOException e) {
            System.out.println("Client error : " + e.getMessage());
            e.getStackTrace();
        }

    }





    public void score(int players, String s){
        String[] split = s.split(";");
        System.out.println("Split text : ");
        System.out.println(split.toString());

        for(int i=0; i<players; i++){
            System.out.println("adding score for : "+i);
            int num = Integer.parseInt(split[i+3]);
            score[i] = num;
            System.out.println("Score for : "+i+" added");
        }


    }

    public void sendButton() {

        System.out.println("Button pressed");
        //sending a Movie OR a Character name to the server
        String message = messageArea.getText();
        System.out.println("text is : "+message);

        toServer.println(message);
        toServer.println(message);
        System.out.println("text sent to server");


        if(score[0]!=-1){
            System.out.println("In the If stateent");
            VBox box = new VBox();

            int i=0;
            while(score[i]!=-1) {
                System.out.println("updating field for player no : "+i+1);
                TextField tx = new TextField("Player no " + (i + 1) + " --->" + score[i]);
                box.getChildren().add(tx);
                i++;
            }
            System.out.println("Updating the top view");
            borderPane.setTop(box);
        }


    }

    public void addToTextArea(String string) {

        String[] str = string.split(";");
        int players = Integer.parseInt(str[0]);
        score(players,string);
        int from = Integer.parseInt(str[1]);

        System.out.println(str[1]);
        System.out.println(string);
        if(from==-1)
            mainTextArea.appendText("\n[server] --> "+str[2]);
        else
            mainTextArea.appendText("\n["+ (from+1) +"]      --> "+str[2]);

    }

}

import java.io.*;
import java.net.*;
import java.util.*;
public class VerySimpleChatServer {
    public static void main (String args[]){
        new VerySimpleChatServer().go();
    }
    static Socket waitingSocket[];
    static Socket chatmsg, gamemsg;
    int cnt=0;
    ArrayList<PrintWriter> clientOutputStream;
    ArrayList<PrintWriter> gameWriter;
    Board board;
    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        int callerid;

        public ClientHandler(Socket clientSocket, int i) {
            try{
                sock = clientSocket;
                callerid=i;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } // close constructor

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    if(callerid==1)
                    {
                        if(message.compareTo("-1")==0)
                        {
                            if(cnt == 0)
                            {
                                PrintWriter w = new PrintWriter(sock.getOutputStream());
                                try {
                                    w.println("X");
                                    w.flush();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                cnt++;
                            }
                            else
                            {
                                PrintWriter w = new PrintWriter(sock.getOutputStream());
                                try {
                                    w.println("O");
                                    w.flush();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                cnt++;
                            }
                        }
                        // game message format : CX where C = coordinate, X = player ('X' or 'O')
                        if(board.is_grid_free(Integer.parseInt(message.substring(0,1)))){
                            board.Move(Integer.parseInt(message.substring(1,2)),new Player(message.substring(0,1)));
                            board.switchTurn();
                        }
                        board.updateMoves();
                        board.printBoard();

                        int win = checkWin();

                        String messageOut = "M " + message + " " + Integer.toString(win);
                        try {
                            for(int i=0;i<gameWriter.size();i++) {
                                PrintWriter w = (PrintWriter) clientOutputStream.get(i);
                                try {
                                    w.println(messageOut);
                                    w.flush();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }

                        return;
                    }
                    else {
                        System.out.println("read " + message);
                        tellEveryone(message);
                    }
                } // close while
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } // close run
    } // close inner class

    public class RequestListner implements Runnable {
        Socket msgs;
        int indx;

        RequestListner(int i, Socket msg) {
            indx = i;
            msgs = msg;
            clientOutputStream=new ArrayList<>();
        }

        public void run() {
            try {
                ServerSocket serverSock;
                if(indx == 0)
                    serverSock = new ServerSocket(5000);
                else
                    serverSock = new ServerSocket(6000);
                while (true) {
                    Socket clientSocket = serverSock.accept();
                    PrintWriter writ=new PrintWriter(clientSocket.getOutputStream());
                    if(indx==1)
                        clientOutputStream.add(writ);
                    else
                        gameWriter.add(writ);

                    Thread t = new Thread(new ClientHandler(clientSocket, indx));
                    t.start();
                    System.out.println("got a connection");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } // close run
    } // close inner class

    public void go()

    {
        clientOutputStream = new ArrayList<>();
        gameWriter = new ArrayList<>();
        waitingSocket = new Socket[2];
        Thread t = new Thread(new RequestListner(0, chatmsg));
        t.start();
        t = new Thread(new RequestListner(1, gamemsg));
        t.start();
        this.board = Main.board;
    }
    public int checkWin() {
        if (board.xWon()) {
            return 1;
            //setAlert(new Player("X"));
        } else if (board.oWon()) {
            return 2;
            //setAlert(new Player("O"));
        } else if (board.isDraw()) {
            return 0;
            //setAlert(null);
        } else {
            return -1;
        }
    }

    public void tellEveryone(String message)  {
        try {
            for(int i=0;i<clientOutputStream.size();i++) {
                PrintWriter w = (PrintWriter) clientOutputStream.get(i);
                try {
                w.println(message);
                w.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    } // close tellEveryone

} // close class
package ae.com.sendreceive;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    String toServer = "";
    int port = 10048;
    String server_ip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }


    //----------------------------------------------------------------------------


    public void run(View view) {




        SendInBg send = new SendInBg();
        send.execute();



    }

    //----------------------------------------------------------------------------


    public class SendInBg extends AsyncTask<String,String,String>{


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView textView = (TextView) findViewById(R.id.text);
            textView.setText(s);
        }

        @Override
        protected String doInBackground(String ... v) {



            try {

                DatagramSocket clientSocket  = new DatagramSocket();

                //DatagramSocket socket = new DatagramSocket(10048);

                InetAddress ip = InetAddress.getByName("192.168.0.255");

                byte[] messageReturn= new byte[13];
                byte[] messageOut = toServer.getBytes();

                DatagramPacket Sendpacket = new DatagramPacket(messageOut,toServer.length(),ip,port);
                clientSocket.send(Sendpacket);

                DatagramPacket Recivepacket = new DatagramPacket(messageReturn,messageReturn.length);
                clientSocket.receive(Recivepacket);
                server_ip = new String(Recivepacket.getData());

                clientSocket.close();



            }



            catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            String urlString = "http://"+ server_ip +"/webapp/test/send.php";
            try {
                URL url = new URL(urlString);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);


                InputStream is = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

                String hello ="";
                String line = "";

                while((line=bufferedReader.readLine())!=null){
                    hello+=line;
                }

                bufferedReader.close();
                is.close();
                httpURLConnection.disconnect();
                return hello;

            }







            catch (MalformedURLException e) {
                e.printStackTrace();
                return e.toString();

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();

            }







        }
}}

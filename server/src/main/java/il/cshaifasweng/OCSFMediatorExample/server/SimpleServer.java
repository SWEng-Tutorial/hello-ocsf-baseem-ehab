package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;


public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
		
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			//we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
			// the IDs at data field in Message entity and send back to all subscribed clients a request to update
			//their IDs text fields. An example of use of observer design pattern.
			//message format: "change submitters IDs: 123456789, 987654321"
			else if(request.startsWith("change submitters IDs:")){
				message.setData(request.substring(23));
				message.setMessage("update submitters IDs");
				sendToAllClients(message);
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
			//we got a message from client requesting to echo Hello, so we will send back to client Hello world!
			else if(request.startsWith("echo Hello")){
				message.setMessage("Hello World!");
				client.sendToClient(message);
			}
			else if(request.startsWith("send Submitters IDs")){
				//add code here to send submitters IDs to client
				message.setMessage("207340514, 318582095");
				client.sendToClient(message);
			}
			else if (request.startsWith("send Submitters")){
				//add code here to send submitters names to client
				message.setMessage("Baseem Salem, Ehab Mansour");
				client.sendToClient(message);
			}
			else if (request.equals("what day it is?")) {
				//add code here to send the date to client
				message.setMessage(LocalDate.now().toString());
				client.sendToClient(message);
			}
			else if (request.startsWith("add")){
				//add code here to sum 2 numbers received in the message and send result back to client
				//(use substring method as shown above)
				//message format: "add n+m"
				String exp = request.toString();
				char[] arr = new char[exp.length()];
				String[] numbers = new String[2];
				exp.getChars(0,exp.length(),arr,0);
				String sum;
				int flag = 1;
				int firstIndex = 0;
				int lastIndex = 0;
				int counter = 0;
				for(int i = 0; i < exp.length(); i++)
				{
					if((arr[i] != 'a') && (arr[i] != 'd') && (arr[i] != ' '))
					{
						if(flag == 1)
						{
							firstIndex = i;
							lastIndex = i;
							flag = 0;
						}
						else if((arr[i] != '+'))
							lastIndex = i;
						if(i == exp.length() - 1)
						{
							numbers[counter] = putNumbers(arr,firstIndex,lastIndex);
							counter++;
							firstIndex = 0;
							lastIndex = 0;
							flag = 1;
						}
						else if(i < exp.length() - 1)
						{
							if(arr[i] == '+')
							{
								numbers[counter] = putNumbers(arr,firstIndex,lastIndex);
								counter++;
								firstIndex = 0;
								lastIndex = 0;
								flag = 1;
							}
						}
					}
				}
				sum = String.valueOf(Integer.parseInt(numbers[0]) + Integer.parseInt(numbers[1]));
				message.setMessage(sum);
				client.sendToClient(message);

			}else{
				//add code here to send received message to all clients.
				//The string we received in the message is the message we will send back to all clients subscribed.
				//Example:
					// message received: "Good morning"
					// message sent: "Good morning"
				//see code for changing submitters IDs for help
				message.setMessage(request.toString());
				client.sendToClient(message);

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static String putNumbers(char[] exp, int F, int L)
	{
		char[] expReturn = new char[L-F+1];
		for(int i = 0; i < L-F+1; i++)
		{
			expReturn[i] = exp[i+F];
		}
		String value = String.valueOf(expReturn);
		return value;
	}

}

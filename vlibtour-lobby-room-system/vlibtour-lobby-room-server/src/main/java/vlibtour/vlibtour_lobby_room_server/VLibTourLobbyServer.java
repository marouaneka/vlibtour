/**
This file is part of the course CSC5002.

Copyright (C) 2017-2020 Télécom SudParis

This is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This software platform is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the muDEBS platform. If not, see <http://www.gnu.org/licenses/>.

Initial developer(s): Denis Conan
Contributor(s):
 */
package vlibtour.vlibtour_lobby_room_server;

import java.io.IOException;
import org.passay.PasswordGenerator;

import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.tools.jsonrpc.JsonRpcServer;
import static org.apache.commons.lang3.RandomStringUtils.*;


import vlibtour.vlibtour_lobby_room_api.InAMQPPartException;
import vlibtour.vlibtour_lobby_room_api.VLibTourLobbyService;

/**
 * This class implements the VLibTour lobby room server. This is the entry point
 * of the system / interface where clients calls arrive, e.g. when they want to
 * start and a tour, or just join an already created group.
 * <p>
 * When launched in its own process via a {@code java} command in shell script,
 * there is no call to {@link #close()}: the process is killed in the shell
 * script that starts this process. But, the class is a
 * {@link java.lang.Runnable} so that the lobby room server can be integrated in
 * another process.
 * <p>
 * An exception {@link UnsupportedOperationException} is thrown when you haven't
 * written the code.
 * <p>
 * When there is an annotation {@code Override} without a Javadoc documentation,
 * please refer to the original method for the documentation.
 * 
 * @author Denis Conan
 */
public class VLibTourLobbyServer implements Runnable, VLibTourLobbyService {
	/**
	 * the connection to the RabbitMQ broker.
	 */
	private Connection connection;
	/**
	 * the channel for consuming and producing.
	 */
	private Channel channel;
	/**
	 * the RabbitMQ JSON RPC server.
	 */
	private JsonRpcServer rpcServer;
	/**
	 * 
	 */
	private int port;
	/**
	 * 
	 */
	private String host;
	/**
	 * creates the lobby room server and the corresponding JSON server object.
	 * 
	 * @throws InAMQPPartException the exception thrown in case of a problem in the
	 *                             AMQP part.
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	public VLibTourLobbyServer() throws InAMQPPartException, IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "direct");
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, BINDING_KEY);
		rpcServer = new JsonRpcServer(channel, queueName, VLibTourLobbyService.class, this);
		port=factory.getPort();
		host=factory.getHost();
	
	
	}

	@Override
	public String createGroupAndJoinIt(final String groupId, final String userId) {
		String password=random(15);
		try {
		new ProcessBuilder("rabbitmqctl", "add_vhost" ,groupId).inheritIO().start().waitFor();
		new ProcessBuilder("rabbitmqctl", "add_user" ,userId, password).inheritIO().start().waitFor();
		new ProcessBuilder("rabbitmqctl", "set_permissions" ,"-p",groupId, userId, ".*", ".*", ".*").inheritIO().start().waitFor();
		}catch(IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "amqp://"+userId+":"+password+"@"+host+":"+port+"/"+groupId;
		return url;
	}

	@Override
	public String joinAGroup(final String groupId, final String userId) {
		String password=random(15);
		try {
		new ProcessBuilder("rabbitmqctl", "add_user" ,userId, password).inheritIO().start().waitFor();
		new ProcessBuilder("rabbitmqctl", "set_permissions" ,"-p",groupId, userId, ".*", ".*", ".*").inheritIO().start().waitFor();
		}catch(IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "amqp://"+userId+":"+password+"@"+host+":"+port+"/"+groupId;
		return url;	
		}

	/**
	 * creates the JSON RPC server and enters into the main loop of the JSON RPC
	 * server. The exit to the main loop is done when calling
	 * {@code stopLobbyRoom()} on the administration server. At the end of this
	 * method, the connectivity is closed.
	 */
	@Override
	public void run() {
		try {
			rpcServer.mainloop();
		} catch (IOException e) {
			e.printStackTrace();
		}	}

	/**
	 * calls for the termination of the main loop if not already done and then
	 * closes the connection and the channel of this server.
	 * 
	 * @throws InAMQPPartException the exception thrown in case of a problem in the
	 *                             AMQP part.
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	public void close() throws InAMQPPartException, IOException, TimeoutException {
		if (rpcServer != null) {
			rpcServer.terminateMainloop();
			rpcServer.close();
		}
		if (channel != null) {
			channel.close();
		}
		if (connection != null) {
			connection.close();
		}	}

	/**
	 * starts the lobby server.
	 * <p>
	 * When launched in its own process via a {@code java} command in shell script,
	 * there is no call to {@link #close()}: the process is killed in the shell
	 * script that starts this process.
	 * 
	 * @param args command line arguments
	 * @throws Exception all the potential problems (since this is a demonstrator,
	 *                   apply the strategy "fail fast", i.e. logging without any
	 *                   treatment).
	 */
	public static void main(final String[] args) throws Exception {
		VLibTourLobbyServer rpcServer = new VLibTourLobbyServer();
		rpcServer.run();	
		}
}

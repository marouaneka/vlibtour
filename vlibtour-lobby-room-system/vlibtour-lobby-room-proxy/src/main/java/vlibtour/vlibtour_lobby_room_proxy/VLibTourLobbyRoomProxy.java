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
package vlibtour.vlibtour_lobby_room_proxy;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.tools.jsonrpc.JsonRpcClient;
import com.rabbitmq.tools.jsonrpc.JsonRpcException;


import vlibtour.vlibtour_lobby_room_api.VLibTourLobbyService;

/**
 * The AMQP/RabbitMQ Proxy (for clients) of the Lobby Room Server.
 * 
 * @author Denis Conan
 */
public class VLibTourLobbyRoomProxy {
	/**
	 * the connection to the RabbitMQ broker.
	 */
	private Connection connection;
	/**
	 * the channel for producing and consuming.
	 */
	private Channel channel;
	/**
	 * the RabbitMQ JSON RPC client.
	 */
	private JsonRpcClient jsonRpcClient;
	/**
	 * the VLibTourLobbyService service.
	 */
	private VLibTourLobbyService client;
	/**
	 * the group Identifier
	 */
	private String groupId;


	public VLibTourLobbyRoomProxy(String tourId, String userId) throws IOException, JsonRpcException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();
		jsonRpcClient = new JsonRpcClient(channel, VLibTourLobbyService.EXCHANGE_NAME, VLibTourLobbyService.BINDING_KEY);
		client = jsonRpcClient.createProxy(VLibTourLobbyService.class);
		groupId = tourId + VLibTourLobbyService.GROUP_TOUR_USER_DELIMITER +userId;
	}
	
	public VLibTourLobbyRoomProxy(String groupId, String tourId, String userId) throws IOException, JsonRpcException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();
		jsonRpcClient = new JsonRpcClient(channel, VLibTourLobbyService.EXCHANGE_NAME, VLibTourLobbyService.BINDING_KEY);
		client = jsonRpcClient.createProxy(VLibTourLobbyService.class);
	}
	
	
	public String createGroupAndJoinIt(final String tourId, final String userId) {
		return client.createGroupAndJoinIt(groupId, userId);
	}
	
	
	public String joinAGroup(final String groupId, final String userId) {
		return client.joinAGroup(groupId, userId);
	}
	/**
	 * closes the JSON RPC client, the channel and the connection with the broker.
	 * 
	 * @throws IOException
	 *             communication problem.
	 * @throws TimeoutException
	 *             broker to long to communicate with.
	 */
	public void close() throws IOException, TimeoutException {
		jsonRpcClient.close();
		if (channel != null) {
			channel.close();
		}
		if (connection != null) {
			connection.close();
		}
	}
	
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
}

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
package vlibtour.vlibtour_group_communication_proxy;

import java.io.IOException;


import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import vlibtour.vlibtour_lobby_room_api.VLibTourLobbyService;



/**
 * The AMQP/RabbitMQ Proxy (for clients) of the VLibTour Group Communication
 * System.
 * 
 * @author Denis Conan
 */
public class VLibTourGroupCommunicationSystemProxy {
	/**
	 * the connection to the RabbitMQ broker.
	 */
	private static String exchangeName="user1_group1";
	/**
	 * the connection to the RabbitMQ broker.
	 */
	private Connection connection;
	
	/**
	 * the queue name.
	 */
	
	private String queueName;
	/**
	 * the consumer thread.
	 */
	private Consumer consumer;
	
	/**
	 * the channel for producing.
	 */
	private Channel channel;

	/**
	 * the number of messages that have been received by this consumer.
	 */
	private int nbMsgReceived = 0;
	public void setNbMsgReceived(int nbMsgReceived) {
		this.nbMsgReceived = nbMsgReceived;
	}


	/**
	 * the total number of messages that have been received by all the consumers.
	 * This attribute is shared by all the consumers when executed in JUnit tests.
	 * Then, this is an atomic integer.
	 */
	private static AtomicInteger totalNbMsgReceived = new AtomicInteger(0);
	
	public VLibTourGroupCommunicationSystemProxy(String tourId,String userId,Optional<String> groupId) throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		queueName=tourId+"_"+userId;
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();
		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
	
		
		}
	
	
	public VLibTourGroupCommunicationSystemProxy(String urlToGCS) throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri(urlToGCS);
		String userId=factory.getUsername();
		String tourId=factory.getHost().split(VLibTourLobbyService.GROUP_TOUR_USER_DELIMITER)[0];
		queueName=tourId+"_"+userId;
		connection = factory.newConnection();
		channel = connection.createChannel();
		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);

		
		}
	public String getQueueName() {
		return queueName;
	}
	public static String getEXCHANGE_NAME() {
		return exchangeName;
	}
	public static void setEXCHANGE_NAME(String eXCHANGE_NAME) {
		exchangeName = eXCHANGE_NAME;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public void publish(String message,String userId,String type,String dest) throws UnsupportedEncodingException, IOException {
		String routingKey=userId+"."+dest+"."+type;
		channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
		System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

		}
	public void setConsumer(Consumer consumer, String queueName, String bindingKey) throws IOException, TimeoutException  {
		
		channel.queueDeclare(queueName, true, false, false, null);
		channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);
		channel.queueBind(queueName, exchangeName, bindingKey);
		this.consumer=consumer;
		
	}
	public String startConsumption() throws IOException {
		return channel.basicConsume(this.queueName, true, this.consumer);
		
		}
	/**
	 * closes the channel and the connection with the broker.
	 * 
	 * @throws IOException      communication problem.
	 * @throws TimeoutException broker to long to communicate with.
	 */
	public void close() throws IOException, TimeoutException {
		if (channel != null) {
			channel.close();
		}
		if (connection != null) {
			connection.close();
		}
	}
	/**
	 * closes the channel and the connection with the broker.
	 * 
	 * @throws IOException      communication problem.
	 * @throws TimeoutException broker to long to communicate with.
	 */
	
	/**
	 * gets the number of messages received by this consumer.
	 * 
	 * @return the number of messages.
	 */
	public int getNbMsgReceived() {
		return nbMsgReceived;
	}

	/**
	 * gets the number of messages received by all the consumers. The method is not
	 * synchronised since the integer is atomic.
	 * 
	 * @return the total number of messages.
	 */
	public static int getNTotalbMsgReceived() {
		return totalNbMsgReceived.get();
	}
	public static int incrementNTotalbMsgReceived() {
		return totalNbMsgReceived.getAndIncrement();
	}
	public Consumer getConsumer() {
		return consumer;
	}

	
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	

	
	public static void setTotalNbMsgReceived(AtomicInteger totalNbMsgReceived) {
		VLibTourGroupCommunicationSystemProxy.totalNbMsgReceived = totalNbMsgReceived;
	}




	
	

}

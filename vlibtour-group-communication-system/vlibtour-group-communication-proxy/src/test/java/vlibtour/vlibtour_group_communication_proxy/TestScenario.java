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
// CHECKSTYLE:OFF
package vlibtour.vlibtour_group_communication_proxy;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.http.client.Client;


import vlibtour.vlibtour_lobby_room_api.InAMQPPartException;

public class TestScenario {

	private static Client c;

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException, URISyntaxException {
		Runtime r = Runtime.getRuntime();
		Process wc = r.exec("netstat -nlp | grep \"[[:space:]]5672\"");
		wc.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(wc.getInputStream()));
		String line = "";
		while ((line = b.readLine()) != null) {
			if (line.contains(":5672")) {
				System.out.println("Warning: Already a RabbitMQ server on address :::5672 =>  stop it");
				new ProcessBuilder("rabbitmqctl", "stop").inheritIO().start().waitFor();
			}
		}
		Thread.sleep(1000);
		new ProcessBuilder("rabbitmq-server", "-detached").inheritIO().start().waitFor();
		Thread.sleep(5000);
		new ProcessBuilder("rabbitmqctl", "stop_app").inheritIO().start().waitFor();
		new ProcessBuilder("rabbitmqctl", "reset").inheritIO().start().waitFor();
		new ProcessBuilder("rabbitmqctl", "start_app").inheritIO().start().waitFor();
		c = new Client("http://127.0.0.1:15672/api/", "guest", "guest");
}

	// Be careful! Remove annotation @Ignore for executing this test.
	
	@Test
	public void test()
			throws IOException, TimeoutException, InterruptedException, ExecutionException, InAMQPPartException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		
		VLibTourGroupCommunicationSystemProxy user1=new VLibTourGroupCommunicationSystemProxy("tour1","user1",null);
		VLibTourGroupCommunicationSystemProxy user2=new VLibTourGroupCommunicationSystemProxy("tour1","user2",null);
		VLibTourGroupCommunicationSystemProxy user3=new VLibTourGroupCommunicationSystemProxy("tour1","user3",null);

		Thread.sleep(5000);
		Consumer consumer = new DefaultConsumer(user2.getChannel()) {
			@Override
			public void handleDelivery(final String consumerTag, final Envelope envelope,
					final AMQP.BasicProperties properties, final byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '"  + " " + envelope.getRoutingKey() + "':'" + message + "'");
				user2.setNbMsgReceived(user2.getNbMsgReceived()+1);
				VLibTourGroupCommunicationSystemProxy.incrementNTotalbMsgReceived();
			
			}
		};
		Consumer consumer2 = new DefaultConsumer(user3.getChannel()) {
			@Override
			public void handleDelivery(final String consumerTag, final Envelope envelope,
					final AMQP.BasicProperties properties, final byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '"  + " " + envelope.getRoutingKey() + "':'" + message + "'");
				user3.setNbMsgReceived(user3.getNbMsgReceived()+1);
				VLibTourGroupCommunicationSystemProxy.incrementNTotalbMsgReceived();
			
			}
		};
		Thread.sleep(5000);
		user2.setConsumer(consumer, user2.getQueueName(), "*.all.*");
		user3.setConsumer(consumer2, user3.getQueueName(), "*.all.*");
		Thread.sleep(5000);
		user1.publish("ya m3afet", "user1", "String", "all");
		user2.startConsumption();
		user3.startConsumption();
		Thread.sleep(5000);
		Assert.assertEquals(1, user2.getNbMsgReceived());
		Assert.assertEquals(1, user3.getNbMsgReceived());
		Assert.assertEquals(2, VLibTourGroupCommunicationSystemProxy.getNTotalbMsgReceived());
		user1.close();
		user2.close();
		user3.close();

		
	}

	@AfterClass
	public static void tearDown() throws InterruptedException, IOException {
		new ProcessBuilder("rabbitmqctl", "stop_app").inheritIO().start().waitFor();
		new ProcessBuilder("rabbitmqctl", "stop").inheritIO().start().waitFor();
	}
}

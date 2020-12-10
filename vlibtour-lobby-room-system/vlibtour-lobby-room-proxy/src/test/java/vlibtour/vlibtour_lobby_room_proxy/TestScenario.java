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
package vlibtour.vlibtour_lobby_room_proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.tools.jsonrpc.JsonRpcException;

import vlibtour.vlibtour_lobby_room_api.InAMQPPartException;
import vlibtour.vlibtour_lobby_room_server.VLibTourLobbyServer;

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
	public void test() throws IOException, TimeoutException, InterruptedException, ExecutionException,
			InAMQPPartException, JsonRpcException {
		VLibTourLobbyServer rpcServer = new VLibTourLobbyServer();
		new Thread(rpcServer).start();
		Assert.assertNotNull(c.getExchanges().stream().filter(q -> q.getName().equals(VLibTourLobbyServer.EXCHANGE_NAME)));
		Assert.assertNotNull(c.getBindings().stream().filter(b -> b.getRoutingKey().equals(VLibTourLobbyServer.BINDING_KEY)));
		VLibTourLobbyRoomProxy rpcClient1 = new VLibTourLobbyRoomProxy("tour1","user1");

		VLibTourLobbyRoomProxy rpcClient2 = new VLibTourLobbyRoomProxy(rpcClient1.getGroupId(),"tour1","user2");

		VLibTourLobbyRoomProxy rpcClient3 = new VLibTourLobbyRoomProxy(rpcClient1.getGroupId(),"tour1","user3");

		rpcClient1.createGroupAndJoinIt("tour1", "user1");
		rpcClient2.joinAGroup(rpcClient1.getGroupId(),"user2");
		rpcClient3.joinAGroup(rpcClient1.getGroupId(),"user3");
		
		ProcessBuilder pb = new ProcessBuilder("rabbitmqctl", "list_permissions","-p",rpcClient1.getGroupId());
		Process p = pb.start();
		BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		StringBuilder sb = new StringBuilder();
		while((line=br.readLine())!=null) sb.append(line);
		String result = sb.toString();
		
		Assert.assertEquals(result.contains("user1"), true);
		Assert.assertEquals(result.contains("user2"), true);
		Assert.assertEquals(result.contains("user3"), true);
		
		Thread.sleep(5000); // wait for consume to read the message
		rpcServer.close();
		rpcClient1.close();
		rpcClient2.close();
		rpcClient3.close();
		
		
	}

	@AfterClass
	public static void tearDown() throws InterruptedException, IOException {
		new ProcessBuilder("rabbitmqctl", "stop_app").inheritIO().start().waitFor();
		new ProcessBuilder("rabbitmqctl", "stop").inheritIO().start().waitFor();
	}
}

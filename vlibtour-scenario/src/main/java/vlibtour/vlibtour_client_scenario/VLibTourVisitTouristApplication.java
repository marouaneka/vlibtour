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
package vlibtour.vlibtour_client_scenario;

import static vlibtour.vlibtour_common.Log.EMULATION;

import static vlibtour.vlibtour_common.Log.LOG_ON;
import vlibtour.vlibtour_common.ExampleOfAVisitWithTwoTourists;
import vlibtour.vlibtour_common.GPSPosition;
import vlibtour.vlibtour_common.Position;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import javax.naming.NamingException;

import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.tools.jsonrpc.JsonRpcException;

import vlibtour.vlibtour_visit_emulation_proxy.VLibTourVisitEmulationProxy;
import vlibtour.vlibtour_group_communication_proxy.VLibTourGroupCommunicationSystemProxy;
import vlibtour.vlibtour_lobby_room_proxy.VLibTourLobbyRoomProxy;
import vlibtour.vlibtour_lobby_room_server.VLibTourLobbyServer;
import vlibtour.vlibtour_client_scenario.map_viewer.BasicMap;
import vlibtour.vlibtour_client_scenario.map_viewer.MapHelper;
import vlibtour.vlibtour_lobby_room_api.InAMQPPartException;
import vlibtour.vlibtour_lobby_room_api.VLibTourLobbyService;
import vlibtour.vlibtour_tour_management.entity.VlibTourTourManagementException;

/**
 * This class is the client application of the tourists. The access to the lobby
 * room server, to the group communication system, and to the visit emulation
 * system should be implemented using the design pattern Delegation (see
 * https://en.wikipedia.org/wiki/Delegation_pattern).
 * <p>
 * A client creates two queues to receive messages from the broker; the binding
 * keys are of the form "{@code *.*.identity}" and "{@code *.*.location}" while
 * the routing keys are of the form "{@code sender.receiver.identity|location}".
 * <p>
 * This class uses the classes
 * {@link vlibtour.vlibtour_client_scenario.map_viewer.MapHelper} and
 * {@link vlibtour.vlibtour_client_scenario.map_viewer.BasicMap} for displaying
 * the tourists on the map of Paris. Use the attributes for the color, the map,
 * the map marker dot, etc.
 * 
 * @author Denis Conan
 */
public class VLibTourVisitTouristApplication {
	/**
	 * the colour onto the map of the user identifier of the first tourist.
	 */
	private static final Color colorJoe = Color.RED;
	/**
	 * the colour onto the map of the user identifier of the second tourist.
	 */
	private static final Color colorAvrel = Color.GREEN;
	/**
	 * the map to manipulate. Not all the clients need to have a map; therefore we
	 * use an optional.
	 * <p>
	 * The annotation {@code @SuppressWarnings} is useful as long as you do not use
	 * this attribute.
	 */
	private Optional<BasicMap> map = Optional.empty();
	/**
	 * the dot on the map for the first tourist.
	 * <p>
	 * The annotation {@code @SuppressWarnings} is useful as long as you do not use
	 * this attribute.
	 */
	
	private static MapMarkerDot mapDotJoe;
	/**
	 * the dot on the map for the second tourist.
	 * <p>
	 * The annotation {@code @SuppressWarnings} is useful as long as you do not use
	 * this attribute. Please remove the annotation when this is so.
	 */
	private static MapMarkerDot mapDotAvrel;
	/**
	 * delegation to the proxy of type
	 * {@link vlibtour.vlibtour_emulation_visit_proxy.VLibTourVisitEmulationProxy}.
	 * <p>
	 * The annotation {@code @SuppressWarnings} is useful as long as you do not use
	 * this attribute. Please remove the annotation when this is so.
	 */
	private VLibTourVisitEmulationProxy emulationVisitProxy;
	/**
	 * delegation to the proxy of type
	 * {@link vlibtour.vlibtour_lobby_room_proxy.VLibTourLobbyRoomProxy}.
	 * <p>
	 * The annotation {@code @SuppressWarnings} is useful as long as you do not use
	 * this attribute. Please remove the annotation when this is so.
	 */
	
	private static VLibTourLobbyRoomProxy lobbyRoomProxy;
	/**
	 * delegation to the proxy of type
	 * {@link vlibtour.vlibtour_group_communication_proxy.VLibTourGroupCommunicationSystemProxy}.
	 * The identifier of the user is obtained from this reference.
	 * <p>
	 * The annotation {@code @SuppressWarnings} is useful as long as you do not use
	 * this attribute. Please remove the annotation when this is so.
	 */
	
	private static VLibTourGroupCommunicationSystemProxy groupCommProxy;

	/**
	 * creates a client application, which will join a group that must already
	 * exist. The group identifier is optional when this is the first user of the
	 * group ---i.e. the group identifier is built upon the user identifier.
	 * Concerning the tour identifier, it must be provided by the calling method.
	 * 
	 * @param tourId  the tour identifier of this application.
	 * @param groupId the group identifier of this client application.
	 * @param userId  the user identifier of this client application.
	 * @throws InAMQPPartException             the exception thrown in case of a
	 *                                         problem in the AMQP part.
	 * @throws VlibTourTourManagementException problem in the name or description of
	 *                                         POIs.
	 * @throws IOException                     problem when setting the
	 *                                         communication configuration with the
	 *                                         broker.
	 * @throws TimeoutException                problem in creation of connection,
	 *                                         channel, client before the RPC to the
	 *                                         lobby room.
	 * @throws JsonRpcException                problem in creation of connection,
	 *                                         channel, client before the RPC to the
	 *                                         lobby room.
	 * @throws InterruptedException            thread interrupted in call sleep.
	 * @throws NamingException                 the EJB server has not been found
	 *                                         when getting the tour identifier.
	 * @throws URISyntaxException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public VLibTourVisitTouristApplication(final String tourId, final Optional<String> groupId, final String userId)
		throws InAMQPPartException, VlibTourTourManagementException, IOException, JsonRpcException,
			TimeoutException, InterruptedException, NamingException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		emulationVisitProxy=new VLibTourVisitEmulationProxy();
		lobbyRoomProxy=new VLibTourLobbyRoomProxy(tourId,userId);
		
}

	/**
	 * executes the tourist application.
	 * <p>
	 * We prefer inserting comments in the method instead of detailing the method
	 * here.
	 * 
	 * @param args the command line arguments.
	 * @throws Exception all the potential problems (since this is a demonstrator,
	 *                   apply the strategy "fail fast").
	 */
	public static void main(final String[] args) throws Exception {
		String usage = "USAGE: " + VLibTourVisitTouristApplication.class.getCanonicalName()
				+ " userId (either Joe or Avrel)";
		if (args.length != 1) {
			throw new IllegalArgumentException(usage);
		}
		String userId = args[0];
		String url;
		String tourId="TheunusualParis";
		final VLibTourVisitTouristApplication client = new VLibTourVisitTouristApplication(tourId,Optional.empty(),userId);
		if (userId.equals(ExampleOfAVisitWithTwoTourists.USER_ID_JOE)) {
			url=lobbyRoomProxy.createGroupAndJoinIt("TheunusualParis", userId);

			groupCommProxy=new VLibTourGroupCommunicationSystemProxy(url);

		}
		else {
			url=lobbyRoomProxy.joinAGroup("TheunusualParis" + VLibTourLobbyService.GROUP_TOUR_USER_DELIMITER +ExampleOfAVisitWithTwoTourists.USER_ID_JOE, userId);
			groupCommProxy=new VLibTourGroupCommunicationSystemProxy(url);

		}
		Consumer consumer = new DefaultConsumer(groupCommProxy.getChannel()) {
			@Override
			public void handleDelivery(final String consumerTag, final Envelope envelope,
					final AMQP.BasicProperties properties, final byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				//System.out.println(" [x] Received '"  + " " + envelope.getRoutingKey() + "':'" + message + "'");
				Position posOther = new Position(message);
				System.out.println("3fat"+posOther.toString());
				if (userId.equals(ExampleOfAVisitWithTwoTourists.USER_ID_JOE)){
					
					String fields=envelope.getRoutingKey();
					String user = fields.substring(0,fields.indexOf("."));
					
					if (!user.equals(ExampleOfAVisitWithTwoTourists.USER_ID_JOE)) {
						MapHelper.moveTouristOnMap(mapDotAvrel, posOther);
					}

				}
				else {
					String fields=envelope.getRoutingKey();
					String user = fields.substring(0,fields.indexOf("."));
					
					if (!user.equals(ExampleOfAVisitWithTwoTourists.USER_ID_JOE)) {
						MapHelper.moveTouristOnMap(mapDotJoe, posOther);
					}

				}
				groupCommProxy.setNbMsgReceived(groupCommProxy.getNbMsgReceived()+1);
				VLibTourGroupCommunicationSystemProxy.incrementNTotalbMsgReceived();
			
			}
		};
		groupCommProxy.setConsumer(consumer, groupCommProxy.getQueueName(), "*.all.*");
		if (LOG_ON && EMULATION.isInfoEnabled()) {
			EMULATION.info(userId + "'s application is starting");
		}
		
		if (LOG_ON && EMULATION.isDebugEnabled()) {
			EMULATION.debug("Current directory = " + System.getProperty("user.dir") + ".\n" + "We assume that class "
					+ client.getClass().getCanonicalName() + " is launched from directory "
					+ "./vlibtour-scenario/src/main/resources/osm-mapnik/");
		}
	
		client.map = Optional.of(MapHelper.createMapWithCenterAndZoomLevel(48.851412, 2.343166, 14));
		Font font = new Font("name", Font.BOLD, 20);
		client.map.ifPresent(m -> {
			MapHelper.addMarkerDotOnMap(m, 48.871799, 2.342355, Color.BLACK, font, "Musée Grevin");
			MapHelper.addMarkerDotOnMap(m, 48.860959, 2.335757, Color.BLACK, font, "Pyramide du Louvres");
			MapHelper.addMarkerDotOnMap(m, 48.833566, 2.332416, Color.BLACK, font, "Les catacombes");

			
			mapDotJoe = MapHelper.addTouristOnMap(m, Color.RED, font, "Joe",
					 client.emulationVisitProxy.getCurrentPosition(userId));
			mapDotAvrel= MapHelper.addTouristOnMap(m, Color.GREEN, font, "Avrel",
					 client.emulationVisitProxy.getCurrentPosition(userId));
		
			
			
			client.map.get().repaint();
			// wait for painting the map
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		Position pos=client.emulationVisitProxy.getCurrentPosition(userId);
		while (true) {
			
			Position newpos=client.emulationVisitProxy.stepInCurrentPath(userId);
			if(!pos.equals(newpos))
			{ 	pos=newpos;
		

				if (userId.equals(ExampleOfAVisitWithTwoTourists.USER_ID_JOE)){
					MapHelper.moveTouristOnMap(mapDotJoe, pos);
					groupCommProxy.publish(newpos.jsonString(), userId, "position", "all");
					Thread.sleep(5000);

					groupCommProxy.startConsumption();
					
					client.map.get().repaint();
					Thread.sleep(3000);
				}
				else if(userId.equals(ExampleOfAVisitWithTwoTourists.USER_ID_AVREL)) {

					MapHelper.moveTouristOnMap(mapDotAvrel, pos);
					groupCommProxy.publish(newpos.jsonString(), userId, "position", "all");
					Thread.sleep(5000);
					groupCommProxy.startConsumption();
					
					client.map.get().repaint();
					Thread.sleep(3000);
				}
				else {
					System.out.println("user n'existe pas");
				}
				
			}
			else {
				Position poi1= client.emulationVisitProxy.getNextPOIPosition(userId);
				client.emulationVisitProxy.stepsInVisit(userId);
				Position poi2= client.emulationVisitProxy.getNextPOIPosition(userId);	
				if (poi1.equals(poi2))
				{
					System.out.println("\nend of visit!\n");
					System.exit(0);
				}
			}
			
			
		}
		
	}
}

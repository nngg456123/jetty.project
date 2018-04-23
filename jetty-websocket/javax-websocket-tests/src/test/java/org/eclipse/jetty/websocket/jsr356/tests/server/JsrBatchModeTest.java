//
//  ========================================================================
//  Copyright (c) 1995-2018 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.websocket.jsr356.tests.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.jsr356.tests.LocalServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsrBatchModeTest
{
    public static class BasicEchoEndpoint extends Endpoint implements MessageHandler.Whole<String>
    {
        private javax.websocket.Session session;
        
        @Override
        public void onMessage(String msg)
        {
            // reply with echo
            session.getAsyncRemote().sendText(msg);
        }
        
        @Override
        public void onOpen(javax.websocket.Session session, EndpointConfig config)
        {
            this.session = session;
            this.session.addMessageHandler(this);
        }
    }
    
    private LocalServer server;
    private WebSocketContainer client;

    @Before
    public void prepare() throws Exception
    {
        server = new LocalServer();
        server.start();

        ServerEndpointConfig config = ServerEndpointConfig.Builder.create(BasicEchoEndpoint.class, "/").build();
        server.getServerContainer().addEndpoint(config);

        client = ContainerProvider.getWebSocketContainer();
    }

    @After
    public void dispose() throws Exception
    {
        server.stop();
    }

    @Test
    public void testBatchModeOn() throws Exception
    {
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

        URI uri = server.getWsUri();

        final CountDownLatch latch = new CountDownLatch(1);
        EndpointAdapter endpoint = new EndpointAdapter()
        {
            @Override
            public void onMessage(String message)
            {
                latch.countDown();
            }
        };

        try (Session session = client.connectToServer(endpoint, config, uri))
        {
            RemoteEndpoint.Async remote = session.getAsyncRemote();
            remote.setBatchingAllowed(true);

            Future<Void> future = remote.sendText("batch_mode_on");
            // The write is aggregated and therefore completes immediately.
            future.get(1, TimeUnit.MICROSECONDS);

            // Did not flush explicitly, so the message should not be back yet.
            assertFalse(latch.await(1, TimeUnit.SECONDS));

            // Explicitly flush.
            remote.flushBatch();

            // Wait for the echo.
            assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testBatchModeOff() throws Exception
    {
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

        URI uri = server.getWsUri();

        final CountDownLatch latch = new CountDownLatch(1);
        EndpointAdapter endpoint = new EndpointAdapter()
        {
            @Override
            public void onMessage(String message)
            {
                latch.countDown();
            }
        };

        try (Session session = client.connectToServer(endpoint, config, uri))
        {
            RemoteEndpoint.Async remote = session.getAsyncRemote();
            remote.setBatchingAllowed(false);

            Future<Void> future = remote.sendText("batch_mode_off");
            // The write is immediate.
            future.get(1, TimeUnit.SECONDS);

            // Wait for the echo.
            assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testBatchModeAuto() throws Exception
    {
        ClientEndpointConfig config = ClientEndpointConfig.Builder.create().build();

        URI uri = server.getWsUri();

        final CountDownLatch latch = new CountDownLatch(1);
        EndpointAdapter endpoint = new EndpointAdapter()
        {
            @Override
            public void onMessage(String message)
            {
                latch.countDown();
            }
        };

        try (Session session = client.connectToServer(endpoint, config, uri))
        {
            RemoteEndpoint.Async remote = session.getAsyncRemote();

            Future<Void> future = remote.sendText("batch_mode_auto");
            // The write is immediate, as per the specification.
            future.get(1, TimeUnit.SECONDS);

            // Wait for the echo.
            assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    public static abstract class EndpointAdapter extends Endpoint implements MessageHandler.Whole<String>
    {
        @Override
        public void onOpen(Session session, EndpointConfig config)
        {
            session.addMessageHandler(this);
        }
    }
}

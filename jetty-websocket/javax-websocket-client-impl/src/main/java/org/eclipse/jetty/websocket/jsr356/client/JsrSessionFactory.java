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

package org.eclipse.jetty.websocket.jsr356.client;

import java.net.URI;

import org.eclipse.jetty.websocket.common.LogicalConnection;
import org.eclipse.jetty.websocket.common.SessionFactory;
import org.eclipse.jetty.websocket.core.WebSocketSession;
import org.eclipse.jetty.websocket.jsr356.ConfiguredEndpoint;
import org.eclipse.jetty.websocket.jsr356.JavaxWebSocketSession;
import org.eclipse.jetty.websocket.jsr356.client.ClientContainer;

public class JsrSessionFactory implements SessionFactory
{
    private final ClientContainer container;

    public JsrSessionFactory(ClientContainer container)
    {
        this.container = container;
    }

    @Override
    public WebSocketSession createSession(URI requestURI, Object websocket, LogicalConnection connection)
    {
        return new JavaxWebSocketSession(container,connection.getId(),requestURI,websocket,connection);
    }

    @Override
    public boolean supports(Object websocket)
    {
        return (websocket instanceof ConfiguredEndpoint);
    }
}

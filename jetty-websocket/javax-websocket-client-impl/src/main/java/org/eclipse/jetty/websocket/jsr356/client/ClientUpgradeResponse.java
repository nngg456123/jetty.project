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

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.websocket.core.extensions.ExtensionConfig;

/**
 * Internal Implementation of a {@link org.eclipse.jetty.websocket.core.handshake.UpgradeResponse}
 */
public class ClientUpgradeResponse extends UpgradeResponseAdapter
{
    private List<ExtensionConfig> extensions;

    public ClientUpgradeResponse()
    {
        super();
    }
    
    public ClientUpgradeResponse(HttpResponse response)
    {
        super();
        setStatusCode(response.getStatus());
        setStatusReason(response.getReason());

        HttpFields fields = response.getHeaders();
        for (HttpField field : fields)
        {
            addHeader(field.getName(),field.getValue());
        }

        HttpField extensionsField = fields.getField(HttpHeader.SEC_WEBSOCKET_EXTENSIONS);
        if (extensionsField != null)
            this.extensions = ExtensionConfig.parseList(extensionsField.getValues());
        setAcceptedSubProtocol(fields.get(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL));
    }
    
    @Override
    public List<ExtensionConfig> getExtensions()
    {
        return this.extensions;
    }

    @Override
    public void sendForbidden(String message) throws IOException
    {
        throw new UnsupportedOperationException("Not supported on client implementation");
    }
}

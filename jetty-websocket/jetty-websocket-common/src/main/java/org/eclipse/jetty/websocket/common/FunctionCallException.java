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

package org.eclipse.jetty.websocket.common;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jetty.websocket.core.WebSocketException;

public class FunctionCallException extends WebSocketException
{
    public FunctionCallException(String message)
    {
        super(message);
    }
    
    public FunctionCallException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public FunctionCallException(Throwable cause)
    {
        super(cause);
    }

    public Throwable getInvokedCause()
    {
        Throwable cause = getCause();
        if (cause instanceof InvocationTargetException)
        {
            return cause.getCause();
        }
        return cause;
    }
}

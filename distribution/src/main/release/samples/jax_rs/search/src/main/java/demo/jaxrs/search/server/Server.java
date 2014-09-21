/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package demo.jaxrs.search.server;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.search.SearchContextProvider;
import org.apache.cxf.jaxrs.ext.search.SearchUtils;
import org.apache.cxf.jaxrs.provider.MultipartProvider;
import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Server {

    protected Server() throws Exception {
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(9000);
        
        // Register and map the dispatcher servlet
        final ServletHolder servletHolder = new ServletHolder(new CXFNonSpringJaxrsServlet());
        final ServletContextHandler context = new ServletContextHandler();      
        context.setContextPath("/");
        context.addServlet(servletHolder, "/jaxrs/*");     
        
        servletHolder.setInitParameter("jaxrs.serviceClasses", Catalog.class.getName());
        servletHolder.setInitParameter("jaxrs.properties", StringUtils.join(
            new String[] {
                "search.query.parameter.name=$filter",
                SearchUtils.DATE_FORMAT_PROPERTY + "=yyyy/MM/dd"
            }, " ")            
        );
        servletHolder.setInitParameter("jaxrs.providers", StringUtils.join(
            new String[] {
                MultipartProvider.class.getName(),
                SearchContextProvider.class.getName(),
                JsrJsonpProvider.class.getName(),
                CrossOriginResourceSharingFilter.class.getName()
            }, ",") 
        );                
        
        // Configuring all static web resource
        final ServletHolder staticHolder = new ServletHolder(new DefaultServlet());
        final ServletContextHandler htmls = new ServletContextHandler();
        htmls.setContextPath("/browser");
        htmls.addServlet(staticHolder, "/*");
        htmls.setResourceBase(getClass().getResource("/browser").toURI().toString());

        final HandlerList handlers = new HandlerList();
        handlers.addHandler(htmls);
        handlers.addHandler(context);        
        
        server.setHandler(handlers);
        server.start();
        server.join();
    }

    public static void main(String args[]) throws Exception {
        new Server();
        System.out.println("Server ready...");

        Thread.sleep(5 * 6000 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}

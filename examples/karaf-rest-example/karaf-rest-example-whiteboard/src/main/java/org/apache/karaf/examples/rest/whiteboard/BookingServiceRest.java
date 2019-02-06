/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.examples.rest.whiteboard;

import org.apache.karaf.examples.rest.api.Booking;
import org.apache.karaf.examples.rest.api.BookingService;
import org.osgi.service.component.annotations.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/booking")
@Component(service = BookingServiceRest.class, property = { "osgi.jaxrs.resource=true" })
public class BookingServiceRest implements BookingService {
    
	List<Booking> userList = new ArrayList<>();

    @Override
    @GET
	@Path("/getusers")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
    public  List<Booking> list() {
        return userList;
    }

    @Override
    @GET
	@Path("/getuser/{id}")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
    public Booking getUser(@PathParam("id") String id) {
        return findById(id);
    }
    
    
    @Override
    @POST
	@Path("/adduser")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response addUser(Booking user) {
		for (Booking element : userList) {
			if (element.getId() == user.getId()) {
				return Response.status(Response.Status.CONFLICT).build();
			}
		}
		userList.add(user);
		return Response.ok(user).build();
	}
    
    @Override
    @PUT
	@Path("/updateuser")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response updateUser(String id, Booking user) {
		Booking existingBooking = findById(id);
		if (existingBooking == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		if (existingBooking.equals(user)) {
			return Response.notModified().build();
		}
		userList.add(user);
		return Response.ok().build();
	}

    

    @Override
    @DELETE
    @Path("/deleteuser")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
    public Response deleteUser(@PathParam("id") String id) {
    	 Booking user = findById(id);
 	    if (user == null) {
 	        return Response.status(Response.Status.NOT_FOUND).build();
 	    }
 	    userList.remove(user);
 	    return Response.ok().build();
    }
    
    private Booking findById(String id) {
		for (Booking user : userList) {
			if (user.getId() == id) {
				return user;
			}
		}
		return null;
	}
}

/**
 *  Copyright 2005-2015 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package jelena.eshopping;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Customer class is just a plain old java object, with a few properties and getters and setters.
 * <p/>
 * By adding the @XmlRootElement annotation, we make it possible for JAXB to unmarshal this object into a XML document and
 * to marshal it back from the same XML document.
 * <p/>
 * The XML representation of a Customer will look like this:
 * <Customer>
 * <id>123</id>
 * <name>National Aquarium</name>
 * </Customer>
 */
@XmlRootElement(name = "Customer")
public class Customer {
	
	private static final Logger LOG = LoggerFactory.getLogger(Order.class);
	
    private long id;
    private String name;
    Map<Long, Order> orders = new HashMap<Long, Order>();
    long currentOrderId = 223;
    
    
    public Customer() {
        init();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
	public Order getOrder(@PathParam("orderId") String orderId) {
		long idNumber = Long.parseLong(orderId);
		Order o = orders.get(idNumber);
		return o;
	}

	public List<Order> getOrders() {
		return new ArrayList<Order>(orders.values());
	}
	
	
	public void addOrder(Order order) {
		order.setId(++currentOrderId);
		orders.put(order.getId(), order);		
	}
	
	public void deleteOrder(long orderID) {
		orders.remove(orderID);		
	}


	final void init() {
		Order o = new Order();
		o.setDescription("order 223");
		o.setId(currentOrderId);
		
        orders.put(o.getId(), o);
    }


}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Java class with be hosted in the URI path defined by the @Path
 * annotation. @Path annotations on the methods of this class always refer to a
 * path relative to the path defined at the class level.
 * <p/>
 * For example, with 'http://localhost:8181/cxf' as the default CXF servlet path
 * and '/crm' as the JAX-RS server path, this class will be hosted in
 * 'http://localhost:8181/cxf/crm/customerservice'. An @Path("/customers")
 * annotation on one of the methods would result in
 * 'http://localhost:8181/cxf/crm/customerservice/customers'.
 */
@Path("/customerservice/")
@Component(service = CustomerService.class, property = { "osgi.jaxrs.resource=true" })
public class CustomerService {
	private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

	long currentId = 123;

	Map<Long, Customer> customers = new HashMap<Long, Customer>();
	Map<Long, Product> products = new HashMap<Long, Product>();
	long currentProductId = 323;
	private MessageContext jaxrsContext;

	public CustomerService() {
		init();
	}

	/**
	 * This method is mapped to an HTTP GET of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/{id}'. The value for
	 * {id} will be passed to this message as a parameter, using the @PathParam
	 * annotation.
	 * <p/>
	 * The method returns a Customer object - for creating the HTTP response, this
	 * object is marshaled into XML using JAXB.
	 * <p/>
	 * For example: surfing to
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/123' will show you
	 * the information of customer 123 in XML format.
	 */
	@GET
	@Path("/customers/{id}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Customer getCustomer(@PathParam("id") String id) {
		LOG.info("Invoking getCustomer, Customer id is: {}", id);
		long idNumber = Long.parseLong(id);
		Customer c = customers.get(idNumber);
		return c;
	}

	/**
	 * This method is mapped to an HTTP GET of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/'.
	 * <p/>
	 * The method returns a Customer list - for creating the HTTP response, this
	 * object is marshaled into XML using JAXB.
	 * <p/>
	 * For example: surfing to
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/' will show you the
	 * information of customer list in XML format.
	 */
	@GET
	@Path("/customers/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public List<Customer> getCustomers() {
		LOG.info("Invoking getCustomers");
		return new ArrayList<Customer>(customers.values());
	}

	/**
	 * Using HTTP PUT, we can can upload the XML representation of a customer
	 * object. This operation will be mapped to the method below and the XML
	 * representation will get unmarshaled into a real Customer object using JAXB.
	 * <p/>
	 * The method itself just updates the customer object in our local data map and
	 * afterwards uses the Reponse class to build the appropriate HTTP response:
	 * either OK if the update succeeded (translates to HTTP Status 200/OK) or not
	 * modified if the method failed to update a customer object (translates to HTTP
	 * Status 304/Not Modified).
	 * <p/>
	 * Note how this method is using the same @Path value as our next method - the
	 * HTTP method used will determine which method is being invoked.
	 */
	@PUT
	@Path("/customers/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response updateCustomer(Customer customer) {
		LOG.info("Invoking updateCustomer, Customer name is: {}", customer.getName());
		Customer c = customers.get(customer.getId());
		Response r;
		if (c != null) {
			customers.put(customer.getId(), customer);
			r = Response.ok().build();
		} else {
			r = Response.notModified().build();
		}

		return r;
	}

	/**
	 * Using HTTP POST, we can add a new customer to the system by uploading the XML
	 * representation for the customer. This operation will be mapped to the method
	 * below and the XML representation will get unmarshaled into a real Customer
	 * object.
	 * <p/>
	 * After the method has added the customer to the local data map, it will use
	 * the Response class to build the HTTP reponse, sending back the inserted
	 * customer object together with a HTTP Status 200/OK. This allows us to send
	 * back the new id for the customer object to the client application along with
	 * any other data that might have been updated in the process.
	 * <p/>
	 * Note how this method is using the same @Path value as our previous method -
	 * the HTTP method used will determine which method is being invoked.
	 */
	@POST
	@Path("/customers/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response addCustomer(Customer customer) {
		LOG.info("Invoking addCustomer, Customer name is: {}", customer.getName());
		customer.setId(++currentId);

		customers.put(customer.getId(), customer);
		if (jaxrsContext.getHttpHeaders().getMediaType().getSubtype().equals("json")) {
			return Response.ok().type("application/json").entity(customer).build();
		} else {
			return Response.ok().type("application/xml").entity(customer).build();
		}
	}

	/**
	 * This method is mapped to an HTTP DELETE of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/{id}'. The value for
	 * {id} will be passed to this message as a parameter, using the @PathParam
	 * annotation.
	 * <p/>
	 * The method uses the Response class to create the HTTP response: either HTTP
	 * Status 200/OK if the customer object was successfully removed from the local
	 * data map or a HTTP Status 304/Not Modified if it failed to remove the object.
	 */
	@DELETE
	@Path("/customers/{id}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response deleteCustomer(@PathParam("id") String id) {
		LOG.info("Invoking deleteCustomer, Customer id is: {}", id);
		long idNumber = Long.parseLong(id);
		Customer c = customers.get(idNumber);

		Response r;
		if (c != null) {
			r = Response.ok().build();
			customers.remove(idNumber);
		} else {
			r = Response.notModified().build();
		}

		return r;
	}

	/**
	 * This method is mapped to an HTTP GET of
	 * 'http://localhost:8181/cxf/crm/customerservice/orders/{id}'. The value for
	 * {id} will be passed to this message as a parameter, using the @PathParam
	 * annotation.
	 * 
	 * 
	 * The method returns an Order object - the class for that object includes a few
	 * more JAX-RS annotations, allowing it to display one of these two outputs,
	 * depending on the actual URI path being used: - display the order information
	 * itself in XML format - display details about a product in the order in XML
	 * format in a path relative to the URI defined here
	 */
	@GET
	@Path("/customers/{id}/orders/{orderId}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Order getOrder(@PathParam("id") String id, @PathParam("orderId") String orderId) {
		LOG.info("Invoking orderId, Order id is: {}", orderId);
		long idNumber = Long.parseLong(id);
		customers.get(idNumber);
		return customers.get(idNumber).getOrder(orderId);
	}

	/**
	 * This method is mapped to an HTTP GET of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/'.
	 * <p/>
	 * The method returns a Customer list - for creating the HTTP response, this
	 * object is marshaled into XML using JAXB.
	 * <p/>
	 * For example: surfing to
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/' will show you the
	 * information of customer list in XML format.
	 */
	@GET
	@Path("/customers/{id}/orders/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public List<Order> getOrders(@PathParam("id") String id) {
		LOG.info("Invoking getOrders, Customer id is: {}", id);
		long idNumber = Long.parseLong(id);
		Customer c = customers.get(idNumber);
		return new ArrayList<Order>(customers.get(idNumber).getOrders());
	}

	/**
	 * Using HTTP POST, we can add a new customer to the system by uploading the XML
	 * representation for the customer. This operation will be mapped to the method
	 * below and the XML representation will get unmarshaled into a real Customer
	 * object.
	 * <p/>
	 * After the method has added the customer to the local data map, it will use
	 * the Response class to build the HTTP reponse, sending back the inserted
	 * customer object together with a HTTP Status 200/OK. This allows us to send
	 * back the new id for the customer object to the client application along with
	 * any other data that might have been updated in the process.
	 * <p/>
	 * Note how this method is using the same @Path value as our previous method -
	 * the HTTP method used will determine which method is being invoked.
	 */
	@POST
	@Path("/customers/{id}/orders/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response addOrder(@PathParam("id") String id, Order order) {
		LOG.info("Invoking addOrder, Order description is: {}", order.getDescription());
		long idNumber = Long.parseLong(id);
		Customer c = customers.get(idNumber);
		customers.get(idNumber).addOrder(order);
		if (jaxrsContext.getHttpHeaders().getMediaType().getSubtype().equals("json")) {
			return Response.ok().type("application/json").entity(order).build();
		} else {
			return Response.ok().type("application/xml").entity(order).build();
		}
	}

	/**
	 * This method is mapped to an HTTP DELETE of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/{id}'. The value for
	 * {id} will be passed to this message as a parameter, using the @PathParam
	 * annotation.
	 * <p/>
	 * The method uses the Response class to create the HTTP response: either HTTP
	 * Status 200/OK if the customer object was successfully removed from the local
	 * data map or a HTTP Status 304/Not Modified if it failed to remove the object.
	 */
	@DELETE
	@Path("/customers/{id}/orders/{orderId}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response deleteOrder(@PathParam("id") String id, @PathParam("orderId") String orderId) {
		LOG.info("Invoking deleteOrder, Orde id is: {}", orderId);
		long idNumber = Long.parseLong(id);
		Customer c = customers.get(idNumber);
		Order o = customers.get(idNumber).getOrder(orderId);

		Response r;
		if (o != null) {
			r = Response.ok().build();
			c.deleteOrder(Long.parseLong(orderId));
		} else {
			r = Response.notModified().build();
		}

		return r;
	}

	/**
	 * This method is mapped to an HTTP GET of
	 * 'http://localhost:8181/cxf/crm/customerservice/orders/{id}'. The value for
	 * {id} will be passed to this message as a parameter, using the @PathParam
	 * annotation.
	 * 
	 * 
	 * The method returns an Order object - the class for that object includes a few
	 * more JAX-RS annotations, allowing it to display one of these two outputs,
	 * depending on the actual URI path being used: - display the order information
	 * itself in XML format - display details about a product in the order in XML
	 * format in a path relative to the URI defined here
	 */
	@GET
	@Path("/customers/{id}/orders/{orderId}/products/{productId}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Product getOrderProduct(@PathParam("id") String id, @PathParam("orderId") String orderId,
			@PathParam("productId") String productId) {
		LOG.info("Invoking getOrderProduct, Product id is: {}", orderId);
		long idNumber = Long.parseLong(id);
		customers.get(idNumber);
		return customers.get(idNumber).getOrder(orderId).getProduct(Long.parseLong(productId));
	}

	/**
	 * This method is mapped to an HTTP GET of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/'.
	 * <p/>
	 * The method returns a Customer list - for creating the HTTP response, this
	 * object is marshaled into XML using JAXB.
	 * <p/>
	 * For example: surfing to
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/' will show you the
	 * information of customer list in XML format.
	 */
	@GET
	@Path("/customers/{id}/orders/{orderId}/products/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public List<Product> getOrderProducts(@PathParam("id") String id, @PathParam("orderId") String orderId) {
		LOG.info("Invoking getOrderProducts, Customer id is: {}", id);
		long idNumber = Long.parseLong(id);
		Customer c = customers.get(idNumber);
		return new ArrayList<Product>(customers.get(idNumber).getOrder(orderId).getProducts());
	}

	/**
	 * Using HTTP POST, we can add a new customer to the system by uploading the XML
	 * representation for the customer. This operation will be mapped to the method
	 * below and the XML representation will get unmarshaled into a real Customer
	 * object.
	 * <p/>
	 * After the method has added the customer to the local data map, it will use
	 * the Response class to build the HTTP reponse, sending back the inserted
	 * customer object together with a HTTP Status 200/OK. This allows us to send
	 * back the new id for the customer object to the client application along with
	 * any other data that might have been updated in the process.
	 * <p/>
	 * Note how this method is using the same @Path value as our previous method -
	 * the HTTP method used will determine which method is being invoked.
	 */
	@PUT
	@Path("/customers/{id}/orders/{orderId}/products/{productId}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response addOrderProduct(@PathParam("id") String id, @PathParam("orderId") String orderId, @PathParam("productId") String productId) {
		LOG.info("Invoking addOrderProduct, Order description is: {}", productId);
		long idNumber = Long.parseLong(id);
		Order o=customers.get(idNumber).getOrder(orderId);
		Product p=products.get(Long.parseLong(productId));
		Response r;		
				if (p != null) {
					r = Response.ok().build();
					o.addProduct(p);
				} else {
					r = Response.notModified().build();
				}
				return r;	
	}

	/**
	 * This method is mapped to an HTTP DELETE of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/{id}'. The value for
	 * {id} will be passed to this message as a parameter, using the @PathParam
	 * annotation.
	 * <p/>
	 * The method uses the Response class to create the HTTP response: either HTTP
	 * Status 200/OK if the customer object was successfully removed from the local
	 * data map or a HTTP Status 304/Not Modified if it failed to remove the object.
	 */
	@DELETE
	@Path("/customers/{id}/orders/{orderId}/products/{productId}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response deleteOrderProducts(@PathParam("id") String id, @PathParam("orderId") String orderId,
			@PathParam("productId") String productId) {
		LOG.info("Invoking deleteOrderProducts, Order id is: {}", orderId);
		long idNumber = Long.parseLong(id);
		Order o = customers.get(idNumber).getOrder(orderId);
		Product p = o.getProduct(Long.parseLong(productId));

		Response r;
		if (p != null) {
			r = Response.ok().build();
			o.deleteProduct(Long.parseLong(productId));
		} else {
			r = Response.notModified().build();
		}

		return r;
	}

	/**
	 * This method is mapped to an HTTP GET of
	 * 'http://localhost:8181/cxf/crm/customerservice/products/{id}'. The value for
	 * {id} will be passed to this message as a parameter, using the @PathParam
	 * annotation.
	 * <p/>
	 * The method returns an Order object - the class for that object includes a few
	 * more JAX-RS annotations, allowing it to display one of these two outputs,
	 * depending on the actual URI path being used: - display the order information
	 * itself in XML format - display details about a product in the order in XML
	 * format in a path relative to the URI defined here
	 */
	@GET
	@Path("/products/{productId}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Product getProduct(@PathParam("productId") String productId) {
		LOG.info("Invoking getProduct, Product id is: {}", productId);
		long idNumber = Long.parseLong(productId);
		Product p = products.get(idNumber);
		return p;
	}

	/**
	 * This method is mapped to an HTTP GET of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/'.
	 * <p/>
	 * The method returns a Customer list - for creating the HTTP response, this
	 * object is marshaled into XML using JAXB.
	 * <p/>
	 * For example: surfing to
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/' will show you the
	 * information of customer list in XML format.
	 */
	@GET
	@Path("/products/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public List<Product> getProducts() {
		LOG.info("Invoking getProducts");
		return new ArrayList<Product>(products.values());
	}

	/**
	 * Using HTTP PUT, we can can upload the XML representation of a customer
	 * object. This operation will be mapped to the method below and the XML
	 * representation will get unmarshaled into a real Customer object using JAXB.
	 * <p/>
	 * The method itself just updates the customer object in our local data map and
	 * afterwards uses the Reponse class to build the appropriate HTTP response:
	 * either OK if the update succeeded (translates to HTTP Status 200/OK) or not
	 * modified if the method failed to update a customer object (translates to HTTP
	 * Status 304/Not Modified).
	 * <p/>
	 * Note how this method is using the same @Path value as our next method - the
	 * HTTP method used will determine which method is being invoked.
	 */
	@PUT
	@Path("/products/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response updateProduct(Product product) {
		LOG.info("Invoking updateProduct, Product id is: {}", product.getId());
		Product p = products.get(product.getId());
		Response r;
		if (p != null) {
			products.put(product.getId(), product);
			r = Response.ok().build();
		} else {
			r = Response.notModified().build();
		}

		return r;
	}

	/**
	 * Using HTTP POST, we can add a new customer to the system by uploading the XML
	 * representation for the customer. This operation will be mapped to the method
	 * below and the XML representation will get unmarshaled into a real Customer
	 * object.
	 * <p/>
	 * After the method has added the customer to the local data map, it will use
	 * the Response class to build the HTTP reponse, sending back the inserted
	 * customer object together with a HTTP Status 200/OK. This allows us to send
	 * back the new id for the customer object to the client application along with
	 * any other data that might have been updated in the process.
	 * <p/>
	 * Note how this method is using the same @Path value as our previous method -
	 * the HTTP method used will determine which method is being invoked.
	 */
	@POST
	@Path("/products/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response addProduct(Product product) {
		LOG.info("Invoking addProduct, Product description is: {}", product.getId());
		product.setId(++currentProductId);

		products.put(product.getId(), product);
		if (jaxrsContext.getHttpHeaders().getMediaType().getSubtype().equals("json")) {
			return Response.ok().type("application/json").entity(product).build();
		} else {
			return Response.ok().type("application/xml").entity(product).build();
		}
	}

	/**
	 * This method is mapped to an HTTP DELETE of
	 * 'http://localhost:8181/cxf/crm/customerservice/customers/{id}'. The value for
	 * {id} will be passed to this message as a parameter, using the @PathParam
	 * annotation.
	 * <p/>
	 * The method uses the Response class to create the HTTP response: either HTTP
	 * Status 200/OK if the customer object was successfully removed from the local
	 * data map or a HTTP Status 304/Not Modified if it failed to remove the object.
	 */
	@DELETE
	@Path("/products/{id}/")
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json", "application/x-www-form-urlencoded" })
	public Response deleteProduct(@PathParam("id") String id) {
		LOG.info("Invoking deleteProduct, Product id is: {}", id);
		long idNumber = Long.parseLong(id);
		Product p = products.get(idNumber);

		Response r;
		if (p != null) {
			r = Response.ok().build();
			products.remove(idNumber);
		} else {
			r = Response.notModified().build();
		}

		return r;
	}

	/**
	 * The init method is used by the constructor to insert a Customer and Order
	 * object into the local data map for testing purposes.
	 */
	final void init() {
		Customer c = new Customer();
		c.setName("Jelena Katusic");
		c.setId(currentId);

		Product p = new Product();
		p.setId(currentProductId);
		p.setPrice(1000);

		products.put(p.getId(), p);

		customers.put(c.getId(), c);
	}

	@Context
	public void setMessageContext(MessageContext messageContext) {
		this.jaxrsContext = messageContext;
	}

}
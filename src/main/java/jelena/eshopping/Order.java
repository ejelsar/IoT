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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Order class is not only a plain old java object, with a few properties and getters and setters, but it also defines
 * a sub-resource for the Order returned by CustomerService.
 * <p/>
 * By adding the @XmlRootElement annotation, we make it possible for JAXB to unmarshal this object into a XML document and
 * to marshal it back from the same XML document.
 * <p/>
 * The XML representation of an Order will look like this:
 * <Order>
 * <id>223</id>
 * <description>Order 223</description>
 * </Order>
 */
@XmlRootElement(name = "Order")
public class Order {

	private static final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private long id;
    private String description;
    Map<Long, Product> products = new HashMap<Long, Product>();

    public Order() {
        init();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    /**
     * This method is mapped to an HTTP GET of 'products/{productId}', relative to the URL that point to this Order resource
     * itself.
     * The value for {productId} will be passed to this message as a parameter, using the @PathParam annotation.
     * 

     * The method returns an Product object - for creating the HTTP response, this object is marshaled into XML using JAXB.
     * 

     * For example: accessing 'http://localhost:8181/cxf/crm/customerservice/orders/223/products/323' will first trigger the
     * CustomerService's getOrder() method to return the Order instance for order 223 and afterwards, it will use the remaining
     * part of the URI ('products/323') to map to this method and return the product details for product 323 in this order.
     */
    @GET
    @Path("products/{productId}/")
    @Produces({ "application/xml", "application/json" })
    public Product getProduct(@PathParam("productId") int productId) {
        LOG.info("----invoking getProduct with id: " + productId);
        Product p = products.get(new Long(productId));
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
	


    final void init() {
        Product p = new Product();
        p.setId(323);
        p.setPrice(20000);
        products.put(p.getId(), p);
    }
}


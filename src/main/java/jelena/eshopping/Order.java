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
	long currentOrderProductId = 423;
    private long total=0;
   


    public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
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

    public Product getProduct(@PathParam("productId") long productId) {
        Product p = products.get(new Long(productId));
        return p;
    }
    
	public List<Product> getProducts() {
		return new ArrayList<Product>(products.values());
	}
	
	public void deleteProduct(long productId) {
		Product p=products.get(productId);
		total-=(p.getPrice()*p.getQuantityOrdered());
		products.remove(productId);			
	}
	
	public void addProduct(Product product) {
		if(products.get(product.getId()) == null) {
			products.put(product.getId(), product);		
		}
		int quantity=product.getQuantityOrdered();
		product.setQuantityOrdered(++quantity);
		total+=product.getPrice();	
	}


}


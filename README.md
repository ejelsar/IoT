# Karaf CXF REST EShop 

This EShop demonstrates how to create a RESTful (JAX-RS) web service using CXF and expose it through the OSGi HTTP Service.

The REST service provides a customer service that supports the following operations

- PUT /customerservice/customers/ - to create or update a customer
- GET /customerservice/customers/{id} - to view a customer with the given id
- DELETE /customerservice/customers/{id} - to delete a customer with the given id
- GET /customerservice/orders/{orderId} - to view an order with the given id
- GET /customerservice/orders/{orderId}/products/{productId} - to view a specific product on an order with the given id

When the application is deployed, you can access the REST service using a web browser.


### Building

The example can be built with

    mvn clean install


### Access services using a web browser

You can use any browser to perform a HTTP GET.  This allows you to very easily test a few of the RESTful services we defined:

Use this URL to display the root of the REST service, which also allows to access the WADL of the service:

    http://localhost:8181/cxf/crm

Use this URL to display the XML representation for customer 123:

    http://localhost:8181/cxf/crm/customerservice/customers/123

You can also access the XML representation for order 223 ...

    http://localhost:8181/cxf/crm/customerservice/customers/123

**Note:** if you use Safari, you will only see the text elements but not the XML tags - you can view the entire document with 'View Source'


### To run a command-line utility:

You can use a command-line utility, such as cURL or wget, to perform the HTTP requests.  We have provided a few files with sample XML representations in `src/test/resources`, so we will use those for testing our services.

1. Open a command prompt and change directory to `cxf-cdi`.
2. Run the following curl commands (curl commands may not be available on all platforms):


    * Create a customer
            curl -X POST -T src\test\resources\add_customer.json -H "Content-Type: application/json" http://localhost:8181/cxf/crm/customerservice/customers

    * Retrieve the customer instance with id 123
            curl http://localhost:8181/cxf/crm/customerservice/customers/123

    * Update the customer instance with id 123
            curl -X PUT -T src\test\resources\update_customer.json -H "Content-Type: application/json" http://localhost:8181/cxf/crm/customerservice/customers

    * Delete the customer instance with id 123
             curl -X DELETE http://localhost:8181/cxf/crm/customerservice/customers/123
             
    * Retrieve all customers
    			curl http://localhost:8181/cxf/crm/customerservice/customers
    			  
    * Create a product
            curl -X POST -T src\test\resources\add_product.json -H "Content-Type: application/json" http://localhost:8181/cxf/crm/customerservice/products

    * Retrieve the product instance with id 323
            curl -v http://localhost:8181/cxf/crm/customerservice/products/323 

    * Update the product instance with id 323
            curl -X PUT -T src\test\resources\update_products.json -H "Content-Type: application/json" http://localhost:8181/cxf/crm/customerservice/products

    * Delete the product instance with id 323
            curl -X DELETE http://localhost:8181/cxf/crm/customerservice/products/323   
             
    * Retrieve all products
            curl -v http://localhost:8181/cxf/crm/customerservice/products/      
            
    * Create a order
            curl -X POST -T src\test\resources\add_order.json -H "Content-Type: application/json" http://localhost:8181/cxf/crm/customerservice/customers/123/orders

    * Retrieve the order instance with id 223
            curl -v http://localhost:8181/cxf/crm/customerservice/customers/123/orders/223 -H "Accept: application/json" 

    * Delete the order instance with id 223
            curl -X DELETE http://localhost:8181/cxf/crm/customerservice/customers/123/orders/223   
             
    * Retrieve all orders
           curl -v http://localhost:8181/cxf/crm/customerservice/customers/123/orders                     

	* Add existing product 323 to order 223
			curl -X PUT http://localhost:8181/cxf/crm/customerservice/customers/123/orders/223/products/323  -H "Content-Type: application/json"
			
	* Remove product 323 from order 223
			curl -X DELETE http://localhost:8181/cxf/crm/customerservice/customers/123/orders/223/products/323
			
	* Retrieve all products from order 223	
			curl -X DELETE http://localhost:8181/cxf/crm/customerservice/customers/123/orders/223/products/
			
	* Retrieve product 323 from order 223
			curl -X DELETE http://localhost:8181/cxf/crm/customerservice/customers/123/orders/223/products/323
			
 
 	


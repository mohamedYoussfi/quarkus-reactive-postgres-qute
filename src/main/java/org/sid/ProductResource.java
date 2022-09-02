package org.sid;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.UniSubscribe;
import io.smallrye.mutiny.subscription.Cancellable;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.sid.entities.Product;
import org.sid.repositories.ProductRepository;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("")
public class ProductResource {
    @Inject
    private ProductRepository productRepository;
    @Inject
    private JsonWebToken jsonWebToken;

    @Inject
    private Template products;

    @GET
    @Path("/hello")
    @RolesAllowed("USER")
    public Map<String,Object> hello() {
        Map<String,Object> data=new HashMap<>();
        data.put("message","Hello From Rest Easy Reactive");
        data.put("subject",jsonWebToken.getSubject());
        data.put("preferred_username",jsonWebToken.getClaim("preferred_username"));
        return data;
    }

    @GET
    @Path("/products")
    public Uni<List<Product>> productList(){
        return Product.listAll();
    }

    @GET
    @Path("/products/search")
    public Uni<List<Product>> search(@QueryParam("name") String name){
        return productRepository.findByName(name);
    }
    @GET
    @Path("/products/{id}")
    public Uni<Product> getProduct(@PathParam("id") String id){
        return Product.findById(id);
    }

    @Transactional
    @POST
    @Path("/products/{id}")
    @RolesAllowed("USER")
    public void delete(@PathParam("id") String id){
         Product.deleteById(id);
    }
    @POST
    @Path("/products")
    @ReactiveTransactional
    public Uni<Product> newProduct(Product product){
        product.id= UUID.randomUUID().toString();
        return Panache.withTransaction(()->product.persist());
    }

    @PUT
    @Path("/products/{id}")
    public Uni<Product> update(Product product, @PathParam("id") String id){
        return Panache.withTransaction(()->Product.<Product>findById(id).onItem().ifNotNull().invoke(entity ->{
            entity.name=product.name;
            entity.price=product.price;
            entity.quantity=product.quantity;
        } )).onItem().ifNotNull().transform(entity->entity);
    }

   @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/productsList")
    public TemplateInstance productsList(){
        return products.data("products",productRepository.findAll().list());
    }

}
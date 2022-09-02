package org.sid.services;

import io.quarkus.runtime.StartupEvent;
import org.sid.entities.Product;
import org.sid.repositories.ProductRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;

@ApplicationScoped
@Transactional
public class ApplicationLifeCycle {
    @Inject
    private ProductRepository productRepository;
    public void onStart(@Observes StartupEvent event){
        System.out.println("Start up *************** >");
        Product product=new Product();
        product.id= UUID.randomUUID().toString();
        product.name="Computer";
        product.price=8700;
        product.quantity=78;
        product.persist();
    }
    public void onStop(@Observes StartupEvent event){

    }
}

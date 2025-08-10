package dev.mainul35.fruits;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class FruitRepository implements PanacheRepository<Fruit> {

    public Uni<Response> save(Fruit fruit) {
        return Panache.withTransaction(fruit::persist)
                .replaceWith(Response.ok().status(Response.Status.CREATED).build());
    }
}

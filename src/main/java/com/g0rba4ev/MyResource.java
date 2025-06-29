package com.g0rba4ev;

import io.quarkus.qute.Qute;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

@Path("/")
public class MyResource {

    private final Template template = Qute.engine().parse("foo {varName}");

    @GET
    @Path("/getTemplateInstance")
    public TemplateInstance getTemplateInstance() {
        return template.data("varName", getUniWithDelay());
    }

    @GET
    @Path("/getCompletionStage")
    public CompletionStage<String> getCompletionStage() {
        return template.data("varName", getUniWithDelay()).renderAsync();
    }

    @GET
    @Path("/getUni")
    public Uni<String> getUni() {
        return template.data("varName", getUniWithDelay()).createUni()
                .log("TemplateInstance+CreateUni");
    }

    @GET
    @Path("/getUniRendered")
    public Uni<String> getUniRendered() {
        return getUniWithDelay()
                .map(value -> {
                    return template.data("varName", value).render();
                })
                .log("getUniRendered");
    }


    public static Uni<String> getUniWithDelay() {
        return Uni.createFrom().item("delayed value")
                .onItem().delayIt().by(Duration.ofSeconds(7))
                .log("UNI WITH DELAY")
                .onCancellation().invoke(() -> System.out.println("UNI WAS CANCELLED"));
    }

}

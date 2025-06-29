package thminiprojthebook.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import thminiprojthebook.domain.*;

@Component
public class SubscriberHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Subscriber>> {

    @Override
    public EntityModel<Subscriber> process(EntityModel<Subscriber> model) {
        model.add(
            Link
                .of(
                    model.getRequiredLink("self").getHref() +
                    "/createsubscriber"
                )
                .withRel("createsubscriber")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/monthlypaid")
                .withRel("monthlypaid")
        );

        return model;
    }
}

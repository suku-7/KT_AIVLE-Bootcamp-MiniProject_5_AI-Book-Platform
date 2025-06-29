package thminiprojthebook.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import thminiprojthebook.domain.*;

@Component
public class SubscribedBookHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<SubscribedBook>> {

    @Override
    public EntityModel<SubscribedBook> process(
        EntityModel<SubscribedBook> model
    ) {
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/buybook")
                .withRel("buybook")
        );

        return model;
    }
}

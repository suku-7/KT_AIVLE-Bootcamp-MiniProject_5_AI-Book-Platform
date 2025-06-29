package thminiprojthebook.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import thminiprojthebook.domain.*;

@Component
public class AuthorHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Author>> {

    @Override
    public EntityModel<Author> process(EntityModel<Author> model) {
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/join")
                .withRel("join")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/approve")
                .withRel("approve")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/disapprove")
                .withRel("disapprove")
        );

        return model;
    }
}

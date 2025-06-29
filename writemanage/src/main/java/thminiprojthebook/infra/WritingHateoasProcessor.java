package thminiprojthebook.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import thminiprojthebook.domain.*;

@Component
public class WritingHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Writing>> {

    @Override
    public EntityModel<Writing> process(EntityModel<Writing> model) {
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/registbook")
                .withRel("registbook")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/modifycontext")
                .withRel("modifycontext")
        );

        return model;
    }
}

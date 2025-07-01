package thminiprojthebook.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import thminiprojthebook.domain.*;

@Component
public class UserHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<User>> {

    @Override
    public EntityModel<User> process(EntityModel<User> model) {
        model.add(
            Link
                .of(
                    model.getRequiredLink("self").getHref() +
                    "/subscribetobookservice"
                )
                .withRel("subscribetobookservice")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/buybook")
                .withRel("buybook")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/updateuser")
                .withRel("updateuser")
        );
        model.add(
            Link
                .of(
                    model.getRequiredLink("self").getHref() +
                    "/cancelsubscribetobookservice"
                )
                .withRel("cancelsubscribetobookservice")
        );

        return model;
    }
}

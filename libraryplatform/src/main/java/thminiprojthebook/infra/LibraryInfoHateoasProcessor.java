package thminiprojthebook.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import thminiprojthebook.domain.*;

@Component
public class LibraryInfoHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<LibraryInfo>> {

    @Override
    public EntityModel<LibraryInfo> process(EntityModel<LibraryInfo> model) {
        return model;
    }
}

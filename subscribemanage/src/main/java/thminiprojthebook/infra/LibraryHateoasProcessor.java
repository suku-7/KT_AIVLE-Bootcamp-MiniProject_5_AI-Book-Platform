package thminiprojthebook.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import thminiprojthebook.domain.*;

@Component
public class LibraryHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Library>> {

    @Override
    public EntityModel<Library> process(EntityModel<Library> model) {
        return model;
    }
}

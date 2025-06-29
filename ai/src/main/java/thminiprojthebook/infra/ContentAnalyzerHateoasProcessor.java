package thminiprojthebook.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import thminiprojthebook.domain.*;

@Component
public class ContentAnalyzerHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<ContentAnalyzer>> {

    @Override
    public EntityModel<ContentAnalyzer> process(
        EntityModel<ContentAnalyzer> model
    ) {
        return model;
    }
}

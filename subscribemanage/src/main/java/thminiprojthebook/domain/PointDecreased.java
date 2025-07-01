package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class PointDecreased extends AbstractEvent {

    private String userId;
    private Integer pointBalance;
}

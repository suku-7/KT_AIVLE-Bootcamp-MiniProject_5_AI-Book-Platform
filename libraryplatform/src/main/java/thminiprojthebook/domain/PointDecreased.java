package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class PointDecreased extends AbstractEvent {

    private Long id;
    private String userId;
    private Integer pointBalance;
    private Integer amount;
    private Date usedAt;
}

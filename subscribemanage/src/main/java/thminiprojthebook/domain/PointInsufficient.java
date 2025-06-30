package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class PointInsufficient extends AbstractEvent {

    private Long pointId;
    private String userId;
    private Integer pointBalance;
    private Long subscribedBookId; // 추가됨

    public PointInsufficient() {
        super();
    }
}

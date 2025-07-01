package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class BookServiceSubscribed extends AbstractEvent {

    private Long userId;
    private String isSubscribe;
}

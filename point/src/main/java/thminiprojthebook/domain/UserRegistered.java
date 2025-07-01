package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class UserRegistered extends AbstractEvent {

    private Long userId;
    private String isKt;
}

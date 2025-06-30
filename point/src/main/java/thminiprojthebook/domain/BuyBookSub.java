package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class BuyBookSub extends AbstractEvent {

    private Long id;
    private String userId;
    private Integer amount; // 구매 금액
}

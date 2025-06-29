package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "SubscribeList_table")
@Data
public class SubscribeList {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
}

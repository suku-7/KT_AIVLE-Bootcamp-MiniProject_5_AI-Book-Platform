package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "ApprovalAuthor_table")
@Data
public class ApprovalAuthor {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long authorId;

    private Boolean isApproved;
}

// BuyBookCommand.java
package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class BuyBookCommand {
    // 구매할 책의 ID를 담을 필드
    private Long bookId;
}
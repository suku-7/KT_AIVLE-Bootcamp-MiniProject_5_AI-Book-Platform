package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class ModifyContextCommand {
    // 1. 제목 수정을 위한 newTitle 필드를 추가합니다.
    private String newTitle;
    private String newContext;
}

// UpdateUserCommand.java

package thminiprojthebook.domain;

import lombok.Data;

@Data
public class UpdateUserCommand {

    // 업데이트할 필드들을 정의합니다.
    private String loginId;
    private String loginPassword;
    private String name;
    private String isKt;
}
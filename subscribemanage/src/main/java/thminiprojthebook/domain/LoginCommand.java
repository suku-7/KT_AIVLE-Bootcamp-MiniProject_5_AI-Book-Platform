// =================================================================
// FILENAME: subscribemanage/domain/LoginCommand.java (신규 생성 또는 확인)
// 역할: 로그인 요청 시 ID와 비밀번호를 담을 '데이터 상자'입니다.
// =================================================================
package thminiprojthebook.domain;

import lombok.Data;

@Data
public class LoginCommand {
    private String loginId;
    private String password;
}
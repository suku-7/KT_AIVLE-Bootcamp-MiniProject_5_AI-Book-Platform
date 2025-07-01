// UserController.java

package thminiprojthebook.infra;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import thminiprojthebook.domain.*;
import java.util.Map;
//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/users")
@Transactional
public class UserController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(
        value = "/users/{id}/subscribetobookservice",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public User subscribeToBookService(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /user/subscribeToBookService  called #####");
        Optional<User> optionalUser = userRepository.findById(id);

        optionalUser.orElseThrow(() -> new Exception("No Entity Found"));
        User user = optionalUser.get();
        user.subscribeToBookService();

        userRepository.save(user);
        return user;
    }

    @RequestMapping(
        value = "/users/{id}/buybook",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public User buyBook(
        @PathVariable(value = "id") Long id,
        @RequestBody BuyBookCommand buyBookCommand, // 요청 본문에서 bookId를 받도록 수정
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /user/buyBook  called #####");
        Optional<User> optionalUser = userRepository.findById(id);

        optionalUser.orElseThrow(() -> new Exception("No Entity Found"));
        User user = optionalUser.get();
        
        // Command 객체를 전달하여 구매 로직 실행
        user.buyBook(buyBookCommand);

        userRepository.save(user);
        return user;
    }

    @RequestMapping(
        value = "/users/{id}/updateuser",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public User updateUser(
        @PathVariable(value = "id") Long id,
        @RequestBody UpdateUserCommand updateUserCommand, // @RequestBody 어노테이션으로 요청 본문을 받습니다.
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /user/updateUser  called #####");
        Optional<User> optionalUser = userRepository.findById(id);

        optionalUser.orElseThrow(() -> new Exception("No Entity Found"));
        User user = optionalUser.get();
        
        // Command 객체를 User 애그리거트의 메서드로 전달하여 업데이트를 위임합니다.
        user.updateUser(updateUserCommand);

        userRepository.save(user);
        return user;
    }

    @RequestMapping(
        value = "/users/{id}/cancelsubscribetobookservice",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public User cancelSubscribeToBookService(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println(
            "##### /user/cancelSubscribeToBookService  called #####"
        );
        Optional<User> optionalUser = userRepository.findById(id);

        optionalUser.orElseThrow(() -> new Exception("No Entity Found"));
        User user = optionalUser.get();
        user.cancelSubscribeToBookService();

        userRepository.save(user);
        return user;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") Long id) throws Exception {
        return userRepository.findById(id)
        .orElseThrow(() -> new Exception("No Entity Found"));
    }

    @PutMapping(
    value = "/users/{id}",
    consumes = "application/json",
    produces = "application/json;charset=UTF-8"
    )
    public User putUpdateUser(
        @PathVariable(value = "id") Long id,
        @RequestBody UpdateUserCommand updateUserCommand,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /user/putUpdateUser called #####");

        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.orElseThrow(() -> new Exception("No Entity Found"));

        User user = optionalUser.get();

        user.setLoginId(updateUserCommand.getLoginId());
        user.setLoginPassword(updateUserCommand.getLoginPassword());
        user.setName(updateUserCommand.getName());
        user.setIsKt(updateUserCommand.getIsKt());

        UserUpdated userUpdated = new UserUpdated(user);
        userUpdated.publishAfterCommit();

        return userRepository.save(user);
    }


    @PatchMapping(
    value = "/users/{id}",
    consumes = "application/json",
    produces = "application/json;charset=UTF-8"
    )
    public User patchUserIsKt(
        @PathVariable(value = "id") Long id,
        @RequestBody Map<String, Object> updates, // JSON Map으로 받음
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /user/patchUserIsKt  called #####");

        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.orElseThrow(() -> new Exception("No Entity Found"));
        User user = optionalUser.get();

        // "isKt"만 수정 처리
        if (updates.containsKey("isKt")) {
            String newIsKt = updates.get("isKt").toString();
            user.setIsKt(newIsKt);

            // 이벤트 발행
            UserUpdated userUpdated = new UserUpdated(user);
            userUpdated.publishAfterCommit();
        }

        return userRepository.save(user);
    }
}
//>>> Clean Arch / Inbound Adaptor

package thminiprojthebook.infra;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import thminiprojthebook.domain.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/points")
@Transactional
public class PointController {

    @Autowired
    PointRepository pointRepository;

    @RequestMapping(
        value = "/points/{id}/pointrecharge",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Point pointRecharge(
        @PathVariable(value = "id") Long id,
        @RequestBody PointRechargeCommand command, // <-- 1. @RequestBody로 command를 받도록 추가
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /point/pointRecharge  called #####");
        Optional<Point> optionalPoint = pointRepository.findById(id);

        optionalPoint.orElseThrow(() -> new Exception("No Entity Found"));
        Point point = optionalPoint.get();
        
        // 2. command를 파라미터로 넘겨주도록 수정
        point.pointRecharge(command);

        pointRepository.save(point);
        return point;
    }
}
//>>> Clean Arch / Inbound Adaptor

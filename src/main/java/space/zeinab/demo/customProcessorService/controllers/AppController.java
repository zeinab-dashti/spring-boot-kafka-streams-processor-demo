package space.zeinab.demo.customProcessorService.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import space.zeinab.demo.customProcessorService.model.IotStatus;
import space.zeinab.demo.customProcessorService.model.UserActivity;
import space.zeinab.demo.customProcessorService.service.IotStatusProducer;
import space.zeinab.demo.customProcessorService.service.UserActivityProducer;

@Slf4j
@RestController
@RequestMapping("/v1/api")
@AllArgsConstructor
public class AppController {
    private UserActivityProducer userActivityProducer;
    private IotStatusProducer iotStatusProducer;

    @PostMapping("/user/activity")
    public void addActivity(@RequestBody UserActivity userActivity) {
        userActivityProducer
                .addActivity(userActivity)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info(
                                "User activity added - key:{} , value:{}",
                                result.getProducerRecord().key(),
                                result.getProducerRecord().value()
                        );
                    } else {
                        log.error("Failed add user activity", ex);
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
                    }
                });
    }

    @PostMapping("/iot/status")
    public void addIotStatus(@RequestBody IotStatus iotStatus) {
        iotStatusProducer
                .addStatus(iotStatus)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info(
                                "IOT status added - key:{} , value:{}",
                                result.getProducerRecord().key(),
                                result.getProducerRecord().value()
                        );
                    } else {
                        log.error("Failed add IOT status", ex);
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
                    }
                });
    }
}

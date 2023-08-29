package com.zf.demo.schedule;

import com.zf.demo.service.TransferService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Configuration
@EnableScheduling
public class ScheduleBack {

    @Resource
    private TransferService transferService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void executePic() {
        try {
            transferService.transferPic();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("999999999999");
        }

    }
}
package se.trigger.web;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GreetingsController {

    private Map<String, Object> devicesMap = new HashMap<>();

    @RabbitListener(queues = "#{heartbeatQueue.name}")
    public void receiveHeartbeat(String data) {
        System.out.println("GreetingsController.receiveHeartbeat() data: " + data);
        if (!devicesMap.containsKey(data)) {
            devicesMap.put(data, data);
        }
    }

    @RabbitListener(queues = "#{deviceMessageQueue.name}")
    public void receiveDeviceMessage(String data) {
        System.out.println("GreetingsController.receiveDeviceMessage() data: " + data);
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("devices", getDevices());
        return "greeting";
    }

    public String getDevices() {
        String devices = "";
        for (String key : devicesMap.keySet()) {
            devices += key + ", ";
        }
        return devices;
    }
}
package com.gavin.cloud.auth.web.message;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class WebSocketController {

    @SendTo("/topic/greeting")
    @MessageMapping("/hello")
    public Response greeting(Request request) {
        Response response = new Response();
        response.setMessage("Hello, " + HtmlUtils.htmlEscape(request.getName()) + "!");
        return response;
    }

    public static class Request {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class Response {

        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}

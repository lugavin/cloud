package com.gavin.cloud.common.web.support;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public abstract class BaseController {

    private static final String FORWARD_PREFIX = "forward:";
    private static final String REDIRECT_PREFIX = "redirect:";

    protected String forward(String view) {
        return FORWARD_PREFIX + view;
    }

    protected String redirect(String view) {
        return REDIRECT_PREFIX + view;
    }

    @GetMapping("/{root}/{page}")
    public String toPage(@PathVariable String root, @PathVariable String page) {
        return root + "/" + page;
    }

}

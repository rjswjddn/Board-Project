package com.example.boardproject.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping({"", "/","/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/401")
    public String error401() {
        return "401";
    }

    @GetMapping("/404")
    public String error404() {
        return "404";
    }

    @GetMapping("/500")
    public String error500() {
        return "500";
    }

    @GetMapping("/charts")
    public String charts() {
        return "charts";
    }

    @GetMapping("/layout-sidenav-light")
    public String layoutSidenavLight() {
        return "layout-sidenav-light";
    }

    @GetMapping("/layout-static")
    public String layoutStatic() {
        return "layout-static";
    }

    @GetMapping("/password")
    public String password() {
        return "password";
    }

    @GetMapping("/tables")
    public String tables() {
        return "tables";
    }

//    @GetMapping("/board_register")
//    public String board_register(){
//        return "board_register";
//    }
}

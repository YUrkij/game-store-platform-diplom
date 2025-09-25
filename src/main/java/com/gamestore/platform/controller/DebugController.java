package com.gamestore.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class DebugController {

    @GetMapping("/debug")
    public String debug() {
        return "Это работает! Сервер запущен правильно.";
    }

    @GetMapping("/test")
    public String testPage() {
        return "Это тестовая страница без авторизации";
    }
}


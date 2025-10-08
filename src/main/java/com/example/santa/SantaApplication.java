package com.example.santa;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class SantaApplication extends Application {
    // 空でOK（JAX-RSアクティベーション用）
}
package com.example.forum.utils;

import org.springframework.util.StringUtils;

import java.util.UUID;

public class UUIDUtil {
    public static String UUID_32() {
        return UUID.randomUUID().toString().replace("-","");
    }
}

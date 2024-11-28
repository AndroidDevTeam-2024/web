package com.example.atry.model;

public class UserSession {
    private static UserSession instance;
    private Integer id = 1;
    private String username = "请修改名称";
    private String email = "请添加邮箱";
    private String token;
    private String avatar = "https://loremflickr.com/400/400?lock=8001606861822272";
    public static String encryptionKey = "1234567890123456";


    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }


    public Integer getId() {
        return id;
    }
    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }
}

package com.hyperspacegamepanel.models.server;

import java.util.Date;

import com.hyperspacegamepanel.models.user.User;

import lombok.Data;

@Data
public class ServerUpdateForm {

    private Integer slots;
    private Integer port;
    private Integer ftpUsername;
    private Integer ftpPassword;

    private Date expirationDate;
    private User owner;
    
}
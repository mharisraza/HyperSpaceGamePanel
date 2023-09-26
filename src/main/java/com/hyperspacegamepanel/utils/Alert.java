package com.hyperspacegamepanel.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Alert {

    private String msg;
    private String alertType;
    private String alertClass;

    public static final String ERROR = "Error";
    public static final String SUCCESS = "Success";
    public static final String WARNING = "Warning";
    public static final String INFO = "Info";

    public static final String ERROR_CLASS = "alert-danger";
    public static final String SUCCESS_CLASS = "alert-success";
    public static final String WARNING_CLASS = "alert-warning";
    public static final String INFO_CLASS = "alert-info";

}

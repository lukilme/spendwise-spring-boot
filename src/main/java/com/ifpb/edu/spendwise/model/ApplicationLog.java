package com.ifpb.edu.spendwise.model;

import java.util.Date;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class ApplicationLog {
    @Id
    private String id;
    private Date timestamp;
    private String level;
    private String message;
    private String loggerName;
    private String threadName;
}

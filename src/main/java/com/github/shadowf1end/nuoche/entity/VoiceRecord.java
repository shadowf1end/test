package com.github.shadowf1end.nuoche.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author su
 * 通知记录
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_voice_record")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler", "delFlag", "createTime", "updateTime", "version"})
public class VoiceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phone")
    private String phone;

    @Column(name = "wechat_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long wechatUserId;

    @Column(name = "result")
    private String result;

    @Column(name = "message")
    private String message;

    @CreatedDate
    @Column(name = "create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}


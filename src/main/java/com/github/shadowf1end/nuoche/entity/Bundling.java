package com.github.shadowf1end.nuoche.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author su
 * 挪车贴绑定
 */
@Data
@Table(name = "b_bundling")
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler", "delFlag", "wechatUserId", "templateId", "partnerId", "claimPointId", "createTime", "updateTime", "version"})
public class Bundling implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @Column(name = "wechat_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long wechatUserId;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "partner_id")
    private Integer partnerId;

    @Column(name = "claim_point_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long claimPointId;

    @Column(name = "plate")
    private String plate;

    @Column(name = "phone")
    private String phone;

    @Column(name = "qr_code", columnDefinition = "mediumblob")
    private byte[] qrCode;

    @Column(name = "nick_name")
    private String nickName;

    @CreatedDate
    @Column(name = "create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
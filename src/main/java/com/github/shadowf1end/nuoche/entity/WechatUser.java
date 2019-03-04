package com.github.shadowf1end.nuoche.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author su
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_wechat_user")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler", "delFlag", "createTime", "updateTime", "version"})
public class WechatUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "snowFlake")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @GenericGenerator(name = "snowFlake", strategy = "com.github.shadowf1end.nuoche.common.util.SnowFlakeIdGenerator")
    private Long id;

    @Column(name = "open_id", unique = true, nullable = false, columnDefinition = "varchar(100)")
    private String openId;

    /**
     * 上级用户id
     */
    @Column(name = "recommend_id")
    private Long recId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "session_key")
    private String sessionKey;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "role")
    private String role = "user";

    @Column(name = "wallet")
    private Integer wallet = 0;

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


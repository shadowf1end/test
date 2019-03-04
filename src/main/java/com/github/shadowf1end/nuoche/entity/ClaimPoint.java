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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author su
 * 领取点
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler", "delFlag", "wechatUserId", "partnerId", "recId", "version"})
@Table(name = "b_claim_point")
public class ClaimPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "snowFlake")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @GenericGenerator(name = "snowFlake", strategy = "com.github.shadowf1end.nuoche.common.util.SnowFlakeIdGenerator")
    private Long id;

    @Column(name = "wechat_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long wechatUserId;

    /**
     * 所属合伙人
     */
    @Column(name = "partner_id")
    private Integer partnerId;

    /**
     * 推荐用户id
     */
    @Column(name = "recommend_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long recId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude", columnDefinition = "Decimal(20, 6)")
    private BigDecimal latitude;

    @Column(name = "longitude", columnDefinition = "Decimal(20, 6)")
    private BigDecimal longitude;

    /**
     * 优惠信息
     */
    @Column(name = "detail")
    private String detail;

    /**
     * 广告跳转内容
     */
    @Column(name = "content", columnDefinition = "longtext")
    private String content;

    /**
     * 0:未支付 1:已支付
     */
    @Column(name = "state")
    private Integer state = 0;

    /**
     * 广告图片
     */
    @Column(name = "url")
    private String url;

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


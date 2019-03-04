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
 * 购买挪车贴订单
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_order")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler", "id", "type", "updateTime"})
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "snowFlake")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @GenericGenerator(name = "snowFlake", strategy = "com.github.shadowf1end.nuoche.common.util.SnowFlakeIdGenerator")
    private Long id;

    /**
     * 推荐用户id
     */
    @Column(name = "recommend_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long recId;

    @Column(name = "name")
    private String name;

    @Column(name = "wechat_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long wechatUserId;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "price")
    private Integer price;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "claim_point_id")
    private Long claimPointId;

    //0:未支付 1：已支付
    @Column(name = "state")
    private Integer state = 0;

    /**
     * 快递名称
     */
    @Column(name = "express_name")
    private String expressName;

    /**
     * 快递单号
     */
    @Column(name = "express_number")
    private String expressNo;

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


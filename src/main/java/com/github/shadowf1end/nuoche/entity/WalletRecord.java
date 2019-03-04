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
 * 钱包记录
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "b_wallet_record")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class WalletRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "wechat_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long wechatUserId;

    @Column(name = "current_wallet")
    private Integer curWallet;

    @Column(name = "previous_wallet")
    private Integer preWallet;

    @Column(name = "amount")
    private Integer amount;

    /**
     * 如果是推荐购买挪车贴有订单id
     */
    @Column(name = "order_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    /**
     * 0:直接推荐领取点 1:间接推荐领取点 2:推荐购买挪车贴 3:提现
     */
    @Column(name = "type")
    private Integer type;

    @Column(name = "description")
    private String des;

    @Column(name = "verify_flag")
    private Integer verifyFlag;

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


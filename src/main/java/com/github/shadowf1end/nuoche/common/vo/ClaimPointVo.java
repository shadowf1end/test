package com.github.shadowf1end.nuoche.common.vo;

import com.github.shadowf1end.nuoche.entity.ClaimPoint;

/**
 * @author Sun
 * @date 2018/9/7
 */
public class ClaimPointVo {
    private ClaimPoint claimPoint;
    private String distance;

    public ClaimPointVo() {
    }

    public ClaimPointVo(ClaimPoint claimPoint, String distance) {
        this.claimPoint = claimPoint;
        this.distance = distance;
    }

    public ClaimPoint getClaimPoint() {
        return claimPoint;
    }

    public void setClaimPoint(ClaimPoint claimPoint) {
        this.claimPoint = claimPoint;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "ClaimPointDTO{" +
                "claimPoint=" + claimPoint +
                ", distance='" + distance + '\'' +
                '}';
    }
}

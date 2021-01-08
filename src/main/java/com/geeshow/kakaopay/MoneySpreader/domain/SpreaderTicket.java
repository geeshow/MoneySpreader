package com.geeshow.kakaopay.MoneySpreader.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geeshow.kakaopay.MoneySpreader.utils.date.SpreaderDateUtils;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SpreaderTicket extends BaseEntity {

    // 받은 금액
    @Column(nullable = false)
    private Long amount;

    // Receiver User
    @Column private Long receiverUserId;

    // Lock 버전
    @Version private Integer version;

    // Spreader User ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spreader_id")
    @JsonIgnore
    private Spreader spreader;

    public long receiveTicket(KakaoUser receiver) {

        setReceiverUserId(receiver.getId());

        return getAmount();
    }

    public boolean isReceived() {
        return Optional.ofNullable(receiverUserId).isPresent();
    }

    public boolean isBelongTo(long userId) {
        return Optional.ofNullable(receiverUserId).orElse(0L) == userId;
    }

    public String getReceiptDate() {
        if ( isReceived() )
            return SpreaderDateUtils.parseToDateString(this.getLastModifiedDate());

        return "";
    }
}

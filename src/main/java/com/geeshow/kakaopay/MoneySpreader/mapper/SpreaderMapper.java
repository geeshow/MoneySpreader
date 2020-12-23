package com.geeshow.kakaopay.MoneySpreader.mapper;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.domain.SpreaderTicket;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto;

import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderTicketDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SpreaderMapper {

    @Mapping(target = "spreadDatetime", source = "createDate")
    @Mapping(target = "spreadAmount", source = "amount")
    @Mapping(target = "receipts", source = "spreader")
    @Mapping(target = "receiptAmount", source = "spreader")
    public abstract SpreaderDto.ReadDto toDto(Spreader spreader);

    List<SpreaderTicketDto.ResponseGet> mapReceipts(Spreader spreader) {
        return spreader.getSpreaderTickets().stream()
                .filter(spreaderTicket -> spreaderTicket.getReceiverUserId() != null)
                .map(ticket ->
                        SpreaderTicketDto.ResponseGet.builder()
                            .amount(ticket.getAmount())
                            .userId(ticket.getReceiverUserId())
                            .build()
                )
                .collect(Collectors.toList());
    }

    Long mapReceiptAmount(Spreader spreader) {
        return spreader.getSpreaderTickets().stream()
                .filter(spreaderTicket -> Optional.ofNullable(spreaderTicket.getReceiverUserId()).orElse(0L) > 0)
                .map(SpreaderTicket::getAmount)
                .reduce(0L, Long::sum);
    }
}

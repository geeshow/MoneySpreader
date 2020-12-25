package com.geeshow.kakaopay.MoneySpreader.mapper;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ResponseReadDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SpreaderMapper {

    @Mapping(target = "spreadDatetime", source = "createDate")
    @Mapping(target = "spreadAmount", source = "amount")
    @Mapping(target = "receipts", source = "spreader")
    @Mapping(target = "receiptAmount", source = "spreader")
    public abstract ResponseReadDto toDto(Spreader spreader);

    List<ResponseReadDto.TicketDto> mapReceipts(Spreader spreader) {
        return spreader.getReceivedTickets()
                .stream()
                .map(ticket ->
                        ResponseReadDto.TicketDto.builder()
                            .amount(ticket.getAmount())
                            .userId(ticket.getReceiverUserId())
                            .build()
                )
                .collect(Collectors.toList());
    }

    long mapReceiptAmount(Spreader spreader) {
        return spreader.getTotalReceiptAmount();
    }
}

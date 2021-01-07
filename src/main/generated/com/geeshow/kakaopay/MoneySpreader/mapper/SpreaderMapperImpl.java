package com.geeshow.kakaopay.MoneySpreader.mapper;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ResponseReadDto;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ResponseReadDto.ResponseReadDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-01-07T20:22:57+0900",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (AdoptOpenJDK)"
)
@Component
public class SpreaderMapperImpl extends SpreaderMapper {

    @Override
    public ResponseReadDto toDto(Spreader spreader) {
        if ( spreader == null ) {
            return null;
        }

        ResponseReadDtoBuilder responseReadDto = ResponseReadDto.builder();

        responseReadDto.receipts( mapReceipts( spreader ) );
        responseReadDto.receiptAmount( mapReceiptAmount( spreader ) );
        responseReadDto.spreadDatetime( spreader.getCreateDate() );
        responseReadDto.spreadAmount( spreader.getAmount() );

        return responseReadDto.build();
    }
}

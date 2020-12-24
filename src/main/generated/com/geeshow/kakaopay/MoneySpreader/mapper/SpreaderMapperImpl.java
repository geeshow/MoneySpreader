package com.geeshow.kakaopay.MoneySpreader.mapper;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ReadDto;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ReadDto.ReadDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-12-24T21:17:26+0900",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (AdoptOpenJDK)"
)
@Component
public class SpreaderMapperImpl extends SpreaderMapper {

    @Override
    public ReadDto toDto(Spreader spreader) {
        if ( spreader == null ) {
            return null;
        }

        ReadDtoBuilder readDto = ReadDto.builder();

        readDto.receipts( mapReceipts( spreader ) );
        readDto.receiptAmount( mapReceiptAmount( spreader ) );
        readDto.spreadDatetime( spreader.getCreateDate() );
        readDto.spreadAmount( spreader.getAmount() );

        return readDto.build();
    }
}

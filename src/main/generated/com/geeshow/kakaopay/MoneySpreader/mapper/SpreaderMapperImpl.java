package com.geeshow.kakaopay.MoneySpreader.mapper;

import com.geeshow.kakaopay.MoneySpreader.domain.Spreader;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ResponseRead;
import com.geeshow.kakaopay.MoneySpreader.dto.SpreaderDto.ResponseRead.ResponseReadBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-12-24T23:22:42+0900",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (AdoptOpenJDK)"
)
@Component
public class SpreaderMapperImpl extends SpreaderMapper {

    @Override
    public ResponseRead toDto(Spreader spreader) {
        if ( spreader == null ) {
            return null;
        }

        ResponseReadBuilder responseRead = ResponseRead.builder();

        responseRead.receipts( mapReceipts( spreader ) );
        responseRead.receiptAmount( mapReceiptAmount( spreader ) );
        responseRead.spreadDatetime( spreader.getCreateDate() );
        responseRead.spreadAmount( spreader.getAmount() );

        return responseRead.build();
    }
}

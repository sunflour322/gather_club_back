package com.gather_club_back.gather_club_back.mapper;

import com.gather_club_back.gather_club_back.entity.CurrencyTransaction;
import com.gather_club_back.gather_club_back.model.CurrencyTransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class CurrencyTransactionMapper {
    
    public CurrencyTransactionResponse toModel(CurrencyTransaction entity) {
        if (entity == null) {
            return null;
        }
        
        return new CurrencyTransactionResponse()
                .setTransactionId(entity.getTransactionId())
                .setUserId(entity.getUser().getUserId())
                .setUsername(entity.getUser().getUsername())
                .setAmount(entity.getAmount())
                .setTransactionType(entity.getTransactionType())
                .setDescription(entity.getDescription())
                .setReferenceId(entity.getReferenceId())
                .setCreatedAt(entity.getCreatedAt());
    }
} 
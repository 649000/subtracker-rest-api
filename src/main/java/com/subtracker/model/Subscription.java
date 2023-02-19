package com.subtracker.model;

import com.google.firebase.database.annotations.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;
@Data
@NoArgsConstructor
public class Subscription {

//    @NotNull
    private UUID subscriptionID;

//    @NotNull
    private LocalDate createdDate;

//    @NotNull
    private LocalDate modifiedDate;

//    @NotNull
    private String serviceName;

//    @NotNull
    private Currency currency;

//    @NotNull
    private BigDecimal monthlyAmount;
    private BigDecimal yearlyAmount;

    // In months
    private double contractualPeriod;

    private String notes;
}

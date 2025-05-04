package com.subtracker.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import com.google.firebase.database.annotations.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Document(collectionName = "subscription")
@Data
@NoArgsConstructor
public class Subscription {

    @DocumentId
    private String subscriptionId;

    @NotNull
    private String userId;

    @NotNull
    private Date createdDate;

    @NotNull
    private Date modifiedDate;

    @NotNull
    private String serviceName;

    @NotNull
    private String currency;

    @NotNull
    private Double monthlyAmount;
    private Double yearlyAmount;

    // In months
    private double contractualPeriod;

    private String notes;
}

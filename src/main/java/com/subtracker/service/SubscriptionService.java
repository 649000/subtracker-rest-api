package com.subtracker.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.subtracker.model.Subscription;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubscriptionService {


    private Firestore firestore;

    private final String SUBSCRIPTION_TABLE = "subscription";

    @Autowired
    public SubscriptionService(Firestore firestore) {
        this.firestore = firestore;
    }

    public Subscription createSubscription(@NonNull Subscription subscription) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = firestore.collection(SUBSCRIPTION_TABLE).add(subscription);
        subscription.setSubscriptionId(addedDocRef.get().getId());
        return subscription;
    }

    public Iterable<Subscription> getSubscriptions() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(SUBSCRIPTION_TABLE).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Subscription> subscriptionList = documents.stream().map(doc -> {
                    Subscription subscription = doc.toObject(Subscription.class);
                    subscription.setSubscriptionId(doc.getId());
                    return subscription;
                }
        ).collect(Collectors.toList());
        return subscriptionList;
    }

    public Subscription getSubscription(@NonNull final String subscriptionId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(SUBSCRIPTION_TABLE).document(subscriptionId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        Subscription subscription = document.toObject(Subscription.class);
        subscription.setSubscriptionId(document.getId());
        return subscription;
    }

    public void deleteSubscription(@NonNull final String subscriptionId) {
        ApiFuture<WriteResult> writeResult = firestore.collection(SUBSCRIPTION_TABLE).document(subscriptionId).delete();
    }

    public Subscription updateSubscription(@NonNull final String subscriptionId, @NonNull final Subscription request) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(SUBSCRIPTION_TABLE).document(subscriptionId);
        Map<String, Object> subUpdates = new HashMap<>();
        subUpdates.put("modifiedDate", new Date());

        if (StringUtils.isNotEmpty(request.getCurrency())) {
            subUpdates.put("currency", request.getCurrency());
        }

        if (StringUtils.isNotEmpty(request.getServiceName())) {
            subUpdates.put("serviceName", request.getServiceName());
        }

        if (ObjectUtils.isNotEmpty(request.getMonthlyAmount())) {
            subUpdates.put("monthlyAmount", request.getMonthlyAmount());
        }

        if (ObjectUtils.isNotEmpty(request.getYearlyAmount())) {
            subUpdates.put("yearlyAmount", request.getYearlyAmount());
        }

        if (ObjectUtils.isNotEmpty(request.getContractualPeriod())) {
            subUpdates.put("contractualPeriod", request.getContractualPeriod());
        }

        if (ObjectUtils.isNotEmpty(request.getNotes())) {
            subUpdates.put("notes", request.getNotes());
        }

        docRef.update(subUpdates);
        return request;
    }
}

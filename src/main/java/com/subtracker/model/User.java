package com.subtracker.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import com.google.firebase.database.annotations.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Document(collectionName = "user")
@Data
@NoArgsConstructor
public class User {

    // Mandatory Annotation and only String type
    @DocumentId
    private String uid;

    private String email;

    private String name;

    private List<String> roles;

    private String country;

    private List<String> subscriptionList;

    @NotNull
    private Date createdDate;

    @NotNull
    private Date modifiedDate;
}

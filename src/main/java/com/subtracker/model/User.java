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

public class User {

    // Mandatory Annotation and only String type
    @DocumentId
    private final String uid;

    private final String email;

    private final String name;

    private final List<String> roles;

    private String country;

    private List<String> subscriptionList;

    @NotNull
    private Date createdDate;

    @NotNull
    private Date modifiedDate;

    public User(String uid, String email, String name, List<String> roles) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }
}

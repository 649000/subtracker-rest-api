package com.subtracker.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class FirebaseConfig {
    @Bean
    public GoogleCredentials googleCredentials() {
        try {
            String base64Key = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_BASE64");
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            return GoogleCredentials.fromStream(new ByteArrayInputStream(decodedKey));
            // Below only works if GOOGLE_APPLICATION_CREDENTIALS is set as a file path
            // Not possible to upload JSON file to Render/Other Cloud Providers
//            return GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials credentials) {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public Firestore firestoreClient(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

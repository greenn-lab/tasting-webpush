package xxx.tasting.webpush;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.TopicManagementResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Collections;

@Service
@Slf4j
public class FcmClient {

  public FcmClient(
      @Value("#{environment.JAVA_HOME}") String privateKey,
      @Value("#{environment.MAVEN_HOME}") String privateKeyId) throws IOException {

    final StringBuilder serviceAccountJson = new StringBuilder();

    String line;
    final BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("todo-lunch-firebase-adminsdk-1irad-7358bbd840.json").getInputStream()));
    while ((line = br.readLine()) != null) {
      serviceAccountJson.append(line
          .replaceAll("\\{\\{PRIVATE_KEY_ID}}", privateKeyId)
          .replaceAll("\\{\\{PRIVATE_KEY}}", privateKey));
    }

    try (InputStream serviceAccount = new ByteArrayInputStream(serviceAccountJson.toString().getBytes())) {
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

      FirebaseApp.initializeApp(options);
    }
    catch (IOException e) {
      log.error("init fcm", e);
    }
  }

  public void send(String clientToken) {

    Message message = Message.builder()
        .putData("score", "123")
        .putData("time", "2:45")
//        .setToken(clientToken)
        .setTopic("chunk")
        .build();

    System.out.println(clientToken);

    String response = null;
    try {
      response = FirebaseMessaging.getInstance().send(message);
      System.out.println("Sent message: " + response);
    }
    catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
  }

  public void subscribe(String topic, String clientToken) {
    try {
      TopicManagementResponse response = FirebaseMessaging.getInstance()
          .subscribeToTopic(Collections.singletonList(clientToken), topic);

      System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");
      System.out.println(clientToken);
    }
    catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
  }

  public void unsubscribe(String topic, String clientToken) {
    try {
      TopicManagementResponse response = FirebaseMessaging.getInstance()
          .unsubscribeFromTopic(Collections.singletonList(clientToken), topic);

      System.out.println(response.getSuccessCount() + " tokens were unsubscribed successfully");
    }
    catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
  }
}

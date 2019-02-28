package xxx.tasting.webpush;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PushController {

  private final FcmClient client;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CrossOrigin
  public void register(@RequestBody String token) {
    client.subscribe("chunk", token);
  }

  @PostMapping("/push")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @CrossOrigin
  public void push(@RequestBody String token) {
    client.send(token);
  }
}

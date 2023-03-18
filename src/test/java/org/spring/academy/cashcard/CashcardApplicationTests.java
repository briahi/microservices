package org.spring.academy.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashcardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
       ResponseEntity<String>  response=restTemplate.getForEntity("/cashcards",String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext =JsonPath.parse(response.getBody());
        int cashCount=documentContext.read("$.length()");
        assertThat(cashCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00);
    }
    @Test
    @DirtiesContext
    void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        Number amount=documentContext.read("$.amount");
        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(99);
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }
   @Test
   @DirtiesContext
    void shouldCreateANewCashCard(){
      CashCard cashCard=new CashCard(null,250.00);
      ResponseEntity<Void> response=restTemplate.postForEntity("/cashcards",cashCard,void.class);
       assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       URI locationNewCard= response.getHeaders().getLocation();
      ResponseEntity<String> responseLocation=restTemplate.getForEntity(locationNewCard,String.class);
      assertThat(HttpStatus.OK.equals(responseLocation.getStatusCode()));

      DocumentContext documentContext=JsonPath.parse(responseLocation.getBody());
      Number id=documentContext.read("$.id");
      Double amount=documentContext.read("$.amount");
      assertThat(id).isNotNull();
      assertThat(amount).isEqualTo(250.00);
    }

}

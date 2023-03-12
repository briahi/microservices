package org.spring.academy.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class CashcardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
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

package org.spring.academy.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
        Optional<CashCard> card=cashCardRepository.findById(requestedId);
        if(card.isPresent()){
            return ResponseEntity.ok(card.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ubc) {
        CashCard savedCard=cashCardRepository.save(cashCard);
        URI location=ubc.path("cashcards/{id}")
                         .buildAndExpand(savedCard.getId())
                         .toUri();
        return ResponseEntity.created(location).build();
    }
    /*
     @PostMapping("/cashcards")
    public ResponseEntity<CashCard> shouldCreateANewCashCard(@RequestBody  CashCard cashCard){
         CashCard card=cashCardRepository.save(cashCard);
         if(card==null) return  ResponseEntity.noContent().build();
         URI location= ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(card.getId()).toUri();
         return ResponseEntity.created(location).build();
     }

     */
}
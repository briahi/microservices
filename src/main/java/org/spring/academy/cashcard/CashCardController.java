package org.spring.academy.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.JsonPath;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        Optional<CashCard> card=Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId,
                principal.getName()));
        if(card.isPresent()){
            return ResponseEntity.ok(card.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard cashCard, UriComponentsBuilder ubc,
                                                Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, cashCard.amount(), principal.getName());
        CashCard savedCard=cashCardRepository.save(cashCardWithOwner);
        URI location=ubc.path("cashcards/{id}")
                         .buildAndExpand(savedCard.id())
                         .toUri();
        return ResponseEntity.created(location).build();
    }
    @GetMapping
    public ResponseEntity<Collection<CashCard>> findAll(Pageable pageable,Principal principal) {
        //default spring values for page number and size are respectively 0 and 20
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount") //
                )));
        return ResponseEntity.ok(page.toList());
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
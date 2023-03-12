package org.spring.academy.cashcard;

import org.springframework.data.annotation.Id;

public class CashCard {
        @Id
        private Long id;
        private  Double amount  ;

        public CashCard(Long id, Double amount) {
                this.id = id;
                this.amount = amount;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Double getAmount() {
                return amount;
        }

        public void setAmount(Double amount) {
                this.amount = amount;
        }
}

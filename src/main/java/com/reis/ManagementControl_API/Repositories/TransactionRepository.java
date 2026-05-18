package com.reis.ManagementControl_API.Repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.ManagementControl_API.Entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	
	List<Transaction> findByTransactionTimeBetween(LocalDateTime dateTime, LocalDateTime finalDateTime);
}

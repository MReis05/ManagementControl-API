package com.reis.ManagementControl_API.Repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.reis.ManagementControl_API.Entities.Order;
import com.reis.ManagementControl_API.Entities.DTO.TotalPerLocationDTO;
import com.reis.ManagementControl_API.Entities.Enums.PaymentMethod;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT SUM(o.totalValue) FROM Order o WHERE o.date = :date")
	BigDecimal sumTotalValueByDate(LocalDate date);
	
	@Query("SELECT SUM(o.totalValue) FROM Order o WHERE o.date = :date AND o.paymentMethod = :method")
	BigDecimal sumTotalValueByDateAndPaymentMethod(LocalDate date, PaymentMethod method);
	
	@Query("SELECT SUM (o.totalValue) FROM Order o WHERE o.date BETWEEN :date AND :finalDate")
	BigDecimal sumTotalValueByDateBetween(LocalDate date, LocalDate finalDate);
	
	@Query("SELECT SUM (o.totalValue) FROM Order o WHERE o.date BETWEEN :date AND :finalDate AND o.paymentMethod = :method")
	BigDecimal sumTotalValueByDateBetweenAndPaymentMethod(LocalDate date, LocalDate finalDate, PaymentMethod method);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.TotalPerLocationDTO(o.location.name, SUM(o.totalValue))"
			+ "FROM Order o WHERE o.date = :date GROUP By o.location.name")
	List<TotalPerLocationDTO> findByDate(LocalDate date);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.TotalPerLocationDTO(o.location.name, SUM(o.totalValue))"
			+ "FROM Order o WHERE o.date = :date AND o.location.name IN :names "
			+ "GROUP By o.location.name")
	List<TotalPerLocationDTO> findByDateAndNames(LocalDate date, List<String> names);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.TotalPerLocationDTO(o.location.name, SUM(o.totalValue))"
			+ "FROM Order o WHERE o.date BETWEEN :date AND :finalDate GROUP By o.location.name")
	List<TotalPerLocationDTO> findByDateBetween(LocalDate date, LocalDate finalDate);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.TotalPerLocationDTO(o.location.name, SUM(o.totalValue))"
			+ "FROM Order o WHERE o.date BETWEEN :date AND :finalDate AND o.location.name IN :names "
			+ "GROUP By o.location.name")
	List<TotalPerLocationDTO> findByDateBetweenAndNames(LocalDate date, LocalDate finalDate, List<String> names);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.TotalPerLocationDTO(o.location.name, SUM(o.totalValue))"
			+ "FROM Order o WHERE o.date BETWEEN :monday AND :sunday GROUP By o.location.name ORDER BY SUM(o.totalValue) DESC")
	List<TotalPerLocationDTO> findByDateBetweenWeekly(LocalDate monday, LocalDate sunday, Pageable pageable);
	
	List<Order> findOrderByDate(LocalDate date);
	
	List<Order> findOrderByDateBetween(LocalDate date, LocalDate finalDate);
}

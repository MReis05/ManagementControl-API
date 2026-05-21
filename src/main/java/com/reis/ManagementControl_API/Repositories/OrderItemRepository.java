package com.reis.ManagementControl_API.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.reis.ManagementControl_API.Entities.OrderItem;
import com.reis.ManagementControl_API.Entities.DTO.OrderItemHistoryDTO;
import com.reis.ManagementControl_API.Entities.Enums.Category;
import com.reis.ManagementControl_API.Entities.PK.OrderItemPK;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), AVG(o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date = :date GROUP By o.id.product.name, o.id.product.category")
	List<OrderItemHistoryDTO> findByDate(LocalDate date);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), AVG(o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date = :date AND o.id.product.name IN :names "
			+ "GROUP By o.id.product.name, o.id.product.category")
	List<OrderItemHistoryDTO> findByDateAndNames(LocalDate date, List<String> names);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), AVG(o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date = :date AND o.id.product.category = :category GROUP By o.id.product.name, o.id.product.category")
	List<OrderItemHistoryDTO> findByDateAndCategory(LocalDate date, Category category);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), AVG(o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date BETWEEN :date AND :finalDate GROUP By o.id.product.name, o.id.product.category")
	List<OrderItemHistoryDTO> findByDateBetween(LocalDate date, LocalDate finalDate);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), AVG(o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date BETWEEN :date AND :finalDate AND o.id.product.name IN :names "
			+ "GROUP By o.id.product.name, o.id.product.category")
	List<OrderItemHistoryDTO> findByDateBetweenAndNames(LocalDate date, LocalDate finalDate, List<String> names);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), AVG(o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date BETWEEN :date AND :finalDate AND o.id.product.category = :category GROUP By o.id.product.name, o.id.product.category")
	List<OrderItemHistoryDTO> findByDateBetweenAndCategory(LocalDate date, LocalDate finalDate, Category category);
	
	@Query("SELECT new com.reis.ManagementControl_API.Entities.DTO.OrderItemHistoryDTO(o.id.product.name, SUM(o.quantity * o.unitValue), AVG(o.unitValue), o.id.product.category)"
			+ "FROM OrderItem o WHERE o.id.order.date BETWEEN :monday AND :sunday GROUP By o.id.product.name, o.id.product.category "
			+ "ORDER BY SUM(o.quantity * o.unitValue) DESC")
	List<OrderItemHistoryDTO> findByDateBetweenWeekly(LocalDate monday, LocalDate sunday, Pageable pageable);
}

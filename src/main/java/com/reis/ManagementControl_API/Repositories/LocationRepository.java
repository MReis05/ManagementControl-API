package com.reis.ManagementControl_API.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.ManagementControl_API.Entities.Location;

public interface LocationRepository extends JpaRepository<Location, Long>  {

}

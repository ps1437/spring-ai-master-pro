package com.syscho.ai.external.db;

import com.syscho.ai.external.db.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment_Name(String departmentName);
}
